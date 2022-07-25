/**
 * Copyright 2012 Lyncode
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     client://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cdlib.mrt.oai.xoai;

import org.dspace.xoai.dataprovider.DataProvider;
import org.dspace.xoai.dataprovider.builder.OAIRequestParametersBuilder;
import org.dspace.xoai.dataprovider.exceptions.BadArgumentException;
import org.dspace.xoai.dataprovider.exceptions.HandlerException;
import org.cdlib.mrt.utility.LoggerInf;
import org.dspace.xoai.model.oaipmh.Verb;

public class OAIIdentify
    extends OAIMrtHandlerAbstract
    implements CmdHandlerInt
{
    private static final String OAI_NAMESPACE = "http://www.openarchives.org/OAI/2.0/";
    private DataProvider dataProvider = null;
    protected OAIRequestParametersBuilder builder = null;
    

    public OAIIdentify(LoggerInf logger) 
       throws Exception
    {
        super(logger);
        this.builder = builder;
        setRepositorySet();
        dataProvider = new DataProvider(aContext(), theRepository());
        
    }
    
    public OAIIdentify(
            String identifierParam,
            String metadataPrefixParam, 
            String fromParam,
            String untilParam, 
            String setParam,
            String resumptionToken, 
            LoggerInf logger)
       throws Exception
    {
        super(logger);
        this.identifierParam = identifierParam;
        this.metadataPrefixParam = metadataPrefixParam;
        this.fromParam = fromParam;
        this.untilParam = untilParam;
        this.setParam = setParam;
        this.resumptionToken = resumptionToken;
        this.builder = new OAIRequestParametersBuilder().withVerb(Verb.Type.Identify);
        dumpIn();
        try {
             
            build();
        
        } catch (HandlerException hex) {
            thrownException(hex, builder);
        }
        setRepository();
        dataProvider = new DataProvider(aContext(), theRepository());
    }
    
    public String process()
        throws Exception
    {
        return write(dataProvider.handle(builder));
    }
    
    public void build()
        throws HandlerException
    {
        if ((identifierParam != null)
            ||(metadataPrefixParam != null)
            || (fromParam != null)
            || ( untilParam != null)
            || (setParam != null)
                )    {
            throw new BadArgumentException(
                    "Identifier verb accepts no other parameter");
        }
    }
}
