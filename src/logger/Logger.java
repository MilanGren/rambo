/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logger;

/**
 *
 * @author gre
 */
public class Logger {
    
    
    public static class Rambo {
        
        public static <T> void logMove(T t) {
    //    System.out.println("Move " + t) ;
        }
    
        public static <T> void logFire(T t) {
    //    System.out.println("Fire" + t) ;
        }
    
        public static <T> void logRadar(T t) {
        //System.out.println("Radar " + t) ;
        }
    
        public static <T> void log(T t) {
    //    System.out.println(t) ;
        }
    }
    
    public static class Enemy {
        
        public static <T> void logEnemy(T t) {
            //System.out.println("Enemy " + t) ;
        }
        
        public static <T> void logAIinfo(T t) {
            //System.out.println("info " + t) ;
        }

    }
    
    
    
    public static <T> void logUtils(T t) {
        //System.out.println("Utils " + t) ;
    }

    
    public static <T> void logSolver(T t) {
        //System.out.println("Solver " + t) ;
    }
    
    
    
}
