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

import com.lyncode.xoai.dataprovider.repository.RepositoryConfiguration;
import com.lyncode.xoai.model.oaipmh.DeletedRecord;
import com.lyncode.xoai.model.oaipmh.Granularity;
import com.lyncode.xoai.model.oaipmh.DeletedRecord;
import java.io.File;
import java.util.Properties;
import org.cdlib.mrt.oai.action.OAIConfig;
import org.cdlib.mrt.core.DateState;
import org.cdlib.mrt.core.ServiceStatus;
import org.cdlib.mrt.inv.utility.DPRFileDB;
import org.cdlib.mrt.oai.element.OAIDate;
import org.cdlib.mrt.oai.utility.OAIUtil;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.PropertiesUtil;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.StateInf;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.TFileLogger;
import org.cdlib.mrt.utility.TFrame;

/**
 * Format container class for Fixity Service
 * @author dloy
 */
public class OAIServiceState
        implements StateInf
{
    private static final String NAME = "OAIServiceState";
    private static final String MESSAGE = NAME + ": ";

    
    
    protected String repositoryName = null;
    protected String baseURL = null;
    protected String protocolVersion = null;
    protected String adminEmail = null;
    protected OAIDate earliestDate = null;
    protected String granularityS = null;
    protected Integer maxListIdentifiers = 0;
    protected Integer maxListRecords= 0;
    protected Integer maxListSets = 0;
    protected DeletedRecord deleteMethod = null;
    private RepositoryConfiguration repositoryConfiguration = null;
    protected Granularity granularity = null;
    protected static OAIConfig oaiConfig = null;
    
    public OAIServiceState() 
        throws TException
    {
        try {
            oaiConfig = OAIConfig.getOAIConfig();
            Properties infoProp = oaiConfig.getServiceProperties();
            setValues(infoProp);
            
        } catch (TException tex) {
            System.out.println(MESSAGE + "Exception:" +  tex);
            throw tex;
        }
    }
    
    public static void main(String[] argv) {
    	
    	try {
            OAIServiceState state = new OAIServiceState();
            
        } catch (Exception ex) {
                // TODO Auto-generated catch block
                System.out.println("Exception:" + ex);
                ex.printStackTrace();
        }
    }

    public void setConfiguration()
    {
        repositoryConfiguration = new RepositoryConfiguration()
            .withRepositoryName(repositoryName)
            .withBaseUrl(baseURL)
            .withAdminEmail(adminEmail)
            .withEarliestDate(earliestDate.getUnixDate())
            .withGranularity(Granularity.Second)
            .withDeleteMethod(deleteMethod)
            .withMaxListIdentifiers(maxListIdentifiers)
            .withMaxListRecords(maxListRecords)
            .withMaxListSets(maxListSets);
    }
    
    public RepositoryConfiguration retrieveConfiguration()
    {
        if (repositoryConfiguration == null) setConfiguration();
        return repositoryConfiguration;
    }
    
    public void setValues(Properties prop)
    {
        setRepositoryName(prop.getProperty("repositoryName"));
        setBaseURL(prop.getProperty("baseURL"));
        setAdminEmail(prop.getProperty("adminEmail"));
        setEarliestDate(prop.getProperty("earliestDate"));
        setGranularity(prop.getProperty("granularity"));
        setDeletedMethod(prop.getProperty("deletedMethod"));
        setMaxListIdentifiers(prop.getProperty("maxListIdentifiers"));
        setMaxListRecords(prop.getProperty("maxListRecords"));
        setMaxListSets(prop.getProperty("maxListSets"));
        
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public Granularity getGranularity() {
        return granularity;
    }

    public void setGranularity(Granularity granularity) {
        this.granularity = granularity;
    }

    public void setGranularity(String granularityS) {
        this.granularity = Granularity.fromRepresentation(granularityS);
    }

    public int getMaxListIdentifiers() {
        return maxListIdentifiers;
    }

    public void setMaxListIdentifiers(int maxListIdentifiers) {
        this.maxListIdentifiers = maxListIdentifiers;
    }

    public void setMaxListIdentifiers(String maxListIdentifiersS) {
        this.maxListIdentifiers = getInteger(maxListIdentifiersS);
    }

    protected Integer getInteger(String in) 
    {
        if (StringUtil.isAllBlank(in)) {
            return null;
        }
        try {
            return Integer.parseInt(in);
        } catch (Exception ex) {
            return null;
        }
        
    }
    public int getMaxListRecords() {
        return maxListRecords;
    }

    public void setMaxListRecords(int maxListRecords) {
        this.maxListRecords = maxListRecords;
    }

    public void setMaxListRecords(String maxListRecordsS) {
        this.maxListRecords = getInteger(maxListRecordsS);
    }

    public int getMaxListSets() {
        return maxListSets;
    }

    public void setMaxListSets(int maxListSets) {
        this.maxListSets = maxListSets;
    }

    public void setMaxListSets(String maxListSetsS) {
        this.maxListSets = getInteger(maxListSetsS);
    }

    public OAIDate getEarliestDate() {
        return earliestDate;
    }

    public void setEarliestDate(OAIDate earliestDate) {
        this.earliestDate = earliestDate;
    }

    public void setEarliestDate(String earliestDateS) {
        if (StringUtil.isAllBlank(earliestDateS)) this.earliestDate = null;
        this.earliestDate = OAIDate.getDBDate(earliestDateS);
    }

    public RepositoryConfiguration getRepositoryConfiguration() {
        return repositoryConfiguration;
    }

    public void setRepositoryConfiguration(RepositoryConfiguration repositoryConfiguration) {
        this.repositoryConfiguration = repositoryConfiguration;
    }

    public DeletedRecord getDeleteMethod() {
        return deleteMethod;
    }

    public void setDeleteMethod(DeletedRecord deletedMethod) {
        this.deleteMethod = deletedMethod;
    }

    public void setDeletedMethod(String deletedMethodS) {
        if (StringUtil.isAllBlank(deletedMethodS)) return;
        deletedMethodS = deletedMethodS.toLowerCase();
        this.deleteMethod = DeletedRecord.fromValue(deletedMethodS);
    }
    
}
