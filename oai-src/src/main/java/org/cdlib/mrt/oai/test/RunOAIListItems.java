
/*********************************************************************
    Copyright 2003 Regents of the University of California
    All rights reserved
*********************************************************************/

package org.cdlib.mrt.oai.test;

import java.sql.Connection;
import java.util.Properties;
import org.cdlib.mrt.inv.utility.DPRFileDB;
import org.cdlib.mrt.oai.action.OAIConfig;
import org.cdlib.mrt.oai.element.OAIDate;
import org.cdlib.mrt.oai.element.OAIMetadata;
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

public class RunOAIListItems
{
    private static final String NAME = "RunListItems";
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
            
            OAIConfig config = OAIConfig.getOAIConfig();
            Properties invProp = config.getServiceProperties();
            LoggerInf logger = new TFileLogger("testFormatter", 10, 10);
            db = config.getDB();
            Connection connect = db.getConnection(true);
            OAIMetadata metaType_dcs = OAIMetadata.setOAIPrefix("dcs3.1");
            OAIMetadata metaType_dc = OAIMetadata.setOAIPrefix("cdl_dryaddev");
            
            OAIMetadata metaType_sw = OAIMetadata.setOAIPrefix("stash_wrapper");
            OAIDate from = OAIDate.getDBDate("2022-07-21 00:06:01");
            OAIDate until = OAIDate.getDBDate("2022-07-21 10:06:06");
            //OAIDate until = OAIDate.getDBDate("2015-02-06 10:32:51"); // test no results
            //String mnemonic = "dash_cdl";
            String mnemonic = null;
            OAIListRecords oaiRecords = new OAIListRecords(
                null,
                metaType_dcs.getOAIPrefix(),
                //metaType_dc.getOAIPrefix(),
                //metaType_dcs.getOAIPrefix(),
                from.getOAI(),
                until.getOAI(),
                //null,
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
