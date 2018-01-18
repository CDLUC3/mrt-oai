/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.oai.element;

import org.cdlib.mrt.core.DateState;
import org.cdlib.mrt.core.Identifier;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.cdlib.mrt.utility.TException;

/**
 *
 * @author dloy
 */
public class OAIDateTest {
    public static final String n2t = "http://n2t.net/ark:/13030/abcdef";

    public OAIDateTest() {
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
    public void TestDB()
        throws TException
    {
        try {
            OAIDate date = OAIDate.getDBDate("2014-08-18 11:52:18");
            String db = date.getDB();
            String cdl = date.getMrt();
            String oai = date.getOAI();
            System.out.println("\n***TestDB:"+ "\n"
                    + " - db=" + db + "\n"
                    + " - cdl=" + cdl + "\n"
                    + " - oai=" + oai + "\n"
            );
            
            assertTrue(db.equals("2014-08-18 11:52:18"));
            assertTrue(cdl.equals("2014-08-18T11:52:18-07:00"));
            assertTrue(oai.equals("2014-08-18T18:52:18Z"));

        } catch (Exception ex) {
            assertFalse("TestIT exception:" + ex, true);
        }
    }
    
    @Test
    public void TestOAI()
        throws TException
    {
        try {
            OAIDate date = OAIDate.getOAIDate("2014-08-18T18:52:18Z");
            String db = date.getDB();
            String cdl = date.getMrt();
            String oai = date.getOAI();
            System.out.println("\n***TestOAI:"+ "\n"
                    + " - db=" + db + "\n"
                    + " - cdl=" + cdl + "\n"
                    + " - oai=" + oai + "\n"
            );
            
            assertTrue(db.equals("2014-08-18 11:52:18"));
            assertTrue(cdl.equals("2014-08-18T11:52:18-07:00"));
            assertTrue(oai.equals("2014-08-18T18:52:18Z"));

        } catch (Exception ex) {
            assertFalse("TestIT exception:" + ex, true);
        }
    }
    
    @Test
    public void TestMrt()
        throws TException
    {
        try {
            OAIDate date = OAIDate.getMrtDate("2014-08-18T11:52:18-07:00");
            String db = date.getDB();
            String cdl = date.getMrt();
            String oai = date.getOAI();
            System.out.println("\n***TestMrt:"+ "\n"
                    + " - db=" + db + "\n"
                    + " - cdl=" + cdl + "\n"
                    + " - oai=" + oai + "\n"
            );
            
            assertTrue(db.equals("2014-08-18 11:52:18"));
            assertTrue(cdl.equals("2014-08-18T11:52:18-07:00"));
            assertTrue(oai.equals("2014-08-18T18:52:18Z"));

        } catch (Exception ex) {
            assertFalse("TestIT exception:" + ex, true);
        }
    }
}