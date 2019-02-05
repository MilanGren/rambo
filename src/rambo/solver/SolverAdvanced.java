
package rambo.solver;

import java.util.List;
import static rambo.Logger.*;
import rambo.Enemy;
import rambo.Utils;


public class SolverAdvanced extends SolverAbstract {

    public SolverAdvanced(Enemy enemy, double dA, double dB, double bulletVelocity) {
        super(enemy, dA, dB, bulletVelocity);
    }

    
    @Override
    public double solve() {
        double alpha = enemy.alpha; //nebo enemy.alphaAI
                
        distanceActual = Math.pow(Math.pow(this.distanceOriginal + dA,2) + Math.pow(dB,2),0.5) ;
        
        logSolver("\nNEW-- " + this.i + " -- distance " + this.distanceActual) ;
        double dt = timeForBullet(this.distanceActual,this.bulletVelocity) ;
        double dsOld = dt*enemy.velocity ;//this.velVec.get(velVec.size()-1) ; //kolik nepritel ujede za cas dt
      
        
        double v0 = enemy.velVec.get(enemy.velVec.size()-1) ; //zde je rozdil oprovi v0 v Enemy - zde predikuji
        double accel = enemy.accelVec.get(enemy.accelVec.size()-1) ;
        int accelDir = enemy.accelDirVec.get(enemy.accelDirVec.size()-1) ;
        
        
        double vMax = 8 ;
        double vMin = 0 ;
        
        double dtM ;
        double ds ;
        
        
        
        if (accelDir == 0) {
            logSolver("0 accel " + Utils.round(accel,3)) ;
            dtM = 0 ;
            ds = v0*dt ;
        } else if (accelDir < 0) { // potom predpokladam zpomalovani
            logSolver("1 sloving down " + accel) ;
            dtM = (Math.abs(v0) - vMin)/Math.abs(accel) ;
            logSolver("dtM " + Utils.round(dtM,1)) ;
            if (dt <= dtM) {
                ds = 0.5*accel*dt*dt + v0*dt ; 
                logSolver("ds only a " + ds) ;
            } else {
                ds = 0.5*accel*dtM*dtM + v0*dtM ;
                logSolver("ds a " + ds) ;
                ds += vMin*(dt-dtM) ;
                logSolver("ds tot " + ds) ;
            }   
        } else { // zrychlovani
            logSolver("2 accelerating " + accel) ;
            dtM = (vMax - Math.abs(v0))/Math.abs(accel) ;
            logSolver("dtM " + Utils.round(dtM,1)) ;
            if (dt <= dtM) {
                ds = 0.5*accel*dt*dt + v0*dt ; 
                logSolver("ds only a " + ds) ;
            } else {
                ds = 0.5*accel*dtM*dtM + v0*dtM ;
                logSolver("ds a " + ds) ;
                ds += vMax*(dt-dtM)*Math.abs(enemy.velocity)/enemy.velocity ;
                logSolver("ds tot " + ds) ;
            }
        }
        
        logSolver("dtM " + dtM) ;
        logSolver("dt " + Utils.round(dt,1) + " a " + Utils.round(accel,2) + " ds " + Utils.round(ds,1) + " dsOld " + Utils.round(dsOld,1)) ;
        logSolver("velocity " + enemy.velocity + " velVec last " + Utils.round(enemy.velVec.get(enemy.velVec.size()-1),2)) ;
        
        
// this.velocity nahradit nejakou stredni hodnotou pres budoucnost
// this.velocity * <velocity>
// 

        //log("  enemy: dt " + dt + " ds " + ds + " alpha deg,rad " + alpha + " , " + toRad(alpha)) ;
        logSolver("                                                   " + alpha + ", " + ds + ", " + distanceActual) ;
        dA = Math.cos(Utils.toRad(alpha))*ds ; 
        dB = Math.sin(Utils.toRad(alpha))*ds ;
        logSolver("  => dA " + dA) ;
        logSolver("  => dB " + dB) ;
        double projection = this.distanceOriginal + dA ; 
        
        this.additionalAngle = Utils.toDeg(-Math.atan(dB/projection)) ;
        
        
        logSolver("  dB " + dB + "  projection " + (projection)) ;
        logSolver("  dB/projection " + (dB/projection)) ;
        //log("fixing additional angle " + (dB/predicted)) ;
        logSolver("  fixing additional angle " + this.additionalAngle) ; 
        this.i++ ;
        logSolver("\n") ;
        return dt ;
    }


}
