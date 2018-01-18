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
import java.sql.Connection;
import com.lyncode.xoai.dataprovider.exceptions.BadArgumentException;
import com.lyncode.xoai.dataprovider.exceptions.CannotDisseminateFormatException;
import com.lyncode.xoai.dataprovider.exceptions.HandlerException;
import com.lyncode.xoai.dataprovider.exceptions.InternalOAIException;
import org.cdlib.mrt.core.Identifier;


import org.cdlib.mrt.inv.content.InvMeta;
import org.cdlib.mrt.inv.content.InvVersion;
import org.cdlib.mrt.inv.utility.InvDBUtil;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.oai.utility.OAIDBUtil;
import org.cdlib.mrt.utility.DateUtil;
import org.cdlib.mrt.utility.StringUtil;

/**
 * OAI Prefix
 * @author dloy
 */
public class OAIMetadata
{

    protected static final String NAME = "OAIMetadata";
    protected static final String MESSAGE = NAME + ": ";
    protected static final boolean DEBUG = true;

    public enum Type {oai, mrt }
    public enum PrefixType
    {
        oai_dc ("oai_dc", "OAI_DublinCore", "2.0"),
        dcs3_1 ("dcs3.1", "DataCite", "3.1"),
        stwrap ("stash_wrapper", "StashWrapper", "1.0"),
        unknown ("UNKNOWN", "UNKNOWN", "UNKNOWN");

        protected final String oai; // oai form
        protected final String mrt; // profile mnemonic form
        protected final String version; // sql version

        
        PrefixType(String oai, String mrt, String version) {
            this.oai = oai;
            this.mrt = mrt;
            this.version = version;
        }

        public String getOAI()   { return oai; }
        public String getMrt()   { return mrt; }


        public static PrefixType getOAIType(String t)
        {
            for (PrefixType p : PrefixType.values()) {
                if (p.getOAI().equals(t)) {
                    return p;
                }
            }
            return null;
        }

        public static PrefixType getMrtType(String t)
        {
            for (PrefixType p : PrefixType.values()) {
                if (p.getMrt().equals(t)) {
                    return p;
                }
            }
            return null;
        }
    }
    protected PrefixType prefixType = null;
    protected String unknownPrefix = null;
    
    public static OAIMetadata setOAIPrefix(
            String prefix)
        throws HandlerException
    {
        return new OAIMetadata(Type.oai, prefix);
    }
    
    public static OAIMetadata setMrtPrefix(
            String prefix)
        throws HandlerException
    {
        return new OAIMetadata(Type.mrt, prefix);
    }
    
    protected OAIMetadata(Type type, String prefix)
        throws HandlerException
    {
        if (StringUtil.isAllBlank(prefix)) {
            throw new BadArgumentException ("Prefix not provided");
        }
        switch (type) {
            case oai:
                inOAI(prefix);
                break;

            case mrt:
                inMrt(prefix);
                break;
                
            default:
                System.out.println("Unknown Type type");
                unknownPrefix = prefix;
                prefixType = PrefixType.unknown;
                break;
        }
    }
    
    protected void inOAI(String oaiPrefix)
        throws HandlerException
    {
        this.prefixType = PrefixType.getOAIType(oaiPrefix);
        if (prefixType == null) {
            setUnknown(oaiPrefix);
        }
    }
    
    protected void inMrt(String mrtPrefix)
        throws HandlerException
    {
        this.prefixType = PrefixType.getMrtType(mrtPrefix);
        if (prefixType == null) {
             setUnknown(mrtPrefix);
        }
    }

    public String getOAIPrefix() {
        return prefixType.getOAI();
    }

    public String getMrtPrefix() {
        return prefixType.getMrt();
    }

    public PrefixType getPrefixType() {
        return prefixType;
    }

    public String getMetaVersion() {
        return prefixType.version;
    }

    public String getUnknownPrefix() {
        return unknownPrefix;
    }
    
    public boolean isUnknown()
    {
        if (prefixType == prefixType.unknown) return true;
        return false;
    }
    
    protected void setUnknown(String unknownPrefix)
    {
        this.prefixType = prefixType.unknown;
        this.unknownPrefix = unknownPrefix;
    }
    
    public String dump(String header)
    {
        StringBuffer buf = new StringBuffer();
        buf.append("OAIPrefix:");
        if (header != null) {
            buf.append(header);
        }
        buf.append(" - mrt:" + getMrtPrefix());
        buf.append(" - oai:" + getOAIPrefix());
        return buf.toString();
    }
}

