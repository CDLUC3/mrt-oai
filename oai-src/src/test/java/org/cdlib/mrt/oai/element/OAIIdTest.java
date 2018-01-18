/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.oai.element;

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
public class OAIIdTest {
    public static final String n2t = "http://n2t.net/ark:/13030/abcdef";

    public OAIIdTest() {
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
    public void TestN2T()
        throws TException
    {
        try {
            OAIId id = OAIId.getN2TId(n2t);
            String idArk = id.getArk();
            String idN2T = id.getN2T();
            System.out.println("OAIIdTest:"
                    + " - idArk=" + idArk
                    + " - idN2T=" + idN2T
            );
            assertTrue(idArk.equals("ark:/13030/abcdef"));
            assertTrue(idN2T.equals(n2t));

        } catch (Exception ex) {
            assertFalse("TestIT exception:" + ex, true);
        }
    }

    @Test
    public void TestArk()
        throws TException
    {
        try {
            String arkS = "ark:/13030/abcdef";
            OAIId id = OAIId.getArkId(arkS);
            String idArk = id.getArk();
            String idN2T = id.getN2T();
            System.out.println("OAIIdTest:"
                    + " - idArk=" + idArk
                    + " - idN2T=" + idN2T
            );
            assertTrue(idArk.equals("ark:/13030/abcdef"));
            assertTrue(idN2T.equals(n2t));

        } catch (Exception ex) {
            assertFalse("TestIT exception:" + ex, true);
        }
    }
}