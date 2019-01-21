/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rambo;

import robocode.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * Rambo - a robot by (your name here)
 */

//TODO - dodelat silu strely jako funkci polohy - cim dal, tim slabsi strela

public class Rambo extends AdvancedRobot {
    
        
    public <T> void logMove(T t) {
    //    System.out.println(getTime() + " Move " + t) ;
    }
    
    public <T> void logFire(T t) {
    //    System.out.println(t) ;
    }
    
    public <T> void logRadar(T t) {
    //    System.out.println(getTime() + " Radar " + t) ;
    }
    
    public <T> void log(T t) {
    //    System.out.println(t) ;
    }
    
 
    double normalizeBearing(double angle) {
	while (angle >  180) angle -= 360;
	while (angle < -180) angle += 360;
	return angle;
    }
    

    

       

    double wallSurfaceAngle ; //uhel potoceni vuci smeru nahoru
  
    double epsilon = 1e-8 ; boolean tankFoundFirstTime = false ;
   
    double angleToEnemy ; //uhel mezi predchozim a aktualnim monitoringem nepritele
    
    boolean firstRingReached = false, setBodyToEnemyLock = false, approachingEnemy, enemyWasFoundFirstTime = false ;
    
    int moveDirection = -1 ;
    
    boolean tooCloseToWallLock = false ;
    
    List<Integer> hitsReceived = new ArrayList<>() ;
    
    Enemy enemy = new Enemy(true) ;
    
    AI ai = new AI(false) ;
    
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
            
            ai.xVec.add(getX()) ;
            ai.yVec.add(getY()) ;
            ai.dtime += 1 ; // OPRAVDU FUNGUJE? MELO BY DIKY SETTERUM VSUDE
            
            log("---------- BOC " + round + " getTime" + getTime()) ;
            doMove(); 
            log("---------- EOC " + round + " getTime" + getTime() + "\n") ;
            round++ ;
            execute() ;
 
  
            
        }
        
    }
    
    public void onCustomEvent(CustomEvent e) {
	if (e.getCondition().getName().equals("too_close_to_walls")) {
            if (!tooCloseToWallLock) {
                log("  wallSurfaceAngle " + wallSurfaceAngle + "\n") ;
                tooCloseToWallLock = true ;
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
        
        setAhead(1000*moveDirection) ;
        
       //logMove("ai.getTotalDistance() " + ai.getTotalDistance()) ;
        
        /*
        
        if (ai.getTotalDistance() > 200) {
            ai.xVec.clear() ;
            ai.yVec.clear() ;
            //
        }

*/

        if (ai.dtime + 3 > ai.getAveHitDt()+25) {
            logMove("CHANGE TANK DIRECTION: ai.dtime " + ai.dtime + " ai.getAveHitDt() " + ai.getAveHitDt()) ;
            moveDirection *= -1 ;
            ai.dtime = 0 ;
        }

        
        if (!enemyWasFoundFirstTime) { //da se toho zbavit?
            setTurnRadarRight(90) ;
        } else {
            double[] radar_direction = {Math.sin(getRadarHeadingRadians()),Math.cos(getRadarHeadingRadians())} ;
            double dire = Utils.angleDirection(enemy.direction(),radar_direction) ;
            double angle = Utils.scalar(enemy.direction(),radar_direction) ;
            logRadar(".... angle to enemy " + angle) ;
            logRadar(".... direction to enemy " + dire) ;
            //setTurnRadarRight(dire*45) ;
            setTurnRadarRight(angle) ;
        }
        
        if (tooCloseToWallLock) {    
            setMaxVelocity(Rules.MAX_VELOCITY*0.7);
            setBodyToEnemyLock = true ;
            logMove("1: holdSettingToBullet " + setBodyToEnemyLock) ;

            double moveLeftBy = normalizeBearing(getHeadingInvariant()-wallSurfaceAngle) ;
            logMove("1: move left by " + moveLeftBy) ;
            
            double remains = normalizeBearing(getHeadingInvariant()) - normalizeBearing(wallSurfaceAngle) ;
     
            logMove("1: remains " + remains) ;
            if (Math.abs( remains ) > 5) {
                logMove("1: moving left because " + Math.abs( remains ) ) ;
                setTurnLeft(moveLeftBy) ;
                
            } else {
                setTurnLeft(0) ;
                tooCloseToWallLock = false ;
            }
            
            logMove("1: getheading " + getHeadingInvariant()) ;
            
            
        } else {
            //setMaxVelocity(Rules.MAX_VELOCITY);
            setMaxVelocity(0) ;
            //logMove("doMove 2: not within wall boundary") ;
            //logMove("doMove 2: getheading " + getHeadingInvariant()) ;
            setBodyToEnemyLock = false ; //pokud jsem uniknul ...
        }
        
    }
   
    
    private void setGun(double angle) {
        logFire("targetting") ;
        //logFire("  gunToBody " + gunToBody() + " bullet from " + angle) ;
        double moveGunBy = angle - gunToBody() ;
        logFire("  moveGunBy " + moveGunBy) ;
        setTurnGunRight(normalizeBearing(moveGunBy)) ;
        //turnGunRight(normalizeBearing(moveGunBy)) ;
        
    }
    
    public void setWhenClose(ScannedRobotEvent e) {
        log("setWhenClose BEGIN") ;
        double distance = e.getDistance() ;
  
        double e_bearing = getAngleInvariant(e.getBearing()) ;
        
        enemy.setForFire(distance,e.getVelocity(),normalizeBearing((getHeadingInvariant() + e_bearing)),e.getHeading()) ;

        double firepower = 1 ;
 
        enemy.fin(20 - 3*firepower) ;
                
        //setGun is happening when moving tank body
        //gun starts to move respecting predictions of enemy. It does not respect moving of self body => the smaller e.getBearing is, the better. 
        // ... This happens after the very first targeting is done. For the very first targeting the gun is overheated, so it does not fire, so
        // ... i don't need to solve that.
    
        //setBodyPerpendicularlyToBullet(e.getBearing()) ;
        
        setGun(e_bearing + enemy.getAdditionalAngle()) ;
        logFire("setGun " + (e_bearing + enemy.getAdditionalAngle()) ) ;
        logFire("getGunTurnRemaining " + getGunTurnRemaining()) ;
        
        //logFire("getGunHeat " + getGunHeat()) ;
        if (getGunHeat() <= 0 && Math.abs( getGunTurnRemaining() ) < 3 && ai.allowFire) {
        //if (getGunHeat() <= 0)   {
            double energy = getEnergy() ;
            logFire("fire!") ;
            setFire(firepower) ;
            //fire(firepower) ;
            double gh = 1+firepower/5 ;
        } else {
        //    logFire("can not fire ........ gunHeat > 0 " + getGunHeat()) ;
        }
        log("setWhenClose END") ;
    }
    

    
    public void onScannedRobot(ScannedRobotEvent e) {
        double distance = e.getDistance() ;

        double absAngle = getHeadingRadians() + e.getBearingRadians() ;    
                        
        double dx = distance*Math.sin(absAngle) ;
        double dy = distance*Math.cos(absAngle) ;
        
        enemy.set(getX() + dx,getY() + dy,dx,dy,absAngle,e.getEnergy(),getTime()) ;
        
        enemyWasFoundFirstTime = true ;

        double e_bearing = getAngleInvariant(e.getBearing()) ;
        logRadar("onScanned: enemy relative angle " + e_bearing + ", absolute angle " + normalizeBearing(Utils.toDeg(absAngle))) ; // stejne jako normalizeBearing((getHeadingInvariant() + e_bearing))
        
        if (distance < ai.FIRSTRINGRADIUS && !firstRingReached) {
            logRadar("onScanned 1: reached " + ai.FIRSTRINGRADIUS) ;
            logRadar("onScanned 1: setting body by " + e_bearing) ;
            approachingEnemy = false ;
            setBodyToEnemy(e_bearing,distance,25) ;
            setWhenClose(e) ;
            firstRingReached = true ;
            //moveDirection *= -1 ;
        }
        
        else if (distance < ai.SECONDRINGRADIUS && firstRingReached) { // pokracuj v prvnim bode, dokud distance < secondRingRadiu
            logRadar("onScanned 2: withing distance " + ai.SECONDRINGRADIUS) ;
            logRadar("onScanned 2: setting body by " + e_bearing) ;
            approachingEnemy = false ;
            setBodyToEnemy(e_bearing,distance,25) ;
            setWhenClose(e) ;
            
        }
        
        else {
            firstRingReached = false ;
            approachingEnemy = true ;
            //setTurnRight( e_bearing ) ; 
            setBodyToEnemy(e_bearing,distance,0) ;//35) ;
            setWhenClose(e) ; 
            
            ai.allowFire = true ;
            
            //TODO - strileni by melo byt vypnuto

            logRadar("onScanned 3: is too far     distance = " + distance) ;
           
        }
    }

    
   
    private void setBodyToEnemy(double b,double distance,double fixingDBabs) {
//log("setBodyPerpendicularlyToBullet hold  = " + holdSettingToBullet) ;        
        if (setBodyToEnemyLock == false) {
        
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
            
            //if (Math.abs(angle) < 1) {
            //    logMove("  angle < " + anglefixed + " so not doing anything") ;
            //} 
            //else {
                //turnLeft(angle) ;
                setTurnLeft(angle) ;
            //}
        } else {
            logMove("holding setting perp to bullet: " + setBodyToEnemyLock) ;
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


}

