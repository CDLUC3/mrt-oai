
/*********************************************************************
    Copyright 2003 Regents of the University of California
    All rights reserved
*********************************************************************/

package org.cdlib.mrt.oai.test;

import java.sql.Connection;
import java.util.Properties;
import org.cdlib.mrt.inv.utility.DPRFileDB;
import org.cdlib.mrt.oai.element.OAIDate;
import org.cdlib.mrt.oai.element.OAIMetadata;
import org.cdlib.mrt.oai.xoai.OAIListIdentifiers;
import org.cdlib.mrt.oai.xoai.OAIListRecords;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.TFileLogger;
import org.cdlib.mrt.utility.TFrame;

/**
 * Load manifest.
 * @author  dloy
 */

public class RunOAIListIdentifiers
{
    private static final String NAME = "RunOAIListIdentifiers";
    private static final String MESSAGE = NAME + ": ";

    private static final String NL = System.getProperty("line.separator");
    private static final boolean DEBUG = true;

    /**
     * 
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
            OAIMetadata prefix = OAIMetadata.setOAIPrefix("oai_dc");
            OAIMetadata metaType_dcs = OAIMetadata.setOAIPrefix("dcs3.1");
            
            OAIMetadata metaType_dc = OAIMetadata.setOAIPrefix("oai_dc");
            OAIDate from = OAIDate.getDBDate("2015-07-14 15:00:00");
            OAIDate until = OAIDate.getDBDate("2015-07-14 15:59:00");
            //OAIDate until = OAIDate.getDBDate("2015-02-06 10:32:51"); // test no results
            String mnemonic = "dash_cdl";
            OAIListIdentifiers oaiRecords = new OAIListIdentifiers(
                null,
                metaType_dcs.getOAIPrefix(),
                from.getOAI(),
                until.getOAI(),
                mnemonic,
                null,
                connect,
                logger
            );
            String out = oaiRecords.process();
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
