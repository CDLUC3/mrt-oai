/******************************************************************************
Copyright (c) 2005-2012, Regents of the University of California
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:
 *
- Redistributions of source code must retain the above copyright notice,
  this list of conditions and the following disclaimer.
- Redistributions in binary form must reproduce the above copyright
  notice, this list of conditions and the following disclaimer in the
  documentation and/or other materials provided with the distribution.
- Neither the name of the University of California nor the names of its
  contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
OF THE POSSIBILITY OF SUCH DAMAGE.
*******************************************************************************/
package org.cdlib.mrt.oai.utility;

import com.lyncode.xoai.dataprovider.exceptions.HandlerException;
import com.lyncode.xoai.dataprovider.exceptions.InternalOAIException;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import org.cdlib.mrt.core.Identifier;
import org.cdlib.mrt.core.DateState;
import org.cdlib.mrt.db.DBUtil;
import org.cdlib.mrt.oai.element.OAIItem;
import org.cdlib.mrt.inv.utility.InvUtil;
import org.cdlib.mrt.oai.element.OAIDate;
import org.cdlib.mrt.oai.element.OAIId;
import org.cdlib.mrt.oai.element.OAIMetadata;
import org.cdlib.mrt.oai.element.OAISet;
import org.cdlib.mrt.inv.content.InvAudit;
import org.cdlib.mrt.inv.content.InvCollection;
import org.cdlib.mrt.inv.content.InvCollectionNode;
import org.cdlib.mrt.inv.content.InvCollectionObject;
import org.cdlib.mrt.inv.content.InvGCopy;
import org.cdlib.mrt.inv.content.InvDKVersion;
import org.cdlib.mrt.inv.content.InvDua;
import org.cdlib.mrt.inv.content.InvFile;
import org.cdlib.mrt.inv.content.InvIngest;
import org.cdlib.mrt.inv.content.InvMeta;
import org.cdlib.mrt.inv.content.InvNode;
import org.cdlib.mrt.inv.content.InvNodeObject;
import org.cdlib.mrt.inv.content.InvObject;
import org.cdlib.mrt.inv.content.InvOwner;
import org.cdlib.mrt.inv.content.InvVersion;
import org.cdlib.mrt.utility.FileUtil;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.PropertiesUtil;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TException;


/**
 * This interface defines the functional API for a Curational Storage Service
 * @author dloy
 */
public class OAIDBUtil
{

    protected static final String NAME = "OAIDBUtil";
    protected static final String MESSAGE = NAME + ": ";
    protected static final boolean DEBUG = false;

    protected static final String NL = System.getProperty("line.separator");

    
    protected OAIDBUtil() {}

    public static List<InvCollection> getPublicCollections(
            Connection connection,
            LoggerInf logger)
        throws TException
    {
        log("getOwner entered");
        String sql = "select * from inv_collections where harvest_privilege='public';";
        Properties[] propArray = DBUtil.cmd(connection, sql, logger);
        
        if ((propArray == null)) {
            log("InvDBUtil - prop null");
            return null;
        } else if (propArray.length == 0) {
            log("InvDBUtil - length == 0");
            return null;
        }
        ArrayList<InvCollection> copiesList = new ArrayList(propArray.length);
        
        for (Properties prop : propArray) {
            InvCollection collection = new InvCollection(prop, logger);
            copiesList.add(collection);
        }
        log("DUMP" + PropertiesUtil.dumpProperties("prop", propArray[0]));
        return copiesList;
    }

    public static InvMeta getMetadata(
            OAIMetadata prefix,
            Identifier objectID,
            Connection connection,
            LoggerInf logger)
        throws TException
    {
        log("getMetadata entered");
        
        String schema = prefix.getMrtPrefix();
        String metaVersion = prefix.getMetaVersion();
        String sql = "select inv_metadatas.* "
            + "from inv_metadatas, inv_versions, inv_objects "
            + "where inv_metadatas.md_schema='" + schema + "' "
            + "and inv_metadatas.version='" + metaVersion + "' "
            + "and inv_objects.ark='" + objectID.getValue() + "' "
            + "and inv_versions.number = inv_objects.version_number "
            + "and inv_versions.inv_object_id = inv_objects.id "
            + "and inv_metadatas.inv_version_id=inv_versions.id;";  
        
        Properties[] propArray = DBUtil.cmd(connection, sql, logger);
        
        if ((propArray == null)) {
            log("InvDBUtil - prop null");
            return null;
        } else if (propArray.length == 0) {
            log("InvDBUtil - length == 0");
            return null;
        }
        
        InvMeta meta = new InvMeta(logger);
        meta.setProp(propArray[0]);
        return meta;
    }

    public static OAIItem getItem(
            OAIMetadata metaType,
            OAIId objectid,
            Connection connection,
            LoggerInf logger)
    {
        try {
            log("getItem entered");
            String schema = metaType.getMrtPrefix();
            String metaVersion = metaType.getMetaVersion();
            String sql = "select inv_objects.ark, inv_objects.modified, inv_metadatas.md_schema, inv_metadatas.value "
                + "from inv_metadatas, inv_versions, inv_objects "
                + "where inv_metadatas.md_schema='" + schema + "' "
                + "and inv_metadatas.version='" + metaVersion + "' "
                + "and inv_objects.ark='" + objectid.getArk() + "' "
                + "and inv_versions.number = inv_objects.version_number "
                + "and inv_versions.inv_object_id = inv_objects.id "
                + "and inv_metadatas.inv_version_id=inv_versions.id;";  

            Properties[] propArray = DBUtil.cmd(connection, sql, logger);

            if ((propArray == null)) {
                log("InvDBUtil - prop null");
                return null;
            } else if (propArray.length == 0) {
                log("InvDBUtil - length == 0");
                return null;
            }
            
            OAIItem item = OAIItem.getOAIItemRecord(propArray[0]);
            Properties[] setProps = getSets(objectid, connection, logger);
            item.setSets(setProps);
            return item;
        
        } catch (Exception ex) {
            throw new InternalOAIException("Texception in getItem:" + ex);
        }
    }
    
    public static Properties [] getSets(
            OAIId objectid,
            Connection connection,
            LoggerInf logger)
        throws HandlerException
    {
        try {
        log("getSets entered");
        String sql = "select mnemonic, name from inv_collections, inv_collections_inv_objects, inv_objects "
           + "where inv_objects.ark='" + objectid.getArk() + "' "
           + "and inv_collections.harvest_privilege='public' "
           + "and inv_collections_inv_objects.inv_object_id = inv_objects.id "
           + "and inv_collections.id = inv_collections_inv_objects.inv_collection_id;";
        Properties[] propArray = DBUtil.cmd(connection, sql, logger);
        
        if ((propArray == null)) {
            log("InvDBUtil - prop null");
            return null;
        } else if (propArray.length == 0) {
            log("InvDBUtil - length == 0");
            return null;
        }
        
        return propArray;
        
        } catch (TException tex) {
            throw new InternalOAIException("Texception in getItem:" + tex);
        }
    }
    
    public static ListOAIItems getItemsContent(
            OAIMetadata metaType,
            String setMnemonic,
            OAIDate from,
            OAIDate until,
            Connection connection,
            LoggerInf logger)
        throws TException
    {
        log("getItemsContent entered");
        
        String schema = metaType.getMrtPrefix();
        String metaVersion = metaType.getMetaVersion();
        String sql1 = "select inv_collections.mnemonic, inv_collections.name, " 
           + "inv_versions.ark, inv_versions.number, inv_objects.modified, inv_metadatas.value "
           + "from inv_versions,inv_metadatas,inv_collections,inv_collections_inv_objects, inv_objects  "
           + "where inv_collections.harvest_privilege = 'public' "
           + "and inv_metadatas.md_schema='" + schema + "' "
           + "and inv_metadatas.version='" + metaVersion + "' ";
        String sql2 = "and inv_collections_inv_objects.inv_collection_id = inv_collections.id "
          + "and inv_versions.inv_object_id = inv_collections_inv_objects.inv_object_id "
          + "and inv_versions.id = inv_metadatas.inv_version_id "
          + "and inv_versions.inv_object_id = inv_metadatas.inv_object_id "
          + "and inv_objects.id = inv_versions.inv_object_id "
          + "and inv_objects.version_number = inv_versions.number;";
        
        
        String fromSql = "";
        if (from != null) {
            String dbDateS = from.getDB();
            fromSql = "and inv_objects.modified >= '" + dbDateS + "' ";
        }
        String untilSql = "";
        if (until != null) {
            String dbDateS = until.getDB();
            untilSql = "and inv_objects.modified <= '" + dbDateS + "' ";
        }
        String metaSetSql = "";
        if (setMnemonic != null) {
            metaSetSql = "and inv_collections.mnemonic='" + setMnemonic + "' ";
        }
        String sql = sql1 + fromSql + untilSql + metaSetSql + sql2;
        System.out.println("getMetadatas sql=" + sql);
        Properties[] propArray = DBUtil.cmd(connection, sql, logger);
        
        if ((propArray == null)) {
            log("InvDBUtil - prop null");
            return null;
        } else if (propArray.length == 0) {
            log("InvDBUtil - length == 0");
            return null;
        }
        ListOAIItems listOAIItems = ListOAIItems.getListOAIItems(propArray);
        return listOAIItems;
    }
    
    public static ListOAIItems getIdsContent(
            OAIMetadata metaType,
            String setMnemonic,
            OAIDate from,
            OAIDate until,
            Connection connection,
            LoggerInf logger)
        throws TException
    {
        log("getIdsContent entered");
        
        String schema = metaType.getMrtPrefix();
        String metaVersion = metaType.getMetaVersion();
        String sql1 = "select inv_collections.mnemonic, inv_collections.name, " 
           + "inv_versions.ark, inv_versions.number, inv_objects.modified "
           + "from inv_versions,inv_metadatas,inv_collections,inv_collections_inv_objects, inv_objects  "
           + "where inv_collections.harvest_privilege = 'public' "
           + "and inv_metadatas.md_schema='" + schema + "' "
           + "and inv_metadatas.version='" + metaVersion + "' ";
        String sql2 = "and inv_collections_inv_objects.inv_collection_id = inv_collections.id "
          + "and inv_versions.inv_object_id = inv_collections_inv_objects.inv_object_id "
          + "and inv_versions.id = inv_metadatas.inv_version_id "
          + "and inv_versions.inv_object_id = inv_metadatas.inv_object_id "
          + "and inv_objects.id = inv_versions.inv_object_id "
          + "and inv_objects.version_number = inv_versions.number;";
        
        
        String fromSql = "";
        if (from != null) {
            String dbDateS = from.getDB();
            fromSql = "and inv_objects.modified >= '" + dbDateS + "' ";
        }
        String untilSql = "";
        if (until != null) {
            String dbDateS = until.getDB();
            untilSql = "and inv_objects.modified <= '" + dbDateS + "' ";
        }
        String metaSetSql = "";
        if (setMnemonic != null) {
            metaSetSql = "and inv_collections.mnemonic='" + setMnemonic + "' ";
        }
        String sql = sql1 + fromSql + untilSql + metaSetSql + sql2;
        System.out.println("getMetadatas sql=" + sql);
        Properties[] propArray = DBUtil.cmd(connection, sql, logger);
        
        if ((propArray == null)) {
            log("InvDBUtil - prop null");
            return null;
        } else if (propArray.length == 0) {
            log("InvDBUtil - length == 0");
            return null;
        }
        
        ListOAIItems listOAIItems = ListOAIItems.getListOAIItems(propArray);
        List<OAIItem> idList =listOAIItems.getList();
        try {
            for (OAIItem id : idList){
                Properties[] setProps = getSets(id.getOaiId(), connection, logger);
                id.setSets(setProps);
            }
            
        } catch (Exception ex) {
                throw new InternalOAIException(ex.toString());
        }
        return listOAIItems;
    }
    
    
    protected static void log(String msg)
    {
        if (!DEBUG) return;
        System.out.println(MESSAGE + msg);
    }
}

