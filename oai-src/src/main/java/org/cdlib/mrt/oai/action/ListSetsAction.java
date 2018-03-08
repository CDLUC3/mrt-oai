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

import java.sql.Connection;
import com.lyncode.xoai.dataprovider.exceptions.HandlerException;
import com.lyncode.xoai.dataprovider.exceptions.InternalOAIException;
import java.util.List;
import java.util.ArrayList;
import org.cdlib.mrt.inv.content.InvCollection;
import org.cdlib.mrt.oai.element.OAISet;
import org.cdlib.mrt.oai.utility.ListItems;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.oai.utility.OAIDBUtil;
import org.cdlib.mrt.utility.DateUtil;

/**
 * Run OAI RecordAction
 * @author dloy
 */
public class ListSetsAction
        extends ActionAbs
{

    protected static final String NAME = "ListSetsAction";
    protected static final String MESSAGE = NAME + ": ";
    protected static final boolean DEBUG = true;

    protected HandlerException handlerException = null;
    protected ListItems listIds = null;
    protected List<InvCollection> list = null;
    protected ArrayList<OAISet> oaiSetList = null;
   
    
   public static ListSetsAction getListSetsAction(
            Connection connection,
            LoggerInf logger)
        throws HandlerException 
    {
        return new ListSetsAction(connection, logger);
    }
   
   
    protected ListSetsAction(
            Connection connection,
            LoggerInf logger)
        throws HandlerException
    {
        super(connection, logger);
        if (DEBUG) {
            System.out.println(MESSAGE + "****start - " + DateUtil.getCurrentIsoDate());
        }
    }
    
    public List<OAISet>  process()
        throws HandlerException
    {
        try {
            list = OAIDBUtil.getPublicCollections(connection, logger);
            oaiSetList = new ArrayList<>(list.size());
            for (InvCollection collection : list) {
                oaiSetList.add(OAISet.getOAISet(collection));
            }
            return oaiSetList;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new InternalOAIException(ex);
            
        }
    }
    
    public void dumpList(String header)
    {
        try {
            System.out.println("***ListSetsAction - " + header);
            for (OAISet set : oaiSetList) {
                System.out.println(set.dump("set"));
            }
            
        } catch (Exception ex) {
            throw new InternalOAIException(ex);
        }
    }
    public HandlerException getHandlerException() {
        return handlerException;
    }

    public List<OAISet> getOAISetList() {
        return oaiSetList;
    }
    
}

