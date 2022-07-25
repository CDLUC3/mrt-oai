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

import org.dspace.xoai.dataprovider.exceptions.HandlerException;
import org.dspace.xoai.dataprovider.exceptions.InternalOAIException;
import org.dspace.xoai.dataprovider.model.Context;
import org.dspace.xoai.dataprovider.model.MetadataFormat;
import java.util.List;
import java.util.Properties;
import org.cdlib.mrt.oai.utility.MetaTypes;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.DateUtil;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TFrame;

/**
 * Run OAI RecordAction
 * @author dloy
 */
public class ListMetadatasAction
        extends ActionAbs
{

    protected static final String NAME = "ListMetadatasAction";
    protected static final String MESSAGE = NAME + ": ";
    protected static final boolean DEBUG = true;

    protected Properties metaProp;
    protected MetaTypes metaTypes;
    protected List<MetaTypes.MetaType> metaList;
    protected HandlerException handlerException = null;
    
   public static ListMetadatasAction getListMetadatasAction(
            LoggerInf logger)
        throws HandlerException
    {
        return new ListMetadatasAction(logger);
    }
    
    protected ListMetadatasAction(
            LoggerInf logger)
        throws HandlerException
    {
        super(logger);
        TFrame tFrame = null;
        try {
            String propertyList[] = {
                "resources/Metadata.properties"};
            tFrame = new TFrame(propertyList, "Metadata");
            metaProp  = tFrame.getProperties();
            if (metaProp == null) {
                throw new InternalOAIException(MESSAGE 
                        + "Properties not found: resources/Metadata.properties");
            }
            metaTypes = MetaTypes.getMetaTypes(metaProp);
            metaList = metaTypes.getList();
        
            if (DEBUG) {
                System.out.println(MESSAGE + "****start - " + DateUtil.getCurrentIsoDate());
            }

            
        } catch(Exception e) {
                e.printStackTrace();
                System.out.println(
                    "Main: Encountered exception:" + e);
                System.out.println(
                        StringUtil.stackTrace(e));
        } 
    }
    
    public void setContext(Context context)
    {
        try {
            for (MetaTypes.MetaType metaType : metaList) {
                MetadataFormat metadataFormat = MetadataFormat.metadataFormat(metaType.prefix.getOAIPrefix());
                metadataFormat.withNamespace(metaType.name)
                        .withSchemaLocation(metaType.schema);
                context.withMetadataFormat(metadataFormat);
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new InternalOAIException("TestAbstractMrtHandler:" + ex);
        }
    }
    
    public void dumpList(String header)
    {
        System.out.println("***ListMetadataAction - " + header);
        for (MetaTypes.MetaType meta : metaList) {
            System.out.println(meta.dump(""));
        }
    }
    public HandlerException getHandlerException() {
        return handlerException;
    }

    public List<MetaTypes.MetaType> getMetaList() {
        return metaList;
    }
    
}

