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

package org.cdlib.mrt.oai.test;

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
import java.sql.Connection;
import java.util.List;
import org.cdlib.mrt.oai.action.ListSetsAction;
import org.cdlib.mrt.oai.element.OAIDate;
import org.cdlib.mrt.oai.element.OAIId;
import org.cdlib.mrt.oai.element.OAIMetadata;
import org.cdlib.mrt.oai.element.OAISet;
import org.cdlib.mrt.utility.LoggerInf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestItemsRequest extends TestAbstractMrtHandler {
    private static final String OAI_NAMESPACE = "http://www.openarchives.org/OAI/2.0/";
    private DataProvider dataProvider = null;
    protected OAIMetadata metaType = null;

    public TestItemsRequest(OAIMetadata metaType, Connection connection, LoggerInf logger) 
       throws Exception
    {
        super(connection, logger);
        this.metaType = metaType;
        setRepositoryRecord(metaType);
        dataProvider = new DataProvider(aContext(), theRepository());
        
    }
    
    public String process(OAIDate from, OAIDate until, String mnemonic)
        throws Exception
    {
        OAIRequestParametersBuilder builder = request().withVerb(ListRecords);
        builder.withMetadataPrefix(metaType.getOAIPrefix());
        builder.withFrom(from.getUnixDate());
        builder.withUntil(until.getUnixDate());
        builder.withSet(mnemonic);
        return write(dataProvider.handle(builder));
    }
}
