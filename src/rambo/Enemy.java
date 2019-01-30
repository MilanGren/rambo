/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rambo;

import rambo.exceptions.WrongTimeStatusException;
import rambo.solver.SolverAbstract ;
import rambo.solver.SolverBasic ;
import rambo.solver.SolverAdvanced ;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


import static logger.Logger.Enemy.* ;
import static rambo.Utils.* ;

/**
 *
 * @author gre
 */
public class Enemy {
    
    public double distance, velocity, velocityAI, angle, enemyHeading, alpha, alphaAI = 0, additionalAngle;
    
    public int timeNow ;
    
    
    
    private double dA, dB ;
    
    public double xC, yC, dx, dy, absAngle ; // udelat gettery???
    
    //Map<Double, Double[]> dXYmap = new HashMap<>() ;
    
    public List<Double> dx_vec = new ArrayList<>() ;
    public List<Double> dy_vec = new ArrayList<>() ;
    public List<Double> xC_vec = new ArrayList<>() ;
    public List<Double> yC_vec = new ArrayList<>() ;
    public List<Integer> time_vec = new ArrayList<>() ;
    
    public List<Double> eneVec = new ArrayList<>() ;
    public List<Boolean> wasFired = new ArrayList<>() ;
    public List<Double> velVec = new ArrayList<>() ;
    public List<Integer> headingVec = new ArrayList<>() ;
    //List<Double> velAveVec = new ArrayList<>() ;
    public List<Double> accelVec = new ArrayList<>() ;
    public List<Integer> accelDirVec = new ArrayList<>() ;
    
    
    public Map<Integer, Double> alphaMap = new HashMap<>() ;
    
    public List<Integer> hitTimeBuffer = new ArrayList<>() ;
    int predictedHitTime ;
    
    
    /*
    public void setTime(long time) {
        timeNow = time ;
        time_vec.add((int) time) ;  //cas se zapisuje vzdy, kdy nepritele uvidim
    }
    */
    private int getHeadingVec(double heading) {

        double[] hding = {Math.sin(Utils.toRad(heading+2)),Math.cos(Utils.toRad(heading+2))} ; //5 aby to nedavalo Nan - HACK
        double[] ddr = {(xC_vec.get(xC_vec.size()-1) - xC_vec.get(xC_vec.size()-2)),(yC_vec.get(yC_vec.size()-1) - yC_vec.get(yC_vec.size()-2))} ;
        
        if (Utils.scalar(hding,ddr) < -90 || Utils.scalar(hding,ddr) > 90) {
            return -1 ;
        } else {
            return 1 ;
        }
    }
    
    public void set(double xC, double yC,double dx, double dy, double absAngle,double energy, double heading, long time) {
        
        //time_vec jde vzdy s dx_vec a dy_vec
        
        //pokud chci najit dx_vec pro dany time, potom musim najit index v time_vec
        
        this.xC = xC ;
        this.yC = yC ;
        this.dx = dx ;
        this.dy = dy ;
        this.absAngle = absAngle ;
        dx_vec.add(dx) ;
        dy_vec.add(dy) ;
        xC_vec.add(xC) ;
        yC_vec.add(yC) ;
        timeNow = (int) time ;
        time_vec.add((int) time) ;  //cas se zapisuje vzdy, kdy nepritele uvidim
        eneVec.add(energy) ;

        
        double dds ;
        if (time_vec.size() == 1) {
            velVec.add(0.0) ;
            //velAveVec.add(0.0) ;
            accelVec.add(0.0) ;
            wasFired.add(false) ;
            accelDirVec.add(0) ;
            headingVec.add(1) ;

        } else {
                
            
            headingVec.add( getHeadingVec(heading) ) ;
            
            dds = Math.pow( Math.pow(xC_vec.get(xC_vec.size()-1)-xC_vec.get(xC_vec.size()-2),2) + Math.pow(yC_vec.get(yC_vec.size()-1)-yC_vec.get(yC_vec.size()-2),2) , 0.5) ;
            double dtime = time_vec.get(time_vec.size()-1) - time_vec.get(time_vec.size()-2) ; 
            
            
            velVec.add(dds/dtime*velocity/Math.abs(velocity)); //zde je HACKem vyresen problem +/- rychlosti
            
            
            double v0 = velVec.get(velVec.size()-2) ; //musi byt ZA pridanim do velVec
            double vAve = (velVec.get(velVec.size()-1) + velVec.get(velVec.size()-2))/2 ;
            
            //velAveVec.add( vAve ) ;
                    
            double accel = 2*(vAve-v0)/dtime ;
            accelVec.add(accel) ;
            
            if (Math.abs(accel) < 0.00001) {
                logEnemy(" no speed change");
                accelDirVec.add(0) ;
            } else if (accel*vAve < 0) {
                logEnemy(" sloving down");
                accelDirVec.add(-1) ;
            } else {
                logEnemy(" accelerating");
                accelDirVec.add(1) ;
            }
            
            
           
            
            if ( Math.abs(eneVec.get(eneVec.size()-1)-eneVec.get(eneVec.size()-2) ) > 1) {
                wasFired.add(true) ;
            } else {
                wasFired.add(false) ;
            }
            
            logEnemy("AI enemy listing") ;
            int index = 0 ; 
            for (int t: time_vec) {
 
                logEnemy(" time " + t 
                         + " dx " + Utils.round(xC_vec.get(index),1) 
                         + " dy " + Utils.round(yC_vec.get(index),1)
                         + " vel " + Utils.round(velVec.get(index),2)
                         //+ " velAve " + Utils.round(velAveVec.get(index),2)
                         + " accel " + Utils.round(accelVec.get(index),2)
                         + " accelDir " + Utils.round(accelDirVec.get(index),2)
                         + " energy " + Utils.round(eneVec.get(index),2)
                         + " wasFired " + wasFired.get(index) ) ;
                index++ ;
            }
        }
    }
    
    public void setForFire(double distance, double velocity, double angle, double enemyHeading) {
        this.distance = distance ;
        this.velocity = velocity ;
        this.angle = angle ;     //absolute - odklon nepritele od Y souradnice
        this.enemyHeading = enemyHeading ;
        //alpha =  normalizeBearing(this.angle - this.enemyHeading) ; // uhel, pod kterym nepritel unika v ramci spojnice ja-nepritel
        alpha =  normalizeBearing(this.angle - this.enemyHeading) ; // uhel, pod kterym nepritel unika v ramci spojnice ja-nepritel
        //alpha += alphaFixing ;
        logEnemy("velocity " + velocity) ;
        logEnemy("angle " + normalizeBearing(angle)) ;
        logEnemy("alpha " + normalizeBearing(alpha)) ;
        logEnemy("heading " + enemyHeading) ;
        logEnemy("heading relative to alpha " + normalizeBearing(alpha) ) ;
        alphaMap.put(timeNow,alpha) ;
        //System.out.println("alpha " + normalizeBearing(alpha) + "for time " + timeNow) ;
    }
   
  
    
    public void statusInfo(int timeNow) {
        double ddxMean = 0, ddyMean = 0, ddrMean = 0, dTimeMean = 0 ;
        
        //ddxMean: dx2 - dx1 .. zmena vzalenosti mezi tankama
        //  pokud konstantni, potom se vzalenost nemeni
        
        //ddr
        int index_actual, index_prev, time_actual = 0, time_prev = 0;
        if (hitTimeBuffer.size() < 2) {
        } else {
            
            logAIinfo("\n --- STATUS --- " + timeNow) ;
            logAIinfo("hitTimeBuffer " + hitTimeBuffer) ;
            
            List<Integer> hitTimeBufferReduced = hitTimeBuffer.stream()     
                                    .filter(line -> line <= timeNow)  
                                    .collect(Collectors.toList()); 
            
            
            logAIinfo("hitTimeBufferReduced " + hitTimeBufferReduced) ;
            
            
            
            for (int i = 2; i <= hitTimeBufferReduced.size()-1;i++) {

                int hitTime_actual = hitTimeBufferReduced.get(i) ;
                int hitTime_prev   = hitTimeBufferReduced.get(i-1) ;
   
                index_actual = time_vec.indexOf(hitTime_actual) ; 
                index_prev = time_vec.indexOf(hitTime_prev) ; 
            
                time_actual = time_vec.get(index_actual) ;
                time_prev = time_vec.get(index_prev) ; 
                
                
                
                
                dTimeMean += time_actual - time_prev ;

                try {
                    if (time_vec.get(index_actual) - time_vec.get(index_prev) != hitTime_actual - hitTime_prev) {
                        throw new WrongTimeStatusException("") ;
                    }
                } catch (WrongTimeStatusException ex) {
                    Logger.getLogger(Enemy.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("System.exit(1)") ;
                    System.exit(1) ;
                }
                    
                double ddx = (xC_vec.get(index_actual) - xC_vec.get(index_prev)) ;
                double ddy = (yC_vec.get(index_actual) - yC_vec.get(index_prev)) ;
                
                ddxMean += ddx ;
                ddyMean += ddy ;
                ddrMean += Utils.sqrtform(ddx,ddy) ; 
                
                double[] vector_to_position_before_fire = {dx_vec.get(index_prev)*headingVec.get(index_prev),dy_vec.get(index_prev)*headingVec.get(index_prev)} ;
                double[] vector_change_direction = {ddx,ddy} ;
                
                alphaAI = Utils.scalar( vector_to_position_before_fire, vector_change_direction) ;
                velocityAI = Utils.sqrtform(ddx,ddy)/(time_actual - time_prev)*headingVec.get(index_prev) ; 
                
                logAIinfo("alphaAI " + round(alphaAI,1)    + " alpha " + round(alphaMap.get(time_prev),1)) ;
                logAIinfo("last alpha " + round(alpha,1)) ;
                logAIinfo("velocityAI " + round(velocityAI,1) + " velocity " + round(velVec.get(time_prev),1)) ;
                //logAIinfo("di " + (alphaMap.get(time_prev)-alphaAI));
                //  logAIinfo("enemy heading " + headingVec.get(time_prev)) ;
                //alphaFixing = alphaAI - alphaMap.get(time_prev) ;
            }
 
            
            
            /*
            ddrMean = ddrMean/(hitTimeBufferReduced.size()-2) ; 
            ddxMean = ddxMean/(hitTimeBufferReduced.size()-2) ; 
            ddyMean = ddyMean/(hitTimeBufferReduced.size()-2) ;
            dTimeMean = dTimeMean/(hitTimeBufferReduced.size()-2) ;
            */
            //double[] ddrVec = {ddxMean,ddyMean} ;
            
            //logAIinfo("1 ddrMean " + ddrMean + " velocity " + velocityAI + " dTimeMean " + dTimeMean) ;
            //logAIinfo("2 ddrMean " + Utils.sqrtform(ddxMean,ddyMean) + "\n") ;
            
        }
    }

    
    public double[] direction() {
        double[] vec = {dx,dy} ;
        return vec ;
    }
    
    public double getAdditionalAngle() {
        return this.additionalAngle ;
    }
    
    double normalizeBearing(double angle) {
	while (angle >  180) angle -= 360;
	while (angle < -180) angle += 360;
	return angle;
    }
    
    private double timeForBullet(double distance,double bulletVelocity) {
        double timeForBullet = distance/bulletVelocity ;
        //log("bullet velocity " + bulletVelocity) ;
        //log("distance " + distance) ;
        //log("enemy time for bullet " + timeForBullet) ;
        return timeForBullet ;
    }
    

    public void fin(double bulletVelocity,int getTime) {
        //SolverAbstract solver = new SolverAdvanced(this, 0, 0, bulletVelocity) ;
        SolverAbstract solver = new SolverBasic(this, 0, 0, bulletVelocity) ;
        solver.solve() ;
        solver.solve() ;
        solver.solve() ;
        solver.solve() ;
        solver.solve() ;
        
        predictedHitTime = (int) solver.solve() + getTime  ; 

        this.additionalAngle = solver.additionalAngle ;
                
    }
    
    
    
}
