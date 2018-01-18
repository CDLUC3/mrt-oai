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
package org.cdlib.mrt.oai.element;

import org.cdlib.mrt.oai.action.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Properties;
import com.lyncode.xoai.dataprovider.model.Set;
import com.lyncode.xoai.dataprovider.exceptions.BadArgumentException;
import com.lyncode.xoai.dataprovider.exceptions.CannotDisseminateFormatException;
import com.lyncode.xoai.dataprovider.exceptions.HandlerException;
import com.lyncode.xoai.dataprovider.exceptions.InternalOAIException;
import org.cdlib.mrt.core.DateState;
import org.cdlib.mrt.core.Identifier;
import com.lyncode.xoai.dataprovider.model.ItemIdentifier;
import java.util.Date;
import java.util.List;

import org.cdlib.mrt.inv.content.InvMeta;
import org.cdlib.mrt.inv.content.InvVersion;
import org.cdlib.mrt.inv.utility.InvDBUtil;
import org.cdlib.mrt.inv.utility.InvUtil;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.oai.utility.OAIDBUtil;
import org.cdlib.mrt.utility.DateUtil;
import org.cdlib.mrt.utility.StringUtil;

/**
 * Run OAI RecordAction
 * @author dloy
 */
public class OAIItem
    implements ItemIdentifier
{

    protected static final String NAME = "OAIItem";
    protected static final String MESSAGE = NAME + ": ";
    protected static final boolean DEBUG = true;
    protected OAIDate oaiDate = null;
    protected OAIId oaiId = null;
    protected OAIMetadata oaiMetaType = null;
    protected ArrayList<OAISet> oaiSets = null;
    protected String value = null;
    protected int versionNum = 0;
    
    public static OAIItem getOAIItemRecord(Properties prop)
    {
        OAIItem oaiItem = new OAIItem();
        oaiItem.setRecordProp(prop);
        return oaiItem;
    }
    
    public static OAIItem getOAIItemItem(Properties prop)
    {
        OAIItem oaiItem = new OAIItem();
        oaiItem.setItemProp(prop);
        return oaiItem;
    }
    
    public static OAIItem getOAIItemMeta(Properties prop)
    {
        OAIItem oaiItem = new OAIItem();
        oaiItem.setMetaProp(prop);
        return oaiItem;
    }
    
    public OAIItem() {}
    
    public OAIItem(String dbId, String dbDate, String dbMetaType, String value)
    {
        setRecord(dbId, dbDate, dbMetaType, value);
    }
    
    public void setRecordProp(Properties prop)
    {
        setRecord(
                prop.getProperty("ark"),
                prop.getProperty("modified"),
                prop.getProperty("md_schema"),
                prop.getProperty("value")
        );
    }
    
    public void setItemProp(Properties prop)
    {
            String mnemonic = prop.getProperty("mnemonic");
            String setName = prop.getProperty("name");
            String arkS = prop.getProperty("ark");
            String modifiedS = prop.getProperty("modified");
            String versionNumS = prop.getProperty("number");
            value = prop.getProperty("value");
            setItem(mnemonic, setName, arkS, modifiedS, versionNumS);
    }
    
    public void setMetaProp(Properties prop)
    {
        setRecordMeta(
                prop.getProperty("ark"),
                prop.getProperty("modified")
        );
    }
        
    private void setItem(
            final String mnemonic,
            final String setName,
            final String arkS,
            final String modifiedS,
            final String versionNumS)
    {
        addSet(mnemonic, setName);
        this.oaiId = OAIId.getArkId(arkS);
        this.oaiDate = OAIDate.getDBDate(modifiedS);
        this.versionNum = Integer.parseInt(versionNumS);
    }
    
    public void setRecord(String dbId, String dbDate, String dbMetaType, String value)
    {
        try {
            if (dbDate == null) this.oaiDate = null;
            else this.oaiDate = OAIDate.getDBDate(dbDate);
            
            if (dbId == null) this.oaiId = null;
            else this.oaiId = OAIId.getArkId(dbId);

            if (dbMetaType == null) this.oaiMetaType = null;
            else this.oaiMetaType = OAIMetadata.setMrtPrefix(dbMetaType);

            if (StringUtil.isAllBlank(value)) this.value = null;
            else this.value = value;
            validate();
        } catch (Exception ex) {
            throw new InternalOAIException(ex.toString());
        }
    }
    
    public void setRecordMeta(String dbId, String dbDate)
    {
        try {
            if (dbDate == null) this.oaiDate = null;
            else this.oaiDate = OAIDate.getDBDate(dbDate);
            
            if (dbId == null) this.oaiId = null;
            else this.oaiId = OAIId.getArkId(dbId);
            
        } catch (Exception ex) {
            throw new InternalOAIException(ex.toString());
        }
    }
    
    private void validate()
    {
        if (oaiDate == null) {
            throw new InternalOAIException("date missing");
        }
        if (oaiId == null) {
            throw new InternalOAIException("id missing");
        }
        if (value == null) {
            throw new InternalOAIException("value missing");
        }
        
    }
    
    public void setSets(Properties [] props)
    {
        if ((props == null) || (props.length == 0)) return;
        oaiSets = new ArrayList<OAISet>(props.length);
        for (Properties prop : props) {
            OAISet set = buildSet(prop);
            oaiSets.add(set);
            //System.out.println("***setSets add");
        }
    }
    
    public void addSet(String mnemonic, String name)
    {
        if (StringUtil.isAllBlank(mnemonic) || StringUtil.isAllBlank(name)) return;
        if (oaiSets == null) {
            oaiSets = new ArrayList<OAISet>();
        }
        OAISet oaiSet = OAISet.getOAISet(mnemonic, name);
        oaiSets.add(oaiSet);
    }
    
    private OAISet buildSet(Properties prop)
    {
        return new OAISet(prop.getProperty("mnemonic"), prop.getProperty("name"));
    }
    
    public String dump(String header)
    {
        StringBuffer buf = new StringBuffer();
        buf.append("OAIItem:");
        if (header != null) {
            buf.append(header + "\n");
        }
        if (oaiId != null) {
            buf.append(oaiId.dump("")+ "\n");
        }
        if (oaiDate != null) {
            buf.append(oaiDate.dump("")+ "\n");
        }
        if (oaiMetaType != null) {
            buf.append(oaiMetaType.dump("")+ "\n");
        }
        if (value != null) {
            buf.append(value + "\n");
        }
        if (oaiSets != null) {
            for (OAISet set : oaiSets) {
                buf.append(set.dump("") + "\n");
            }
        }
        return buf.toString();
    }

    public OAIDate getOaiDate() {
        return oaiDate;
    }

    public OAIId getOaiId() {
        return oaiId;
    }
    
    public ArrayList<Set> getXoaiSets()
    {
        if ((oaiSets == null) || (oaiSets.size() == 0)) return null;
        ArrayList<Set> xoaiSets = new ArrayList(oaiSets.size());
        for (OAISet set : oaiSets) {
            Set xoaiSet = set.getXOAISet();
            xoaiSets.add(xoaiSet);
        }
        return xoaiSets;
    }

    public OAIMetadata getOaiMetaType() {
        return oaiMetaType;
    }

    public ArrayList<OAISet> getOaiSets() {
        return oaiSets;
    }

    public String getValue() {
        return value;
    }
    
    /**
     * Returns the OAI-PMH unique identifier.
     *
     * @return OAI-PMH unique identifier.
     * @see <a href="client://www.openarchives.org/OAI/openarchivesprotocol.html#UniqueIdentifier">Unique identifier definition</a>
     */
    public String getIdentifier() {
        if (oaiId == null) return null;
        return oaiId.getN2T();
    }

    /**
     * Creation, modification or deletion date.
     *
     * @return OAI-PMH records datestamp
     * @see <a href="client://www.openarchives.org/OAI/openarchivesprotocol.html#Record">Record definition</a>
     */
    public Date getDatestamp() {
        if (oaiDate == null) return null;
        return oaiDate.getUnixDate();
    }

    /**
     * Exposes the list of sets (using the set_spec) that contains the item (OAI-PMH records).
     *
     * @return List of sets
     * @see <a href="client://www.openarchives.org/OAI/openarchivesprotocol.html#Set">Set definition</a>
     * @see <a href="client://www.openarchives.org/OAI/openarchivesprotocol.html#Record">Record definition</a>
     */
    public List<Set> getSets() {
        if ((oaiSets == null) || (oaiSets.size() == 0)) return null;
        ArrayList<Set> sets = new ArrayList(oaiSets.size());
        for (OAISet oaiSet : oaiSets) {
            Set set = oaiSet.getXOAISet();
            sets.add(set);
        }
        return sets;
    }

    /**
     * Checks if the item is deleted or not.
     *
     * @return Checks if the item is deleted or not.
     * @see <a href="client://www.openarchives.org/OAI/openarchivesprotocol.html#Record">Record definition</a>
     */
    public boolean isDeleted() {
        return false;
    }
}

