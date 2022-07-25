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

import static org.dspace.xoai.model.oaipmh.Verb.Type.ListRecords;
import static org.dspace.xoai.model.oaipmh.Verb.Type.GetRecord;
import org.dspace.xoai.dataprovider.builder.OAIRequestParametersBuilder;
import org.dspace.xoai.dataprovider.exceptions.HandlerException;

import org.dspace.xoai.dataprovider.repository.RepositoryConfiguration;
import java.sql.Connection;
import org.cdlib.mrt.oai.element.OAIVerb;
import org.cdlib.mrt.oai.service.OAIServiceState;
import org.cdlib.mrt.utility.LoggerInf;

public class CmdHandler  {
    private Connection connection;
    private LoggerInf logger;
    
    private String verbParam = null;
    private String identifierParam = null;
    private String metadataPrefixParam = null;
    private String fromParam = null;
    private String untilParam = null;
    private String setParam = null;
    private String resumptionToken = null;
    private CmdHandlerInt cmdHandler = null;
    private OAIVerb verb = null;
    private OAIServiceState state = null;
    private RepositoryConfiguration configuration = null;
    private OAIRequestParametersBuilder builder = new OAIRequestParametersBuilder();
    private HandlerException handlerException = null;
    private String oaiOut = null;
    
    public static CmdHandler getCmdHandler(
            String verbParam,
            String identifierParam,
            String metadataPrefixParam, 
            String fromParam,
            String untilParam, 
            String setParam,
            String resumptionToken, 
            Connection connection, 
            LoggerInf logger) 
        throws Exception
    {
        return new CmdHandler(
                verbParam,
                identifierParam,
                metadataPrefixParam,
                fromParam,
                untilParam,
                setParam,
                resumptionToken,
                connection,
                logger
        );
    }
    
    protected CmdHandler(
            String verbParam,
            String identifierParam,
            String metadataPrefixParam, 
            String fromParam,
            String untilParam, 
            String setParam,
            String resumptionToken, 
            Connection connection, 
            LoggerInf logger) 
        throws Exception
    {
        this.verbParam = verbParam;
        this.identifierParam = identifierParam;
        this.metadataPrefixParam = metadataPrefixParam;
        this.fromParam = fromParam;
        this.untilParam = untilParam;
        this.setParam = setParam;
        this.resumptionToken = resumptionToken;
        this.connection = connection;
        this.logger = logger;
        setVerb(verbParam);
    }
    
    protected void setVerb(String verbParam)
        throws Exception
    {
        try {
            verb = new OAIVerb(verbParam);
        } catch (HandlerException hex) {
            // default Identify if not found
            verb = new OAIVerb("Identify");
        }
        try {
            switch (verb.getType() ) {
                case Identify:
                    cmdHandler = 
                        new OAIIdentify(
                            identifierParam,
                            metadataPrefixParam, 
                            fromParam,
                            untilParam, 
                            setParam,
                            resumptionToken, 
                            logger);
                    break;
                    
                case ListMetadataFormats:
                    cmdHandler = 
                        new OAIListMetadataFormats(
                            identifierParam,
                            metadataPrefixParam, 
                            fromParam,
                            untilParam, 
                            setParam,
                            resumptionToken, 
                            connection, 
                            logger);
                    break;
                    
                case ListSets:
                    cmdHandler = 
                        new OAIListSets(
                            identifierParam,
                            metadataPrefixParam, 
                            fromParam,
                            untilParam, 
                            setParam,
                            resumptionToken, 
                            connection, 
                            logger);
                    break;
                    
                case GetRecord:
                    cmdHandler = 
                        new OAIGetRecord(
                            identifierParam,
                            metadataPrefixParam, 
                            fromParam,
                            untilParam, 
                            setParam,
                            resumptionToken, 
                            connection, 
                            logger);
                    break;
                    
                case ListIdentifiers:
                    cmdHandler = 
                        new OAIListIdentifiers(
                            identifierParam,
                            metadataPrefixParam, 
                            fromParam,
                            untilParam, 
                            setParam,
                            resumptionToken, 
                            connection, 
                            logger);
                    break;
                    
                case ListRecords:
                    cmdHandler = 
                        new OAIListRecords(
                            identifierParam,
                            metadataPrefixParam, 
                            fromParam,
                            untilParam, 
                            setParam,
                            resumptionToken, 
                            connection, 
                            logger);
                    break;
            }
            
            
        } catch (HandlerException hex) {
            thrownException(hex, builder);
        }
    }
    
    public String process()
        throws Exception
    {
        oaiOut = cmdHandler.process();
        return oaiOut;
    }
    
    public void thrownException(HandlerException handlerException, OAIRequestParametersBuilder builder)
    {
        //builder.withHandlerException(handlerException);
        this.handlerException = handlerException;
    }

    public String getOaiOut() {
        return oaiOut;
    }
}
