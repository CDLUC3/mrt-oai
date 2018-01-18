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

import org.cdlib.mrt.oai.app.*;
import org.cdlib.mrt.oai.test.*;
import org.cdlib.mrt.oai.test.TestAbstractMrtHandler;
import com.lyncode.xoai.dataprovider.DataProvider;
import com.lyncode.test.matchers.xml.XPathMatchers;
import static com.lyncode.xoai.model.oaipmh.Verb.Type.ListRecords;
import static com.lyncode.xoai.model.oaipmh.Verb.Type.GetRecord;
import com.lyncode.xml.exceptions.XmlWriteException;
import com.lyncode.xoai.model.oaipmh.ResumptionToken;
import com.lyncode.xoai.xml.XmlWritable;
import com.lyncode.xoai.xml.XmlWriter;
import org.hamcrest.Matcher;

import javax.xml.stream.XMLStreamException;

import static com.lyncode.test.matchers.xml.XPathMatchers.hasXPath;
import com.lyncode.xoai.dataprovider.builder.OAIRequestParametersBuilder;
import com.lyncode.xoai.dataprovider.exceptions.BadArgumentException;
import com.lyncode.xoai.dataprovider.exceptions.IdDoesNotExistException;
import com.lyncode.xoai.dataprovider.exceptions.CannotDisseminateFormatException;
import com.lyncode.xoai.dataprovider.exceptions.HandlerException;
import com.lyncode.xoai.model.oaipmh.Verb;

import com.lyncode.xoai.dataprovider.repository.RepositoryConfiguration;
import java.sql.Connection;
import java.util.List;
import org.cdlib.mrt.oai.action.ListSetsAction;
import org.cdlib.mrt.oai.element.OAIDate;
import org.cdlib.mrt.oai.element.OAIId;
import org.cdlib.mrt.oai.element.OAIMetadata;
import org.cdlib.mrt.oai.element.OAISet;
import org.cdlib.mrt.oai.element.OAIVerb;
import org.cdlib.mrt.oai.service.OAIServiceState;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.StringUtil;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

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
        builder.withHandlerException(handlerException);
        this.handlerException = handlerException;
    }

    public String getOaiOut() {
        return oaiOut;
    }
}
