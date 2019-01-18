/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rambo;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gre
 */
public class AITest {
    
    public AITest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getTotalDistance method, of class AI.
     */
    /*
    @Test
    public void testGetTotalDistance() {
        System.out.println("getTotalDistance");
        AI instance = new AI();
        double expResult = 0.0;
        double result = instance.getTotalDistance();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    */
    /**
     * Test of set method, of class AI.
     */
    @Test
    public void testSet() {
        System.out.println("set");
        double gunCoolingRate = 0.0;
        AI instance = new AI(true);
        instance.set(gunCoolingRate);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of estimateBulletDt method, of class AI.
     */
    @Test
    public void testEstimateBulletDt() {
        System.out.println("estimateBulletDt");
        AI instance = new AI(true);
        instance.set(0.1);
        double v = instance.getBulletDt() ;
        assertEquals(v,12,0.000001) ;
        
    }
    
}
