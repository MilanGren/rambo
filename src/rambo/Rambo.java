


package rambo;

import robocode.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static rambo.Logger.Rambo.* ;
import static rambo.Utils.normalizeBearing ;




public class Rambo extends AdvancedRobot {
 
    double wallSurfaceAngle ; //uhel potoceni vuci smeru nahoru
  
    double testingRandomAngle = 45 ;
    
    boolean firstRingReached = false, enemyWasFoundFirstTime = false ;
    
    int moveDirection = -1 ;
    
    boolean tooCloseToWall = false, setBodyToEnemy = true, approachingEnemy = true ;
    
    List<Integer> hitsReceived = new ArrayList<>() ;
    
    Enemy enemy = new Enemy() ;
    
    AI ai = new AI() ;
    
    Utils.Randomer randomer = new Utils.Randomer() ; 
    
    /////////////////////////////////////////////////////
    /////////////////////////////////////////////////////
    
    public void run() {
		// Initialization of the robot should be put here
        addCustomEvent(new Condition("too_close_to_walls") {
            public boolean test() {
                boolean bol ;
                String fromWhichSide = "" ;
                if (getX() <= ai.WALLMARGIN) {
                    fromWhichSide = "left" ;
                    wallSurfaceAngle = 90 ;
                } else if (getX() >= getBattleFieldWidth() - ai.WALLMARGIN) {
                    bol = true ;
                    fromWhichSide = "right" ;
                    wallSurfaceAngle = -90 ;
                } else if (getY() <= ai.WALLMARGIN) {
                    wallSurfaceAngle = 0 ;
                    fromWhichSide = "bottom" ;
                } else if (getY() >= getBattleFieldHeight() - ai.WALLMARGIN) {
                    wallSurfaceAngle = 180 ;
                    fromWhichSide = "top" ;
                }
                
                if (!fromWhichSide.equals("")) {
                    log("\n  too_close_to_walls " + fromWhichSide + "\n") ;
                } 
                
		return (
                    // we're too close to the left wall
                    (getX() <= ai.WALLMARGIN ||
                    // or we're too close to the right wall
                    getX() >= getBattleFieldWidth() - ai.WALLMARGIN ||
                    // or we're too close to the bottom wall
                    getY() <= ai.WALLMARGIN ||
                    // or we're too close to the top wall
                    getY() >= getBattleFieldHeight() - ai.WALLMARGIN)
		);
            }
	}) ;
        

        setColors(Color.blue,Color.yellow,Color.white); // body,gun,radar
        setAdjustRadarForGunTurn(true) ;
        setAdjustGunForRobotTurn(true) ;
        
        int round = 0 ;
        
        while(true) {
            log("---------- BOC ") ;
            if (enemy.timeVec.size() > 0) {
                log("-- main time " + getTime() + " enemy time " + enemy.timeVec.get(enemy.timeVec.size()-1) + " --") ;
            } else {
                log("main time " + getTime()) ;
            }
            
            
            // TESTING FOR GUN AI
            if (enemy.hitTimeBuffer.size() > 0 && enemy.hitTimeBuffer.contains( (int) getTime() ) ) {  //pokud nastal cas.. 

                log(" last in hitTimeBuffer " + enemy.hitTimeBuffer.get(enemy.hitTimeBuffer.size()-1)) ;

                //vzdy me zajima stredni rychlost a uhel odjezdu mezi dvema predpokladanymy casy dopadu

                //1. porovnavam alpha a alphaAI za predikovany cas. lepsi nez predikovany cas NEMAM

                //2. porovnavam stredni rychlosti za predikovany cas.

                //    zajima me finalni ddx, ddy - zbran nepotrebuje vedet, jak se tam dostal.. 

                //enemy.statusInfo((int) getTime()) ;

            }

            ai.xVec.add(getX()) ;
            ai.yVec.add(getY()) ;
            ai.dtime += 1 ; 
            
            doMove(); 
            round++ ;
            execute() ;

        }
    }
    
    private void doMove() {
        
        setAhead(1000*moveDirection) ;

        if (ai.dtime > randomer.get()) {
            testingRandomAngle = randomer.get() ;
            logMove( "         random " + randomer.get() + "testingAngle " + testingRandomAngle + " moveDirection " + moveDirection + "\n") ;
            moveDirection *= randomer.getIntBooleanRandom() ;
            randomer.reset() ;
            ai.dtime = 0 ;
        }
        ai.dtime++ ;
        
        
        if (!enemyWasFoundFirstTime) { //da se toho zbavit?
            setTurnRadarRight(360) ;
        } else {
            double[] radar_direction = {Math.sin(getRadarHeadingRadians()),Math.cos(getRadarHeadingRadians())} ;
            double dire = Utils.angleDirection(enemy.direction(),radar_direction) ;
            double angle = Utils.scalar(enemy.direction(),radar_direction) ;
            logRadar(".... angle to enemy " + angle) ;
            logRadar(".... direction to enemy " + dire) ;
            setTurnRadarRight(angle*1.5) ;
        }
        
        if (tooCloseToWall) {    
            setMaxVelocity(Rules.MAX_VELOCITY*0.7);
            logMove("1: holdSettingToBullet " + setBodyToEnemy) ;
            double moveLeftBy = normalizeBearing(getHeadingInvariant() - wallSurfaceAngle) ;
      
            logMove("1: moving left " + moveLeftBy ) ;
            setTurnLeft(moveLeftBy) ;
            
            if (Math.abs(getTurnRemaining()) < 1) {
                logMove("1: ESCAPED WALL " + tooCloseToWall) ;
                tooCloseToWall = false ;
                setBodyToEnemy = true ;
                approachingEnemy = true ;
            }

        } else {
            setMaxVelocity(Rules.MAX_VELOCITY);
//            setMaxVelocity(0) ;

        }
        
    }
   
    
  
    public void setFireMode(ScannedRobotEvent e) {
        double distance = e.getDistance() ;
        double e_bearing = getAngleInvariant(e.getBearing()) ;
        
        enemy.setForFire(distance,e.getVelocity(),normalizeBearing((getHeadingInvariant() + e_bearing)),e.getHeading()) ;
        
        ai.setFirePower(distance) ; // 
        
        double firePower = ai.getFirePower() ;
        double bulletSpeed = ai.getBulletSpeed() ; 
        
        //double firePower = 1 ;
        //double bulletSpeed = 20 - 3*firePower ;
        
        enemy.fin(bulletSpeed,(int) getTime()) ; //
      
        setGun(e_bearing + enemy.getAdditionalAngle()) ;
        
        logFire("setGun " + (e_bearing + enemy.getAdditionalAngle()) ) ;
        logFire("getGunTurnRemaining " + getGunTurnRemaining()) ;
        
        if (getGunHeat() <= 0 && Math.abs( getGunTurnRemaining() ) < 3 && ai.allowFire) {
            double energy = getEnergy() ;
            logFire("fire!") ;
            logFire(" firePower " + firePower + " bulletSpeed " + bulletSpeed) ;
            
            setFire(firePower) ;
            //double gh = 1+firepower/5 ;
            enemy.hitTimeBuffer.add( enemy.predictedHitTime ) ; //asi HACK - prasarna
        } else {
        //    logFire("can not fire ........ gunHeat > 0 " + getGunHeat()) ;
        }

    }
    

    
    public void onScannedRobot(ScannedRobotEvent e) {
        double distance = e.getDistance() ;

        double absAngle = getHeadingRadians() + e.getBearingRadians() ; 
                        
        double dx = distance*Math.sin(absAngle) ;
        double dy = distance*Math.cos(absAngle) ;
        
        enemy.set(getX() + dx,getY() + dy,dx,dy,absAngle,e.getEnergy(),e.getHeading(),getTime()) ;
        enemyWasFoundFirstTime = true ;

        double e_bearing = getAngleInvariant(e.getBearing()) ;
        logRadar("onScanned: enemy relative angle " + e_bearing + ", absolute angle " + normalizeBearing(Utils.toDeg(absAngle))) ; // stejne jako normalizeBearing((getHeadingInvariant() + e_bearing))
        
        if (distance < ai.FIRSTRINGRADIUS && !firstRingReached) {
            logRadar("onScanned 1: reached " + ai.FIRSTRINGRADIUS) ;
            logRadar("onScanned 1: setting body by " + e_bearing) ;
            setBodyToEnemy(e_bearing,distance,15) ;
            setFireMode(e) ;
            firstRingReached = true ;
        }
        
        else if (distance < ai.SECONDRINGRADIUS && firstRingReached) { // pokracuj v prvnim bode, dokud distance < secondRingRadiu
            logRadar("onScanned 2a: withing distance " + ai.SECONDRINGRADIUS + " distance " + distance) ;
            logRadar("onScanned 2a: setting body by " + e_bearing) ;
            setBodyToEnemy(e_bearing,distance,30) ;
            setFireMode(e) ;
            
        } else if (distance < ai.SECONDRINGRADIUS && !firstRingReached) {
            logRadar("onScanned 2b: withing distance " + ai.SECONDRINGRADIUS + " distance " + distance) ;
            logRadar("onScanned 2b: setting body by " + e_bearing) ;
            setApproachingEnemy(e_bearing);
            setFireMode(e) ;
        }
        
        else {
            logRadar("onScanned 3: approaching enemy ... distance = " + distance) ;
            firstRingReached = false ;
            approachingEnemy = true ;
            //setFireMode(e) ;
            setApproachingEnemy(e_bearing);
        }
    }

    private void setApproachingEnemy(double e_bearing) {
        if (setBodyToEnemy) {
            //setTurnRight( e_bearing + testingAngle) ; 
            //logRadar("  testingAngle " + testingRandomAngle) ;
            setTurnRight(e_bearing + testingRandomAngle) ; 
        }
    }
   
    private void setBodyToEnemy(double b,double distance,double fixingDBabs) {
//log("setBodyPerpendicularlyToBullet hold  = " + holdSettingToBullet) ;        
        if (setBodyToEnemy) {
        
            double angle = 0;
            double bfixed = 0;
            double anglefixed = 0 ;
            double fixingdb = 0;
            
            logMove("setting body to enemy") ;
            logMove("  " + distance + " / " + ai.FIRSTRINGRADIUS) ;
            
            if (distance < ai.FIRSTRINGRADIUS && b>= 0) { //chci se oddalit
                    fixingdb = fixingDBabs ; //kladny uhel doleva
                    logMove("doMove:   1 get away") ;
            } else if (distance > ai.FIRSTRINGRADIUS && b>= 0) { //chci se priblizit
                    fixingdb = -fixingDBabs ; //zaporny uhel doleva
                    logMove("doMove:  2 get closer") ;
            } else if (distance < ai.FIRSTRINGRADIUS && b < 0) { //chci se oddalit
                    fixingdb = -fixingDBabs ; // 
                    logMove("doMove:  3 get away") ;
            } else if (distance > ai.FIRSTRINGRADIUS && b < 0) {
                    fixingdb = fixingDBabs ; // 
                    logMove("doMove:  4 get closer") ;
            }

            angle = getAngleToEnemyDefault(b) ;     
            logMove("body by angle: no   fix " + angle) ;
            angle = angle + fixingdb ;
            logMove("body by angle: with fix " + angle) ;
            setTurnLeft(angle) ;

        } else {
            logMove("holding setting perp to bullet: " + setBodyToEnemy) ;
        }
               
    }
    
    public double getAngleToEnemyDefault(double b) {
        double angle = 0 ;
        if (b <= 0) {
            if ( b >= -90) { // -90 .. 0
                angle = 90 + b ;
                logMove("  1 right " + angle) ;
                angle = -angle ;
            } else {
                angle = -b - 90 ;
                logMove("  2 left " + angle) ;
            }            
        }
        else {              
            if ( b <= 90) {
                  angle = (90 - b) ;
                logMove("  3 left " + angle) ;
            } else {
                angle = (b - 90) ;
                logMove("  4 right " + angle) ;
                angle = -angle ; 
            }
        }
        return angle ;
    }
    
    
    public void onHitByBullet(HitByBulletEvent e) {     
        ai.hitVec.add((int) getTime()) ;
        moveDirection *= randomer.getIntBooleanRandom() ;
    }
    
    private double getAngleInvariant(double angle) { 
        
        hitsReceived.add(1) ;
        
        double out ;
        if (moveDirection == -1) {
            out = normalizeBearing(angle + 180) ;
        } else {
            out = normalizeBearing(angle) ;
        }
        return out ;
    }
    
    private double gunToBody() {
        double angle = getGunHeading() - getHeadingInvariant() ;
        return normalizeBearing(angle) ;
    }

    public void onHitWall(HitWallEvent e) {
        this.moveDirection *= -1 ;
    }

    
    public void onCustomEvent(CustomEvent e) {
	if (e.getCondition().getName().equals("too_close_to_walls")) {
            if (!tooCloseToWall) {
                log("  wallSurfaceAngle " + wallSurfaceAngle + "\n") ;
                tooCloseToWall = true ;
                setBodyToEnemy = false ;
                approachingEnemy = false ;
            }
	}
    }
    
    private double getHeadingInvariant() {
        double out ;
        if (moveDirection == -1) {
            out = normalizeBearing(getHeading()+180) ;
        } else {
            out = normalizeBearing(getHeading()) ;
        }
        return out ;
    }
    
      
    private void setGun(double angle) {
        logFire("targetting") ;
        //logFire("  gunToBody " + gunToBody() + " bullet from " + angle) ;
        double moveGunBy = angle - gunToBody() ;
        logFire("  moveGunBy " + moveGunBy) ;
        setTurnGunRight(normalizeBearing(moveGunBy)) ;
        //turnGunRight(normalizeBearing(moveGunBy)) ;
        
    }
    

}

