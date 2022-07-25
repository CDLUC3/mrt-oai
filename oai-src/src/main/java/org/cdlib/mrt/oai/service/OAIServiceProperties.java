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
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.LoggerInf;
import java.sql.Connection;


import org.cdlib.mrt.inv.utility.DPRFileDB;
import org.cdlib.mrt.oai.action.OAIConfig;
import org.dspace.xoai.dataprovider.repository.RepositoryConfiguration;

/**
 * Base properties for Inv
 * @author  dloy
 */

public class OAIServiceProperties
{
    private static final String NAME = "OAIServiceProperties";
    private static final String MESSAGE = NAME + ": ";
    private static final boolean DEBUG = false;

    protected DPRFileDB db = null;
    protected Thread zooHandlerThread = null;
    protected long zooPollTime = 120000;
    protected int zooThreadCnt = 1;
    protected LoggerInf logger = null;
    protected boolean shutdown = true;
    protected String storageBase = null;
    protected OAIServiceState serviceState = null;
    protected OAIConfig oaiConfig = null;

    public static OAIServiceProperties getOAIServiceProperties()
        throws TException
    {
        OAIConfig oaiConfig = OAIConfig.getOAIConfig();
        return new OAIServiceProperties(oaiConfig);
    }

    protected OAIServiceProperties(OAIConfig oaiConfig)
        throws TException
    {
        try {
            this.oaiConfig = oaiConfig;
            logger = oaiConfig.getLogger();
            db = oaiConfig.startDB();
            //Properties serviceProperties = addInit(oaiService, setupProp);
            serviceState = new OAIServiceState();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new TException(ex);
        }
    }
    
    protected DPRFileDB getNewDb()
        throws TException
    {
        return oaiConfig.startDB();
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
