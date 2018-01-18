
/*********************************************************************
    Copyright 2003 Regents of the University of California
    All rights reserved
*********************************************************************/

package org.cdlib.mrt.oai.test;

import org.cdlib.mrt.inv.utility.DPRFileDB;
import org.cdlib.mrt.oai.element.OAIDate;
import org.cdlib.mrt.oai.element.OAIId;
import org.cdlib.mrt.oai.element.OAIMetadata;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TFrame;

/**
 * Build curl requests for testing
 * @author  dloy
 */

public class BuildCurlServiceRequestsWrap
{
    private static final String NAME = "RunServiceHandler";
    private static final String MESSAGE = NAME + ": ";

    private static final String NL = System.getProperty("line.separator");
    private static final boolean DEBUG = true;
    
    private static final String PREFIX = "http://uc3-mrtoai-dev.cdlib.org:37001/mrtoai/oai/v2?";
    
    public static OAIMetadata metaType_dwrap;
    public static OAIMetadata metaType_dcs;
    public static OAIMetadata metaType_dc;
    
    public static OAIDate from;
    public static OAIDate until;
    
    public static OAIId id;
    
    public static String mnemonic;
    public static String mnemonicNone;
    /**
     * Main method
     */
    public static void main(String args[])
            throws Exception
    {

        TFrame tFrame = null;
        DPRFileDB db = null;
        try {
            metaType_dwrap = OAIMetadata.setOAIPrefix("stash_wrapper");
            metaType_dcs = OAIMetadata.setOAIPrefix("dcs3.1");
            metaType_dc = OAIMetadata.setOAIPrefix("oai_dc");
            from = OAIDate.getDBDate("2015-10-09 11:25:02");
            until = OAIDate.getDBDate("2015-10-09 11:28:01");
            id = OAIId.getArkId("ark:/99999/fk4ng4vt22");
            mnemonic = "dash_cdl";
            mnemonicNone = "oai_none";
            
            testList();
            testIdentifiers();
            testMetadata();
            testIdentity();
            testGetRecord();
            testListSets();
            
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
 
            //OAIDate until = OAIDate.getDBDate("2015-02-06 10:32:51"); // test no results
            String out = null;
            String cmd = "ListRecords";
            
            
            //*********************************************
            out =  getCurl(
                cmd,
                null,
                metaType_dwrap.getOAIPrefix(),
                from.getOAI(),
                until.getOAI(),
                mnemonic,
                null
            );
            
                        
            
            //*********************************************
            out =  getCurl(
                cmd,
                null,
                metaType_dwrap.getOAIPrefix(),
                null,
                until.getOAI(),
                mnemonic,
                null
            );
            
            //*********************************************
            out =  getCurl(
                cmd,
                null,
                metaType_dwrap.getOAIPrefix(),
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
                metaType_dwrap.getOAIPrefix(),
                from.getOAI(),
                until.getOAI(),
                null,
                null
            );
            
            
            //*********************************************
            out =  getCurl(
                cmd,
                null,
                metaType_dwrap.getOAIPrefix(),
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
            String out = null;
            String cmd = "ListIdentifiers";
            
            
            //*********************************************
            out =  getCurl(
                cmd,
                null,
                metaType_dwrap.getOAIPrefix(),
                from.getOAI(),
                until.getOAI(),
                mnemonic,
                null
            );
            
                        
            
            //*********************************************
            out =  getCurl(
                cmd,
                null,
                metaType_dwrap.getOAIPrefix(),
                null,
                until.getOAI(),
                mnemonic,
                null
            );
            
            //*********************************************
            out =  getCurl(
                cmd,
                null,
                metaType_dwrap.getOAIPrefix(),
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
                metaType_dwrap.getOAIPrefix(),
                from.getOAI(),
                until.getOAI(),
                null,
                null
            );
            
            //*********************************************
            out =  getCurl(
                cmd,
                null,
                metaType_dwrap.getOAIPrefix(),
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
            String out = null;
            String cmd = "GetRecord";
            
            
            //*********************************************
            out =  getCurl(
                cmd,
                id.getN2T(),
                metaType_dwrap.getOAIPrefix(),
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
