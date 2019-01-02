/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rambo;

import robocode.*;
import java.awt.Color;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * Rambo - a robot by (your name here)
 */

//TODO - dodelat silu strely jako funkci polohy - cim dal, tim slabsi strela

public class Rambo extends AdvancedRobot {
    
    double epsilon = 1e-8 ; boolean tankFoundFirstTime = false ;
   
    boolean moveGRtogether = false ;
    
    int moveDirection = 1 ;
    
    Enemy enemy = new Enemy() ;
    
    public void run() {
		// Initialization of the robot should be put here

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
    
    private void doMove() {
        setMaxVelocity(6) ;
        log("searching for enemy tank...") ;
        setAhead(1000*moveDirection) ;
        setTurnRadarRight(360) ;
    }
    
    double normalizeBearing(double angle) {
	while (angle >  180) angle -= 360;
	while (angle < -180) angle += 360;
	return angle;
    }
    
    public <T> void log(T t) {
        System.out.println(t) ;
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
                       
        enemy.set(distance,e.getVelocity(),normalizeBearing((getHeading() + e.getBearing())),e.getHeading(),getTime()) ;

        
        
        log("  additional targetting") ;
        double firepower = 1 ;
        enemy.finalize(getTime(),20 - 3*firepower) ;
                
        //setGun is happening when moving tank body
        //gun starts to move respecting predictions of enemy. It does not respect moving of self body => the smaller e.getBearing is, the better. 
        // ... This happens after the very first targeting is done. For the very first targeting the gun is overheated, so it does not fire, so
        // ... i don't need to solve that.
    
        setBodyPerpendicularlyToBullet(e.getBearing()) ;
        // until setGun is done, radar moving is off
        if (!this.moveGRtogether) 
            setGun(e.getBearing() + enemy.getAdditionalAngle()) ;
        else
            setGun(e.getBearing() + enemy.getAdditionalAngle()) ; //pokud vypnuto, hlaven se pretoci a strela jde hodne mimo

        if (getGunHeat() <= 0) {
            log("fire!") ;
            fire(firepower) ;
        } else {
            log("can not fire ........ gunHeat > 0 " + getGunHeat()) ;
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
               
    }
            
    
    public void onHitByBullet(HitByBulletEvent e) {        
       // log("rel: " + e.getBearing() + " abs: " + (getHeading() + e.getBearing())) ;
       //setAdjustRadarForRobotTurn(true) ;
       //setGun(e.getBearing()) ;
       //setBodyPerpendicularlyToBullet(e) ;
       
       

    }


}

