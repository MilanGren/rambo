/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rambo;

import robocode.*;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * Rambo - a robot by (your name here)
 */

//TODO - dodelat silu strely jako funkci polohy - cim dal, tim slabsi strela

public class Rambo extends AdvancedRobot {
    
    public <T> void logMove(T t) {
    //    System.out.println(t) ;
    }
    
    public <T> void logFire(T t) {
        System.out.println(t) ;
    }
    
    public <T> void logRadar(T t) {
    //    System.out.println(t) ;
    }
    
    public <T> void log(T t) {
    //    System.out.println(t) ;
    }
    
    private static final double WALLMARGIN = 50; //150
    private static final double tooCloseToWallDescrement = 3 ;
    private static final double firstRingRadius = 200 ;
    private static final double secondRingRadius = 500 ;
    private static final double FIXINGDB = 12 ;
    
    
    //String fromWhichSide = "" ;
    double wallSurfaceAngle ; //uhel potoceni vuci smeru nahoru
            
    
    //boolean escapingStartingAngleOpened ; //pokud true, potom muzu zapsat hodnotu do setRemainingAngle
    //double startingEscapingAngle ;
    
    double epsilon = 1e-8 ; boolean tankFoundFirstTime = false ;
   
    boolean moveGRtogether, firstRingReached = false, holdSettingBodyToBullet = false, approachingEnemy ;
    
    int moveDirection = -1 ;
    
    
    
    private int tooCloseToWall = 0;
    
    Map<Double, Double> map = new HashMap<>() ;
    
    Enemy enemy = new Enemy() ;
    
    public void run() {
		// Initialization of the robot should be put here
        
        addCustomEvent(new Condition("too_close_to_walls") {
            public boolean test() {
                boolean bol ;
                String fromWhichSide = "" ;
                if (getX() <= WALLMARGIN) {
                    fromWhichSide = "left" ;
                    wallSurfaceAngle = 90 ;
                } else if (getX() >= getBattleFieldWidth() - WALLMARGIN) {
                    bol = true ;
                    fromWhichSide = "right" ;
                    wallSurfaceAngle = -90 ;
                } else if (getY() <= WALLMARGIN) {
                    wallSurfaceAngle = 0 ;
                    fromWhichSide = "bottom" ;
                } else if (getY() >= getBattleFieldHeight() - WALLMARGIN) {
                    wallSurfaceAngle = 180 ;
                    fromWhichSide = "top" ;
                }
                
                if (!fromWhichSide.equals("")) {
                    log("\n  too_close_to_walls " + fromWhichSide + "\n") ;
                }
                
		return (
                    // we're too close to the left wall
                    (getX() <= WALLMARGIN ||
                    // or we're too close to the right wall
                    getX() >= getBattleFieldWidth() - WALLMARGIN ||
                    // or we're too close to the bottom wall
                    getY() <= WALLMARGIN ||
                    // or we're too close to the top wall
                    getY() >= getBattleFieldHeight() - WALLMARGIN)
		);
            }
	}) ;
        
        
        
        setColors(Color.blue,Color.yellow,Color.white); // body,gun,radar

        if (!moveGRtogether)
            setAdjustRadarForGunTurn(true) ;
        else
            setAdjustRadarForGunTurn(false) ;
                
        setAdjustGunForRobotTurn(true) ;
        
        boolean stop = false ;

        while(true) {
            //if (!stop) {
            //    turnGunRight(70) ;
            //    log(gunToBody()) ;
            //    stop = true ;
            // }
            logFire("  getTime " + getTime()) ;
            doMove(); 
            execute() ;
            log("") ;
            log("####################") ;
            log("") ;
        }
    }
    
    public void onCustomEvent(CustomEvent e) {
	if (e.getCondition().getName().equals("too_close_to_walls")) {
            if (tooCloseToWall <= 0) {
                //log("\n too_close_to_walls " + fromWhichSide + "\n") ;
                log("  wallSurfaceAngle " + wallSurfaceAngle + "\n") ;
		tooCloseToWall += WALLMARGIN;
		setMaxVelocity(0); 
                //escapingStartingAngleOpened = true ;
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
    
    private void doMove() {
        //setMaxVelocity(8) ;
        //log("searching for enemy tank...") ;
        setAhead(1000*moveDirection) ;
        setTurnRadarRight(360) ;

	if (tooCloseToWall > 0) {
            
            holdSettingBodyToBullet = true ;
            tooCloseToWall -= tooCloseToWallDescrement ;
            logMove("doMove 1: wall " + tooCloseToWall) ;
            logMove("doMove 1: holdSettingToBullet " + holdSettingBodyToBullet) ;

            double moveLeftBy = normalizeBearing(getHeadingInvariant()-wallSurfaceAngle) ;
            logMove("doMove 1: move left by " + moveLeftBy) ;
            
            /*
            if (escapingStartingAngleOpened) {
                startingEscapingAngle = Math.abs(moveLeftBy) ;
                escapingStartingAngleOpened = false ;
                log("doMove 1: setStartingEscapingAngle " + startingEscapingAngle) ;
            }
            */
            
            double remains = normalizeBearing(getHeadingInvariant()) - normalizeBearing(wallSurfaceAngle) ;
     
            logMove("doMove 1: remains " + remains) ;
            if (Math.abs( remains ) > 5) {
                logMove("doMove 1: moving left because " + Math.abs( remains ) ) ;
                setTurnLeft(moveLeftBy) ;
                
            } else {
                setTurnLeft(0) ;
            }
            
            logMove("doMove 1: getheading " + getHeadingInvariant()) ;
            
            
        } else {
            logMove("doMove 2: not within wall boundary") ;
            logMove("doMove 2: getheading " + getHeadingInvariant()) ;
            holdSettingBodyToBullet = false ; //pokud jsem uniknul ...
        }
        
        //is valid also for the battle beginning since the tank starts at zero velocity
	if (getVelocity() == 0) { 
            logMove("doMove 3: getheading " + getHeadingInvariant()) ;
            //moveDirection *= -1;
            //setMaxVelocity(Rules.MAX_VELOCITY) ;
            setMaxVelocity(0) ;
	}
        
        
    }
    
    double normalizeBearing(double angle) {
	while (angle >  180) angle -= 360;
	while (angle < -180) angle += 360;
	return angle;
    }
    

    
    public void setWhenClose(ScannedRobotEvent e) {
        
        double distance = e.getDistance() ;
  
        double e_bearing = getAngleInvariant(e.getBearing()) ;
        
        enemy.set(distance,e.getVelocity(),normalizeBearing((getHeadingInvariant() + e_bearing)),e.getHeading(),getTime()) ;

        double firepower = 1 ;
        logFire("additional targetting, firepower" + firepower) ; 
        logFire( Rules.MAX_BULLET_POWER ) ;
        enemy.fin(20 - 3*firepower) ;
                
        //setGun is happening when moving tank body
        //gun starts to move respecting predictions of enemy. It does not respect moving of self body => the smaller e.getBearing is, the better. 
        // ... This happens after the very first targeting is done. For the very first targeting the gun is overheated, so it does not fire, so
        // ... i don't need to solve that.
    
        //setBodyPerpendicularlyToBullet(e.getBearing()) ;
        
        double errT1 = getTime() ;
        // until setGun is done, radar moving is off

        setGun(e_bearing + enemy.getAdditionalAngle()) ;

        
        double errDt = getTime() - errT1 ;
        //logFire("               error due to move before fire and after setting angles " + errDt) ;
        
        logFire("getGunHeat " + getGunHeat()) ;
        if (getGunHeat() <= 0) {
            double energy = getEnergy() ;
            logFire("fire!") ;
            
            fire(firepower) ;
            double gh = 1+firepower/5 ;
        //    logFire("  getEnergy() " + energy + "  gunHeat " + getGunHeat() + " gunHeat " + gh) ;
        } else {
        //    logFire("can not fire ........ gunHeat > 0 " + getGunHeat()) ;
        }
    }
    
    public void onScannedRobot(ScannedRobotEvent e) {
        double distance = e.getDistance() ;
        //double angle = e.getBearingRadians() ;
        //double absAngle = getHeadingRadians() + e.getBearingRadians() ;        
        //double dx = distance*Math.sin(absAngle) ;
        //double dy = distance*Math.cos(absAngle) ;
        //double x = getX() + dx ;
        //double y = getY() + dy ;
        double e_bearing = getAngleInvariant(e.getBearing()) ;
        logRadar("enemy relative angle " + e_bearing + ", absolute angle " + normalizeBearing((getHeadingInvariant() + e_bearing))) ;
        
        if (distance < firstRingRadius && !firstRingReached) {
            logRadar("onScanned 1: reached " + firstRingRadius) ;
            logRadar("onScanned 1: setting body by " + e_bearing) ;
            approachingEnemy = false ;
            setBodyToEnemy(e_bearing,distance) ;
            setWhenClose(e) ;
            firstRingReached = true ;
            //moveDirection *= -1 ;
        }
        
        else if (distance < secondRingRadius && firstRingReached) { // pokracuj v prvnim bode, dokud distance < secondRingRadiu
            logRadar("onScanned 2: withing distance " + secondRingRadius) ;
            logRadar("onScanned 2: setting body by " + e_bearing) ;
            approachingEnemy = false ;
            setBodyToEnemy(e_bearing,distance) ;
            setWhenClose(e) ;
            
        }
        
        else {
            firstRingReached = false ;
            approachingEnemy = true ;
            setTurnRight( e_bearing ) ; 
setWhenClose(e) ;
            logRadar("onScanned 3: is too far     distance = " + distance) ;
            logRadar("onScanned 3: setTurnRight " + e_bearing ) ;
        }
    }

    // 
    private double getAngleInvariant(double angle) { 
        double out ;
        if (moveDirection == -1) {
            out = normalizeBearing(angle + 180) ;
        } else {
            out = normalizeBearing(angle) ;
        }
        return out ;
    }

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
    public void onHitWall(HitWallEvent e) {
        //this.moveDirection *= -1 ;
    }
    
    private double gunToBody() {
        double angle = getGunHeading() - getHeadingInvariant() ;
        return normalizeBearing(angle) ;
    }
    
    private void setGun(double angle) {
        logFire("targetting") ;
        //logFire("  gunToBody " + gunToBody() + " bullet from " + angle) ;
        double moveGunBy = angle - gunToBody() ;
        logFire("  moveGunBy " + moveGunBy) ;
        turnGunRight(normalizeBearing(moveGunBy)) ;
        //setTurnGunRight(normalizeBearing(moveGunBy)) ;
        //execute() ;
        
    }
    
    //TODO: nemelo by fungovat uplne vzdy, staci mimo ramec +-20 stupnu       
    private void setBodyToEnemy(double b,double distance) {
//log("setBodyPerpendicularlyToBullet hold  = " + holdSettingToBullet) ;        
        if (holdSettingBodyToBullet == false) {
        
            double angle = 0;
            double bfixed = 0;
            double anglefixed = 0 ;
            double fixingdb = 0;
            
            logMove("setting body to enemy") ;
            
            logMove("  " + distance + " / " + firstRingRadius) ;
            
            if (distance < firstRingRadius && b>= 0) { //chci se oddalit
                    fixingdb = FIXINGDB ; //kladny uhel doleva
                    logMove("  1 get away") ;
            } else if (distance > firstRingRadius && b>= 0) { //chci se priblizit
                    fixingdb = -FIXINGDB ; //zaporny uhel doleva
                    logMove("  2 get closer") ;
            } else if (distance < firstRingRadius && b < 0) { //chci se oddalit
                    fixingdb = -FIXINGDB ; // 
                    logMove("  3 get away") ;
            } else if (distance > firstRingRadius && b < 0) {
                    fixingdb = FIXINGDB ; // 
                    logMove("  4 get closer") ;
            }

            angle = getAngleToEnemyDefault(b) ;     
            logMove(  "no   fix " + angle) ;
            angle = angle + fixingdb ;
            logMove(  "with fix " + angle) ;
            
            //if (Math.abs(angle) < 1) {
            //    logMove("  angle < " + anglefixed + " so not doing anything") ;
            //} 
            //else {
                //turnLeft(angle) ;
                setTurnLeft(angle) ;
            //}
        } else {
            logMove("holding setting perp to bullet: " + holdSettingBodyToBullet) ;
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
        if (!approachingEnemy) {
            moveDirection *= -1 ;
        } else {
            //moveDirection *= -1 ;
        }
        
        
        
    }


}

