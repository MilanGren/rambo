/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rambo.interpolator;

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
public class InterpolatorTest {
    
    public InterpolatorTest() {
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
     * Test of bracket method, of class Interpolator.
     */
    @Test
    public void testBracket() {
        System.out.println("bracket");
        double[] x = {0,1,2,3} ;
        double[] y = {1,1,4,-2} ;
        Interpolator instance = new Interpolator(x,y) ;
        int result = instance.bracket(0.1);
        assertEquals(0, result);
    }
    
    @Test
    public void testInterpolate() {
        System.out.println("interpolate");
        double[] x = {0,1,2,3} ;
        double[] y = {1,1,4,-2} ;
        Interpolator instance = new Interpolator(x,y) ;
        double result = instance.interpolate(2.1);
        assertEquals(3.4, result,0.0001) ;
    }
    
    @Test
    public void testInterpolateOutLeft() {
        System.out.println("interpolate out left");
        double[] x = {0,1,2,3} ;
        double[] y = {1,1,4,-2} ;
        Interpolator instance = new Interpolator(x,y) ;
        double result = instance.interpolate(-5);
        assertEquals(1.0, result,0.0001) ;
    }
    
    @Test
    public void testInterpolateOutRight() {
        System.out.println("interpolate out right");
        double[] x = {0,1,2,3} ;
        double[] y = {1,1,4,-2} ;
        Interpolator instance = new Interpolator(x,y) ;
        double result = instance.interpolate(10.0);
        assertEquals(-44, result,0.0001) ;
    }
    
    @Test
    public void testInterpolate2() {
        System.out.println("interpolate2");
        double[] x = {0,1,2,3} ;
        double[] y = {1,1,4,-2} ;
        Interpolator instance = new Interpolator(x,y) ;
        double result = instance.interpolate(5.1);
        result = instance.interpolate(2.1);
        assertEquals(3.4, result,0.0001) ;
    }
    
}
