package org.cdlib.mrt.oai.xoai;

import java.io.ByteArrayInputStream;
import org.cdlib.mrt.oai.element.OAIDate;
import org.cdlib.mrt.oai.element.OAIId;
import org.cdlib.mrt.oai.element.OAIItem;
import org.cdlib.mrt.oai.element.OAIMetadata;
import org.cdlib.mrt.oai.element.OAISet;
import com.google.common.base.Function;
import com.lyncode.builder.ListBuilder;
import com.lyncode.xoai.dataprovider.exceptions.InternalOAIException;
import com.lyncode.xoai.model.oaipmh.About;
import com.lyncode.xoai.model.oaipmh.Metadata;
import com.lyncode.xoai.model.xoai.Element;
import com.lyncode.xoai.model.xoai.XOAIMetadata;
import com.lyncode.xoai.dataprovider.model.Item;
import java.sql.Connection;

import java.util.*;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import org.cdlib.mrt.core.Identifier;
import org.cdlib.mrt.inv.content.InvMeta;
import org.cdlib.mrt.oai.utility.OAIDBUtil;
import org.cdlib.mrt.utility.LoggerInf;

public class MrtMemoryItem 
        implements Item 
{
    public static final boolean DELETED = false;
    public static final boolean DEBUG = false;
    
    public static MrtMemoryItem item () {
        return new MrtMemoryItem();
    }

    private Map<String, Object> values = new HashMap<String, Object>();
    private List<com.lyncode.xoai.dataprovider.model.Set> setList = null;
    private String metadata = null;
    private Metadata localMetadata = null;

    public static MrtMemoryItem set(OAIItem oaiItem) {
        if (DEBUG) System.out.println(oaiItem.dump("***DUMP***"));
        MrtMemoryItem item = new MrtMemoryItem()
                .with("identifier", oaiItem.getOaiId().getN2T())
                .with("datestamp", oaiItem.getOaiDate().getUnixDate());
        ListBuilder<String> listBuilder = new ListBuilder<String>();
        for (OAISet set : oaiItem.getOaiSets()) {
            listBuilder.add(set.getSetSpec());
        }
        item.with("sets", listBuilder);
        item.values.put("sets", oaiItem.getOaiSets());
        //item.setStringMetadata(oaiItem.getValue());
        if (oaiItem.getValue() != null) item.setStringMetadata(oaiItem.getValue());
        item.setList = oaiItem.getXoaiSets();
        return item;
    }

    public MrtMemoryItem with(String name, Object value) {
        values.put(name, value);
        return this;
    }

    public MrtMemoryItem withSet(String name) {
        ((List<String>) values.get("sets")).add(name);
        return this;
    }

    @Override
    public List<About> getAbout() {
        return new ArrayList<About>();
    }

    @Override
    public Metadata getMetadata() {
        return localMetadata;
    }
    
    public Metadata setMetadata() {
        return localMetadata;
    }

    @Override
    public String getIdentifier() {
        return (String) values.get("identifier");
    }

    @Override
    public Date getDatestamp() {
        return (Date) values.get("datestamp");
    }

    @Override
    public List<com.lyncode.xoai.dataprovider.model.Set> getSets() {
        return setList;
    }
    
    public List<com.lyncode.xoai.dataprovider.model.Set> getSetsOriginal() {
        List<String> list = ((List<String>) values.get("sets"));
        return new ListBuilder<String>().add(list.toArray(new String[list.size()])).build(new Function<String, com.lyncode.xoai.dataprovider.model.Set>() {
            @Override
            public com.lyncode.xoai.dataprovider.model.Set apply(String elem) {
                return new com.lyncode.xoai.dataprovider.model.Set(elem);
            }
        });
    }

    @Override
    public boolean isDeleted() {
        return DELETED;
    }

    public MrtMemoryItem withDefaults() {
        this
                .with("identifier", randomAlphabetic(10))
                .with("datestamp", new Date())
                .with("sets", new ListBuilder<String>().add(randomAlphabetic(3)).build())
                .with("deleted", Integer.parseInt(randomNumeric(1)) > 5);
        return this;
    }

    public MrtMemoryItem withIdentifier(String identifier) {
        this.with("identifier", identifier);
        return this;
    }
    
    public void setStringMetadata(String metadata)
    {
        if (metadata.contains("xxxoai_dcxxx")) {
            try {
                String send = "<metadata>" + metadata  + "</metadata>";
                //send = metadata;
                ByteArrayInputStream inStream = new ByteArrayInputStream(send.getBytes("utf-8"));
                XOAIMetadata xoaiMetadata  = XOAIMetadata.parse (inStream);
                localMetadata = new Metadata(xoaiMetadata);
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new InternalOAIException(ex);
            }
        } else {
            this.metadata = metadata;
            localMetadata = new Metadata(metadata);
            localMetadata.setStraigthCopy(true);
        }
    }
}
