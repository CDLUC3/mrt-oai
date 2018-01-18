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
import org.cdlib.mrt.core.DateState;
import org.cdlib.mrt.core.Identifier;


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
public class OAIId
{

    protected static final String NAME = "OAIId";
    protected static final String MESSAGE = NAME + ": ";
    protected static final boolean DEBUG = true;
    public static final String N2T = "http://n2t.net/";
    public static final String N2TARK = N2T + "ark";
    public enum Type {ark, n2t, test }
    protected String in = null;
    protected Identifier id = null;
    
    
    public static OAIId getArkId(String idS)
    {
        return new OAIId(Type.ark, idS);
    }
    
    public static OAIId getN2TId(String idS)
    {
        return new OAIId(Type.n2t, idS);
    }
    
    public static OAIId getDate(String idS)
    {
        return new OAIId(Type.test, idS);
    }
    
    protected OAIId(Type type, String idS)
    {
        try {
            this.in = idS;
            if (StringUtil.isAllBlank(idS)) {
                throw new RuntimeException(MESSAGE + "Empty data");
            }
            switch (type) {
                case ark:
                    inArk();
                    break;

                case n2t:
                    inN2T();
                    break;

                case test:
                    inTest();
                    break;

                default:
                    System.out.println("Unknown Date type");
                    break;
            }
        } catch (Exception ex) {
            throw new RuntimeException(MESSAGE + "OAIId" 
                    + " - in:" + in
                    + " - ex:" + ex
                );
        }
    }
        
    protected void inArk()
        throws TException
    {
        if (!in.startsWith("ark")) {
            throw new RuntimeException(MESSAGE + "OAIId not ark" 
                    + " - in:" + in
                );
        }
        this.id = new Identifier(in);
    }
        
    protected void inN2T()
        throws TException
    {
        if (!in.startsWith(N2TARK)) {
            throw new RuntimeException(MESSAGE + "OAIId not N2T" 
                    + " - in:" + in
                );
        }
        String arkS = in.substring(N2T.length());
        this.id = new Identifier(arkS);
    }
    
    protected void inTest()
        throws TException
    {
        if (in.startsWith(N2TARK)) {
            inN2T();
            return;
        } else {
            inArk();
        }
    }
    
    public Identifier getId()
    {
        return this.id;
    }
    
    public String getArk()
    {
        return id.getValue();
    }
    
    public String getN2T()
    {
        String retId = id.getValue();
        
        return N2T + retId;
    }
    
    public String dump(String header)
    {
        StringBuffer buf = new StringBuffer();
        buf.append("OAIId:");
        if (header != null) {
            buf.append(header);
        }
        buf.append(" - id:" + getArk());
        buf.append(" - n2T:" + getN2T());
        return buf.toString();
    }
}

