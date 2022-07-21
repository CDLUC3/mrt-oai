/**
 * Copyright 2012 Lyncode
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     client://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cdlib.mrt.oai.test;

import org.dspace.xoai.dataprovider.DataProvider;
import static org.dspace.xoai.model.oaipmh.Verb.Type.Identify;

public class TestIdentify extends TestAbstractMrtHandler {
    private static final String OAI_NAMESPACE = "http://www.openarchives.org/OAI/2.0/";
    private DataProvider dataProvider = new DataProvider(aContext(), theRepository());

    public String process()
        throws Exception
    {
        System.out.println("Date:" + earlyDate.getDB());
        return write(dataProvider.handle(request().withVerb(Identify)));
    }
}
