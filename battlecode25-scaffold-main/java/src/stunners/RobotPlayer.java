package stunners;

import battlecode.common.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.lang.Math;

/**
 * RobotPlayer is the class that describes your main robot strategy.
 * The run() method inside this class is like your main function: this is what we'll call once your robot
 * is created!
 */
public strictfp class RobotPlayer {

    /**
     * We will use this variable to count the number of turns this robot has been alive.
     * You can use static variables like this to save any information you want. Keep in mind that even though
     * these variables are static, in Battlecode they aren't actually shared between your robots.
     */
    static int turnCount = 0;

    /**
     * A random number generator.
     * We will use this RNG to make some random moves. The Random class is provided by the java.util.Random
     * import at the top of this file. Here, we *seed* the RNG with a constant number (6147); this makes sure
     * we get the same sequence of numbers every time this code is run. This is very useful for debugging!
     */
    static final Random rng = new Random(6147);
    static final Random num = new Random(8);

    /** Array containing all the possible movement directions. */
    static final Direction[] directions = {
        Direction.NORTH,
        Direction.NORTHEAST,
        Direction.EAST,
        Direction.SOUTHEAST,
        Direction.SOUTH,
        Direction.SOUTHWEST,
        Direction.WEST,
        Direction.NORTHWEST,
    };

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * It is like the main function for your robot. If this method returns, the robot dies!
     *
     * @param rc  The RobotController object. You use it to perform actions from this robot, and to get
     *            information on its current status. Essentially your portal to interacting with the world.
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        // Hello world! Standard output is very useful for debugging.
        // Everything you say here will be directly viewable in your terminal when you run a match!
        //System.out.println("I'm alive");

        // You can also use indicators to save debug notes in replays.
        rc.setIndicatorString("Hello world!");

        while (true) {
            // This code runs during the entire lifespan of the robot, which is why it is in an infinite
            // loop. If we ever leave this loop and return from run(), the robot dies! At the end of the
            // loop, we call Clock.yield(), signifying that we've done everything we want to do.

            turnCount += 1;  // We have now been alive for one more turn!

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode.
            try {
                // Make sure you spawn your robot in before you attempt to take any actions!
                // Robots not spawned in do not have vision of any tiles and cannot perform any actions.
                if (!rc.isSpawned()){
                    MapLocation[] spawnLocs = rc.getAllySpawnLocations();
                    spawnLocs = shuffleArray(spawnLocs, rng);
                    // Pick a random spawn location to attempt spawning in.
                    for (MapLocation aLoc : spawnLocs) {
                        if (rc.canSpawn(aLoc)) rc.spawn(aLoc);
                    }
                }
                else{
                	//moveTo(rc, new MapLocation(15,15));
                    if(turnCount <= 200){
                        duckPrep(rc);
                    } else {
						if (turnCount == 600){
							if(rc.canBuyGlobal(GlobalUpgrade.ATTACK)){
								rc.buyGlobal(GlobalUpgrade.ATTACK);
							}
						}
						if (turnCount == 1200){
							if(rc.canBuyGlobal(GlobalUpgrade.HEALING)){
								rc.buyGlobal(GlobalUpgrade.HEALING);
							}
						}
						if (turnCount == 1800){
							if(rc.canBuyGlobal(GlobalUpgrade.CAPTURING)){
								rc.buyGlobal(GlobalUpgrade.CAPTURING);
							}
						}
                        ducksDo(rc);
                    }

                }

            } catch (GameActionException e) {
                // Oh no! It looks like we did something illegal in the Battlecode world. You should
                // handle GameActionExceptions judiciously, in case unexpected events occur in the game
                // world. Remember, uncaught exceptions cause your robot to explode!
                System.out.println("GameActionException");
                e.printStackTrace();

            } catch (Exception e) {
                // Oh no! It looks like our code tried to do something bad. This isn't a
                // GameActionException, so it's more likely to be a bug in our code.
                System.out.println("Exception");
                e.printStackTrace();

            } finally {
                // Signify we've done everything we want to do, thereby ending our turn.
                // This will make our code wait until the next turn, and then perform this loop again.
                Clock.yield();
            }
            // End of loop: go back to the top. Clock.yield() has ended, so it's time for another turn!
        }

        // Your code should never reach here (unless it's intentional)! Self-destruction imminent...
    }

    /**
     * Code Goes Here!! Yaaaaa. 
     */

    //Get Our Spawn Zones
    static void ducksDo(RobotController rc) throws GameActionException {
    	MapLocation here = rc.getLocation();

		rc.setIndicatorString("Out of ideas");
    //     MapLocation[] nearestCrumbs = rc.senseNearbyCrumbs(-1);
    //     if (nearestCrumbs.length > 1){
    //         MapLocation nearestCrumbDirection = nearestCrumbs[0];
    //         Direction moveThisWay = rc.getLocation().directionTo(nearestCrumbDirection);
    //         if(rc.canMove(moveThisWay)){
    //             rc.move(moveThisWay);
    //         }
    //     } else{
    //         Direction randomDir = directions[rng.nextInt(directions.length)];
    //         if (rc.canMove(randomDir)){
    //             rc.move(randomDir);
    //         }
    //     }
    	
    	//Designate surrounding robots
    	RobotInfo[] allVisibleRobots = rc.senseNearbyRobots();
    	RobotInfo nearestEnemy = null;
    	int nearestEnemyDistanceSquared = 65537;
    	RobotInfo nearestAlly = null;
    	int nearestAllyDistanceSquared = 65537;
    	RobotInfo nearestInjuredAlly = null;
    	int nearestInjuredAllyDistanceSquared = 65537;
		RobotInfo nearestEnemyWithFlag = null;
		int nearestEnemyWithFlagDistanceSquared = 65537;
		RobotInfo nearlowestHPEnemy = null;
		int nearlowestHPEnemyDistanceSquared = 65537;
		RobotInfo farlowestHPEnemy = null;
		int farlowestHPEnemyDistanceSquared = 65537;
    	for (RobotInfo aBot : allVisibleRobots) {
    		if (aBot.team.equals(rc.getTeam())) { //handle ally bots
    			int botDist = aBot.location.distanceSquaredTo(here);
    			// all allies - not used currently
    			/*
    			if (botDist < nearestAllyDistanceSquared) {
    				nearestAllyDistanceSquared = botDist;
    				nearestAlly = aBot;
    			}
    			*/
    			// Injured allies only
    			if (aBot.health < 1000) {
    				if (botDist < nearestInjuredAllyDistanceSquared) {
        				nearestInjuredAllyDistanceSquared = botDist;
        				nearestInjuredAlly = aBot;
        			}
    			}
    		} else { //handle enemy bots
    			int botDist = aBot.location.distanceSquaredTo(here);
    			if (botDist < nearestEnemyDistanceSquared) {
    				nearestEnemyDistanceSquared = botDist;
    				nearestEnemy = aBot;
    			}
				if (botDist <= 4 && aBot.health < nearlowestHPEnemyDistanceSquared){
					nearlowestHPEnemyDistanceSquared = botDist;
        			nearlowestHPEnemy = aBot;
				}
				if (botDist > 4 && aBot.health < farlowestHPEnemyDistanceSquared){
					farlowestHPEnemyDistanceSquared = botDist;
        			farlowestHPEnemy = aBot;
				}
				if(aBot.hasFlag()){
					if(botDist < nearestEnemyWithFlagDistanceSquared){
						nearestEnemyWithFlagDistanceSquared = botDist;
						nearestEnemyWithFlag = aBot;
					}
				}	
    		}
    	}
    	
    	//If see enemy with flag, attack. 
		if(nearestEnemyWithFlag != null){
			shootScoot(rc, nearestEnemyWithFlag, null);
		}
		
		//If have the flag, run back!!
		if(rc.hasFlag()){
			MapLocation[] allySpawnLocations = rc.getAllySpawnLocations();
				int closestSpawn = allySpawnLocations[0].distanceSquaredTo(rc.getLocation());
				MapLocation locOfSpawn = allySpawnLocations[0];
				for(int i = 0; i< allySpawnLocations.length; i++){
					if(closestSpawn > allySpawnLocations[i].distanceSquaredTo(rc.getLocation())){
						closestSpawn = allySpawnLocations[i].distanceSquaredTo(rc.getLocation());
						locOfSpawn = allySpawnLocations[i];
					}
				}
			//rc.setIndicatorString("Heading To: " + locOfSpawn.toString());
			moveTo(rc, locOfSpawn);
		}
		
		//If see enemy with flag, chase it
		if(nearestEnemyWithFlag != null) {
			
			shootScoot(rc, nearestEnemyWithFlag, nearestEnemyWithFlag);
		}

    	//Attack enemy
		boolean crumbsThreshold = rc.getCrumbs() > 500;
		if (rc.getHealth() <= 750) {
			if (nearlowestHPEnemy != null) {
				MapLocation plantSite = rc.getLocation().add(rc.getLocation().directionTo(nearlowestHPEnemy.location));
				if (crumbsThreshold && rc.canBuild(TrapType.STUN, plantSite)) {
					rc.build(TrapType.STUN, plantSite);
				}
				if (crumbsThreshold && rc.canBuild(TrapType.STUN, rc.getLocation())) {
					rc.build(TrapType.STUN, rc.getLocation());
				}
				flee(rc, nearlowestHPEnemy.location);
			} else if (farlowestHPEnemy != null) {
				MapLocation plantSite = rc.getLocation().add(rc.getLocation().directionTo(farlowestHPEnemy.location));
				if (crumbsThreshold && rc.canBuild(TrapType.STUN, plantSite)) {
					rc.build(TrapType.STUN, plantSite);
				}
				if (crumbsThreshold && rc.canBuild(TrapType.STUN, rc.getLocation())) {
					rc.build(TrapType.STUN, rc.getLocation());
				}
				flee(rc, farlowestHPEnemy.location);
			}
		} else {
			shootScoot(rc, nearlowestHPEnemy, farlowestHPEnemy);
		}
		
		//If see flag, get flag.
		FlagInfo[] nearbyEnemyFlags = rc.senseNearbyFlags(-1, rc.getTeam().opponent());
		if(nearbyEnemyFlags.length > 0){
			if(!nearbyEnemyFlags[0].isPickedUp()){
				moveTo(rc, nearbyEnemyFlags[0].getLocation());
				if(rc.canPickupFlag(nearbyEnemyFlags[0].getLocation())){
					rc.pickupFlag(nearbyEnemyFlags[0].getLocation());
				}
			}
		}

    	//Heal allies
    	if (nearestInjuredAlly != null) {
    		if (nearestEnemy == null || nearestEnemyDistanceSquared > 9) {
    			if (nearestInjuredAllyDistanceSquared > 4) { //don't waste movement if already close
        			if (turnCount >= 200) { //don't try to move if in prep phase
                		moveTo(rc, nearestInjuredAlly.location);
                	}
            	}
        		if (rc.canHeal(nearestInjuredAlly.location)) {
        			rc.heal(nearestInjuredAlly.location);
        			rc.setIndicatorString("Healing");
        		}
    		}
    	}

    	if (turnCount > 200) {
    		seekCrumb(rc);
    		
    		//low priority, go to flag
        	//rc.setIndicatorString(rc.getMovementCooldownTurns() + "");
    		if (rc.getMovementCooldownTurns() < 10) {
        		MapLocation nearestFlag = senseNearestFlagBroadcast(rc);
        		if (nearestFlag != null) {
        			moveTo(rc, nearestFlag);
        		}
        	}
		}
    	
    	if (rc.getMovementCooldownTurns() < 10) {
    		MapLocation[] flagBroadcasts = rc.senseBroadcastFlagLocations();
    		if (flagBroadcasts.length > 0) {
        		MapLocation flagApprox = flagBroadcasts[0];
        		moveTo(rc, flagApprox);
    		} else {
    			MapLocation[] spawnLocs = rc.getAllySpawnLocations();
    			for (int i = 0; i < spawnLocs.length; i++) {
    				int x = spawnLocs[i].x;
                	int y = spawnLocs[i].y;
                	x = -x + rc.getMapWidth();
                	y = -y + rc.getMapHeight();
                	spawnLocs[i] = new MapLocation(x,y);
    			}
            	moveTo(rc, spawnLocs[rc.getID() % spawnLocs.length]);
    		}
    	}
        
    	//Fallback option
		//Nothing should come after this block of code
		if (turnCount > 200) {
			duckPrep(rc);
		}
    }
    
    public static void shootScoot(RobotController rc, RobotInfo nearTarget, RobotInfo farTarget) throws GameActionException {
    	shootyStuff(rc, nearTarget, farTarget, false);
    }
    
    public static void shootScoot(RobotController rc, RobotInfo nearTarget, RobotInfo farTarget, boolean charge) throws GameActionException {
    	shootyStuff(rc, nearTarget, farTarget, charge);
    }
    
    public static void shootyStuff(RobotController rc, RobotInfo nearTarget, RobotInfo farTarget, boolean charge) throws GameActionException {
		boolean crumbsThreshold = rc.getCrumbs() > 500;
		if (nearTarget != null && rc.getActionCooldownTurns() < 10) {
			if (rc.canAttack(nearTarget.location)) {
				rc.attack(nearTarget.location);
				rc.setIndicatorString("Pew Pew!!");
				flee(rc, nearTarget.location);
			}
		} else if (farTarget != null && (rc.getActionCooldownTurns() < 10 || charge)) {
			moveTo(rc, farTarget.location);
			if (rc.canAttack(farTarget.location)){
				rc.attack(farTarget.location);
				rc.setIndicatorString("Pew Pew!!");
			}
			MapLocation plantSite = rc.getLocation().add(rc.getLocation().directionTo(farTarget.location));
			if (crumbsThreshold && rc.canBuild(TrapType.STUN, plantSite)) {
				rc.build(TrapType.STUN, plantSite);
			}
		}
		// should make a variant and test if this is effective or not
		if (nearTarget != null && nearTarget.location.distanceSquaredTo(rc.getLocation()) <= 4) {
			MapLocation plantSite = rc.getLocation().add(rc.getLocation().directionTo(nearTarget.location));
			if (crumbsThreshold && rc.canBuild(TrapType.STUN, plantSite)) {
				rc.build(TrapType.STUN, plantSite);
			}
			if (crumbsThreshold && rc.canBuild(TrapType.STUN, rc.getLocation())) {
				rc.build(TrapType.STUN, rc.getLocation());
			}
			if (flee(rc, nearTarget.location)) {rc.setIndicatorString("Retreat!");};
			if (crumbsThreshold && rc.canBuild(TrapType.STUN, rc.getLocation())) {
				rc.build(TrapType.STUN, rc.getLocation());
			}
		} else if (farTarget != null) {
			//if (flee(rc, farTarget.location)) {rc.setIndicatorString("Retreat!");};
			MapLocation plantSite = rc.getLocation().add(rc.getLocation().directionTo(farTarget.location));
			if (crumbsThreshold && rc.canBuild(TrapType.STUN, plantSite)) {
				rc.build(TrapType.STUN, plantSite);
			}
		}
    }

    public static MapLocation coordSetUp(RobotController rc){
        int mapHeight = rc.getMapHeight();
        int mapWidth = rc.getMapWidth();
        MapLocation[] spawnZones = rc.getAllySpawnLocations();
        int xCoord = spawnZones[0].x;
        int yCoord = spawnZones[0].y;
        if(xCoord > mapHeight/2 && yCoord > mapWidth/2){
            return new MapLocation(mapWidth, mapHeight);
        } else if (xCoord > mapHeight/2 && yCoord < mapWidth/2){
            return new MapLocation(mapWidth, 0);
        } else if (xCoord < mapHeight/2 && yCoord < mapWidth/2){
            return new MapLocation(0, 0);
        } else{
            return new MapLocation(0, mapHeight);
        }
    }


    static void brokenFlagMvment(RobotController rc) throws GameActionException{
		Team ourTeam = rc.getTeam();
        int mapHeight = rc.getMapHeight();
        int mapWidth = rc.getMapWidth();
        int flagPlaceHeight = (mapHeight/10);
        int flagPlaceWidth = (mapWidth - mapWidth/10);
		MapLocation[] crumbArray = rc.senseNearbyCrumbs(-1);
		FlagInfo[] ourFlagLoc = rc.senseNearbyFlags(-1);
        if(rc.canPickupFlag(ourFlagLoc[0].getLocation()) && (ourFlagLoc[0].getLocation().y < 3 || ourFlagLoc[0].getLocation().y < mapHeight - 3)){
            rc.pickupFlag(ourFlagLoc[0].getLocation());
        }
		if(rc.hasFlag() && ((rc.getLocation().y < 3 || rc.getLocation().y < mapHeight -3) &&((rc.getLocation().x < 3 || rc.getLocation().x < mapHeight -3)))){
			moveTo(rc, coordSetUp(rc));
			rc.setIndicatorString("Cant sense location");
		}
		else if (rc.hasFlag() && (rc.getLocation().y > 3 || rc.getLocation().y < mapHeight -3) && ((rc.getLocation().x < 3 || rc.getLocation().x < mapHeight -3))){ //If robot has flag and is in a droppable location
			rc.dropFlag(rc.getLocation());
			rc.setIndicatorString("Dropped Flag"); //drop it
			if(!rc.senseLegalStartingFlagPlacement(rc.getLocation())){ //is the flag in a legal starting position? if not, go there
				rc.pickupFlag(rc.getLocation());
				MapLocation fleeFlags = rc.senseNearbyFlags(-1, ourTeam)[0].getLocation();
				flee(rc, fleeFlags);
				rc.setIndicatorString("fleeing flags");
			}
		} else{
			moveTo(rc, coordSetUp(rc));
			rc.setIndicatorString("Moving to Corner II");
		}
		if(!rc.hasFlag() && crumbArray.length > 0){
			moveTo(rc, crumbArray[0]);
			rc.setIndicatorString("gathering crumbs");
		}
    }
    
	static void duckPrep(RobotController rc) throws GameActionException{
		MapLocation here = rc.getLocation();
		MapLocation nearestFlag = senseNearestFlagBroadcast(rc);
		boolean atDam = false;
		if (turnCount > 200 - ((rc.getMapHeight() + rc.getMapWidth()) / 4) && turnCount <= 200) {
			MapLocation[] adjacencies = rc.getAllLocationsWithinRadiusSquared(rc.getLocation(), 2);
			for (MapLocation tile : adjacencies) {
				if (rc.senseMapInfo(tile).isDam()) {
					atDam = true;
				}
			}
			if (!atDam) {
				//go to flag (not great ai, but low priority issue)
				ducksDo(rc);
				if (nearestFlag != null) {
					moveTo(rc, nearestFlag);
				}
			} else {
				/*
				if (rc.canBuild(TrapType.EXPLOSIVE, rc.getLocation())) {
					rc.build(TrapType.EXPLOSIVE, rc.getLocation());
				}
				*/
				RobotInfo[] allVisibleRobots = rc.senseNearbyRobots(4, rc.getTeam().opponent());
				RobotInfo nearlowestHPEnemy = null;
				int nearlowestHPEnemyDistanceSquared = 65537;
		    	for (RobotInfo aBot : allVisibleRobots) {
		    		int botDist = aBot.location.distanceSquaredTo(here);
					nearlowestHPEnemyDistanceSquared = botDist;
        			nearlowestHPEnemy = aBot;
		    	}

		    	//Attack enemy
				shootScoot(rc, nearlowestHPEnemy, null);
			}
		} else {
			seekCrumb(rc);
			//spread out
			RobotInfo nearestFriendly = senseNearestRobot(rc, -1, rc.getTeam());
			MapLocation nearestEdge = senseNearestEdge(rc);
			if (nearestEdge != null) {
				flee(rc, nearestEdge);
			} else if (nearestFriendly != null) {
				flee(rc, nearestFriendly.location);
			} else {
				if (nearestFlag != null) {
					flee(rc, nearestFlag);
				}
			}
		}
		
		/*
		if (turnCount > 195 && rc.getCrumbs() > 2500) {
			if (rc.canBuild(TrapType.EXPLOSIVE, rc.getLocation())) {
				rc.build(TrapType.EXPLOSIVE, rc.getLocation());
			}
		}
		*/
		
		//Fallback option
		//Nothing should come after this block of code
		if (!atDam) {
			if (turnCount <= 200) {
				ducksDo(rc);
			}
		}
		
		/*
		MapLocation[] crumbArray = rc.senseNearbyCrumbs(-1);
		FlagInfo[] ourFlagLoc = rc.senseNearbyFlags(-1);
		if(crumbArray.length > 0){
			rc.setIndicatorString("Collecting Crumbs");
			moveTo(rc, crumbArray[0]);
		} else if((ourFlagLoc.length > 0 && trapDetection(rc, TrapType.EXPLOSIVE) < 8) && rc.getCrumbs() > 250 ){
			rc.setIndicatorString("Planting Bomb");
			moveTo(rc, ourFlagLoc[0].getLocation());
			if(rc.canBuild(TrapType.EXPLOSIVE, rc.getLocation())){
				rc.build(TrapType.EXPLOSIVE, rc.getLocation());
			}
		} else if((ourFlagLoc.length > 0 && trapDetection(rc, TrapType.EXPLOSIVE) > 1) && rc.getCrumbs() > 100){
			rc.setIndicatorString("Planting Water");
			if(rc.senseMapInfo(rc.getLocation()).isSpawnZone() || adjacentToSpawn(rc) ){
				rc.setIndicatorString("Leaving Spawn Zone");
				flee(rc, ourFlagLoc[0].getLocation());
			}
			if(rc.canBuild(TrapType.WATER, rc.getLocation())){
				rc.build(TrapType.WATER, rc.getLocation());
			}
		}
		 else{
			rc.setIndicatorString("Fleeing");
			flee(rc, rc.senseNearbyRobots(-1)[0].location);
		}
		*/
	}
	
	public static MapLocation senseNearestEdge(RobotController rc) {
		MapLocation here = rc.getLocation();
		boolean north = rc.onTheMap(here.add(Direction.NORTH).add(Direction.NORTH).add(Direction.NORTH).add(Direction.NORTH));
		boolean east = rc.onTheMap(here.add(Direction.EAST).add(Direction.EAST).add(Direction.EAST).add(Direction.EAST));
		boolean south = rc.onTheMap(here.add(Direction.SOUTH).add(Direction.SOUTH).add(Direction.SOUTH).add(Direction.SOUTH));
		boolean west = rc.onTheMap(here.add(Direction.WEST).add(Direction.WEST).add(Direction.WEST).add(Direction.WEST));
		
		if (!north) {
			return here.add(Direction.NORTH).add(Direction.NORTH).add(Direction.NORTH).add(Direction.NORTH);
		} else if (!east) {
			return here.add(Direction.EAST).add(Direction.EAST).add(Direction.EAST).add(Direction.EAST);
		} else if (!south) {
			return here.add(Direction.SOUTH).add(Direction.SOUTH).add(Direction.SOUTH).add(Direction.SOUTH);
		} else if (!west) {
			return here.add(Direction.WEST).add(Direction.WEST).add(Direction.WEST).add(Direction.WEST);
		} else return null;
	}
	
	public static MapLocation senseNearestFlagBroadcast(RobotController rc) {
		MapLocation[] broadcasts = rc.senseBroadcastFlagLocations();
		MapLocation nearestFlag = null;
		MapLocation here = rc.getLocation();
		int leastDistanceSquared = 65537;
		if (broadcasts.length > 0) {
			for (MapLocation aFlag : broadcasts) {
				int aFlagDistance = aFlag.distanceSquaredTo(here);
				if (aFlagDistance < leastDistanceSquared) {
					leastDistanceSquared = aFlagDistance;
					nearestFlag = aFlag;
				}
			}
		}
		return nearestFlag;
	}
	
	public static RobotInfo senseNearestRobot(RobotController rc, int radiusSquared, Team team) throws GameActionException {
		RobotInfo[] robots = rc.senseNearbyRobots(radiusSquared, team);
		RobotInfo nearestRobot = null;
		int leastDistanceSquared = 65537;
		MapLocation here = rc.getLocation();
		if (robots.length > 0) {
			for (RobotInfo aRobot : robots) {
				int aRobotDistance = aRobot.location.distanceSquaredTo(here);
				if (aRobotDistance < leastDistanceSquared) {
					leastDistanceSquared = aRobotDistance;
					nearestRobot = aRobot;
				}
			}
		}
		return nearestRobot;
	}
	
	public static void seekCrumb(RobotController rc) throws GameActionException {
		MapLocation[] crumbArray = rc.senseNearbyCrumbs(-1);
		if(crumbArray.length > 0){
			rc.setIndicatorString("Collecting Crumbs");
			moveTo(rc, crumbArray[0]);
		}
	}

	static boolean adjacentToSpawn(RobotController rc) throws GameActionException{
		MapInfo[] checkAdjacent = rc.senseNearbyMapInfos(3);
		for(int i = 0; i < checkAdjacent.length; i++){
			if (checkAdjacent[i].isSpawnZone() == true){
				return true;
			}
		}
		return false;
	}
	
	static int trapDetection(RobotController rc, TrapType trap){
		MapInfo[] detectingTrapTypes = rc.senseNearbyMapInfos();
		int counter = 0;
		for ( int i = 0; i < detectingTrapTypes.length; i++){
			TrapType detected =  detectingTrapTypes[i].getTrapType();
			if(detected == trap){
				counter = counter + 1;
		}
		}
		return counter;
	}


    
    public static boolean moveTo(RobotController rc, MapLocation loc) throws GameActionException {
    	return moveUnified(rc, loc, 3);
    }
    
    public static boolean moveTo(RobotController rc, MapLocation loc, int threshold) throws GameActionException {
    	return moveUnified(rc, loc, threshold);
    }
    
    public static boolean flee(RobotController rc, MapLocation loc) throws GameActionException {
    	MapLocation here = rc.getLocation();
    	int dx = loc.x - here.x;
    	int dy = loc.y - here.y;
    	return moveUnified(rc, new MapLocation(here.x - 2*dx, here.y - 2*dy), 3);
    }
    
    public static boolean flee(RobotController rc, MapLocation loc, int threshold) throws GameActionException {
    	MapLocation here = rc.getLocation();
    	int dx = loc.x - here.x;
    	int dy = loc.y - here.y;
    	return moveUnified(rc, new MapLocation(here.x - 2*dx, here.y - 2*dy), threshold);
    }
    
    public static boolean moveUnified(RobotController rc, MapLocation loc, int threshold) throws GameActionException {
    	if (rc.getLocation().equals(loc) || rc.getMovementCooldownTurns() >= 10) {
    		return false;
    	} else if(rc.getLocation().distanceSquaredTo(loc) > 2) {
    		if (wallRider(rc, loc, threshold)) {
    			return true;
    		} else if (!lookTwoMove(rc, loc)) {
    			return mooTwo(rc, loc);
    		} else {
    			return true;
    		}
    	} else {
    		return mooTwo(rc, loc);
    	}
    }
    
    public static boolean wallRider(RobotController rc, MapLocation loc, int threshold) throws GameActionException {
    	if (threshold == 0) {return false;}    	
    	MapLocation fakeSetPoint = rc.getLocation();
    	int useFakeSetPoint = 0;
    	MapLocation here = rc.getLocation();
    	
    	if (rc.onTheMap(here.add(Direction.NORTH)) && rc.senseMapInfo(here.add(Direction.NORTH)).isWall()) {fakeSetPoint = fakeSetPoint.add(Direction.EAST); useFakeSetPoint += 1;}
    	if (rc.onTheMap(here.add(Direction.NORTHEAST)) && rc.senseMapInfo(here.add(Direction.NORTHEAST)).isWall()) {fakeSetPoint = fakeSetPoint.add(Direction.SOUTHEAST); useFakeSetPoint += 1;}
    	if (rc.onTheMap(here.add(Direction.EAST)) && rc.senseMapInfo(here.add(Direction.EAST)).isWall()) {fakeSetPoint = fakeSetPoint.add(Direction.SOUTH); useFakeSetPoint += 1;}
    	if (rc.onTheMap(here.add(Direction.SOUTHEAST)) && rc.senseMapInfo(here.add(Direction.SOUTHEAST)).isWall()) {fakeSetPoint = fakeSetPoint.add(Direction.SOUTHWEST); useFakeSetPoint += 1;}
    	if (rc.onTheMap(here.add(Direction.SOUTH)) && rc.senseMapInfo(here.add(Direction.SOUTH)).isWall()) {fakeSetPoint = fakeSetPoint.add(Direction.WEST); useFakeSetPoint += 1;}
    	if (rc.onTheMap(here.add(Direction.SOUTHWEST)) && rc.senseMapInfo(here.add(Direction.SOUTHWEST)).isWall()) {fakeSetPoint = fakeSetPoint.add(Direction.NORTHWEST); useFakeSetPoint += 1;}
    	if (rc.onTheMap(here.add(Direction.WEST)) && rc.senseMapInfo(here.add(Direction.WEST)).isWall()) {fakeSetPoint = fakeSetPoint.add(Direction.NORTH); useFakeSetPoint += 1;}
    	if (rc.onTheMap(here.add(Direction.NORTHWEST)) && rc.senseMapInfo(here.add(Direction.NORTHWEST)).isWall()) {fakeSetPoint = fakeSetPoint.add(Direction.NORTHEAST); useFakeSetPoint += 1;}

    	Direction goalDir = here.directionTo(loc);
    	Direction fakeSetPointDir = here.directionTo(fakeSetPoint);
    	if (fakeSetPointDir.rotateRight().equals(goalDir) || fakeSetPointDir.rotateRight().rotateRight().equals(goalDir) || fakeSetPointDir.rotateRight().rotateRight().rotateRight().equals(goalDir)) {
    		return false;
    	}
    	
    	if (useFakeSetPoint >= threshold) {
    		rc.setIndicatorLine(here, fakeSetPoint, 0, 0, 0);
        	return mooTwo(rc, fakeSetPoint);
    	}
    	return false;
    }
    
    public static boolean lookTwoMove(RobotController rc, MapLocation loc) throws GameActionException {
    	int leastDistanceSquared = 65537;
    	int xOffset = 0;
    	int yOffset = 0;
    	int botX = rc.getLocation().x;
    	int botY = rc.getLocation().y;
    	
    	MapLocation locOffset = new MapLocation(rc.getLocation().x + 0, rc.getLocation().y + 2);
    	int distanceSquared = loc.distanceSquaredTo(locOffset);
    	if(distanceSquared < leastDistanceSquared) {
    		leastDistanceSquared = distanceSquared;
    		xOffset = 0;
    		yOffset = 2;
    	}
    	locOffset = new MapLocation(rc.getLocation().x + 1, rc.getLocation().y + 2);
    	distanceSquared = loc.distanceSquaredTo(locOffset);
    	if(distanceSquared < leastDistanceSquared) {
    		leastDistanceSquared = distanceSquared;
    		xOffset = 1;
    		yOffset = 2;
    	}
    	locOffset = new MapLocation(rc.getLocation().x + 2, rc.getLocation().y + 2);
    	distanceSquared = loc.distanceSquaredTo(locOffset);
    	if(distanceSquared < leastDistanceSquared) {
    		leastDistanceSquared = distanceSquared;
    		xOffset = 2;
    		yOffset = 2;
    	}
    	locOffset = new MapLocation(rc.getLocation().x + 2, rc.getLocation().y + 1);
    	distanceSquared = loc.distanceSquaredTo(locOffset);
    	if(distanceSquared < leastDistanceSquared) {
    		leastDistanceSquared = distanceSquared;
    		xOffset = 2;
    		yOffset = 1;
    	}
    	locOffset = new MapLocation(rc.getLocation().x + 2, rc.getLocation().y + 0);
    	distanceSquared = loc.distanceSquaredTo(locOffset);
    	if(distanceSquared < leastDistanceSquared) {
    		leastDistanceSquared = distanceSquared;
    		xOffset = 2;
    		yOffset = 0;
    	}
    	locOffset = new MapLocation(rc.getLocation().x + 2, rc.getLocation().y - 1);
    	distanceSquared = loc.distanceSquaredTo(locOffset);
    	if(distanceSquared < leastDistanceSquared) {
    		leastDistanceSquared = distanceSquared;
    		xOffset = 2;
    		yOffset = -1;
    	}
    	locOffset = new MapLocation(rc.getLocation().x + 2, rc.getLocation().y -2);
    	distanceSquared = loc.distanceSquaredTo(locOffset);
    	if(distanceSquared < leastDistanceSquared) {
    		leastDistanceSquared = distanceSquared;
    		xOffset = 2;
    		yOffset = -2;
    	}
    	locOffset = new MapLocation(rc.getLocation().x + 1, rc.getLocation().y -2);
    	distanceSquared = loc.distanceSquaredTo(locOffset);
    	if(distanceSquared < leastDistanceSquared) {
    		leastDistanceSquared = distanceSquared;
    		xOffset = 1;
    		yOffset = -2;
    	}
    	locOffset = new MapLocation(rc.getLocation().x + 0, rc.getLocation().y -2);
    	distanceSquared = loc.distanceSquaredTo(locOffset);
    	if(distanceSquared < leastDistanceSquared) {
    		leastDistanceSquared = distanceSquared;
    		xOffset = 0;
    		yOffset = -2;
    	}
    	locOffset = new MapLocation(rc.getLocation().x -1, rc.getLocation().y -2);
    	distanceSquared = loc.distanceSquaredTo(locOffset);
    	if(distanceSquared < leastDistanceSquared) {
    		leastDistanceSquared = distanceSquared;
    		xOffset = -1;
    		yOffset = -2;
    	}
    	locOffset = new MapLocation(rc.getLocation().x - 2, rc.getLocation().y -2);
    	distanceSquared = loc.distanceSquaredTo(locOffset);
    	if(distanceSquared < leastDistanceSquared) {
    		leastDistanceSquared = distanceSquared;
    		xOffset = -2;
    		yOffset = -2;
    	}
    	locOffset = new MapLocation(rc.getLocation().x - 2, rc.getLocation().y -1);
    	distanceSquared = loc.distanceSquaredTo(locOffset);
    	if(distanceSquared < leastDistanceSquared) {
    		leastDistanceSquared = distanceSquared;
    		xOffset = -2;
    		yOffset = -1;
    	}
    	locOffset = new MapLocation(rc.getLocation().x - 2, rc.getLocation().y + 0);
    	distanceSquared = loc.distanceSquaredTo(locOffset);
    	if(distanceSquared < leastDistanceSquared) {
    		leastDistanceSquared = distanceSquared;
    		xOffset = -2;
    		yOffset = 0;
    	}
    	locOffset = new MapLocation(rc.getLocation().x - 2, rc.getLocation().y + 1);
    	distanceSquared = loc.distanceSquaredTo(locOffset);
    	if(distanceSquared < leastDistanceSquared) {
    		leastDistanceSquared = distanceSquared;
    		xOffset = -2;
    		yOffset = 1;
    	}
    	locOffset = new MapLocation(rc.getLocation().x - 2, rc.getLocation().y + 2);
    	distanceSquared = loc.distanceSquaredTo(locOffset);
    	if(distanceSquared < leastDistanceSquared) {
    		leastDistanceSquared = distanceSquared;
    		xOffset = -2;
    		yOffset = 2;
    	}
    	locOffset = new MapLocation(rc.getLocation().x - 1, rc.getLocation().y + 2);
    	distanceSquared = loc.distanceSquaredTo(locOffset);
    	if(distanceSquared < leastDistanceSquared) {
    		leastDistanceSquared = distanceSquared;
    		xOffset = -1;
    		yOffset = 2;
    	}
    	
    	if (xOffset == 0 && yOffset == 2) {
    		if (moveN(rc)) {return true;} else if
    		(moveNNE(rc)) {return true;} else if
    		(moveNNW(rc)) {return true;} else if
    		(wallRider(rc, loc, 1)) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset-1,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset+1,botY+yOffset))) {return true;} else if
    		(moveNE(rc)) {return true;} else if
    		(moveNW(rc)) {return true;} else if
    		(moveENE(rc)) {return true;} else if
    		(moveWNW(rc)) {return true;} else if
    		(moveE(rc)) {return true;} else if
    		(moveW(rc)) {return true;} else if
    		(moveESE(rc)) {return true;} else if
    		(moveWSW(rc)) {return true;} else if
    		(moveSE(rc)) {return true;} else if
    		(moveSW(rc)) {return true;} else if
    		(moveSSE(rc)) {return true;} else if
    		(moveSSW(rc)) {return true;} else if
    		(moveS(rc)) {return true;} else return false;
    	}
    	
    	if (xOffset == 1 && yOffset == 2) {
    		if (moveNNE(rc)) {return true;} else if
    		(moveN(rc)) {return true;} else if
    		(moveNE(rc)) {return true;} else if
    		(wallRider(rc, loc, 1)) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset-1,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset+1,botY+yOffset))) {return true;} else if
    		(moveENE(rc)) {return true;} else if
    		(moveNNW(rc)) {return true;} else if
    		(moveE(rc)) {return true;} else if
    		(moveNW(rc)) {return true;} else if
    		(moveESE(rc)) {return true;} else if
    		(moveWNW(rc)) {return true;} else if
    		(moveSE(rc)) {return true;} else if
    		(moveW(rc)) {return true;} else if
    		(moveSSE(rc)) {return true;} else if
    		(moveWSW(rc)) {return true;} else if
    		(moveS(rc)) {return true;} else if
    		(moveSW(rc)) {return true;} else if
    		(moveSSW(rc)) {return true;} else return false;
    	}
    	
    	if (xOffset == 2 && yOffset == 2) {
    		if (moveNE(rc)) {return true;} else if
    		(moveENE(rc)) {return true;} else if
    		(moveNNE(rc)) {return true;} else if
    		(wallRider(rc, loc, 1)) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset-1,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset-1))) {return true;} else if
    		(moveE(rc)) {return true;} else if
    		(moveN(rc)) {return true;} else if
    		(moveESE(rc)) {return true;} else if
    		(moveNNW(rc)) {return true;} else if
    		(moveSE(rc)) {return true;} else if
    		(moveNW(rc)) {return true;} else if
    		(moveSSE(rc)) {return true;} else if
    		(moveWNW(rc)) {return true;} else if
    		(moveS(rc)) {return true;} else if
    		(moveW(rc)) {return true;} else if
    		(moveSSW(rc)) {return true;} else if
    		(moveWSW(rc)) {return true;} else if
    		(moveSW(rc)) {return true;} else return false;
    	}
    	
    	if (xOffset == 2 && yOffset == 1) {
    		if (moveENE(rc)) {return true;} else if
    		(moveNE(rc)) {return true;} else if
    		(moveE(rc)) {return true;} else if
    		(wallRider(rc, loc, 1)) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset+1))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset-1))) {return true;} else if
    		(moveESE(rc)) {return true;} else if
    		(moveNNE(rc)) {return true;} else if
    		(moveSE(rc)) {return true;} else if
    		(moveN(rc)) {return true;} else if
    		(moveSSE(rc)) {return true;} else if
    		(moveNNW(rc)) {return true;} else if
    		(moveS(rc)) {return true;} else if
    		(moveNW(rc)) {return true;} else if
    		(moveSSW(rc)) {return true;} else if
    		(moveWNW(rc)) {return true;} else if
    		(moveSW(rc)) {return true;} else if
    		(moveW(rc)) {return true;} else if
    		(moveWSW(rc)) {return true;} else return false;
    	}
    	
    	if (xOffset == 2 && yOffset == 0) {
    		if (moveE(rc)) {return true;} else if
    		(moveESE(rc)) {return true;} else if
    		(moveENE(rc)) {return true;} else if
    		(wallRider(rc, loc, 1)) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset+1))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset-1))) {return true;} else if
    		(moveSE(rc)) {return true;} else if
    		(moveNE(rc)) {return true;} else if
    		(moveSSE(rc)) {return true;} else if
    		(moveNNE(rc)) {return true;} else if
    		(moveS(rc)) {return true;} else if
    		(moveN(rc)) {return true;} else if
    		(moveSSW(rc)) {return true;} else if
    		(moveNNW(rc)) {return true;} else if
    		(moveSW(rc)) {return true;} else if
    		(moveNW(rc)) {return true;} else if
    		(moveWSW(rc)) {return true;} else if
    		(moveWNW(rc)) {return true;} else if
    		(moveW(rc)) {return true;} else return false;
    	}
    	
    	if (xOffset == 2 && yOffset == -1) {
    		if (moveESE(rc)) {return true;} else if
    		(moveE(rc)) {return true;} else if
    		(moveSE(rc)) {return true;} else if
    		(wallRider(rc, loc, 1)) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset+1))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset-1))) {return true;} else if
    		(moveSSE(rc)) {return true;} else if
    		(moveENE(rc)) {return true;} else if
    		(moveS(rc)) {return true;} else if
    		(moveNE(rc)) {return true;} else if
    		(moveSSW(rc)) {return true;} else if
    		(moveNNE(rc)) {return true;} else if
    		(moveSW(rc)) {return true;} else if
    		(moveN(rc)) {return true;} else if
    		(moveWSW(rc)) {return true;} else if
    		(moveNNW(rc)) {return true;} else if
    		(moveW(rc)) {return true;} else if
    		(moveNW(rc)) {return true;} else if
    		(moveWNW(rc)) {return true;} else return false;
    	}
    	
    	if (xOffset == 2 && yOffset == -2) {
    		if (moveSE(rc)) {return true;} else if
    		(moveSSE(rc)) {return true;} else if
    		(moveESE(rc)) {return true;} else if
    		(wallRider(rc, loc, 1)) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset+1))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset-1,botY+yOffset))) {return true;} else if
    		(moveS(rc)) {return true;} else if
    		(moveE(rc)) {return true;} else if
    		(moveSSW(rc)) {return true;} else if
    		(moveENE(rc)) {return true;} else if
    		(moveSW(rc)) {return true;} else if
    		(moveNE(rc)) {return true;} else if
    		(moveWSW(rc)) {return true;} else if
    		(moveNNE(rc)) {return true;} else if
    		(moveW(rc)) {return true;} else if
    		(moveN(rc)) {return true;} else if
    		(moveWNW(rc)) {return true;} else if
    		(moveNNW(rc)) {return true;} else if
    		(moveNW(rc)) {return true;} else return false;
    	}
    	
    	if (xOffset == 1 && yOffset == -2) {
    		if (moveSSE(rc)) {return true;} else if
    		(moveSE(rc)) {return true;} else if
    		(moveS(rc)) {return true;} else if
    		(wallRider(rc, loc, 1)) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset+1,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset-1,botY+yOffset))) {return true;} else if
    		(moveSSW(rc)) {return true;} else if
    		(moveESE(rc)) {return true;} else if
    		(moveSW(rc)) {return true;} else if
    		(moveE(rc)) {return true;} else if
    		(moveWSW(rc)) {return true;} else if
    		(moveENE(rc)) {return true;} else if
    		(moveW(rc)) {return true;} else if
    		(moveNE(rc)) {return true;} else if
    		(moveWNW(rc)) {return true;} else if
    		(moveNNE(rc)) {return true;} else if
    		(moveNW(rc)) {return true;} else if
    		(moveN(rc)) {return true;} else if
    		(moveNNW(rc)) {return true;} else return false;
    	}
    	
    	if (xOffset == 0 && yOffset == -2) {
    		if (moveS(rc)) {return true;} else if
    		(moveSSW(rc)) {return true;} else if
    		(moveSSE(rc)) {return true;} else if
    		(wallRider(rc, loc, 1)) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset+1,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset-1,botY+yOffset))) {return true;} else if
    		(moveSW(rc)) {return true;} else if
    		(moveSE(rc)) {return true;} else if
    		(moveWSW(rc)) {return true;} else if
    		(moveESE(rc)) {return true;} else if
    		(moveW(rc)) {return true;} else if
    		(moveE(rc)) {return true;} else if
    		(moveWNW(rc)) {return true;} else if
    		(moveENE(rc)) {return true;} else if
    		(moveNW(rc)) {return true;} else if
    		(moveNE(rc)) {return true;} else if
    		(moveNNW(rc)) {return true;} else if
    		(moveNNE(rc)) {return true;} else if
    		(moveN(rc)) {return true;} else return false;
    	}
    	
    	if (xOffset == -1 && yOffset == -2) {
    		if (moveSSW(rc)) {return true;} else if
    		(moveS(rc)) {return true;} else if
    		(moveSW(rc)) {return true;} else if
    		(wallRider(rc, loc, 1)) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset+1,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset-1,botY+yOffset))) {return true;} else if
    		(moveWSW(rc)) {return true;} else if
    		(moveSSE(rc)) {return true;} else if
    		(moveW(rc)) {return true;} else if
    		(moveSE(rc)) {return true;} else if
    		(moveWNW(rc)) {return true;} else if
    		(moveESE(rc)) {return true;} else if
    		(moveNW(rc)) {return true;} else if
    		(moveE(rc)) {return true;} else if
    		(moveNNW(rc)) {return true;} else if
    		(moveENE(rc)) {return true;} else if
    		(moveN(rc)) {return true;} else if
    		(moveNE(rc)) {return true;} else if
    		(moveNNE(rc)) {return true;} else return false;
    	}
    	
    	if (xOffset == -2 && yOffset == -2) {
    		if (moveSW(rc)) {return true;} else if
    		(moveWSW(rc)) {return true;} else if
    		(moveSSW(rc)) {return true;} else if
    		(wallRider(rc, loc, 1)) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset+1))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset+1,botY+yOffset))) {return true;} else if
    		(moveW(rc)) {return true;} else if
    		(moveS(rc)) {return true;} else if
    		(moveWNW(rc)) {return true;} else if
    		(moveSSE(rc)) {return true;} else if
    		(moveNW(rc)) {return true;} else if
    		(moveSE(rc)) {return true;} else if
    		(moveNNW(rc)) {return true;} else if
    		(moveESE(rc)) {return true;} else if
    		(moveN(rc)) {return true;} else if
    		(moveE(rc)) {return true;} else if
    		(moveNNE(rc)) {return true;} else if
    		(moveENE(rc)) {return true;} else if
    		(moveNE(rc)) {return true;} else return false;
    	}
    	
    	if (xOffset == -2 && yOffset == -1) {
    		if (moveWSW(rc)) {return true;} else if
    		(moveSW(rc)) {return true;} else if
    		(moveW(rc)) {return true;} else if
    		(wallRider(rc, loc, 1)) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset+1))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset-1))) {return true;} else if
    		(moveWNW(rc)) {return true;} else if
    		(moveSSW(rc)) {return true;} else if
    		(moveNW(rc)) {return true;} else if
    		(moveS(rc)) {return true;} else if
    		(moveNNW(rc)) {return true;} else if
    		(moveSSE(rc)) {return true;} else if
    		(moveN(rc)) {return true;} else if
    		(moveSE(rc)) {return true;} else if
    		(moveNNE(rc)) {return true;} else if
    		(moveESE(rc)) {return true;} else if
    		(moveNE(rc)) {return true;} else if
    		(moveE(rc)) {return true;} else if
    		(moveENE(rc)) {return true;} else return false;
    	}
    	
    	if (xOffset == -2 && yOffset == 0) {
    		if (moveW(rc)) {return true;} else if
    		(moveWNW(rc)) {return true;} else if
    		(moveWSW(rc)) {return true;} else if
    		(wallRider(rc, loc, 1)) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset+1))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset-1))) {return true;} else if
    		(moveNW(rc)) {return true;} else if
    		(moveSW(rc)) {return true;} else if
    		(moveNNW(rc)) {return true;} else if
    		(moveSSW(rc)) {return true;} else if
    		(moveN(rc)) {return true;} else if
    		(moveS(rc)) {return true;} else if
    		(moveNNE(rc)) {return true;} else if
    		(moveSSE(rc)) {return true;} else if
    		(moveNE(rc)) {return true;} else if
    		(moveSE(rc)) {return true;} else if
    		(moveENE(rc)) {return true;} else if
    		(moveESE(rc)) {return true;} else if
    		(moveE(rc)) {return true;} else return false;
    	}
    	
    	if (xOffset == -2 && yOffset == 1) {
    		if (moveWNW(rc)) {return true;} else if
    		(moveW(rc)) {return true;} else if
    		(moveNW(rc)) {return true;} else if
    		(wallRider(rc, loc, 1)) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset+1))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset-1))) {return true;} else if
    		(moveNNW(rc)) {return true;} else if
    		(moveWSW(rc)) {return true;} else if
    		(moveN(rc)) {return true;} else if
    		(moveSW(rc)) {return true;} else if
    		(moveNNE(rc)) {return true;} else if
    		(moveSSW(rc)) {return true;} else if
    		(moveNE(rc)) {return true;} else if
    		(moveS(rc)) {return true;} else if
    		(moveENE(rc)) {return true;} else if
    		(moveSSE(rc)) {return true;} else if
    		(moveE(rc)) {return true;} else if
    		(moveSE(rc)) {return true;} else if
    		(moveESE(rc)) {return true;} else return false;
    	}
    	
    	if (xOffset == -2 && yOffset == 2) {
    		if (moveNW(rc)) {return true;} else if
    		(moveNNW(rc)) {return true;} else if
    		(moveWNW(rc)) {return true;} else if
    		(wallRider(rc, loc, 1)) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset+1,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset-1))) {return true;} else if
    		(moveN(rc)) {return true;} else if
    		(moveW(rc)) {return true;} else if
    		(moveNNE(rc)) {return true;} else if
    		(moveWSW(rc)) {return true;} else if
    		(moveNE(rc)) {return true;} else if
    		(moveSW(rc)) {return true;} else if
    		(moveENE(rc)) {return true;} else if
    		(moveSSW(rc)) {return true;} else if
    		(moveE(rc)) {return true;} else if
    		(moveS(rc)) {return true;} else if
    		(moveESE(rc)) {return true;} else if
    		(moveSSE(rc)) {return true;} else if
    		(moveSE(rc)) {return true;} else return false;
    	}
    	
    	if (xOffset == -1 && yOffset == 2) {
    		if (moveNNW(rc)) {return true;} else if
    		(moveNW(rc)) {return true;} else if
    		(moveN(rc)) {return true;} else if
    		(wallRider(rc, loc, 1)) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset+1,botY+yOffset))) {return true;} else if
    		(mooToo(rc, new MapLocation(botX+xOffset-1,botY+yOffset))) {return true;} else if
    		(moveNNE(rc)) {return true;} else if
    		(moveWNW(rc)) {return true;} else if
    		(moveNE(rc)) {return true;} else if
    		(moveW(rc)) {return true;} else if
    		(moveENE(rc)) {return true;} else if
    		(moveWSW(rc)) {return true;} else if
    		(moveE(rc)) {return true;} else if
    		(moveSW(rc)) {return true;} else if
    		(moveESE(rc)) {return true;} else if
    		(moveSSW(rc)) {return true;} else if
    		(moveSE(rc)) {return true;} else if
    		(moveS(rc)) {return true;} else if
    		(moveSSE(rc)) {return true;} else return false;
    	}
    	
    	return false;
    }
    
    public static boolean moveN(RobotController rc) throws GameActionException {
    	//rc.setIndicatorString("Greedy N");
    	MapLocation lookTwo = rc.getLocation().add(Direction.NORTH).add(Direction.NORTH);
    	if (!rc.onTheMap(lookTwo)) {
    		return false;
    	} else if (rc.senseMapInfo(lookTwo).isWall()) {
    		return false;
    	}
    	if (moveN2(rc)) {return true;} 
    	else if (rc.canFill(rc.getLocation().add(Direction.NORTH))) {rc.fill(rc.getLocation().add(Direction.NORTH));}
    	else if (rc.canFill(rc.getLocation().add(Direction.NORTHEAST))) {rc.fill(rc.getLocation().add(Direction.NORTHEAST));}
    	else if (rc.canFill(rc.getLocation().add(Direction.NORTHWEST))) {rc.fill(rc.getLocation().add(Direction.NORTHWEST));}
    	return moveN2(rc);
    }
    
    public static boolean moveN2(RobotController rc) throws GameActionException {
    	if (rc.canMove(Direction.NORTH)) {
    		rc.move(Direction.NORTH); 
    		return true;
    	} else if (rc.canMove(Direction.NORTHEAST)) {
    		rc.move(Direction.NORTHEAST); 
    		return true;
    	} else if (rc.canMove(Direction.NORTHWEST)) {
    		rc.move(Direction.NORTHWEST); 
    		return true;
    	}
    	return false;
    }
    
    public static boolean moveNNE(RobotController rc) throws GameActionException {
    	//rc.setIndicatorString("Greedy NNE");
    	MapLocation lookTwo = rc.getLocation().add(Direction.NORTH).add(Direction.NORTHEAST);
    	if (!rc.onTheMap(lookTwo)) {
    		return false;
    	} else if (rc.senseMapInfo(lookTwo).isWall()) {
    		return false;
    	}
    	if (moveNNE2(rc)) {return true;}
    	else if (rc.canFill(rc.getLocation().add(Direction.NORTH))) {rc.fill(rc.getLocation().add(Direction.NORTH));}
    	else if (rc.canFill(rc.getLocation().add(Direction.NORTHEAST))) {rc.fill(rc.getLocation().add(Direction.NORTHEAST));}
    	return moveNNE2(rc);
    }
    
    public static boolean moveNNE2(RobotController rc) throws GameActionException {
    	if (rc.canMove(Direction.NORTH)) {
    		rc.move(Direction.NORTH); 
    		return true;
    	} else if (rc.canMove(Direction.NORTHEAST)) {
    		rc.move(Direction.NORTHEAST); 
    		return true;
    	}
    	return false;
    }
    
    public static boolean moveNE(RobotController rc) throws GameActionException {
    	//rc.setIndicatorString("Greedy NE");
    	MapLocation lookTwo = rc.getLocation().add(Direction.NORTHEAST).add(Direction.NORTHEAST);
    	if (!rc.onTheMap(lookTwo)) {
    		return false;
    	} else if (rc.senseMapInfo(lookTwo).isWall()) {
    		return false;
    	}
    	if (moveNE2(rc)) {return true;}
    	else if (rc.canFill(rc.getLocation().add(Direction.NORTHEAST))) {rc.fill(rc.getLocation().add(Direction.NORTHEAST));}
    	return moveNE2(rc);
    }
    
    public static boolean moveNE2(RobotController rc) throws GameActionException {
    	if (rc.canMove(Direction.NORTHEAST)) {
    		rc.move(Direction.NORTHEAST); 
    		return true;
    	}
    	return false;
    }
    
    public static boolean moveENE(RobotController rc) throws GameActionException {
    	//rc.setIndicatorString("Greedy ENE");
    	MapLocation lookTwo = rc.getLocation().add(Direction.EAST).add(Direction.NORTHEAST);
    	if (!rc.onTheMap(lookTwo)) {
    		return false;
    	} else if (rc.senseMapInfo(lookTwo).isWall()) {
    		return false;
    	}
    	if (moveENE2(rc)) {return true;}
    	else if (rc.canFill(rc.getLocation().add(Direction.EAST))) {rc.fill(rc.getLocation().add(Direction.EAST));}
    	else if (rc.canFill(rc.getLocation().add(Direction.NORTHEAST))) {rc.fill(rc.getLocation().add(Direction.NORTHEAST));}
    	return moveENE2(rc);
    }
    
    public static boolean moveENE2(RobotController rc) throws GameActionException {
    	if (rc.canMove(Direction.EAST)) {
    		rc.move(Direction.EAST); 
    		return true;
    	} else if (rc.canMove(Direction.NORTHEAST)) {
    		rc.move(Direction.NORTHEAST); 
    		return true;
    	}
    	return false;
    }
    
    public static boolean moveE(RobotController rc) throws GameActionException {
    	//rc.setIndicatorString("Greedy E");
    	MapLocation lookTwo = rc.getLocation().add(Direction.EAST).add(Direction.EAST);
    	if (!rc.onTheMap(lookTwo)) {
    		return false;
    	} else if (rc.senseMapInfo(lookTwo).isWall()) {
    		return false;
    	}
    	if (moveE2(rc)) {return true;}
    	else if (rc.canFill(rc.getLocation().add(Direction.EAST))) {rc.fill(rc.getLocation().add(Direction.EAST));}
    	else if (rc.canFill(rc.getLocation().add(Direction.NORTHEAST))) {rc.fill(rc.getLocation().add(Direction.NORTHEAST));}
    	else if (rc.canFill(rc.getLocation().add(Direction.SOUTHEAST))) {rc.fill(rc.getLocation().add(Direction.SOUTHEAST));}
    	return moveE2(rc);
    }
    
    public static boolean moveE2(RobotController rc) throws GameActionException {
    	if (rc.canMove(Direction.EAST)) {
    		rc.move(Direction.EAST); 
    		return true;
    	} else if (rc.canMove(Direction.NORTHEAST)) {
    		rc.move(Direction.NORTHEAST); 
    		return true;
    	} else if (rc.canMove(Direction.SOUTHEAST)) {
    		rc.move(Direction.SOUTHEAST); 
    		return true;
    	} 
    	return false;
    }
    
    public static boolean moveESE(RobotController rc) throws GameActionException {
    	//rc.setIndicatorString("Greedy ESE");
    	MapLocation lookTwo = rc.getLocation().add(Direction.EAST).add(Direction.SOUTHEAST);
    	if (!rc.onTheMap(lookTwo)) {
    		return false;
    	} else if (rc.senseMapInfo(lookTwo).isWall()) {
    		return false;
    	}
    	if (moveESE2(rc)) {return true;}
    	else if (rc.canFill(rc.getLocation().add(Direction.EAST))) {rc.fill(rc.getLocation().add(Direction.EAST));}
    	else if (rc.canFill(rc.getLocation().add(Direction.SOUTHEAST))) {rc.fill(rc.getLocation().add(Direction.SOUTHEAST));}
    	return moveESE2(rc);
    }
    
    public static boolean moveESE2(RobotController rc) throws GameActionException {
    	if (rc.canMove(Direction.EAST)) {
    		rc.move(Direction.EAST); 
    		return true;
    	} else if (rc.canMove(Direction.SOUTHEAST)) {
    		rc.move(Direction.SOUTHEAST); 
    		return true;
    	} 
    	return false;
    }
    
    public static boolean moveSE(RobotController rc) throws GameActionException {
    	//rc.setIndicatorString("Greedy SE");
    	MapLocation lookTwo = rc.getLocation().add(Direction.SOUTHEAST).add(Direction.SOUTHEAST);
    	if (!rc.onTheMap(lookTwo)) {
    		return false;
    	} else if (rc.senseMapInfo(lookTwo).isWall()) {
    		return false;
    	}
    	if (moveSE2(rc)) {return true;}
    	else if (rc.canFill(rc.getLocation().add(Direction.SOUTHEAST))) {rc.fill(rc.getLocation().add(Direction.SOUTHEAST));}
    	return moveSE2(rc);
    }
    
    public static boolean moveSE2(RobotController rc) throws GameActionException {
    	if (rc.canMove(Direction.SOUTHEAST)) {
    		rc.move(Direction.SOUTHEAST); 
    		return true;
    	} 
    	return false;
    }
    
    public static boolean moveSSE(RobotController rc) throws GameActionException {
    	//rc.setIndicatorString("Greedy SSE");
    	MapLocation lookTwo = rc.getLocation().add(Direction.SOUTH).add(Direction.SOUTHEAST);
    	if (!rc.onTheMap(lookTwo)) {
    		return false;
    	} else if (rc.senseMapInfo(lookTwo).isWall()) {
    		return false;
    	}
    	if (moveSSE2(rc)) {return true;}
    	else if (rc.canFill(rc.getLocation().add(Direction.SOUTH))) {rc.fill(rc.getLocation().add(Direction.SOUTH));}
    	else if (rc.canFill(rc.getLocation().add(Direction.SOUTHEAST))) {rc.fill(rc.getLocation().add(Direction.SOUTHEAST));}
    	return moveSSE2(rc);
    }
    
    public static boolean moveSSE2(RobotController rc) throws GameActionException {
    	if (rc.canMove(Direction.SOUTH)) {
    		rc.move(Direction.SOUTH); 
    		return true;
    	} else if (rc.canMove(Direction.SOUTHEAST)) {
    		rc.move(Direction.SOUTHEAST); 
    		return true;
    	} 
    	return false;
    }
    
    public static boolean moveS(RobotController rc) throws GameActionException {
    	//rc.setIndicatorString("Greedy S");
    	MapLocation lookTwo = rc.getLocation().add(Direction.SOUTH).add(Direction.SOUTH);
    	if (!rc.onTheMap(lookTwo)) {
    		return false;
    	} else if (rc.senseMapInfo(lookTwo).isWall()) {
    		return false;
    	}
    	if (moveS2(rc)) {return true;}
    	else if (rc.canFill(rc.getLocation().add(Direction.SOUTH))) {rc.fill(rc.getLocation().add(Direction.SOUTH));}
    	else if (rc.canFill(rc.getLocation().add(Direction.SOUTHWEST))) {rc.fill(rc.getLocation().add(Direction.SOUTHWEST));}
    	else if (rc.canFill(rc.getLocation().add(Direction.SOUTHEAST))) {rc.fill(rc.getLocation().add(Direction.SOUTHEAST));}
    	return moveS2(rc);
    }
    
    public static boolean moveS2(RobotController rc) throws GameActionException {
    	if (rc.canMove(Direction.SOUTH)) {
    		rc.move(Direction.SOUTH); 
    		if (rc.canFill(rc.getLocation().add(Direction.SOUTH))) {rc.fill(rc.getLocation().add(Direction.SOUTH));};
    		return true;
    	} else if (rc.canMove(Direction.SOUTHWEST)) {
    		rc.move(Direction.SOUTHWEST); 
    		if (rc.canFill(rc.getLocation().add(Direction.SOUTHEAST))) {rc.fill(rc.getLocation().add(Direction.SOUTHEAST));};
    		return true;
    	} else if (rc.canMove(Direction.SOUTHEAST)) {
    		rc.move(Direction.SOUTHEAST); 
    		if (rc.canFill(rc.getLocation().add(Direction.SOUTHWEST))) {rc.fill(rc.getLocation().add(Direction.SOUTHWEST));};
    		return true;
    	} 
    	return false;
    }
    
    public static boolean moveSSW(RobotController rc) throws GameActionException {
    	//rc.setIndicatorString("Greedy SSW");
    	MapLocation lookTwo = rc.getLocation().add(Direction.SOUTH).add(Direction.SOUTHWEST);
    	if (!rc.onTheMap(lookTwo)) {
    		return false;
    	} else if (rc.senseMapInfo(lookTwo).isWall()) {
    		return false;
    	}
    	if (moveSSW2(rc)) {return true;}
    	else if (rc.canFill(rc.getLocation().add(Direction.SOUTH))) {rc.fill(rc.getLocation().add(Direction.SOUTH));}
    	else if (rc.canFill(rc.getLocation().add(Direction.SOUTHWEST))) {rc.fill(rc.getLocation().add(Direction.SOUTHWEST));}
    	return moveSSW2(rc);
    }
    
    public static boolean moveSSW2(RobotController rc) throws GameActionException {
    	if (rc.canMove(Direction.SOUTH)) {
    		rc.move(Direction.SOUTH); 
    		return true;
    	} else if (rc.canMove(Direction.SOUTHWEST)) {
    		rc.move(Direction.SOUTHWEST); 
    		return true;
    	} 
    	return false;
    }
    
    public static boolean moveSW(RobotController rc) throws GameActionException {
    	//rc.setIndicatorString("Greedy SW");
    	MapLocation lookTwo = rc.getLocation().add(Direction.SOUTHWEST).add(Direction.SOUTHWEST);
    	if (!rc.onTheMap(lookTwo)) {
    		return false;
    	} else if (rc.senseMapInfo(lookTwo).isWall()) {
    		return false;
    	}
    	if (moveSW2(rc)) {return true;}
    	else if (rc.canFill(rc.getLocation().add(Direction.SOUTHWEST))) {rc.fill(rc.getLocation().add(Direction.SOUTHWEST));}
    	return moveSW2(rc);
    }
    
    public static boolean moveSW2(RobotController rc) throws GameActionException {
    	if (rc.canMove(Direction.SOUTHWEST)) {
    		rc.move(Direction.SOUTHWEST); 
    		return true;
    	} 
    	return false;
    }
    
    public static boolean moveWSW(RobotController rc) throws GameActionException {
    	//rc.setIndicatorString("Greedy WSW");
    	MapLocation lookTwo = rc.getLocation().add(Direction.WEST).add(Direction.SOUTHWEST);
    	if (!rc.onTheMap(lookTwo)) {
    		return false;
    	} else if (rc.senseMapInfo(lookTwo).isWall()) {
    		return false;
    	}
    	if (moveWSW2(rc)) {return true;}
    	else if (rc.canFill(rc.getLocation().add(Direction.WEST))) {rc.fill(rc.getLocation().add(Direction.WEST));}
    	else if (rc.canFill(rc.getLocation().add(Direction.SOUTHWEST))) {rc.fill(rc.getLocation().add(Direction.SOUTHWEST));}
    	return moveWSW2(rc);
    }
    
    public static boolean moveWSW2(RobotController rc) throws GameActionException {
    	if (rc.canMove(Direction.WEST)) {
    		rc.move(Direction.WEST); 
    		return true;
    	} else if (rc.canMove(Direction.SOUTHWEST)) {
    		rc.move(Direction.SOUTHWEST); 
    		return true;
    	} 
    	return false;
    }
    
    public static boolean moveW(RobotController rc) throws GameActionException {
    	//rc.setIndicatorString("Greedy W");
    	MapLocation lookTwo = rc.getLocation().add(Direction.WEST).add(Direction.WEST);
    	if (!rc.onTheMap(lookTwo)) {
    		return false;
    	} else if (rc.senseMapInfo(lookTwo).isWall()) {
    		return false;
    	}
    	if (moveW2(rc)) {return true;}
    	else if (rc.canFill(rc.getLocation().add(Direction.WEST))) {rc.fill(rc.getLocation().add(Direction.WEST));}
    	else if (rc.canFill(rc.getLocation().add(Direction.SOUTHWEST))) {rc.fill(rc.getLocation().add(Direction.SOUTHWEST));}
    	else if (rc.canFill(rc.getLocation().add(Direction.NORTHWEST))) {rc.fill(rc.getLocation().add(Direction.NORTHWEST));}
    	return moveW2(rc);
    }
    
    public static boolean moveW2(RobotController rc) throws GameActionException {
    	if (rc.canMove(Direction.WEST)) {
    		rc.move(Direction.WEST); 
    		return true;
    	} else if (rc.canMove(Direction.SOUTHWEST)) {
    		rc.move(Direction.SOUTHWEST); 
    		return true;
    	} else if (rc.canMove(Direction.NORTHWEST)) {
    		rc.move(Direction.NORTHWEST); 
    		return true;
    	} 
    	return false;
    }
    
    public static boolean moveWNW(RobotController rc) throws GameActionException {
    	//rc.setIndicatorString("Greedy WNW");
    	MapLocation lookTwo = rc.getLocation().add(Direction.WEST).add(Direction.NORTHWEST);
    	if (!rc.onTheMap(lookTwo)) {
    		return false;
    	} else if (rc.senseMapInfo(lookTwo).isWall()) {
    		return false;
    	}
    	if (moveWNW2(rc)) {return true;}
    	else if (rc.canFill(rc.getLocation().add(Direction.WEST))) {rc.fill(rc.getLocation().add(Direction.WEST));}
    	else if (rc.canFill(rc.getLocation().add(Direction.NORTHWEST))) {rc.fill(rc.getLocation().add(Direction.NORTHWEST));}
    	return moveWNW2(rc);
    }

    public static boolean moveWNW2(RobotController rc) throws GameActionException {
    	if (rc.canMove(Direction.WEST)) {
    		rc.move(Direction.WEST); 
    		return true;
    	} else if (rc.canMove(Direction.NORTHWEST)) {
    		rc.move(Direction.NORTHWEST); 
    		return true;
    	} 
    	return false;
    }
    
    public static boolean moveNW(RobotController rc) throws GameActionException {
    	//rc.setIndicatorString("Greedy NW");
    	MapLocation lookTwo = rc.getLocation().add(Direction.NORTHWEST).add(Direction.NORTHWEST);
    	if (!rc.onTheMap(lookTwo)) {
    		return false;
    	} else if (rc.senseMapInfo(lookTwo).isWall()) {
    		return false;
    	}
    	if (moveNW2(rc)) {return true;}
    	else if (rc.canFill(rc.getLocation().add(Direction.NORTHWEST))) {rc.fill(rc.getLocation().add(Direction.NORTHWEST));}
    	return moveNW2(rc);
    }
    
    public static boolean moveNW2(RobotController rc) throws GameActionException {
    	if (rc.canMove(Direction.NORTHWEST)) {
    		rc.move(Direction.NORTHWEST); 
    		return true;
    	} 
    	return false;
    }
    
    public static boolean moveNNW(RobotController rc) throws GameActionException {
    	//rc.setIndicatorString("Greedy NNW");
    	MapLocation lookTwo = rc.getLocation().add(Direction.NORTH).add(Direction.NORTHWEST);
    	if (!rc.onTheMap(lookTwo)) {
    		return false;
    	} else if (rc.senseMapInfo(lookTwo).isWall()) {
    		return false;
    	}
    	if (moveNNW2(rc)) {return true;}
    	else if (rc.canFill(rc.getLocation().add(Direction.NORTH))) {rc.fill(rc.getLocation().add(Direction.NORTH));}
    	else if (rc.canFill(rc.getLocation().add(Direction.NORTHWEST))) {rc.fill(rc.getLocation().add(Direction.NORTHWEST));}
    	return moveNNW2(rc);
    }
    
    public static boolean moveNNW2(RobotController rc) throws GameActionException {
    	if (rc.canMove(Direction.NORTH)) {
    		rc.move(Direction.NORTH); 
    		return true;
    	} else if (rc.canMove(Direction.NORTHWEST)) {
    		rc.move(Direction.NORTHWEST); 
    		return true;
    	} 
    	return false;
    }

    public static Direction dirSecDir(MapLocation fromLoc, MapLocation toLoc) {
        if (fromLoc == null) {
            return null;
        }

        if (toLoc == null) {
            return null;
        }

        double dx = toLoc.x - fromLoc.x;
        double dy = toLoc.y - fromLoc.y;

        if (Math.abs(dx) >= 2.414 * Math.abs(dy)) {
            if (dx > 0) {
                if (dy > 0) {
                    return Direction.NORTHEAST;
                } else {
                    return Direction.SOUTHEAST;
                }
            } else if (dx < 0) {
                 if (dy > 0) {
                    return Direction.NORTHWEST;
                } else {
                    return Direction.SOUTHWEST;
                }
            } else {
                return Direction.CENTER;
            }
        } else if (Math.abs(dy) >= 2.414 * Math.abs(dx)) {
            if (dy > 0) {
                 if (dx > 0) {
                    return Direction.NORTHEAST;
                } else {
                    return Direction.NORTHWEST;
                }
            } else {
                if (dx > 0) {
                    return Direction.SOUTHEAST;
                } else {
                    return Direction.SOUTHWEST;
                }
            }
        } else {
            if (dy > 0) {
                if (dx > 0) {
                    if (Math.abs(dx) > Math.abs(dy)) {
                        return Direction.EAST;
                    } else {
                        return Direction.NORTH;
                    }
                } else {
                    if (Math.abs(dx) > Math.abs(dy)) {
                        return Direction.WEST;
                    } else {
                        return Direction.NORTH;
                    }
                }
            } else {
                if (dx > 0) {
                    if (Math.abs(dx) > Math.abs(dy)) {
                        return Direction.EAST;
                    } else {
                        return Direction.SOUTH;
                    }
                } else {
                    if (Math.abs(dx) > Math.abs(dy)) {
                        return Direction.WEST;
                    } else {
                        return Direction.SOUTH;
                    }
                }
            }
        }
    }

    public static boolean mooTwo(RobotController rc, MapLocation loc) throws GameActionException {
        Direction dir = rc.getLocation().directionTo(loc);
        if (dir == Direction.CENTER) {
        	int width = rc.getMapWidth();
            int height = rc.getMapHeight();
        	int centerWidth = Math.round(width/2);
            int centerHeight = Math.round(height/2);
            MapLocation centerOfMap = new MapLocation(centerWidth, centerHeight);
        	dir = rc.getLocation().directionTo(centerOfMap);
        }
        Direction secDir = dirSecDir(rc.getLocation(), loc);
        return scoot(rc, dir, secDir, false);
    }
    
    public static boolean mooToo(RobotController rc, MapLocation loc) throws GameActionException {
        Direction dir = rc.getLocation().directionTo(loc);
        if (dir == Direction.CENTER) {
        	int width = rc.getMapWidth();
            int height = rc.getMapHeight();
        	int centerWidth = Math.round(width/2);
            int centerHeight = Math.round(height/2);
            MapLocation centerOfMap = new MapLocation(centerWidth, centerHeight);
        	dir = rc.getLocation().directionTo(centerOfMap);
        }
        Direction secDir = dirSecDir(rc.getLocation(), loc);
        return scoot(rc, dir, secDir, true);
    }
    
    public static boolean scoot(RobotController rc, Direction dir, Direction secDir, boolean restrictive) throws GameActionException {
    	rc.setIndicatorString("Ultra Greedy " + dir.toString());
    	if (rc.canMove(dir) || rc.canFill(rc.getLocation().add(dir))) {
    		if(!rc.canMove(dir)) {
    			rc.fill(rc.getLocation().add(dir));
    			return true;
    		} else {
                rc.move(dir);
    			return true;
    		}
        } else if (rc.canMove(secDir) || rc.canFill(rc.getLocation().add(secDir))) {
        	if(!rc.canMove(secDir)) {
    			rc.fill(rc.getLocation().add(secDir));
    			return true;
    		} else {
                rc.move(secDir);
    			return true;
    		}
        } else if (dir.rotateLeft() == secDir) {
        	if (rc.canMove(dir.rotateRight()) || rc.canFill(rc.getLocation().add(dir.rotateRight()))) {
        		if(!rc.canMove(dir.rotateRight())) {
        			rc.fill(rc.getLocation().add(dir.rotateRight()));
        			return true;
        		} else {
                    rc.move(dir.rotateRight());
        			return true;
        		}
        	} else if (restrictive) {
        		return false;
        	} else if (rc.canMove(dir.rotateLeft().rotateLeft()) || rc.canFill(rc.getLocation().add(dir.rotateLeft().rotateLeft()))) {
        		if(!rc.canMove(dir.rotateLeft().rotateLeft())) {
        			rc.fill(rc.getLocation().add(dir.rotateLeft().rotateLeft()));
        			return true;
        		} else {
            		rc.move(dir.rotateLeft().rotateLeft());
        			return true;
        		}
        	} else if (rc.canMove(dir.rotateRight().rotateRight()) || rc.canFill(rc.getLocation().add(dir.rotateRight().rotateRight()))) {
        		if(!rc.canMove(dir.rotateRight().rotateRight())) {
        			rc.fill(rc.getLocation().add(dir.rotateRight().rotateRight()));
        			return true;
        		} else {
            		rc.move(dir.rotateRight().rotateRight());
        			return true;
        		}
        	} else if (rc.canMove(dir.rotateLeft().rotateLeft().rotateLeft()) || rc.canFill(rc.getLocation().add(dir.rotateLeft().rotateLeft().rotateLeft()))) {
        		if(!rc.canMove(dir.rotateLeft().rotateLeft().rotateLeft())) {
        			rc.fill(rc.getLocation().add(dir.rotateLeft().rotateLeft().rotateLeft()));
        			return true;
        		} else {
            		rc.move(dir.rotateLeft().rotateLeft().rotateLeft());
        			return true;
        		}
        	} else if (rc.canMove(dir.rotateRight().rotateRight().rotateRight()) || rc.canFill(rc.getLocation().add(dir.rotateRight().rotateRight().rotateRight()))) {
        		if(!rc.canMove(dir.rotateRight().rotateRight().rotateRight())) {
        			rc.fill(rc.getLocation().add(dir.rotateRight().rotateRight().rotateRight()));
        			return true;
        		} else {
            		rc.move(dir.rotateRight().rotateRight().rotateRight());
        			return true;
        		}
        	}
        } else if (rc.canMove(dir.rotateLeft()) || rc.canFill(rc.getLocation().add(dir.rotateLeft()))) {
        	if(!rc.canMove(dir.rotateLeft())) {
    			rc.fill(rc.getLocation().add(dir.rotateLeft()));
    			return true;
    		} else {
            	rc.move(dir.rotateLeft());
    			return true;
    		}
    	} else if (restrictive) {
    		return false;
    	} else if (rc.canMove(dir.rotateRight().rotateRight()) || rc.canFill(rc.getLocation().add(dir.rotateRight().rotateRight()))) {
    		if(!rc.canMove(dir.rotateRight().rotateRight())) {
    			rc.fill(rc.getLocation().add(dir.rotateRight().rotateRight()));
    			return true;
    		} else {
        		rc.move(dir.rotateRight().rotateRight());
    			return true;
    		}
    	} else if (rc.canMove(dir.rotateLeft().rotateLeft()) || rc.canFill(rc.getLocation().add(dir.rotateLeft().rotateLeft()))) {
    		if(!rc.canMove(dir.rotateLeft().rotateLeft())) {
    			rc.fill(rc.getLocation().add(dir.rotateLeft().rotateLeft()));
    			return true;
    		} else {
        		rc.move(dir.rotateLeft().rotateLeft());
    			return true;
    		}
    	} else if (rc.canMove(dir.rotateRight().rotateRight().rotateRight()) || rc.canFill(rc.getLocation().add(dir.rotateRight().rotateRight().rotateRight()))) {
    		if(!rc.canMove(dir.rotateRight().rotateRight().rotateRight())) {
    			rc.fill(rc.getLocation().add(dir.rotateRight().rotateRight().rotateRight()));
    			return true;
    		} else {
        		rc.move(dir.rotateRight().rotateRight().rotateRight());
    			return true;
    		}
    	} else if (rc.canMove(dir.rotateLeft().rotateLeft().rotateLeft()) || rc.canFill(rc.getLocation().add(dir.rotateLeft().rotateLeft().rotateLeft()))) {
    		if(!rc.canMove(dir.rotateLeft().rotateLeft().rotateLeft())) {
    			rc.fill(rc.getLocation().add(dir.rotateLeft().rotateLeft().rotateLeft()));
    			return true;
    		} else {
        		rc.move(dir.rotateLeft().rotateLeft().rotateLeft());
    			return true;
    		}
    	}
    return false;
    }
    
    static MapLocation[] shuffleArray(MapLocation[] spawnLocs, Random rnd) {
	  for (int i = spawnLocs.length - 1; i > 0; i--) {
	    int index = rnd.nextInt(i + 1);
	    // Simple swap
	    MapLocation a = spawnLocs[index];
	    spawnLocs[index] = spawnLocs[i];
	    spawnLocs[i] = a;
	  }
	  return spawnLocs;
    }
}
