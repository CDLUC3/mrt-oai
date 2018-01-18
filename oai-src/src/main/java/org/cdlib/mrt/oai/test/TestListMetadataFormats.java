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
import static com.lyncode.xoai.model.oaipmh.Verb.Type.ListMetadataFormats;
import com.lyncode.xml.exceptions.XmlWriteException;
import com.lyncode.xoai.model.oaipmh.ResumptionToken;
import com.lyncode.xoai.xml.XmlWritable;
import com.lyncode.xoai.xml.XmlWriter;
import org.hamcrest.Matcher;

import javax.xml.stream.XMLStreamException;

import static com.lyncode.test.matchers.xml.XPathMatchers.hasXPath;
import static com.lyncode.xoai.model.oaipmh.Verb.Type.ListRecords;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestListMetadataFormats extends TestAbstractMrtHandler {
    private static final String OAI_NAMESPACE = "http://www.openarchives.org/OAI/2.0/";
    private DataProvider dataProvider = new DataProvider(aContext(), theRepository());

    public String process()
        throws Exception
    {
        return write(dataProvider.handle(request().withVerb(ListMetadataFormats)));
    }
}
