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

import java.sql.Connection;
import org.cdlib.mrt.oai.xoai.CmdHandler;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.LoggerInf;

/**
 * Inv Service Interface
 * @author  dloy
 */

public class OAIService
    implements OAIServiceInt
{
    protected OAIServiceProperties serviceProperties = null;
    protected LoggerInf logger = null;
    
    public static OAIService getOAIService(OAIServiceProperties serviceProperties)
    {
        return new OAIService(serviceProperties);
    }
    
    protected OAIService(OAIServiceProperties serviceProperties) 
    {
        this.serviceProperties = serviceProperties;
        this.logger = serviceProperties.getLogger();
    }
 
    /**
     * @return pointer to merritt logger
     */
    public LoggerInf getLogger() {
        return serviceProperties.getLogger();
    }
    
    public OAIServiceState getOAIServiceState()
        throws TException
    {
        return serviceProperties.getServiceState();
    }
    
    public String getOAIResponse(
            String verbParam,
            String identifierParam,
            String metadataPrefixParam, 
            String fromParam,
            String untilParam, 
            String setParam,
            String resumptionToken) 
        throws Exception
    {
        Connection connect = null;
        System.out.println("***getOAIResponse:\n"
                + " - verbParam=" + verbParam + "\n"
                + " - identifierParam=" + identifierParam + "\n"
                + " - metadataPrefixParam=" + metadataPrefixParam + "\n"
                + " - fromParam=" + fromParam + "\n"
                + " - untilParam=" + untilParam + "\n"
                + " - setParam=" + setParam + "\n"
        );
        try {
            connect = serviceProperties.getConnection(true);
            CmdHandler handler = CmdHandler.getCmdHandler(
                    verbParam, 
                    identifierParam, 
                    metadataPrefixParam, 
                    fromParam, untilParam, 
                    setParam, 
                    resumptionToken, 
                    connect, 
                    logger);
            return handler.process();
            
        } catch (Exception ex) {
            throw ex;
            
        } finally {
            try {
                if (connect != null) {
                    connect.close();
                    System.out.println("Close connection");
                }
            } catch (Exception ex) { }
        }
    }
}
