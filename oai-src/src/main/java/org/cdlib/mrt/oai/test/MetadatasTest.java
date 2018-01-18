
/*********************************************************************
    Copyright 2003 Regents of the University of California
    All rights reserved
*********************************************************************/

package org.cdlib.mrt.oai.test;

import java.sql.Connection;
import java.util.List;
import java.util.Properties;


import org.cdlib.mrt.core.Identifier;
import org.cdlib.mrt.oai.action.RecordAction;
import com.lyncode.xoai.dataprovider.exceptions.HandlerException;
import org.cdlib.mrt.utility.PropertiesUtil;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.TFileLogger;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.TFrame;
import org.cdlib.mrt.inv.content.InvMeta;
import org.cdlib.mrt.inv.content.InvVersion;
import org.cdlib.mrt.inv.utility.DPRFileDB;
import org.cdlib.mrt.inv.utility.InvDBUtil;
import org.cdlib.mrt.inv.utility.InvUtil;
import org.cdlib.mrt.inv.zoo.ItemRun;
import org.cdlib.mrt.inv.action.SaveObject;
import org.cdlib.mrt.oai.action.ListMetadatasAction;
import org.cdlib.mrt.oai.element.OAIId;
import org.cdlib.mrt.oai.element.OAIMetadata;

/**
 * Load manifest.
 * @author  dloy
 */

public class MetadatasTest
{
    private static final String NAME = "MetadatasTest";
    private static final String MESSAGE = NAME + ": ";

    private static final String NL = System.getProperty("line.separator");
    private static final boolean DEBUG = true;

    /**
     * Main method
     */
    public static void main(String args[])
    {

        try {
            LoggerInf logger = new TFileLogger("testFormatter", 10, 10);
            ListMetadatasAction metadatas = ListMetadatasAction.getListMetadatasAction(logger);
            metadatas.dumpList(NAME);

        } catch(Exception e) {
                e.printStackTrace();
                System.out.println(
                    "Main: Encountered exception:" + e);
                System.out.println(
                        StringUtil.stackTrace(e));
        } 
    }
    
    protected static void test(
            String prefix,
            Connection connection,
            String objectIDS,
            LoggerInf logger
            )
        throws TException
    {
        RecordAction recordAction = null;
        try {
            OAIMetadata prefixOAI = OAIMetadata.setOAIPrefix(prefix);
            OAIId idOAI = OAIId.getArkId(objectIDS);
            recordAction = RecordAction.getRecordAction(
                idOAI,
                prefixOAI,
                connection,
                logger);
            String out = recordAction.process();
            System.out.println("\n\n***Prefix=" + prefix + " - ark:" + objectIDS + " - OUT=\n" + out);
            InvMeta meta = recordAction.getMeta();
            InvVersion invVersion = recordAction.getInvVersion();
            System.out.println("Meta:"
                    + " - schema:" + meta.getSchema()
                    + " - Serialization:" + meta.getSerialization()
                    + " - Date:" + invVersion.getCreated()
            );
        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            HandlerException handlerException = recordAction.getHandlerException();
            System.out.println("HandlerException:" + handlerException);
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
