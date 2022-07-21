/**
 * Copyright 2012 Lyncode
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain setup copy of the License at
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

import com.lyncode.xml.exceptions.XmlWriteException;
import com.lyncode.builder.Builder;
import org.cdlib.mrt.oai.xoai.MrtSetRepository;
import org.cdlib.mrt.oai.xoai.MrtItemRepository;
import org.cdlib.mrt.oai.element.OAIMetadata;
import org.dspace.xoai.dataprovider.builder.OAIRequestParametersBuilder;
import org.dspace.xoai.dataprovider.exceptions.*;
import org.dspace.xoai.dataprovider.filter.Filter;
import org.dspace.xoai.dataprovider.filter.FilterResolver;
import org.dspace.xoai.exceptions.InvalidResumptionTokenException;
import org.dspace.xoai.dataprovider.model.Context;
import org.dspace.xoai.dataprovider.model.ItemIdentifier;
import org.dspace.xoai.dataprovider.model.conditions.Condition;
import org.dspace.xoai.model.oaipmh.ResumptionToken;
import org.dspace.xoai.dataprovider.parameters.OAICompiledRequest;
import org.dspace.xoai.dataprovider.repository.InMemoryItemRepository;
import org.dspace.xoai.dataprovider.repository.InMemorySetRepository;
import org.dspace.xoai.dataprovider.repository.Repository;
import org.dspace.xoai.dataprovider.repository.RepositoryConfiguration;
import org.dspace.xoai.services.impl.SimpleResumptionTokenFormat;
import org.dspace.xoai.xml.XmlWritable;
import org.dspace.xoai.xml.XmlWriter;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import javax.xml.stream.XMLStreamException;

import static org.dspace.xoai.dataprovider.model.MetadataFormat.identity;
import org.dspace.xoai.model.oaipmh.DeletedRecord;
import org.dspace.xoai.model.oaipmh.Granularity;
import java.sql.Connection;
import org.cdlib.mrt.oai.element.OAIDate;
import org.cdlib.mrt.utility.LoggerInf;

public abstract class TestAbstractMrtHandler {

    private Connection connection = null;
    private LoggerInf logger = null;
    protected static final String FORMAT_OAI_DC = "oai_dc";
    protected static final String FORMAT_DCS = "dcs3.1";
    private Context context = new Context()
            .withMetadataFormat(FORMAT_OAI_DC, identity())
            .withMetadataFormat(FORMAT_DCS, identity());
    private InMemorySetRepository setRepository = new InMemorySetRepository();
    private MrtSetRepository mrtSetRepository = null;
    private MrtItemRepository mrtItemRepository = null;
    private InMemoryItemRepository itemRepository = new InMemoryItemRepository();
    protected String repositoryName = "Merritt";
    protected String baseURL = "http://merritt.cdlib.org/oai/v2";
    protected String protocolVersion = "2.0"; // not set here
    protected String adminEmail = "uc3@ucop.edu";
    protected String earliestDatestamp = "2013-05-22 09:47:24";
    protected String granularity = "YYYY-MM-DDThh:mm:ss";
    protected OAIDate earlyDate = OAIDate.getDBDate(earliestDatestamp);
    private RepositoryConfiguration repositoryConfiguration = new RepositoryConfiguration()
            .withRepositoryName(repositoryName)
            .withBaseUrl(baseURL)
            .withAdminEmail(adminEmail)
            .withEarliestDate(earlyDate.getUnixDate())
            .withGranularity(Granularity.Second)
            .withDeleteMethod(DeletedRecord.NO)
            .withMaxListIdentifiers(10000)
            .withMaxListRecords(10000)
            .withMaxListSets(100);
    private Repository repository = null;
    
    protected TestAbstractMrtHandler()
    {
        setRepository();
    }
    
    protected TestAbstractMrtHandler(Connection connection, LoggerInf logger)
    {
        try {
            this.logger = logger;
            this.connection = connection;
        
        } catch (Exception ex) {
            throw new InternalOAIException("TestAbstractMrtHandler:" + ex);
        }
    }
    protected String write(final XmlWritable handle) throws XMLStreamException, XmlWriteException {
        return XmlWriter.toString(new XmlWritable() {
            @Override
            public void write(XmlWriter writer) throws XmlWriteException {
                try {
                    //writer.writeStartElement("root");
                    //writer.writeNamespace("xsi", "something");
                    writer.write(handle);
                    //writer.writeEndElement();
                } catch (Exception e) {
                    throw new XmlWriteException(e);
                }
            }
        });
    }
    
    private void setRepository()
    {
        repository = new Repository()
            .withSetRepository(setRepository)
            .withItemRepository(itemRepository)
            .withResumptionTokenFormatter(new SimpleResumptionTokenFormat())
            .withConfiguration(repositoryConfiguration);
    }

    
    protected void setRepositorySet()
        throws Exception
    {
        mrtSetRepository = new MrtSetRepository(connection, logger);    
        repository = new Repository()
            .withSetRepository(mrtSetRepository)
            .withItemRepository(itemRepository)
            .withResumptionTokenFormatter(new SimpleResumptionTokenFormat())
            .withConfiguration(repositoryConfiguration);
    }
    
        
    protected void setRepositoryRecord(OAIMetadata metaType)
        throws Exception
    {
        mrtSetRepository = new MrtSetRepository(connection, logger);  
        mrtItemRepository = new MrtItemRepository(metaType, connection, logger);
        repository = new Repository()
            .withSetRepository(mrtSetRepository)
            .withItemRepository(mrtItemRepository)
            .withResumptionTokenFormatter(new SimpleResumptionTokenFormat())
            .withConfiguration(repositoryConfiguration);
    }

    protected String writeOriginal(final XmlWritable handle) throws XMLStreamException, XmlWriteException {
        return XmlWriter.toString(new XmlWritable() {
            @Override
            public void write(XmlWriter writer) throws XmlWriteException {
                try {
                    writer.writeStartElement("root");
                    writer.writeNamespace("xsi", "something");
                    writer.write(handle);
                    writer.writeEndElement();
                } catch (XMLStreamException e) {
                    throw new XmlWriteException(e);
                }
            }
        });
    }

    protected OAICompiledRequest a (OAIRequestParametersBuilder builder) throws BadArgumentException, InvalidResumptionTokenException, UnknownParameterException, IllegalVerbException, DuplicateDefinitionException {
        return OAICompiledRequest.compile(builder);
    }

    protected OAIRequestParametersBuilder request() {
        return new OAIRequestParametersBuilder();
    }

    protected Context aContext () {
        return context;
    }
    protected Context theContext () {
        return context;
    }

    protected InMemorySetRepository theSetRepository() {
        return setRepository;
    }

    protected InMemoryItemRepository theItemRepository () {
        return itemRepository;
    }

    protected RepositoryConfiguration theRepositoryConfiguration() {
        return repositoryConfiguration;
    }

    protected Repository theRepository() {
        return repository;
    }

    protected Matcher<String> asInteger(final Matcher<Integer> matcher) {
        return new TypeSafeMatcher<String>() {
            @Override
            protected boolean matchesSafely(String item) {
                return matcher.matches(Integer.valueOf(item));
            }

            @Override
            public void describeTo(Description description) {
                description.appendDescriptionOf(matcher);
            }
        };
    }

    protected Condition alwaysFalseCondition() {
        return new Condition() {
            @Override
            public Filter getFilter(FilterResolver filterResolver) {
                return new Filter() {
                    @Override
                    public boolean isItemShown(ItemIdentifier item) {
                        return false;
                    }
                };
            }
        };
    }

    protected String valueOf(ResumptionToken.Value resumptionToken) {
        return theRepository().getResumptionTokenFormatter()
                .format(resumptionToken);
    }
}
