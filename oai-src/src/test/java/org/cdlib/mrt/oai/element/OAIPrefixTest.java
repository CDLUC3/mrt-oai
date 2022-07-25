/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.oai.element;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.cdlib.mrt.utility.TException;
import org.dspace.xoai.dataprovider.exceptions.HandlerException;

/**
 *
 * @author dloy
 */
public class OAIPrefixTest {
    public static final String n2t = "http://n2t.net/ark:/13030/abcdef";

    public OAIPrefixTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
  
    @Test
    public void TestOAI()
        throws HandlerException
    {
        try {
            System.out.println("TestOAI");
            OAIMetadata prefix = OAIMetadata.setOAIPrefix("oai_dc");
            String mrt1 = prefix.getMrtPrefix();
            assertTrue(true);
            
            OAIMetadata prefix2 = OAIMetadata.setOAIPrefix("dcs3.1");
            String mrt2 = prefix2.getMrtPrefix();
            System.out.println("OAIPrefixTest:"
                    + " - mrt1=" + mrt1
                    + " - mrt2=" + mrt2
            );
            assertTrue(true);

        } catch (Exception ex) {
            assertFalse("TestIT exception:" + ex, true);
        }
    }
  
    @Test
    public void TestMrt()
        throws HandlerException
    {
        System.out.println("TestMrt");
        try {
            OAIMetadata prefix = OAIMetadata.setMrtPrefix("OAI_DublinCore");
            String oai1 = prefix.getOAIPrefix();
            assertTrue(true);
            
            OAIMetadata prefix2 = OAIMetadata.setMrtPrefix("DataCite");
            String oai2 = prefix2.getOAIPrefix();
            System.out.println("OAIPrefixTest:"
                    + " - oai1=" + oai1
                    + " - oai2=" + oai2
            );
            assertTrue(true);

        } catch (Exception ex) {
            assertFalse("TestIT exception:" + ex, true);
        }
    }
    
    
  
    @Test
    public void TestOAIExc()
        throws HandlerException
    {
        System.out.println("TestOAIExc");
        try {
            OAIMetadata prefix = OAIMetadata.setOAIPrefix("xxx");
            String mrt1 = prefix.getMrtPrefix();
            System.out.println("TestOAIExc:" + mrt1);
            assertTrue(mrt1.equals("UNKNOWN"));

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            assertTrue(false);
        }
    }
    
    @Test
    public void TestMrtEmpty()
        throws TException
    {
        System.out.println("TestMrtEmpty");
        try {
            OAIMetadata prefix = OAIMetadata.setMrtPrefix("");
            String mrt1 = prefix.getMrtPrefix();
            System.out.println("TestMrtEmpty:" + mrt1);
            assertTrue(false);

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            assertTrue(true);
        }
    }
    @Test
    public void TestMrtExc()
        throws HandlerException
    {
        System.out.println("TestMrtExc");
        try {
            OAIMetadata prefix = OAIMetadata.setMrtPrefix("xxx");
            String mrt1 = prefix.getMrtPrefix();
            assertTrue(mrt1.equals("UNKNOWN"));

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            assertTrue(false);
        }
    }
    
    @Test
    public void TestOAIEmpty()
        throws TException
    {
        System.out.println("TestOAIEmpty");
        try {
            OAIMetadata prefix = OAIMetadata.setOAIPrefix("");
            String mrt1 = prefix.getMrtPrefix();
            assertTrue(false);

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            assertTrue("TestEmpty exception:" + ex, true);
        }
    }

}