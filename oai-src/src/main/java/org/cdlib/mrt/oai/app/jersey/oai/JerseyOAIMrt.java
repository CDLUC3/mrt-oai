/*
Copyright (c) 2005-2012, Regents of the University of California
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

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
*********************************************************************/
package org.cdlib.mrt.oai.app.jersey.oai;



import javax.servlet.ServletConfig;
import org.glassfish.jersey.server.CloseableService;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

import javax.ws.rs.Produces;
import javax.ws.rs.core.UriInfo;

import org.cdlib.mrt.formatter.FormatterInf;
import org.cdlib.mrt.oai.app.jersey.KeyNameHttpInf;
import org.cdlib.mrt.oai.app.jersey.JerseyBase;
import org.cdlib.mrt.oai.service.OAIServiceInt;
import org.cdlib.mrt.oai.app.OAIServiceInit;
import org.cdlib.mrt.utility.StateInf;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.StringUtil;

import org.cdlib.mrt.oai.element.OAIDate;
import org.cdlib.mrt.oai.element.OAIId;
import org.cdlib.mrt.oai.element.OAISet;

/**
 * Thin Jersey layer for inv handling
 * @author  David Loy
 */
@Path ("/")
public class JerseyOAIMrt
        extends JerseyBase
        implements KeyNameHttpInf
{

    protected static final String NAME = "JerseyOAIMrt";
    protected static final String MESSAGE = NAME + ": ";
    protected static final FormatterInf.Format DEFAULT_OUTPUT_FORMAT
            = FormatterInf.Format.xml;
    protected static final boolean DEBUG = true;
    protected static final String NL = System.getProperty("line.separator");
    protected OAIDate from = null;
    protected OAIDate until = null;
    protected OAISet set = null;
    protected OAIId id = null;
    
    @GET
    @Path ("oai/v2")
    @Produces(MediaType.TEXT_XML)
    public String callOAI(
            final @QueryParam("verb") String verbParam,
            final @QueryParam("identifier") String identifierParam,
            final @QueryParam("metadataPrefix") String metadataPrefixParam, 
            final @QueryParam("from") String fromParam,
            final @QueryParam("until") String untilParam, 
            final @QueryParam("set") String setParam,
            final @QueryParam("resumptionToken") String resumptionToken, 
            @Context CloseableService cs,
            @Context ServletConfig sc)
        throws TException
    {       
    
        return processOAI(
            verbParam,
            identifierParam,
            metadataPrefixParam, 
            fromParam,
            untilParam, 
            setParam,
            resumptionToken, 
            cs,
            sc);
    }

    
    /**
     * Get state information about a specific node
     * @param nodeID node identifier
     * @param formatType user provided format type
     * @param cs on close actions
     * @param sc ServletConfig used to get system configuration
     * @return formatted service information
     * @throws TException
     */
    
    @GET
    @Path("state")
    public Response callGetServiceState(
            @DefaultValue("xhtml") @QueryParam(KeyNameHttpInf.RESPONSEFORM) String formatType,
            @Context CloseableService cs,
            @Context ServletConfig sc)
        throws TException
    {
        LoggerInf logger = defaultLogger;
        try {
            log("getServiceState entered:"
                    + " - formatType=" + formatType
                    );
 
            OAIServiceInit oaiServiceInit = OAIServiceInit.getOAIServiceInit(sc);
            OAIServiceInt service = oaiServiceInit.getOAIService();
            logger = service.getLogger();

            StateInf responseState = service.getOAIServiceState();
            return getStateResponse(responseState, formatType, logger, cs, sc);

        } catch (TException tex) {
            return getExceptionResponse(tex, formatType, logger);

        } catch (Exception ex) {
            System.out.println("TRACE:" + StringUtil.stackTrace(ex));
            throw new TException.GENERAL_EXCEPTION(MESSAGE + "Exception:" + ex);
        }
    }


}
