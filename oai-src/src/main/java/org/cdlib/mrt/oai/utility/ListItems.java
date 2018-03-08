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
import org.cdlib.mrt.core.Identifier;
import org.cdlib.mrt.core.DateState;
import org.cdlib.mrt.db.DBUtil;
import org.cdlib.mrt.inv.utility.InvUtil;
import org.cdlib.mrt.oai.element.OAIDate;
import org.cdlib.mrt.oai.element.OAIId;
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
import static org.cdlib.mrt.oai.utility.OAIDBUtil.log;
import org.cdlib.mrt.utility.FileUtil;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.PropertiesUtil;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TException;


/**
 * This interface defines the functional API for a Curational Storage Service
 * @author dloy
 */
public class ListItems
{

    protected static final String NAME = "OAIDBUtil";
    protected static final String MESSAGE = NAME + ": ";
    protected static final boolean DEBUG = true;

    protected static final String NL = System.getProperty("line.separator");
    protected ArrayList<ListItem> list = new ArrayList<>();
    
    protected ListItems() {}
    
    public ListItem add(
            final Properties prop)
        throws TException
    {
        System.out.println(PropertiesUtil.dumpProperties("***ADD***", prop));
        ListItem newItem = getListItem(prop);
        list.add(newItem);
        return newItem;
    }
    
    public void addProps(
            final Properties[] propArray)
        throws TException
    {
        for (Properties prop : propArray) {
            add(prop);
        }
    }
    
    public List<ListItem> getList()
    {
        if (list.size() == 0) return null;
        return list;
    }
    
    public static ListItem getListItem(
            final Properties prop)
        throws TException
    {
        return new ListItem(prop);
    }
    
    public static class ListItem
    {
        public OAISet set;
        public OAIId id;
        public OAIDate modified;
        public int versionNum;
        public String metaRecord;
        
        public ListItem(
                final Properties prop)
            throws TException
        {
            String mnemonic = prop.getProperty("mnemonic");
            String setName = prop.getProperty("name");
            String arkS = prop.getProperty("ark");
            String modifiedS = prop.getProperty("modified");
            String versionNumS = prop.getProperty("number");
            metaRecord = prop.getProperty("value");
            set(mnemonic, setName, arkS, modifiedS, versionNumS);
        }
        
        private void set(
                final String mnemonic,
                final String setName,
                final String arkS,
                final String modifiedS,
                final String versionNumS)
            throws TException
        {
            this.set = OAISet.getOAISet(mnemonic, setName);
            this.id = OAIId.getArkId(arkS);
            this.modified = OAIDate.getDBDate(modifiedS);
            this.versionNum = Integer.parseInt(versionNumS);
        }
        
        public String dump(String header)
        {
            StringBuffer buf = new StringBuffer();
            buf.append("ListId:");
            if (header != null) {
                buf.append(header);
            }
            buf.append("\n");
            buf.append(" - mnemonic:" + set.dump("") + "\n");
            buf.append(" - ark:" + id.dump("") + "\n");
            buf.append(" - modified:" + modified.dump("") + "\n");
            buf.append(" - versionNum:" + versionNum + "\n");
            buf.append(" - data:" + metaRecord + "\n");
            return buf.toString();
        }
    }
}

