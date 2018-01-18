/******************************************************************************
Copyright (c) 2005-2012, Regents of the University of California
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:
 *
- Redistributions of source code must retain the above copyright notice,
  this list of conditions and the following disclaimer.
- Redistributions in binary form must reproduce the above copyright
  notice, this list of conditions and the following disclaimer in the
  documentation and/or other materials provided with the distribution.
- Neither the name of the University of California nor the names of its
  contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
OF THE POSSIBILITY OF SUCH DAMAGE.
*******************************************************************************/

package org.cdlib.mrt.oai.service;
import java.util.Properties;

import org.cdlib.mrt.core.ServiceStatus;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.StringUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;


import org.cdlib.mrt.inv.utility.DPRFileDB;
import org.cdlib.mrt.queue.DistributedQueue;
import org.cdlib.mrt.utility.PropertiesUtil;
import org.cdlib.mrt.utility.TFileLogger;
import com.lyncode.xoai.dataprovider.repository.RepositoryConfiguration;
import org.cdlib.mrt.utility.TFrame;

/**
 * Base properties for Inv
 * @author  dloy
 */

public class OAIServiceProperties
{
    private static final String NAME = "OAIServiceProperties";
    private static final String MESSAGE = NAME + ": ";
    private static final boolean DEBUG = false;

    protected Properties serviceProperties = null;
    protected Properties setupProperties = null;
    protected File oaiService = null;
    protected File oaiInfo = null;
    protected DPRFileDB db = null;
    protected Thread zooHandlerThread = null;
    protected long zooPollTime = 120000;
    protected int zooThreadCnt = 1;
    protected LoggerInf logger = null;
    protected boolean shutdown = true;
    protected String storageBase = null;
    protected OAIServiceState serviceState = null;

    public static OAIServiceProperties getOAIServiceProperties()
        throws TException
    {
        try {
            String propertyList[] = {
                "resources/OAI.properties",
                "resources/Logger.properties",
                "resources/Mysql.properties",
            };
            TFrame mFrame = new TFrame(propertyList, NAME);
            Properties prop = mFrame.getAllProperties();
            System.out.println(PropertiesUtil.dumpProperties(NAME, prop));
            return new OAIServiceProperties(prop);
            
        } catch (TException tex) {
            System.out.println(MESSAGE + "Exception:" +  tex);
            throw tex;
        }
    }

    public static OAIServiceProperties getOAIServiceProperties(Properties prop)
        throws TException
    {
        return new OAIServiceProperties(prop);
    }

    protected OAIServiceProperties(Properties setupProp)
        throws TException
    {
        try {
            this.setupProperties = setupProp;
            
            //System.out.println(PropertiesUtil.dumpProperties("setupProp", setupProp));
            String oaiServiceS = setupProp.getProperty("OAIService");
            if (StringUtil.isEmpty(oaiServiceS)) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "missing property: OAIService");
            }
            oaiService = new File(oaiServiceS);
            if (!oaiService.exists()) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "inv service directory does not exist:"
                        + oaiService.getCanonicalPath());
            }
            File logDir = new File(oaiService, "log");
            if (!logDir.exists()) {
                logDir.mkdir();
            }
            logger = new TFileLogger("oai", logDir.getCanonicalPath() + '/', setupProp);
            
            setupProp.remove("db.pw");
            if (DEBUG) System.out.println(PropertiesUtil.dumpProperties("***" + NAME, setupProp));
            
            db = new DPRFileDB(logger, setupProp);
            //Properties serviceProperties = addInit(oaiService, setupProp);
            serviceState = new OAIServiceState();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new TException(ex);
        }
    }
    
    public DPRFileDB getNewDb()
        throws TException
    {
        return new DPRFileDB(logger, setupProperties);
    }

    public DPRFileDB getDb() {
        return db;
    }

    public void dbShutDown()
        throws TException
    {
        if (db == null) return;
        db.shutDown();
        db = null;
    }

    public void dbStartup()
        throws TException
    {
        if (db != null) return;
        db = getNewDb();
    }
    
    public Connection getConnection(boolean autoCommit)
        throws TException
    {
        if (db == null) return null;
        return db.getSingleConnection(autoCommit);
    }
    

    public LoggerInf getLogger() {
        return logger;
    }
    
    public RepositoryConfiguration getRepositoryConfiguration()
    {
        return serviceState.getRepositoryConfiguration();
    }

    public OAIServiceState getServiceState() {
        return serviceState;
    }

}
