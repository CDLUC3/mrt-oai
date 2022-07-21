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
package org.cdlib.mrt.oai.action;

import java.io.File;
import java.sql.Connection;
import org.dspace.xoai.dataprovider.exceptions.BadArgumentException;
import org.dspace.xoai.dataprovider.exceptions.CannotDisseminateFormatException;
import org.dspace.xoai.dataprovider.exceptions.HandlerException;
import org.dspace.xoai.dataprovider.exceptions.InternalOAIException;
import org.cdlib.mrt.core.Identifier;


import org.cdlib.mrt.inv.content.InvMeta;
import org.cdlib.mrt.inv.content.InvVersion;
import org.cdlib.mrt.inv.utility.InvDBUtil;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.oai.element.OAIId;
import org.cdlib.mrt.oai.element.OAIMetadata;
import org.cdlib.mrt.oai.utility.OAIDBUtil;
import org.cdlib.mrt.utility.DateUtil;
import org.cdlib.mrt.utility.StringUtil;

/**
 * Run OAI RecordAction
 * @author dloy
 */
public class RecordAction
        extends ActionAbs
{

    protected static final String NAME = "RecordAction";
    protected static final String MESSAGE = NAME + ": ";
    protected static final boolean DEBUG = true;

    protected OAIMetadata prefix = null;
    protected OAIId id = null;
    protected InvMeta meta = null;
    protected InvVersion invVersion = null;
    protected HandlerException handlerException = null;
    
    public static RecordAction getRecordAction(
            OAIId id,
            OAIMetadata prefix,
            Connection connection,
            LoggerInf logger)
        throws TException
    {
        return new RecordAction(id, prefix, connection, logger);
    }
    
    protected RecordAction(
            OAIId id,
            OAIMetadata prefix, 
            Connection connection,
            LoggerInf logger)
        throws TException
    {
        super(connection, logger);
        this.id = id;
        this.prefix = prefix;
        if (DEBUG) {
            System.out.println(MESSAGE + "****start - " + DateUtil.getCurrentIsoDate());
        }
    }
    
    public String process()
        throws TException
    {
        try {
            Identifier objectID = id.getId();
            meta = OAIDBUtil.getMetadata(prefix, objectID, connection, logger);
            if (meta == null) {
                handlerException = new CannotDisseminateFormatException ("GetRecord metadata prefix not available :" + prefix);
                throw new TException.REQUEST_ELEMENT_UNSUPPORTED("GetRecord metadata prefix not available:" + prefix);
            }
            invVersion = InvDBUtil.getVersionFromId(meta.versionID, connection, logger);
            return meta.getValue();
            
        } catch (Exception ex) {
            throw new TException(ex);
            
        }
    }

    public InvMeta getMeta() {
        return meta;
    }  

    public InvVersion getInvVersion() {
        return invVersion;
    }

    public HandlerException getHandlerException() {
        return handlerException;
    }
}

