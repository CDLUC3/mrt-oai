/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cdlib.mrt.oai.xoai;

import com.lyncode.xoai.dataprovider.repository.Repository;
import com.lyncode.xoai.dataprovider.repository.RepositoryConfiguration;
import com.lyncode.xoai.model.oaipmh.DeletedRecord;
import com.lyncode.xoai.model.oaipmh.Granularity;
import com.lyncode.xoai.services.impl.SimpleResumptionTokenFormat;
import java.util.Date;
import org.cdlib.mrt.core.DateState;
import org.cdlib.mrt.oai.element.OAIDate;
/**
 *
 * @author replic
 */
public class Initial
{
    private RepositoryConfiguration repositoryConfiguration;
    private Repository repository = null;
            
    protected String repositoryName = "Merritt";
    protected String baseURL = "http://merritt.cdlib.org/oai/v2</baseURL";
    protected String protocolVersion = "2.0</protocolVersion"; // not set here
    protected String adminEmail = "uc3@ucop.edu</adminEmail";
    protected String earliestDatestamp = "2013-05-22";
    protected String granularity = "YYYY-MM-DDThh:mm:ss</granularity";
    
    public Initial()
    {
        OAIDate earliestDate = OAIDate.getDBDate("2013-05-22");
        Date confDate = earliestDate.getUnixDate();
        repositoryConfiguration = new RepositoryConfiguration()
            .withRepositoryName(repositoryName)
            .withBaseUrl(baseURL)
            .withAdminEmail(adminEmail)
            .withEarliestDate(confDate)
            .withGranularity(Granularity.Second)
            .withDeleteMethod(DeletedRecord.NO)
            .withMaxListIdentifiers(10000)
            .withMaxListRecords(10000)
            .withMaxListSets(100);
        
        
    repository = new Repository()
            //.withSetRepository(setRepository)
            //.withItemRepository(itemRepository)
            .withResumptionTokenFormatter(new SimpleResumptionTokenFormat())
            .withConfiguration(repositoryConfiguration);
    }
    
    public void dumpConfiguration(String header)
    {
        System.out.println("repositoryName:" + repositoryConfiguration.getRepositoryName());
        System.out.println("baseURL:" + repositoryConfiguration.getBaseUrl());
        System.out.println("adminEmail:" + repositoryConfiguration.getAdminEmails());
        Date confDate = repositoryConfiguration.getEarliestDate();
        DateState dateState = new DateState(confDate);
        OAIDate oaiDate = OAIDate.getOAIDate(dateState);
        System.out.println("earliestDate:" + oaiDate.getOAI());
    }
    
    
}
