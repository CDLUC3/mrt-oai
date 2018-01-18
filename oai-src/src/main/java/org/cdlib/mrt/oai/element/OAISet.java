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



import org.cdlib.mrt.inv.content.InvCollection;
import com.lyncode.xoai.dataprovider.model.Set;

/**
 * Run OAI RecordAction
 * @author dloy
 */
public class OAISet
{

    protected static final String NAME = "OAISet";
    protected static final String MESSAGE = NAME + ": ";
    protected static final boolean DEBUG = true;
    protected String mnemonic = null;
    protected String name = null;
    
    
    public static OAISet getOAISet(InvCollection collection)
    {
        return new OAISet(collection);
    }
    
    public static OAISet getOAISet(String mnemonic, String name)
    {
        return new OAISet(mnemonic, name);
    }
    
    protected OAISet(InvCollection collection)
    {
        this.mnemonic = collection.getMnemonic();
        this.name = collection.getName();
    }
    
    protected OAISet(String mnemonic, String name)
    {
        this.mnemonic = mnemonic;
        this.name = name;
    }
        
    
    public String getCollectionMnemonic()
    {
        return mnemonic;
    }  
    
    public String getCollectionName()
    {
        return name;
    }  
    
    public String getSetSpec()
    {
        return mnemonic;
    }  
    
    public String getSetName()
    {
        return name;
    }
    
    public Set getXOAISet()
    {
        return new Set(mnemonic)
                .withName(name);
    }
    
    public String dump(String header)
    {
        StringBuffer buf = new StringBuffer();
        buf.append("OAISet:");
        if (header != null) {
            buf.append(header);
        }
        buf.append(" - spec:" + getSetSpec());
        buf.append(" - name:" + getSetName());
        return buf.toString();
    }
}

