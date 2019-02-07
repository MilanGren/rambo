

package rambo.solver;

import java.util.List;
import static rambo.Logger.* ;
import rambo.Enemy;
import rambo.Utils;



//vstupem je bulletVelocity, distanceOriginal, enemyVelocity
// aktualizuju distanceActual, additionalAngle (TODO: mel bych asi aktualizovat i velocity)
//   na tomto zaklade aktualizuju i dt


public class SolverBasic extends SolverAbstract {

    public SolverBasic(Enemy enemy, double dA, double dB, double bulletVelocity) {
        super(enemy, dA, dB, bulletVelocity);
    }

    @Override
    public double solve() {
        double alpha = enemy.alpha; //nebo enemy.alphaAI
        double enemyVelocity = enemy.velocity ; //nebo enemy.velocityAI
       
        
        // vzdycky se aktualizuje uhel a tim i vzdalenost
        
        distanceActual = Math.pow(Math.pow(distanceOriginal + dA,2) + Math.pow(dB,2),0.5) ;
        
        logSolver("\nNEW-- " + i + " -- distance " + distanceActual) ;
        double dt = timeForBullet(distanceActual,bulletVelocity) ;
        double ds = dt*enemyVelocity ;//this.velVec.get(velVec.size()-1) ; //kolik nepritel ujede za cas dt

        logSolver("                                                   " + enemy.alpha + ", " + ds + ", " + distanceActual) ;
        dA = Math.cos(Utils.toRad(alpha))*ds ; 
        dB = Math.sin(Utils.toRad(alpha))*ds ;
        logSolver("  => dA " + dA) ;
        logSolver("  => dB " + dB) ;

        double projection = distanceOriginal + dA ; 
        

        additionalAnglePrev = additionalAngle ;
        additionalAngle = Utils.toDeg(-Math.atan(dB/projection)) ;
        epsilon = Math.abs(additionalAnglePrev - additionalAngle) ;
        
    
        logSolver("  dB " + dB + "  projection " + (projection)) ;
        logSolver("  dB/projection " + (dB/projection)) ;
        //log("fixing additional angle " + (dB/predicted)) ;
        logSolver("  fixing additional angle " + additionalAngle) ; 
        i++ ;
        logSolver("\n") ;
        
        return dt ;
    }


}
