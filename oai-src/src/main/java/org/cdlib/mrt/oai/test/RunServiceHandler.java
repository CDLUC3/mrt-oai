
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

public class RunServiceHandler
{
    private static final String NAME = "RunServiceHandler";
    private static final String MESSAGE = NAME + ": ";

    private static final String NL = System.getProperty("line.separator");
    private static final boolean DEBUG = true;

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
            LoggerInf logger = tFrame.getLogger();
            db = new DPRFileDB(logger, invProp);
            Connection connect = db.getConnection(true);
            testList(connect, logger);
            testIdentifiers(connect, logger);
            testMetadata(connect, logger);
            testIdentity(connect, logger);
            testGetRecord(connect, logger);
            testListSets(connect, logger);
            
            //String out = testListIdsAll(connect, logger);
            
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
    
    public static void logResult(LoggerInf log, String header, String result)
    {
        String error = "OK";
        int pos = result.indexOf("error code=");
        if (pos > 0) {
            String exc = result.substring(pos);
            pos = exc.indexOf("</error");
            error = exc.substring(0, pos);
        }
        String msg = "Result:" + header + " - Result=" + error;
        log.logMessage(msg, 0, true);
    }
    
    public static void logResult(LoggerInf log, String cmd, String type, String result)
    {
        String error = "OK";
        int pos = result.indexOf("error code=");
        if (pos > 0) {
            String exc = result.substring(pos);
            pos = exc.indexOf("</error");
            error = exc.substring(0, pos);
        }
        String msg = "Result:" + cmd + " - Type=" + type + " - status=" + error;
        log.logMessage(msg, 0, true);
        String out = "Out:" + cmd + " - Type=" + type + " - out=" + result;
        log.logMessage(out, 10, true);
    }
    
    public static OAIServiceInt getService()
        throws Exception
    {
        OAIServiceProperties serviceProp = OAIServiceProperties.getOAIServiceProperties();
        return OAIService.getOAIService(serviceProp);
    }
    
    public static void testList(Connection connect, LoggerInf logger)
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
            OAIServiceInt service = getService();
            out =  service.getOAIResponse(
                cmd,
                null,
                metaType_dcs.getOAIPrefix(),
                from.getOAI(),
                until.getOAI(),
                mnemonic,
                null
            );
            logResult(logger, cmd, "All", out);
            
                        
            
            //*********************************************
            out =  service.getOAIResponse(
                cmd,
                null,
                metaType_dcs.getOAIPrefix(),
                null,
                until.getOAI(),
                mnemonic,
                null
            );
            logResult(logger, cmd, "NoFrom", out); 
            
            //*********************************************
            out =  service.getOAIResponse(
                cmd,
                null,
                metaType_dcs.getOAIPrefix(),
                from.getOAI(),
                null,
                mnemonic,
                null
            );
            logResult(logger, cmd, "NoUntil", out);
            
            //*********************************************1
            out =  service.getOAIResponse(
                cmd,
                null,
                null,
                from.getOAI(),
                until.getOAI(),
                mnemonic,
                null
            );
            logResult(logger, cmd, "NoPrefix", out);
            
            //*********************************************
            out =  service.getOAIResponse(
                cmd,
                null,
                metaType_dcs.getOAIPrefix(),
                from.getOAI(),
                until.getOAI(),
                null,
                null
            );
            logResult(logger, cmd, "NoMnemonic", out);
            
            
            //*********************************************
            out =  service.getOAIResponse(
                cmd,
                null,
                metaType_dcs.getOAIPrefix(),
                from.getOAI(),
                until.getOAI(),
                mnemonicNone,
                null
            );
            logResult(logger, cmd, "mnemonicNone", out);
            
            
            
        } catch(Exception e) {
                e.printStackTrace();
                System.out.println(
                    "Main: Encountered exception:" + e);
                System.out.println(
                        StringUtil.stackTrace(e));
        }
    }
    
    public static void testIdentifiers(Connection connect, LoggerInf logger)
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
            OAIServiceInt service = getService();
            out =  service.getOAIResponse(
                cmd,
                null,
                metaType_dcs.getOAIPrefix(),
                from.getOAI(),
                until.getOAI(),
                mnemonic,
                null
            );
            logResult(logger, cmd, "All", out);
            
                        
            
            //*********************************************
            out =  service.getOAIResponse(
                cmd,
                null,
                metaType_dcs.getOAIPrefix(),
                null,
                until.getOAI(),
                mnemonic,
                null
            );
            logResult(logger, cmd, "NoFrom", out); 
            
            //*********************************************
            out =  service.getOAIResponse(
                cmd,
                null,
                metaType_dcs.getOAIPrefix(),
                from.getOAI(),
                null,
                mnemonic,
                null
            );
            logResult(logger, cmd, "NoUntil", out);
            
            //*********************************************1
            out =  service.getOAIResponse(
                cmd,
                null,
                null,
                from.getOAI(),
                until.getOAI(),
                mnemonic,
                null
            );
            logResult(logger, cmd, "NoPrefix", out);
            
            //*********************************************
            out =  service.getOAIResponse(
                cmd,
                null,
                metaType_dcs.getOAIPrefix(),
                from.getOAI(),
                until.getOAI(),
                null,
                null
            );
            logResult(logger, cmd, "NoMnemonic", out);
            
            //*********************************************
            out =  service.getOAIResponse(
                cmd,
                null,
                metaType_dcs.getOAIPrefix(),
                from.getOAI(),
                until.getOAI(),
                mnemonicNone,
                null
            );
            logResult(logger, cmd, "mnemonicNone", out);
            
            
        } catch(Exception e) {
                e.printStackTrace();
                System.out.println(
                    "Main: Encountered exception:" + e);
                System.out.println(
                        StringUtil.stackTrace(e));
        }
    }
    
    public static void testMetadata(Connection connect, LoggerInf logger)
    {

        try {
            OAIId id = OAIId.getArkId("ark:/13030/oai005");
            
            String out = null;
            String cmd = "ListMetadataFormats";
            
            
            //*********************************************

            OAIServiceInt service = getService();
            out =  service.getOAIResponse(
                cmd,
                id.getN2T(),
                    //null,
                null,
                null,
                null,
                null,
                null
            );
            logResult(logger, cmd, "ark", out);
            
            //*********************************************
            out =  service.getOAIResponse(
                cmd,
                null,
                null,
                null,
                null,
                null,
                null
            );
            logResult(logger, cmd, "No ark", out);
            
        } catch(Exception e) {
                e.printStackTrace();
                System.out.println(
                    "Main: Encountered exception:" + e);
                System.out.println(
                        StringUtil.stackTrace(e));
        }
    }
    
    public static void testIdentity(Connection connect, LoggerInf logger)
    {

        try {
            OAIId id = OAIId.getArkId("ark:/13030/oai005");
            
            String out = null;
            String cmd = "Identity";
            
            
            //*********************************************
            OAIServiceInt service = getService();
            out =  service.getOAIResponse(
                cmd,
                null,
                null,
                null,
                null,
                null,
                null
            );
            logResult(logger, cmd, "standard", out);
            
            
        } catch(Exception e) {
                e.printStackTrace();
                System.out.println(
                    "Main: Encountered exception:" + e);
                System.out.println(
                        StringUtil.stackTrace(e));
        }
    }
    
    public static void testGetRecord(Connection connect, LoggerInf logger)
    {

        try {
            OAIId id = OAIId.getArkId("ark:/13030/oai005");
            OAIMetadata metaType_dc = OAIMetadata.setOAIPrefix("oai_dc");
            OAIMetadata metaType_dcs = OAIMetadata.setOAIPrefix("dcs3.1");
            String out = null;
            String cmd = "GetRecord";
            
            
            //*********************************************
            OAIServiceInt service = getService();
            out =  service.getOAIResponse(
                cmd,
                id.getN2T(),
                metaType_dcs.getOAIPrefix(),
                null,
                null,
                null,
                null
            );
            logResult(logger, cmd, "dcs3.1", out);
            
            //*********************************************
            out =  service.getOAIResponse(
                cmd,
                id.getN2T(),
                metaType_dc.getOAIPrefix(),
                null,
                null,
                null,
                null
            );
            logResult(logger, cmd, "dc", out);
            
                        
            
            
            
        } catch(Exception e) {
                e.printStackTrace();
                System.out.println(
                    "Main: Encountered exception:" + e);
                System.out.println(
                        StringUtil.stackTrace(e));
        }
    }
    
    public static void testListSets(Connection connect, LoggerInf logger)
    {

        try {
            OAIId id = OAIId.getArkId("ark:/13030/oai005");
            OAIMetadata metaType_dc = OAIMetadata.setOAIPrefix("oai_dc");
            OAIMetadata metaType_dcs = OAIMetadata.setOAIPrefix("dcs3.1");
            String out = null;
            String cmd = "ListSets";
            
            
            //*********************************************
            OAIServiceInt service = getService();
            out =  service.getOAIResponse(
                cmd,
                null,
                null,
                null,
                null,
                null,
                null
            );
            logResult(logger, cmd, "standard", out);
            
            
        } catch(Exception e) {
                e.printStackTrace();
                System.out.println(
                    "Main: Encountered exception:" + e);
                System.out.println(
                        StringUtil.stackTrace(e));
        }
    }


    protected static String get(Properties prop, String key)
        throws TException
    {
        String retVal = prop.getProperty(key);
        if (StringUtil.isEmpty(retVal)) {
            
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "missing property:" + key);
        }
        return retVal;
    } 
}
