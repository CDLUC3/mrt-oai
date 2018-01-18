
/*********************************************************************
    Copyright 2003 Regents of the University of California
    All rights reserved
*********************************************************************/

package org.cdlib.mrt.oai.test;

import java.sql.Connection;
import java.util.Properties;
import org.cdlib.mrt.inv.utility.DPRFileDB;
import org.cdlib.mrt.oai.element.OAIDate;
import org.cdlib.mrt.oai.element.OAIId;
import org.cdlib.mrt.oai.element.OAIMetadata;
import org.cdlib.mrt.oai.app.OAIServiceInit;
import org.cdlib.mrt.oai.service.OAIService;
import org.cdlib.mrt.oai.service.OAIServiceInt;
import org.cdlib.mrt.oai.service.OAIServiceProperties;
import org.cdlib.mrt.oai.xoai.CmdHandler;
import org.cdlib.mrt.oai.xoai.OAIListMetadataFormats;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.TFileLogger;
import org.cdlib.mrt.utility.TFrame;

/**
 * Load manifest.
 * @author  dloy
 */

public class BuildServiceRequests
{
    private static final String NAME = "RunServiceHandler";
    private static final String MESSAGE = NAME + ": ";

    private static final String NL = System.getProperty("line.separator");
    private static final boolean DEBUG = true;
    
    private static final String PREFIX = "http://uc3-mrt-inv-dev.cdlib.org:37001/mrtoai/oai/v2?";
    
    /**
     * Main method
     */
    public static void main(String args[])
    {

        TFrame tFrame = null;
        DPRFileDB db = null;
        try {
            testList();
            testIdentifiers();
            testMetadata();
            testIdentity();
            testGetRecord();
            testListSets();
            
            //String out = testListIdsAll(connect, logger);
            
        } catch(Exception e) {
                e.printStackTrace();
                System.out.println(
                    "Main: Encountered exception:" + e);
                System.out.println(
                        StringUtil.stackTrace(e));
        }
    }
    
    public static String getCurl(
            String verbParam,
            String identifierParam,
            String metadataPrefixParam, 
            String fromParam,
            String untilParam, 
            String setParam,
            String resumptionToken) 
        throws Exception
    {
        StringBuffer buf = new StringBuffer();
        String amp = "&";
        buf.append("curl -X GET '");
        buf.append(PREFIX);
        if (verbParam != null) {
            buf.append("verb=" + verbParam);
        }
        if (identifierParam != null) {
            buf.append("&identifier=" + identifierParam);
        }
        if (metadataPrefixParam != null) {
            buf.append("&metadataPrefix=" + metadataPrefixParam);
        }
        if (fromParam != null) {
            buf.append("&from=" + fromParam);
        }
        if (untilParam != null) {
            buf.append("&until=" + untilParam);
        }
        if (setParam != null) {
            buf.append("&set=" + setParam);
        }
        buf.append("'");
        String out = buf.toString();
        outResult(out);
        return out;
    }
    
    public static void outResult(String out)
    {
        System.out.println(out);
    }
    
    public static void testList()
    {

        try {
            OAIMetadata prefix = OAIMetadata.setOAIPrefix("oai_dc");
            OAIMetadata metaType_dcs = OAIMetadata.setOAIPrefix("dcs3.1");
            
            OAIMetadata metaType_dc = OAIMetadata.setOAIPrefix("oai_dc");
            OAIDate from = OAIDate.getDBDate("2015-02-06 10:32:51");
            OAIDate until = OAIDate.getDBDate("2015-03-30 10:32:51");
            //OAIDate until = OAIDate.getDBDate("2015-02-06 10:32:51"); // test no results
            String mnemonic = "oai_mnemonic";
            String mnemonicNone = "oai_none";
            String out = null;
            String cmd = "ListRecords";
            
            
            //*********************************************
            out =  getCurl(
                cmd,
                null,
                metaType_dcs.getOAIPrefix(),
                from.getOAI(),
                until.getOAI(),
                mnemonic,
                null
            );
            
                        
            
            //*********************************************
            out =  getCurl(
                cmd,
                null,
                metaType_dcs.getOAIPrefix(),
                null,
                until.getOAI(),
                mnemonic,
                null
            );
            
            //*********************************************
            out =  getCurl(
                cmd,
                null,
                metaType_dcs.getOAIPrefix(),
                from.getOAI(),
                null,
                mnemonic,
                null
            );
            
            //*********************************************1
            out =  getCurl(
                cmd,
                null,
                null,
                from.getOAI(),
                until.getOAI(),
                mnemonic,
                null
            );
            
            //*********************************************
            out =  getCurl(
                cmd,
                null,
                metaType_dcs.getOAIPrefix(),
                from.getOAI(),
                until.getOAI(),
                null,
                null
            );
            
            
            //*********************************************
            out =  getCurl(
                cmd,
                null,
                metaType_dcs.getOAIPrefix(),
                from.getOAI(),
                until.getOAI(),
                mnemonicNone,
                null
            );
            
            
            
        } catch(Exception e) {
                e.printStackTrace();
                System.out.println(
                    "Main: Encountered exception:" + e);
                System.out.println(
                        StringUtil.stackTrace(e));
        }
    }
    
    public static void testIdentifiers()
    {

        try {
            OAIMetadata prefix = OAIMetadata.setOAIPrefix("oai_dc");
            OAIMetadata metaType_dcs = OAIMetadata.setOAIPrefix("dcs3.1");
            
            OAIMetadata metaType_dc = OAIMetadata.setOAIPrefix("oai_dc");
            OAIDate from = OAIDate.getDBDate("2015-02-06 10:32:51");
            OAIDate until = OAIDate.getDBDate("2015-03-30 10:32:51");
            //OAIDate until = OAIDate.getDBDate("2015-02-06 10:32:51"); // test no results
            String mnemonic = "oai_mnemonic";
            String mnemonicNone = "oai_none";
            String out = null;
            String cmd = "ListIdentifiers";
            
            
            //*********************************************
            out =  getCurl(
                cmd,
                null,
                metaType_dcs.getOAIPrefix(),
                from.getOAI(),
                until.getOAI(),
                mnemonic,
                null
            );
            
                        
            
            //*********************************************
            out =  getCurl(
                cmd,
                null,
                metaType_dcs.getOAIPrefix(),
                null,
                until.getOAI(),
                mnemonic,
                null
            );
            
            //*********************************************
            out =  getCurl(
                cmd,
                null,
                metaType_dcs.getOAIPrefix(),
                from.getOAI(),
                null,
                mnemonic,
                null
            );
            
            //*********************************************1
            out =  getCurl(
                cmd,
                null,
                null,
                from.getOAI(),
                until.getOAI(),
                mnemonic,
                null
            );
            
            //*********************************************
            out =  getCurl(
                cmd,
                null,
                metaType_dcs.getOAIPrefix(),
                from.getOAI(),
                until.getOAI(),
                null,
                null
            );
            
            //*********************************************
            out =  getCurl(
                cmd,
                null,
                metaType_dcs.getOAIPrefix(),
                from.getOAI(),
                until.getOAI(),
                mnemonicNone,
                null
            );
            
            
        } catch(Exception e) {
                e.printStackTrace();
                System.out.println(
                    "Main: Encountered exception:" + e);
                System.out.println(
                        StringUtil.stackTrace(e));
        }
    }
    
    public static void testMetadata()
    {

        try {
            OAIId id = OAIId.getArkId("ark:/13030/oai005");
            
            String out = null;
            String cmd = "ListMetadataFormats";
            
            
            //*********************************************
            out =  getCurl(
                cmd,
                id.getN2T(),
                    //null,
                null,
                null,
                null,
                null,
                null
            );
            
            //*********************************************
            out =  getCurl(
                cmd,
                null,
                null,
                null,
                null,
                null,
                null
            );
            
        } catch(Exception e) {
                e.printStackTrace();
                System.out.println(
                    "Main: Encountered exception:" + e);
                System.out.println(
                        StringUtil.stackTrace(e));
        }
    }
    
    public static void testIdentity()
    {

        try {
            OAIId id = OAIId.getArkId("ark:/13030/oai005");
            
            String out = null;
            String cmd = "Identity";
            
            
            //*********************************************
            out =  getCurl(
                cmd,
                null,
                null,
                null,
                null,
                null,
                null
            );
            
            
        } catch(Exception e) {
                e.printStackTrace();
                System.out.println(
                    "Main: Encountered exception:" + e);
                System.out.println(
                        StringUtil.stackTrace(e));
        }
    }
    
    public static void testGetRecord()
    {

        try {
            OAIId id = OAIId.getArkId("ark:/13030/oai005");
            OAIMetadata metaType_dc = OAIMetadata.setOAIPrefix("oai_dc");
            OAIMetadata metaType_dcs = OAIMetadata.setOAIPrefix("dcs3.1");
            String out = null;
            String cmd = "GetRecord";
            
            
            //*********************************************
            out =  getCurl(
                cmd,
                id.getN2T(),
                metaType_dcs.getOAIPrefix(),
                null,
                null,
                null,
                null
            );
            
            //*********************************************
            out =  getCurl(
                cmd,
                id.getN2T(),
                metaType_dc.getOAIPrefix(),
                null,
                null,
                null,
                null
            );
            
                        
            
            
            
        } catch(Exception e) {
                e.printStackTrace();
                System.out.println(
                    "Main: Encountered exception:" + e);
                System.out.println(
                        StringUtil.stackTrace(e));
        }
    }
    
    public static void testListSets()
    {

        try {
            OAIId id = OAIId.getArkId("ark:/13030/oai005");
            OAIMetadata metaType_dc = OAIMetadata.setOAIPrefix("oai_dc");
            OAIMetadata metaType_dcs = OAIMetadata.setOAIPrefix("dcs3.1");
            String out = null;
            String cmd = "ListSets";
            
            
            //*********************************************
            out =  getCurl(
                cmd,
                null,
                null,
                null,
                null,
                null,
                null
            );
            
            
        } catch(Exception e) {
                e.printStackTrace();
                System.out.println(
                    "Main: Encountered exception:" + e);
                System.out.println(
                        StringUtil.stackTrace(e));
        }
    }

}
