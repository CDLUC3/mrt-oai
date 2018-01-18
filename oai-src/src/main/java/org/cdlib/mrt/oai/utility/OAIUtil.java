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
package org.cdlib.mrt.oai.utility;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TException;


/**
 * This interface defines the functional API for a Curational Storage Service
 * @author dloy
 */
public class OAIUtil
{
    private static final String NAME = "OAIUtil";
    private static final String MESSAGE = NAME + ": ";
    private static final boolean DEBUG = false;

    public static Properties getInfoProp(String servicePath, String infoName)
        throws TException
    {
        try {
            //System.out.println(PropertiesUtil.dumpProperties("setupProp", setupProp))
            if (StringUtil.isEmpty(servicePath)) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "missing property: servicePath");
            }
            File serviceDir = new File(servicePath);
            if (!serviceDir.exists()) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "Service directory does not exist:"
                        + serviceDir.getCanonicalPath());
            }
            File infoF = new File(serviceDir, infoName);
            if (!infoF.exists()) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + infoName + " does not exist:"
                        + infoF.getCanonicalPath());
            }
            FileInputStream fis = new FileInputStream(infoF);
            Properties serviceProperties = new Properties();
            serviceProperties.load(fis);
            return serviceProperties; 
            
        } catch (TException tex) {
            System.out.println(MESSAGE + "TException:" +  tex);
            throw tex;
            
        } catch (Exception ex) {
            System.out.println(MESSAGE + "Exception:" +  ex);
            throw new TException(ex);
        }
    }
    
    protected static void log(String msg)
    {
        if (!DEBUG) return;
        System.out.println(MESSAGE + msg);
    }
}

