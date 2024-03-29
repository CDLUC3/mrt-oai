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
import org.dspace.xoai.dataprovider.exceptions.BadArgumentException;
import org.dspace.xoai.dataprovider.exceptions.HandlerException;
import org.dspace.xoai.dataprovider.exceptions.IdDoesNotExistException;
import org.dspace.xoai.dataprovider.exceptions.InternalOAIException;
import org.dspace.xoai.dataprovider.handlers.results.ListItemsResults;
import org.dspace.xoai.dataprovider.model.Item;
import java.util.ArrayList;
import java.util.List;
import org.cdlib.mrt.oai.element.OAIDate;
import org.cdlib.mrt.oai.element.OAIItem;
import org.cdlib.mrt.oai.element.OAIId;
import org.cdlib.mrt.oai.element.OAIMetadata;
import org.cdlib.mrt.oai.xoai.MrtMemoryItem;
import org.cdlib.mrt.oai.utility.ListOAIItems;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.oai.utility.OAIDBUtil;
import org.cdlib.mrt.utility.DateUtil;

/**
 * Run OAI RecordAction
 * @author dloy
 */
public class ListOAIItemsAction
        extends ActionAbs
{

    protected static final String NAME = "ListIdentifiersAction";
    protected static final String MESSAGE = NAME + ": ";
    protected static final boolean DEBUG = true;

    protected OAIMetadata metaType = null;
    protected String setMnemonic = null;
    protected OAIDate from = null;
    protected OAIDate until = null;
    protected ListOAIItems listOAIItems = null;
    protected List<OAIItem> list = null;
    
   public static ListOAIItemsAction getListOAIItemsAction(
            OAIMetadata metaType,
            String setMnemonic,
            OAIDate from,
            OAIDate until,
            Connection connection,
            LoggerInf logger)
        throws HandlerException 
    {
        return new ListOAIItemsAction(metaType, setMnemonic, from, until, connection, logger);
    }
    
    protected ListOAIItemsAction(
            OAIMetadata metaType,
            String setMnemonic,
            OAIDate from,
            OAIDate until,
            Connection connection,
            LoggerInf logger)
        throws HandlerException
    {
        super(connection, logger);
        this.metaType = metaType;
        this.setMnemonic = setMnemonic;
        this.from = from;
        this.until = until;
        if (DEBUG) {
            System.out.println(MESSAGE + "****start - " + DateUtil.getCurrentIsoDate());
        }
    }
    
    public void  process()
        throws HandlerException
    {
        try {
            if (metaType == null) {
                throw new  BadArgumentException ("ListIdentifiers prefix not provided");
            }
            
            listOAIItems = OAIDBUtil.getItemsContent(metaType, setMnemonic, from, until, connection, logger);
            if (listOAIItems == null) {
                list = new ArrayList<>();
            } else {
                list = listOAIItems.getList();
            }
            
            
        } catch (Exception ex) {
            throw new InternalOAIException(ex);
            
        }
    }
    
    public void dumpList(String header)
    {
        System.out.println("***ListIdentifiersAction - " + header);
        for (OAIItem item : list) {
            System.out.println(item.dump(""));
        }
    }

    public List<OAIItem> getList() {
        return list;
    }
    
    public OAIItem getIdentifier(String id)
         throws IdDoesNotExistException
    {
        for (OAIItem listItem : list) {
            OAIId itemId = listItem.getOaiId();
            if (itemId.getN2T().equals(id)) return listItem;
        }
        throw new IdDoesNotExistException("getIdentifier:" + id + " not founc");
    }
    
    public ListItemsResults getListItemsResults(Integer offsetIn, Integer lengthIn)
    {
        System.out.println("***getListItemsResults:" 
                + " - offset=" + offsetIn 
                + " - length=" + lengthIn 
        );
        int offset = 0;
        int length = 1000000;
        if (offsetIn != null) offset = offsetIn;
        if (lengthIn != null) length = lengthIn;
        ArrayList<Item> listItems = new ArrayList<>();
        for (int len = 0; len < length; len++)
        {
            if ((offset + len) >= list.size()) break;
            OAIItem oaiItem = list.get(offset + len);
            MrtMemoryItem memItem = MrtMemoryItem.set(oaiItem);
            listItems.add((Item)memItem);
        }
        boolean hasMoreResults =false;
        if ((offset + length) < list.size()) hasMoreResults = true;
        ListItemsResults listItemsResults = new ListItemsResults(hasMoreResults, listItems);
        return listItemsResults;
    }
}

