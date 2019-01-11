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
    
    private static final double WALLMARGIN = 50 ; //150
    private static final double tooCloseToWallDescrement = 3 ;
    private static final double firstRingRadius = 250 ;
    private static final double secondRingRadius = 400 ;
    
    
    //String fromWhichSide = "" ;
    double wallSurfaceAngle ; //uhel potoceni vuci smeru nahoru
            
    
    //boolean escapingStartingAngleOpened ; //pokud true, potom muzu zapsat hodnotu do setRemainingAngle
    //double startingEscapingAngle ;
    
    double epsilon = 1e-8 ; boolean tankFoundFirstTime = false ;
   
    boolean moveGRtogether, firstRingReached = false, holdSettingBodyToBullet = false ;
    
    int moveDirection = 1 ;
    
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
    
    private void doMove() {
        //setMaxVelocity(8) ;
        //log("searching for enemy tank...") ;
        setAhead(1000*moveDirection) ;
        setTurnRadarRight(360) ;

	if (tooCloseToWall > 0) {
            
            holdSettingBodyToBullet = true ;
            tooCloseToWall -= tooCloseToWallDescrement ;
            log("doMove 1: wall " + tooCloseToWall) ;
            log("doMove 1: holdSettingToBullet " + holdSettingBodyToBullet) ;

            double moveLeftBy = normalizeBearing(getHeading()-wallSurfaceAngle) ;
            log("doMove 1: move left by " + moveLeftBy) ;
            
            /*
            if (escapingStartingAngleOpened) {
                startingEscapingAngle = Math.abs(moveLeftBy) ;
                escapingStartingAngleOpened = false ;
                log("doMove 1: setStartingEscapingAngle " + startingEscapingAngle) ;
            }
            */
            
            double remains = normalizeBearing(getHeading()) - normalizeBearing(wallSurfaceAngle) ;
     
            log("doMove 1: remains " + remains) ;
            if (Math.abs( remains ) > 5) {
                log("doMove 1: moving left because " + Math.abs( remains ) ) ;
                setTurnLeft(moveLeftBy) ;
                
            } else {
                setTurnLeft(0) ;
            
            }
            
            log("doMove 1: getheading " + getHeading()) ;
            
            
        } else {
            log("doMove 2: not within wall boundary") ;
            log("doMove 2: getheading " + getHeading()) ;
            holdSettingBodyToBullet = false ; //pokud jsem uniknul ...
        }
        
	if (getVelocity() == 0) {
            log("doMove 3: getheading " + getHeading()) ;
            //moveDirection *= -1;
            setMaxVelocity(Rules.MAX_VELOCITY) ;
	}
        
        
    }
    
    double normalizeBearing(double angle) {
	while (angle >  180) angle -= 360;
	while (angle < -180) angle += 360;
	return angle;
    }
    
    public <T> void log(T t) {
        System.out.println(t) ;
    }
    
    
    public void setWhenClose(ScannedRobotEvent e) {
        
        double distance = e.getDistance() ;
  
        enemy.set(distance,e.getVelocity(),normalizeBearing((getHeading() + e.getBearing())),e.getHeading(),getTime()) ;

        log("  additional targetting") ;
        double firepower = 1 ;
        enemy.fin(20 - 3*firepower) ;
                
        //setGun is happening when moving tank body
        //gun starts to move respecting predictions of enemy. It does not respect moving of self body => the smaller e.getBearing is, the better. 
        // ... This happens after the very first targeting is done. For the very first targeting the gun is overheated, so it does not fire, so
        // ... i don't need to solve that.
    
        //setBodyPerpendicularlyToBullet(e.getBearing()) ;
        log( "!!!!!!!!!!!!!!!! +   " + enemy.getAdditionalAngle() ) ;
        double errT1 = getTime() ;
        // until setGun is done, radar moving is off
        if (!this.moveGRtogether) 
            setGun(e.getBearing() + enemy.getAdditionalAngle()) ;
        else
            setGun(e.getBearing() + enemy.getAdditionalAngle()) ; //pokud vypnuto, hlaven se pretoci a strela jde hodne mimo
        
        double errDt = getTime() - errT1 ;
        log("               error due to move before fire and after setting angles " + errDt) ;
        

        if (getGunHeat() <= 0) {
            log("fire!") ;
            fire(firepower) ;
        } else {
            log("can not fire ........ gunHeat > 0 " + getGunHeat()) ;
        }
    }
    
    public void onScannedRobot(ScannedRobotEvent e) {
        double distance = e.getDistance() ;
        double angle = e.getBearingRadians() ;
        double absAngle = getHeadingRadians() + e.getBearingRadians() ;        
        double dx = distance*Math.sin(absAngle) ;
        double dy = distance*Math.cos(absAngle) ;
        double x = getX() + dx ;
        double y = getY() + dy ;
       

        
        log("enemy relative angle " + e.getBearing() + ", absolute angle " + normalizeBearing((getHeading() + e.getBearing()))) ;
        
        if (distance < firstRingRadius && !firstRingReached) {
            log("onScanned 1: reached " + firstRingRadius) ;
            log("onScanned 2: setting body") ;
            setBodyPerpendicularlyToBullet(e.getBearing()) ;
            setWhenClose(e) ;
            firstRingReached = true ;
        }
        
        else if (distance < secondRingRadius && firstRingReached) {
            log("onScanned 2: withing distance " + secondRingRadius) ;
            log("onScanned 2: setting body") ;
            setBodyPerpendicularlyToBullet(e.getBearing()) ;
            setWhenClose(e) ;
        }
        
        else {
            firstRingReached = false ;
            setTurnRight(e.getBearing()) ;
            log("onScanned 3: is too far     distance = " + distance) ;
            log("onScanned 3: setTurnRight " + (e.getBearing())) ;
            
        }
            
        
        
        

    }

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
    public void onHitWall(HitWallEvent e) {
        this.moveDirection *= -1 ;
    }
    
    private double gunToBody() {
        double angle = getGunHeading() - getHeading() ;
        return normalizeBearing(angle) ;
    }
    
    private void setGun(double angle) {
        log("targetting") ;
        //log("  gunToBody " + gunToBody() + " bullet from " + angle) ;
        double moveGunBy = angle - gunToBody() ;
        log("  moveGunBy " + moveGunBy) ;
        turnGunRight(normalizeBearing(moveGunBy)) ;
        //setTurnGunRight(normalizeBearing(moveGunBy)) ;
        //execute() ;
        
    }
    
    //TODO: nemelo by fungovat uplne vzdy, staci mimo ramec +-20 stupnu       
    private void setBodyPerpendicularlyToBullet(double b) {
//log("setBodyPerpendicularlyToBullet hold  = " + holdSettingToBullet) ;        
        if (holdSettingBodyToBullet == false) {
        
            double angle ;
        
            log("move body by") ;
        
            if (b <= 0) {
                if ( b >= -90) {
                    angle = 90 + b ;
                    log("  1 right " + angle) ;
                    //turnRight(angle) ;
                    angle = -angle ; ///then I can turnLeft(angle)
                } else {
                    //angle = 90 - (180 + b) ;
                    angle = -b - 90 ;
                    log("  2 left " + angle) ;
                    //turnLeft(angle) ;
                }            
            }
            else {            
                if ( b <= 90) {
                    angle = (90 - b) ;
                    log("  3 left " + angle) ;
                    //turnLeft(angle) ;
                } else {
                    angle = (b - 90) ;
                    log("  4 right " + angle ) ;
                    //turnRight(angle) ;
                    angle = -angle ; //then I can turnLeft(angle)
                }
            }

            if (Math.abs(angle) < 15) {
                log("  angle < " + angle + " so not doing anything") ;
            } 
            else {
                //turnLeft(angle) ;
                setTurnLeft(angle) ;
            }
        } else {
            log("holding setting perp to bullet: " + holdSettingBodyToBullet) ;
        }
               
    }
            
    
    public void onHitByBullet(HitByBulletEvent e) {        
       // log("rel: " + e.getBearing() + " abs: " + (getHeading() + e.getBearing())) ;
       //setAdjustRadarForRobotTurn(true) ;
       //setGun(e.getBearing()) ;
       //setBodyPerpendicularlyToBullet(e) ;
       
       

    }


}

