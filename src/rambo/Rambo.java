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
            
            mainSequence(); 

        }
    }
    
    private void mainSequence() {
        if (!this.tankFoundFirstTime) {
            findEnemyTankFirstTime();
        }
        this.tankFoundFirstTime = false ;
            
        log("A") ;
        ahead(40*moveDirection) ;
        log("B") ;
    }
    
    double normalizeBearing(double angle) {
	while (angle >  180) angle -= 360;
	while (angle < -180) angle += 360;
	return angle;
    }
    
    public <T> void log(T t) {
        System.out.println(t) ;
    }
    
    private void findEnemyTankFirstTime() {
        log("searching for enemy tank...") ;
        int i = 360 ;
        while (!this.tankFoundFirstTime) {
            if (this.moveGRtogether)
                turnGunRight(i) ;
            else
                turnRadarRight(i) ;
            
            //execute() ;
            i += 1 ;
        } 
    }
    
    public void onScannedRobot(ScannedRobotEvent e) {
        
        if (!this.tankFoundFirstTime && getGunHeat() <= 0) {
            log("found enemy tank " + e.getName()) ;
            log("------") ;
            log("gun heading " + getGunHeading()) ;
            log("radar heading " + getRadarHeading()) ;
            doNothing() ;
            //setGun(e.getBearing()) ;
            log("gun heading " + getGunHeading()) ;
            log("radar heading " + getRadarHeading()) ;
            doNothing() ;

            log("gun heading " + getGunHeading()) ;
            log("radar heading " + getRadarHeading()) ;
            log("------") ;
            
            if (!this.moveGRtogether) 
                setGun(e.getBearing()) ;
            else
                setGun(e.getBearing()) ; //pokud vypnuto, hlaven se pretoci a strela jde hodne mimo
            
          
            setBodyPerpendicularlyToBullet(e.getBearing());
            fire(1) ;
            log("fire!") ;
        }
        else {
            //log("can not fire ........ gunHeat > 0 " + getGunHeat()) ;
        }
        
        
        //log("this.tankFoundFirstTime " + this.tankFoundFirstTime) ;
        this.tankFoundFirstTime = true ;
        
        
        double distance = e.getDistance() ;
        double angle = e.getBearingRadians() ;
        double absAngle = getHeadingRadians() + e.getBearingRadians() ;        
        double dx = distance*Math.sin(absAngle) ;
        double dy = distance*Math.cos(absAngle) ;
        double x = getX() + dx ;
        double y = getY() + dy ;
        
        
        //log("relative angle " + e.getBearing() + ", absolute angle " + (getHeading() + e.getBearing())) ;
        //log("dx " + dx + "  x " + x) ;
        //log("dy " + dy + "  y " + y) ;
        //log("his heading " + e.getHeading()) ;    
    }

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
    public void onHitWall(HitWallEvent e) {
        this.moveDirection *= -1 ;
    }
    
    private double gunToBody() {
        double angle = getGunHeading() - getHeading() ;
        if (angle < -180) {
            angle = 360 + angle ;
        }
        return angle ;
    }
    
    private void setGun(double angle) {
        log("gunToBody " + gunToBody() + " bullet from " + angle) ;
        double moveGunBy = angle -gunToBody() ;
        log("moveGunBy " + moveGunBy) ;
        turnGunRight(normalizeBearing(moveGunBy)) ;
        //execute() ;
        //turnGunRight( Math.abs(angle - 360.0) < epsilon ? 0 : angle ) ;
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
        
        if (Math.abs(angle) < 20) {
            log("  angle < 20 so not doing anything") ;
        } 
        else {
            turnLeft(angle) ;
        }
            
        //execute();
               
    }
            
    
    public void onHitByBullet(HitByBulletEvent e) {        
       // log("rel: " + e.getBearing() + " abs: " + (getHeading() + e.getBearing())) ;
       //setAdjustRadarForRobotTurn(true) ;
       //setGun(e.getBearing()) ;
       //setBodyPerpendicularlyToBullet(e) ;
       
       

    }


}

