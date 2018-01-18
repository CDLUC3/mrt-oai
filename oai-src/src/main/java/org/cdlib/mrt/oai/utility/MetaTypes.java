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

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Properties;

import com.lyncode.xoai.dataprovider.exceptions.HandlerException;
import com.lyncode.xoai.dataprovider.exceptions.InternalOAIException;
import org.cdlib.mrt.core.Identifier;
import org.cdlib.mrt.core.DateState;
import org.cdlib.mrt.db.DBUtil;
import org.cdlib.mrt.inv.utility.InvUtil;
import org.cdlib.mrt.oai.element.OAIDate;
import org.cdlib.mrt.oai.element.OAIId;
import org.cdlib.mrt.oai.element.OAIMetadata;
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
public class MetaTypes
{

    protected static final String NAME = "MetaTypes";
    protected static final String MESSAGE = NAME + ": ";
    protected static final boolean DEBUG = true;

    protected final Properties metaProp;
    protected ArrayList<MetaType> list = new ArrayList();
    
    public static MetaTypes getMetaTypes(Properties metaProp)
        throws HandlerException
    {
        try {
            return new MetaTypes(metaProp);
        } catch (HandlerException hex) {
            hex.printStackTrace();
            throw hex;
        }
    }
    protected MetaTypes(Properties metaProp) 
        throws HandlerException
    {
        this.metaProp = metaProp;
        addProps(metaProp);
    }
    
    public MetaType add(
            OAIMetadata metadataPrefix,
            String schema,
            String metadataNamespace,
            String sqlVersion)
        throws HandlerException
    {
        MetaType metaType = getMetaType(metadataPrefix, schema, metadataNamespace, sqlVersion);
        list.add(metaType);
        return metaType;
    }
    
    public void addProps(
            final Properties prop)
        throws HandlerException
    {
        System.out.println(PropertiesUtil.dumpProperties("addProps", prop));
        for (int i=1; true; i++) {
            String schema = prop.getProperty("schema." + i);
            String metadataPrefixS = prop.getProperty("metadataPrefix." + i);
            String metadataNamespace = prop.getProperty("metadataNamespace." + i);
            String sqlVersion = prop.getProperty("sqlVersion." + i);
            if (StringUtil.isAllBlank(schema)
                    && StringUtil.isAllBlank(metadataPrefixS)
                    && StringUtil.isAllBlank(metadataNamespace)
                    ) break;
            if (StringUtil.isAllBlank(schema)
                    || StringUtil.isAllBlank(metadataPrefixS)
                    || StringUtil.isAllBlank(metadataNamespace)
                    || StringUtil.isAllBlank(sqlVersion)
                    ) {
                throw new InternalOAIException("MetaType not complete:"
                        + " - schema:" + schema
                        + " - metadataPrefix:" + metadataPrefixS
                        + " - metadataNamespace:" + metadataNamespace
                );
                        
            }
            OAIMetadata metadataPrefix= OAIMetadata.setOAIPrefix(metadataPrefixS);
            add(metadataPrefix, schema, metadataNamespace, sqlVersion);
        }
    }
    
    
    
    public List<MetaType> getList()
    {
        if (list.size() == 0) return null;
        return list;
    }
    
    public static MetaType getMetaType(
                final OAIMetadata prefix,
                final String schema,
                final String name,
                final String sqlVersion)
        throws HandlerException
    {
        return new MetaType(prefix, schema, name, sqlVersion);
    }
    
    public static class MetaType
    {
        public final OAIMetadata prefix;
        public final String schema;
        public final String name;
        public final String sqlVersion;
        
        public MetaType(
                final OAIMetadata prefix,
                final String schema,
                final String name,
                final String sqlVersion)
            throws HandlerException
        {
            
            this.prefix = prefix;
            this.schema = schema;
            this.name = name;
            this.sqlVersion = sqlVersion;
        }
        
        public String dump(String header)
        {
            StringBuffer buf = new StringBuffer();
            buf.append("MetaType:");
            if (header != null) {
                buf.append(header);
            }
            buf.append("\n");
            buf.append(" - prefix:" + prefix.dump("") + "\n");
            buf.append(" - schema:" + schema + "\n");
            buf.append(" - name:" + name + "\n");
            buf.append(" - sqlVersion:" + sqlVersion + "\n");
            return buf.toString();
        }
    }
}

