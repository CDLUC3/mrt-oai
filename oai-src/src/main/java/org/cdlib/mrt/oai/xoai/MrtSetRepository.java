/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cdlib.mrt.oai.xoai;

import com.lyncode.xoai.dataprovider.exceptions.HandlerException;
import com.lyncode.xoai.dataprovider.exceptions.InternalOAIException;
import com.lyncode.xoai.dataprovider.handlers.results.ListSetsResult;
import com.lyncode.xoai.dataprovider.repository.Repository;
import com.lyncode.xoai.dataprovider.repository.RepositoryConfiguration;
import com.lyncode.xoai.model.oaipmh.DeletedRecord;
import com.lyncode.xoai.model.oaipmh.Granularity;
import com.lyncode.xoai.services.impl.SimpleResumptionTokenFormat;
import com.lyncode.xoai.dataprovider.repository.SetRepository;
import com.lyncode.xoai.dataprovider.handlers.results.ListSetsResult;
import com.lyncode.xoai.dataprovider.model.Set;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.cdlib.mrt.inv.content.InvCollection;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.inv.utility.DPRFileDB;
import org.cdlib.mrt.oai.action.ListSetsAction;
import org.cdlib.mrt.oai.element.OAISet;
import org.cdlib.mrt.oai.utility.OAIDBUtil;
/**
 *
 * @author replic
 */
public class MrtSetRepository implements SetRepository
{
    private final Connection connection;
    private final LoggerInf logger;
    private List<OAISet> list;
    
    public MrtSetRepository(Connection connection, LoggerInf logger)
    {
        this.connection = connection;
        this.logger = logger;
        getList();
    }
    
    private List<OAISet> getList()
    {
        try {
            ListSetsAction lsa = ListSetsAction.getListSetsAction(connection, logger);
            list = lsa.process();
            return list;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new InternalOAIException(ex);
        }
    }
    
    /**
     * Checks if the actual data source supports sets.
     *
     * @return Supports sets?
     */
    public boolean supportSets()
    {
        return true;
    }

    /**
     * Returns a paged list of sets.
     * It is common to use a partial result of 100 sets however, in XOAI this is a configured parameter.
     *
     * @param offset Starting offset
     * @param length Max size of the returned list
     * @return List of Sets
     */
    public ListSetsResult retrieveSets(int offset, int length)
    {
        System.out.println("***retrieveSets:" 
                + " - offset=" + offset 
                + " - length=" + length 
        );
        ArrayList<Set> setList = new ArrayList(list.size());
        for (int len = 0; len < length; len++)
        {
            if ((offset + len) >= list.size()) break;
            OAISet oaiSet = list.get(offset + len);
            Set set = new Set(oaiSet.getSetSpec())
                    .withName(oaiSet.getSetName());
            setList.add(set);
        }
        boolean hasMoreResults =false;
        if ((offset + length) < list.size()) hasMoreResults = true;
        ListSetsResult result = new ListSetsResult(hasMoreResults, setList);
        return result;
    }

    /**
     * Checks if a specific sets exists in the data source.
     *
     * @param setSpec Set spec
     * @return Set exists
     * @see <a href="client://www.openarchives.org/OAI/openarchivesprotocol.html#Set">Set definition</a>
     */
    public boolean exists(String setSpec)
    {
        for (OAISet oaiSet : list) {
            if (oaiSet.getSetSpec().equals(setSpec)) return true;
        }
        return false;
    }
    
}
