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

import com.lyncode.xoai.dataprovider.DataProvider;
import com.lyncode.xoai.dataprovider.builder.OAIRequestParametersBuilder;
import com.lyncode.xoai.dataprovider.exceptions.BadArgumentException;
import com.lyncode.xoai.dataprovider.exceptions.HandlerException;
import com.lyncode.xoai.dataprovider.repository.RepositoryConfiguration;
import java.sql.Connection;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.oai.element.OAIDate;
import org.cdlib.mrt.oai.element.OAIMetadata;
import org.cdlib.mrt.oai.element.OAISet;
import org.cdlib.mrt.utility.LoggerInf;
import com.lyncode.xoai.model.oaipmh.Verb;

public class OAIListIdentifiers
    extends OAIMrtHandlerAbstract
    implements CmdHandlerInt
{
    private static final String OAI_NAMESPACE = "http://www.openarchives.org/OAI/2.0/";
    private DataProvider dataProvider = null;
    protected OAIRequestParametersBuilder builder = null;
    
    
    private OAISet set = null;
    private OAIDate from = null; 
    private OAIDate until = null; 
    private String mnemonic = null;
    protected OAIMetadata metaType = null;

    public OAIListIdentifiers(OAIMetadata metaType, OAIRequestParametersBuilder builder, Connection connection, LoggerInf logger) 
       throws Exception
    {
        super(connection, logger);
        this.metaType = metaType;
        this.builder = builder;
        setRepositoryRecord(metaType);
        dataProvider = new DataProvider(aContext(), theRepository());
        
    }
    
    public OAIListIdentifiers(
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
        super(connection, logger);
        this.identifierParam = identifierParam;
        this.metadataPrefixParam = metadataPrefixParam;
        this.fromParam = fromParam;
        this.untilParam = untilParam;
        this.setParam = setParam;
        this.resumptionToken = resumptionToken;
        this.builder = new OAIRequestParametersBuilder().withVerb(Verb.Type.ListIdentifiers);
        dumpIn();
        try {
             
            build();
        
        } catch (HandlerException hex) {
            thrownException(hex, builder);
        }
        if (metaType != null) System.out.println(metaType.dump("before repository"));
        setRepositoryRecord(metaType);
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
        if (identifierParam != null)
            throw new BadArgumentException(
                    "ListIdentifiers verb does not accept the identifier parameter");
        
        if (setParam == null)
            throw new BadArgumentException(
                    "ListIdentifiers verb requires set parameter");
        mnemonic = setParam;
        builder.withSet(mnemonic);
        if (fromParam != null) {
            try {
                from = OAIDate.getOAIDate(fromParam);
                builder.withFrom(from.getUnixDate());
            } catch (Exception ex) {
                throw new BadArgumentException("ListIdentifiers invalid form date");
            }
        }
        if (untilParam != null) {
            try {
                until = OAIDate.getOAIDate(untilParam);
                builder.withUntil(until.getUnixDate());
            } catch (Exception ex) {
                throw new BadArgumentException("ListRecords invalid form date");
            }
        }
        
        if (StringUtil.isAllBlank(metadataPrefixParam)) {
            throw new BadArgumentException("ListIdentifiers metadataPrefix required");
        }
        metaType = OAIMetadata.setOAIPrefix(metadataPrefixParam);
        builder.withMetadataPrefix(metadataPrefixParam);
        
    }
}
