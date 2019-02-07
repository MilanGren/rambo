
package rambo;

import rambo.exceptions.WrongTimeStatusException;
import rambo.solver.SolverAbstract ;
import rambo.solver.SolverBasic ;
import rambo.solver.SolverAdvanced ;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import static rambo.Logger.Enemy.* ;
import static rambo.Utils.* ;


public class Enemy {
    
    public double distance, velocity, velocityAI, angle, enemyHeading, alpha, alphaAI = 0, additionalAngle;
    
    public int timeNow ;

    private double dA, dB ;
    
    public double xC, yC, dx, dy, absAngle ; // udelat gettery???
    
    //Map<Double, Double[]> dXYmap = new HashMap<>() ;
    
    public List<Double> dxVec = new ArrayList<>() ;
    public List<Double> dyVec = new ArrayList<>() ;
    public List<Double> xVec = new ArrayList<>() ;
    public List<Double> yVec = new ArrayList<>() ;
    public List<Integer> timeVec = new ArrayList<>() ;
    
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

    private int getHeadingVec(double heading) {

        double[] hding = {Math.sin(Utils.toRad(heading+2)),Math.cos(Utils.toRad(heading+2))} ; //5 aby to nedavalo Nan - HACK
        double[] ddr = {(xVec.get(xVec.size()-1) - xVec.get(xVec.size()-2)),(yVec.get(yVec.size()-1) - yVec.get(yVec.size()-2))} ;
        
        if (Utils.scalar(hding,ddr) < -90 || Utils.scalar(hding,ddr) > 90) {
            return -1 ;
        } else {
            return 1 ;
        }
    }
    
    public void set(double xC, double yC,double dx, double dy, double absAngle,double energy, double heading, long time) {

        //pokud chci najit dx_vec pro dany time point - getTime(), potom musim najit index v time_vec
        //zapisuje se obecne vzdy, kdy nepritele uvidim
        
        this.xC = xC ;
        this.yC = yC ;
        this.dx = dx ;
        this.dy = dy ;
        this.absAngle = absAngle ;
        dxVec.add(dx) ;
        dyVec.add(dy) ;
        xVec.add(xC) ;
        yVec.add(yC) ;
        timeNow = (int) time ;
        timeVec.add((int) time) ;  
        eneVec.add(energy) ;

        
        double dds ;
        if (timeVec.size() == 1) {
            velVec.add(0.0) ;
            //velAveVec.add(0.0) ;
            accelVec.add(0.0) ;
            wasFired.add(false) ;
            accelDirVec.add(0) ;
            headingVec.add(1) ;

        } else {
                
            headingVec.add( getHeadingVec(heading) ) ;

            dds = Math.pow(Math.pow(xVec.get(xVec.size()-1)-xVec.get(xVec.size()-2),2) + Math.pow(yVec.get(yVec.size()-1)-yVec.get(yVec.size()-2),2) , 0.5) ;
            double dtime = timeVec.get(timeVec.size()-1) - timeVec.get(timeVec.size()-2) ; 
            
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
            for (int t: timeVec) {
 
                logEnemy(" time " + t 
                         + " dx " + Utils.round(xVec.get(index),1) 
                         + " dy " + Utils.round(yVec.get(index),1)
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
        int index_actual, index_prev, time_ptr = 0, time_prev = 0;
        if (hitTimeBuffer.size() < 2) {
        } else {
            
            logAIinfo("\n --- STATUS --- " + timeNow) ;
            logAIinfo("hitTimeBuffer " + hitTimeBuffer) ;
            
            List<Integer> hitTimeBufferReduced = hitTimeBuffer.stream()     
                                    .filter(hitTime -> hitTime <= timeNow)  
                                    .collect(Collectors.toList()); 
            
            
            logAIinfo("hitTimeBufferReduced " + hitTimeBufferReduced) ;
            
            
            
            for (int i = 2; i <= hitTimeBufferReduced.size()-1;i++) {
logAIinfo("--") ;
                int hitTime_ptr = hitTimeBufferReduced.get(i) ;
                int hitTime_prev   = hitTimeBufferReduced.get(i-1) ;

// mezi index actual a prev je vzdy vyhodnocena poloha, na kterou se dostal enemy za predikovany cas
// je spoctena alphaAI a stredni rychlost velocityAI - rychlost, aby se dostal o dx a dy za predikovany cas
// tyto hodnoty se muzou porovnat s pouzitymy odhady alpha a velocity - odhady z jednoho bodu

// problem je predikovany cas: dr/bulletVelocity nemusi byt rovna predikovanemu casu

                index_actual = timeVec.indexOf(hitTime_ptr) ; 
                index_prev = timeVec.indexOf(hitTime_prev) ; 

logAIinfo("  time_vec.size() " + timeVec.size())  ;
logAIinfo("  index_actual " + index_actual ) ;

                time_ptr = timeVec.get(index_actual) ;
                time_prev = timeVec.get(index_prev) ; 
                
logAIinfo(" ") ;  
              


                try {
                    if (timeVec.get(index_actual) - timeVec.get(index_prev) != hitTime_ptr - hitTime_prev) {
                        throw new WrongTimeStatusException("") ;
                    }
                } catch (WrongTimeStatusException ex) {
                    System.out.println("System.exit(1)") ;
                    System.exit(1) ;
                }
                    
                double ddx = (xVec.get(index_actual) - xVec.get(index_prev)) ;
                double ddy = (yVec.get(index_actual) - yVec.get(index_prev)) ;
                
                double[] vector_to_position_before_fire = {dxVec.get(index_prev)*headingVec.get(index_prev),dyVec.get(index_prev)*headingVec.get(index_prev)} ;
                double[] vector_change_direction = {ddx,ddy} ;
                
                alphaAI = Utils.scalar( vector_to_position_before_fire, vector_change_direction) ;
                velocityAI = Utils.sqrtform(ddx,ddy)/(time_ptr - time_prev)*headingVec.get(index_prev) ; 
                
                //POZOR ! alphaMap je HashMap 
                
                logAIinfo("alphaAI " + round(alphaAI,1)    + " alpha " + round(alphaMap.get(time_prev),1)) ;
                logAIinfo("velocityAI " + round(velocityAI,1) + " velocity " + round(velVec.get(index_prev),1)) ;
                logAIinfo("di " + (alphaMap.get(time_prev)-alphaAI));
                logAIinfo("enemy heading " + headingVec.get(index_prev)) ;

            }
            
            logAIinfo("last alpha used " + round(alpha,1)) ;
            
        }
    }

    
    public double[] direction() {
        double[] vec = {dx,dy} ;
        return vec ;
    }
    
    public double getAdditionalAngle() {
        return this.additionalAngle ;
    }
    

    private double timeForBullet(double distance,double bulletVelocity) {
        return distance/bulletVelocity ;
    }
    

    public void fin(double bulletVelocity,int getTime) {
        SolverAbstract solver = new SolverAdvanced(this, 0, 0, bulletVelocity) ;
        //SolverAbstract solver = new SolverBasic(this, 0, 0, bulletVelocity) ;
        
        while (solver.epsilon > 1) {
            solver.solve() ;
        }
        
        predictedHitTime = (int) solver.solve() + getTime  ; 
        this.additionalAngle = solver.additionalAngle ;
                
    }
    
    
    
}
