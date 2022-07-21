/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cdlib.mrt.oai.xoai;

import org.dspace.xoai.dataprovider.exceptions.CannotDisseminateFormatException;
import org.dspace.xoai.dataprovider.exceptions.IdDoesNotExistException;
import org.dspace.xoai.dataprovider.exceptions.OAIException;
import org.dspace.xoai.dataprovider.filter.ScopedFilter;
import org.dspace.xoai.dataprovider.handlers.results.ListItemIdentifiersResult;
import org.dspace.xoai.dataprovider.handlers.results.ListItemsResults;
import org.dspace.xoai.dataprovider.model.Item;
import org.dspace.xoai.dataprovider.repository.ItemRepository;
import java.sql.Connection;
import java.util.Date;
import java.util.List;
import org.cdlib.mrt.core.Identifier;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.oai.action.ListOAIIdsAction;
import org.cdlib.mrt.oai.action.ListOAIItemsAction;
import org.cdlib.mrt.oai.action.RecordAction;
import org.cdlib.mrt.oai.element.OAIDate;
import org.cdlib.mrt.oai.element.OAIId;
import org.cdlib.mrt.oai.element.OAIItem;
import org.cdlib.mrt.oai.element.OAIMetadata;
import org.cdlib.mrt.oai.utility.OAIDBUtil;
/**
 *
 * @author replic
 */
public class MrtItemRepository implements ItemRepository
{
    private final Connection connection;
    private final LoggerInf logger;
    private final OAIMetadata metaType;
    private RecordAction recordAction = null;
    
    public MrtItemRepository(
            OAIMetadata metaType, 
            Connection connection,
            LoggerInf logger)
    {
        this.connection = connection;
        this.logger = logger;
        this.metaType = metaType;
    }
    
    private MrtMemoryItem setItem(OAIId id)
        throws IdDoesNotExistException
    {
        Identifier objectID = id.getId();
        OAIItem oaiItem = OAIDBUtil.getItem(metaType, id, connection,  logger);
        if (oaiItem == null) {
            throw new IdDoesNotExistException("No item found for id:" + objectID.getValue());
        }
        return MrtMemoryItem.set(oaiItem);
    }
    
    /**
     * Gets an item from the data source.
     *
     * @param identifier Unique identifier of the item
     * @return ItemHelper
     * @throws com.lyncode.xoai.dataprovider.exceptions.IdDoesNotExistException
     *
     * @throws com.lyncode.xoai.dataprovider.exceptions.OAIException
     *
     * @see <a href="client://www.openarchives.org/OAI/openarchivesprotocol.html#UniqueIdentifier">Unique identifier definition</a>
     */
    public Item getItem(String identifier)
            throws IdDoesNotExistException, OAIException
    {
        OAIId id = OAIId.getN2TId(identifier);
        MrtMemoryItem mrtItem = null;
        mrtItem = setItem(id);
        return mrtItem;
    }

    /**
     * Gets a paged list of identifiers. The metadata prefix parameter is internally converted to a list of filters.
     * That is, when configuring XOAI, it is possible to associate to each metadata format a list of filters.
     *
     * @param filters List of Filters <a href="https://github.com/lyncode/xoai/wiki/XOAI-Data-Provider-Architecture">details</a>
     * @param offset  Start offset
     * @param length  Max items returned
     * @return List of identifiers
     * @throws OAIException
     * @see <a href="client://www.openarchives.org/OAI/openarchivesprotocol.html#ListIdentifiers">List Identifiers definition</a>
     */
    public ListItemIdentifiersResult getItemIdentifiers(
            List<ScopedFilter> filters, int offset, int length) throws OAIException
    {       
        try {
            //throw new OAIException("metadata prefix required");
            ListItemIdentifiersResult results = getIds(
                offset, length,
                metaType,
                null,
                null,
                null,
                connection,
                logger);
            return results;
            
        } catch (OAIException ox) {
            throw ox;
            
        } catch (Exception ex) {
            throw new OAIException("getItem:" + ex.toString());
        }
    }
            

    /**
     * Gets a paged list of identifiers. The metadata prefix parameter is internally converted to a list of filters.
     * That is, when configuring XOAI, it is possible to associate to each metadata format a list of filters.
     *
     * @param filters List of Filters <a href="https://github.com/lyncode/xoai/wiki/XOAI-Data-Provider-Architecture">details</a>
     * @param offset  Start offset
     * @param length  Max items returned
     * @param from    Date parameter
     * @return List of identifiers
     * @throws OAIException
     * @see <a href="client://www.openarchives.org/OAI/openarchivesprotocol.html#ListIdentifiers">List Identifiers definition</a>
     */
    public ListItemIdentifiersResult getItemIdentifiers(
            List<ScopedFilter> filters, int offset, int length, Date from) throws OAIException
    {       
        try {
            //throw new OAIException("metadata prefix required");
            OAIDate fromOAI = OAIDate.getUnixDate(from);
            ListItemIdentifiersResult results = getIds(
                offset, length,
                metaType,
                null,
                fromOAI,
                null,
                connection,
                logger);
            return results;
            
        } catch (OAIException ox) {
            throw ox;
            
        } catch (Exception ex) {
            throw new OAIException("getItem:" + ex.toString());
        }
    }

    /**
     * Gets a paged list of identifiers. The metadata prefix parameter is internally converted to a list of filters.
     * That is, when configuring XOAI, it is possible to associate to each metadata format a list of filters.
     *
     * @param filters List of Filters <a href="https://github.com/lyncode/xoai/wiki/XOAI-Data-Provider-Architecture">details</a>
     * @param offset  Start offset
     * @param length  Max items returned
     * @param until   Date parameter
     * @return List of identifiers
     * @throws OAIException
     * @see <a href="client://www.openarchives.org/OAI/openarchivesprotocol.html#ListIdentifiers">List Identifiers definition</a>
     */
    public ListItemIdentifiersResult getItemIdentifiersUntil(
            List<ScopedFilter> filters, int offset, int length, Date until) throws OAIException
    {       
        try {
            //throw new OAIException("metadata prefix required");
            OAIDate untilOAI = OAIDate.getUnixDate(until);
            ListItemIdentifiersResult results = getIds(
                offset, length,
                metaType,
                null,
                null,
                untilOAI,
                connection,
                logger);
            return results;
            
        } catch (OAIException ox) {
            throw ox;
            
        } catch (Exception ex) {
            throw new OAIException("getItem:" + ex.toString());
        }
    }

    /**
     * Gets a paged list of identifiers. The metadata prefix parameter is internally converted to a list of filters.
     * That is, when configuring XOAI, it is possible to associate to each metadata format a list of filters.
     *
     * @param filters List of Filters <a href="https://github.com/lyncode/xoai/wiki/XOAI-Data-Provider-Architecture">details</a>
     * @param offset  Start offset
     * @param length  Max items returned
     * @param from    Date parameter
     * @param until   Date parameter
     * @return List of identifiers
     * @throws OAIException
     * @see <a href="client://www.openarchives.org/OAI/openarchivesprotocol.html#ListIdentifiers">List Identifiers definition</a>
     */
    public ListItemIdentifiersResult getItemIdentifiers(
            List<ScopedFilter> filters, int offset, int length, Date from, Date until) throws OAIException
    {       
        try {
            //throw new OAIException("metadata prefix required");
            OAIDate fromOAI = OAIDate.getUnixDate(from);
            OAIDate untilOAI = OAIDate.getUnixDate(until);
            ListItemIdentifiersResult results = getIds(
                offset, length,
                metaType,
                null,
                fromOAI,
                untilOAI,
                connection,
                logger);
            return results;
            
        } catch (OAIException ox) {
            throw ox;
            
        } catch (Exception ex) {
            throw new OAIException("getItem:" + ex.toString());
        }
    }

    /**
     * Gets a paged list of identifiers. The metadata prefix parameter is internally converted to a list of filters.
     * That is, when configuring XOAI, it is possible to associate to each metadata format a list of filters.
     *
     * @param filters List of Filters <a href="https://github.com/lyncode/xoai/wiki/XOAI-Data-Provider-Architecture">details</a>
     * @param offset  Start offset
     * @param length  Max items returned
     * @param setSpec Set Spec
     * @return List of identifiers
     * @throws OAIException
     * @see <a href="client://www.openarchives.org/OAI/openarchivesprotocol.html#ListIdentifiers">List Identifiers definition</a>
     */
    public ListItemIdentifiersResult getItemIdentifiers(
            List<ScopedFilter> filters, int offset, int length, String setSpec) throws OAIException
    {
        try {
            ListItemIdentifiersResult results = getIds(
                offset, length,
                metaType,
                setSpec,
                null,
                null,
                connection,
                logger);
            return results;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new OAIException("getItem:" + ex.toString());
        }
    }

    /**
     * Gets a paged list of identifiers. The metadata prefix parameter is internally converted to a list of filters.
     * That is, when configuring XOAI, it is possible to associate to each metadata format a list of filters.
     *
     * @param filters List of Filters <a href="https://github.com/lyncode/xoai/wiki/XOAI-Data-Provider-Architecture">details</a>
     * @param offset  Start offset
     * @param length  Max items returned
     * @param setSpec Set Spec
     * @param from    Date parameter
     * @return List of identifiers
     * @throws OAIException
     * @see <a href="client://www.openarchives.org/OAI/openarchivesprotocol.html#ListIdentifiers">List Identifiers definition</a>
     */
    public ListItemIdentifiersResult getItemIdentifiers(
            List<ScopedFilter> filters, int offset, int length, String setSpec,
            Date from) throws OAIException
    {
        try {
            OAIDate fromOAI = OAIDate.getUnixDate(from);
            ListItemIdentifiersResult results = getIds(
                offset, length,
                metaType,
                setSpec,
                fromOAI,
                null,
                connection,
                logger);
            return results;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new OAIException("getItem:" + ex.toString());
        }
    }

    /**
     * Gets a paged list of identifiers. The metadata prefix parameter is internally converted to a list of filters.
     * That is, when configuring XOAI, it is possible to associate to each metadata format a list of filters.
     *
     * @param filters List of Filters <a href="https://github.com/lyncode/xoai/wiki/XOAI-Data-Provider-Architecture">details</a>
     * @param offset  Start offset
     * @param length  Max items returned
     * @param setSpec Set Spec
     * @param until   Date parameter
     * @return List of identifiers
     * @throws OAIException
     * @see <a href="client://www.openarchives.org/OAI/openarchivesprotocol.html#ListIdentifiers">List Identifiers definition</a>
     */
    public ListItemIdentifiersResult getItemIdentifiersUntil(
            List<ScopedFilter> filters, int offset, int length, String setSpec,
            Date until) throws OAIException
    {
        try {
            OAIDate untilOAI = OAIDate.getUnixDate(until);
            ListItemIdentifiersResult results = getIds(
                offset, length,
                metaType,
                setSpec,
                null,
                untilOAI,
                connection,
                logger);
            return results;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new OAIException("getItem:" + ex.toString());
        }
    }

    /**
     * Gets a paged list of identifiers. The metadata prefix parameter is internally converted to a list of filters.
     * That is, when configuring XOAI, it is possible to associate to each metadata format a list of filters.
     *
     * @param filters List of Filters <a href="https://github.com/lyncode/xoai/wiki/XOAI-Data-Provider-Architecture">details</a>
     * @param offset  Start offset
     * @param length  Max items returned
     * @param setSpec Set Spec
     * @param from    Date parameter
     * @param until   Date parameter
     * @return List of identifiers
     * @throws OAIException
     * @see <a href="client://www.openarchives.org/OAI/openarchivesprotocol.html#ListIdentifiers">List Identifiers definition</a>
     */
    public ListItemIdentifiersResult getItemIdentifiers(
            List<ScopedFilter> filters, int offset, int length, String setSpec,
            Date from, Date until) throws OAIException
    {
        try {
            OAIDate fromOAI = OAIDate.getUnixDate(from);
            OAIDate untilOAI = OAIDate.getUnixDate(until);
            ListItemIdentifiersResult results = getIds(
                offset, length,
                metaType,
                setSpec,
                fromOAI,
                untilOAI,
                connection,
                logger);
            return results;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new OAIException("getItem:" + ex.toString());
        }
    }

    /**
     * Gets a paged list of items. The metadata prefix parameter is internally converted to a list of filters.
     * That is, when configuring XOAI, it is possible to associate to each metadata format a list of filters.
     *
     * @param filters List of Filters <a href="https://github.com/lyncode/xoai/wiki/XOAI-Data-Provider-Architecture">details</a>
     * @param offset  Start offset
     * @param length  Max items returned
     * @return List of Items
     * @throws OAIException
     * @see <a href="client://www.openarchives.org/OAI/openarchivesprotocol.html#ListRecords">List Records Definition</a>
     */
    public ListItemsResults getItems(List<ScopedFilter> filters,
                                     int offset, int length) throws OAIException
    {
        
        try {
            //throw new OAIException("metadata prefix required");
            ListItemsResults results = getResults(
                offset, length,
                metaType,
                null,
                null,
                null,
                connection,
                logger);
            return results;
            
        } catch (OAIException ox) {
            throw ox;
            
        } catch (Exception ex) {
            throw new OAIException("getItem:" + ex.toString());
        }
    }

    /**
     * Gets a paged list of items. The metadata prefix parameter is internally converted to a list of filters.
     * That is, when configuring XOAI, it is possible to associate to each metadata format a list of filters.
     *
     * @param filters List of Filters <a href="https://github.com/lyncode/xoai/wiki/XOAI-Data-Provider-Architecture">details</a>
     * @param offset  Start offset
     * @param length  Max items returned
     * @param from    Date parameter
     * @return List of Items
     * @throws OAIException
     * @see <a href="client://www.openarchives.org/OAI/openarchivesprotocol.html#ListRecords">List Records Definition</a>
     */
    public ListItemsResults getItems(List<ScopedFilter> filters,
                                     int offset, int length, Date from) throws OAIException
    {
        
        try {
            //throw new OAIException("metadata prefix required");
            OAIDate fromOAI = OAIDate.getUnixDate(from);
            ListItemsResults results = getResults(
                offset, length,
                metaType,
                null,
                fromOAI,
                null,
                connection,
                logger);
            return results;
            
        } catch (OAIException ox) {
            throw ox;
            
        } catch (Exception ex) {
            throw new OAIException("getItem:" + ex.toString());
        }
    }

    /**
     * Gets a paged list of items. The metadata prefix parameter is internally converted to a list of filters.
     * That is, when configuring XOAI, it is possible to associate to each metadata format a list of filters.
     *
     * @param filters List of Filters <a href="https://github.com/lyncode/xoai/wiki/XOAI-Data-Provider-Architecture">details</a>
     * @param offset  Start offset
     * @param length  Max items returned
     * @param until   Date parameter
     * @return List of Items
     * @throws OAIException
     * @see <a href="client://www.openarchives.org/OAI/openarchivesprotocol.html#ListRecords">List Records Definition</a>
     */
    public ListItemsResults getItemsUntil(List<ScopedFilter> filters,
                                          int offset, int length, Date until) throws OAIException
    {
        
        try {
            //throw new OAIException("metadata prefix required");
            OAIDate untilOAI = OAIDate.getUnixDate(until);
            ListItemsResults results = getResults(
                offset, length,
                metaType,
                null,
                null,
                untilOAI,
                connection,
                logger);
            return results;
            
        } catch (OAIException ox) {
            throw ox;
            
        } catch (Exception ex) {
            throw new OAIException("getItem:" + ex.toString());
        }
    }

    /**
     * Gets a paged list of items. The metadata prefix parameter is internally converted to a list of filters.
     * That is, when configuring XOAI, it is possible to associate to each metadata format a list of filters.
     *
     * @param filters List of Filters <a href="https://github.com/lyncode/xoai/wiki/XOAI-Data-Provider-Architecture">details</a>
     * @param offset  Start offset
     * @param length  Max items returned
     * @param from    Date parameter
     * @param until   Date parameter
     * @return List of Items
     * @throws OAIException
     * @see <a href="client://www.openarchives.org/OAI/openarchivesprotocol.html#ListRecords">List Records Definition</a>
     */
    public ListItemsResults getItems(List<ScopedFilter> filters,
                                     int offset, int length, Date from, Date until) throws OAIException
    {
        try {
            //throw new OAIException("metadata prefix required");
            OAIDate fromOAI = OAIDate.getUnixDate(from);
            OAIDate untilOAI = OAIDate.getUnixDate(until);
            ListItemsResults results = getResults(
                offset, length,
                metaType,
                null,
                fromOAI,
                untilOAI,
                connection,
                logger);
            return results;
            
        } catch (OAIException ox) {
            throw ox;
            
        } catch (Exception ex) {
            throw new OAIException("getItem:" + ex.toString());
        }
    }

    /**
     * Gets a paged list of items. The metadata prefix parameter is internally converted to a list of filters.
     * That is, when configuring XOAI, it is possible to associate to each metadata format a list of filters.
     *
     * @param filters List of Filters <a href="https://github.com/lyncode/xoai/wiki/XOAI-Data-Provider-Architecture">details</a>
     * @param offset  Start offset
     * @param length  Max items returned
     * @param setSpec Set spec
     * @return List of Items
     * @throws OAIException
     * @see <a href="client://www.openarchives.org/OAI/openarchivesprotocol.html#ListRecords">List Records Definition</a>
     */
    public ListItemsResults getItems(List<ScopedFilter> filters,
                                     int offset, int length, String setSpec) throws OAIException
    {
        try {
            ListItemsResults results = getResults(
                offset, length,
                metaType,
                setSpec,
                null,
                null,
                connection,
                logger);
            return results;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new OAIException("getItem:" + ex.toString());
        }
    }

    /**
     * Gets a paged list of items. The metadata prefix parameter is internally converted to a list of filters.
     * That is, when configuring XOAI, it is possible to associate to each metadata format a list of filters.
     *
     * @param filters List of Filters <a href="https://github.com/lyncode/xoai/wiki/XOAI-Data-Provider-Architecture">details</a>
     * @param offset  Start offset
     * @param length  Max items returned
     * @param from    Date parameter
     * @param setSpec Set spec
     * @return List of Items
     * @throws OAIException
     * @see <a href="client://www.openarchives.org/OAI/openarchivesprotocol.html#ListRecords">List Records Definition</a>
     */
    public ListItemsResults getItems(List<ScopedFilter> filters,
                                     int offset, int length, String setSpec, Date from) throws OAIException
    {
        try {
            OAIDate fromOAI = OAIDate.getUnixDate(from);
            ListItemsResults results = getResults(
                offset, length,
                metaType,
                setSpec,
                fromOAI,
                null,
                connection,
                logger);
            return results;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new OAIException("getItem:" + ex.toString());
        }
    }

    /**
     * Gets a paged list of items. The metadata prefix parameter is internally converted to a list of filters.
     * That is, when configuring XOAI, it is possible to associate to each metadata format a list of filters.
     *
     * @param filters List of Filters <a href="https://github.com/lyncode/xoai/wiki/XOAI-Data-Provider-Architecture">details</a>
     * @param offset  Start offset
     * @param length  Max items returned
     * @param until   Date parameter
     * @param setSpec Set spec
     * @return List of Items
     * @throws OAIException
     * @see <a href="client://www.openarchives.org/OAI/openarchivesprotocol.html#ListRecords">List Records Definition</a>
     */
    public ListItemsResults getItemsUntil(List<ScopedFilter> filters,
                                          int offset, int length, String setSpec, Date until) throws OAIException
    {
        try {
            OAIDate untilOAI = OAIDate.getUnixDate(until);
            ListItemsResults results = getResults(
                offset, length,
                metaType,
                setSpec,
                null,
                untilOAI,
                connection,
                logger);
            return results;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new OAIException("getItem:" + ex.toString());
        }
    }

    /**
     * Gets a paged list of items. The metadata prefix parameter is internally converted to a list of filters.
     * That is, when configuring XOAI, it is possible to associate to each metadata format a list of filters.
     *
     * @param filters List of Filters <a href="https://github.com/lyncode/xoai/wiki/XOAI-Data-Provider-Architecture">details</a>
     * @param offset  Start offset
     * @param length  Max items returned
     * @param from    Date parameter
     * @param until   Date parameter
     * @param setSpec Set spec
     * @return List of Items
     * @throws OAIException
     * @see <a href="client://www.openarchives.org/OAI/openarchivesprotocol.html#ListRecords">List Records Definition</a>
     */
    public ListItemsResults getItems(List<ScopedFilter> filters,
                                     int offset, int length, String setSpec, Date from, Date until) throws OAIException
    {
        try {
            OAIDate fromOAI = OAIDate.getUnixDate(from);
            OAIDate untilOAI = OAIDate.getUnixDate(until);
            ListItemsResults results = getResults(
                offset, length,
                metaType,
                setSpec,
                fromOAI,
                untilOAI,
                connection,
                logger);
            return results;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new OAIException("getItem:" + ex.toString());
        }
    }
    
    public ListItemsResults getResults(
            Integer offset, Integer length,
            OAIMetadata metaType,
            String setMnemonic,
            OAIDate fromOAI,
            OAIDate untilOAI,
            Connection connection,
            LoggerInf logger)
            throws OAIException
    {
        try {
            if (metaType.isUnknown()) {
                throw new CannotDisseminateFormatException(metaType.getUnknownPrefix() + " is not a suipported dissemination format");
            }
            ListOAIItemsAction action = ListOAIItemsAction.getListOAIItemsAction(
                    metaType,
                    setMnemonic,
                    fromOAI, 
                    untilOAI, 
                    connection,
                    logger);
            action.process();
            ListItemsResults results = action.getListItemsResults(offset, length);
            return results;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new OAIException("getItem:" + ex.toString());
        }
    }
    
    public ListItemIdentifiersResult getIds(
            Integer offset, Integer length,
            OAIMetadata metaType,
            String setMnemonic,
            OAIDate fromOAI,
            OAIDate untilOAI,
            Connection connection,
            LoggerInf logger)
            throws OAIException
    {
        try {
            if (metaType.isUnknown()) {
                throw new CannotDisseminateFormatException(metaType.getUnknownPrefix() + " is not a suipported dissemination format");
            }
            
            ListOAIIdsAction action = ListOAIIdsAction.getListOAIIdsAction(
                    metaType,
                    setMnemonic,
                    fromOAI, 
                    untilOAI, 
                    connection,
                    logger);
            action.process();
            ListItemIdentifiersResult results = action.getListIdsResults(offset, length);
            return results;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new OAIException("getItem:" + ex.toString());
        }
    }
}
