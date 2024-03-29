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

import org.dspace.xoai.dataprovider.DataProvider;
import static org.dspace.xoai.model.oaipmh.Verb.Type.ListRecords;
//import com.lyncode.xoai.dataprovider.builder.OAIRequestParametersBuilder;
//import com.lyncode.xoai.dataprovider.exceptions.*;
import java.sql.Connection;
import java.util.Properties;
import org.cdlib.mrt.inv.utility.DPRFileDB;
import org.cdlib.mrt.oai.element.OAIDate;
import org.cdlib.mrt.oai.element.OAIMetadata;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TFileLogger;
import org.cdlib.mrt.utility.TFrame;
import org.dspace.xoai.dataprovider.builder.OAIRequestParametersBuilder;
import org.dspace.xoai.dataprovider.exceptions.BadArgumentException;

public class TestItemsNoSet extends TestAbstractMrtHandler {
    private static final String NAME = "TestItemsNoSet";
    private static final String MESSAGE = NAME + ": ";
    private static final String OAI_NAMESPACE = "http://www.openarchives.org/OAI/2.0/";
    private DataProvider dataProvider = null;
    protected OAIMetadata metaType = null;

    public TestItemsNoSet(OAIMetadata metaType, Connection connection, LoggerInf logger) 
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
        if (StringUtil.isAllBlank(mnemonic)) {
            System.out.println("***OAIRequestParametersBuilder set bad Argument exception");
            throw new BadArgumentException("Missing \"metadataPrefix\"");
        }
        builder.withMetadataPrefix(metaType.getOAIPrefix());
        builder.withFrom(from.getUnixDate());
        builder.withUntil(until.getUnixDate());
        builder.withSet(mnemonic);
        return write(dataProvider.handle(builder));
    }
    
    

    /**
     * Main method
     */
    public static void main(String args[])
    {

        TFrame tFrame = null;
        DPRFileDB db = null;
        try {
            String propertyList[] = {
                "testresources/OAITest.properties"};
            tFrame = new TFrame(propertyList, "InvLoad");
            Properties invProp  = tFrame.getProperties();
            LoggerInf logger = new TFileLogger("testFormatter", 10, 10);
            db = new DPRFileDB(logger, invProp);
            Connection connect = db.getConnection(true);
            OAIMetadata metaType = OAIMetadata.setOAIPrefix("dcs3.1");
            
            OAIMetadata prefix = OAIMetadata.setOAIPrefix("oai_dc");
            OAIDate from = OAIDate.getDBDate("2015-02-06 10:32:51");
            OAIDate until = OAIDate.getDBDate("2015-03-30 10:32:51");
            String mnemonic = null;
            TestItemsNoSet test = new TestItemsNoSet(metaType, connect, logger);
            String out = test.process(from, until, mnemonic);
            System.out.println(MESSAGE + out);
            
        } catch(Exception e) {
                e.printStackTrace();
                System.out.println(
                    "Main: Encountered exception:" + e);
                System.out.println(
                        StringUtil.stackTrace(e));
        } finally {
            try {
                db.shutDown();
            } catch (Exception ex) {
                System.out.println("db Exception:" + ex);
            }
        }
    }
}
