package TinkyWinky;

import java.util.Random;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.PaintType;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.UnitType;


/**
 * RobotPlayer is the class that describes your main robot strategy.
 * The run() method inside this class is like your main function: this is what we'll call once your robot
 * is created!
 */
public class RobotPlayer {
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
        System.out.println("I'm alive");

        // You can also use indicators to save debug notes in replays.
        rc.setIndicatorString("Hello world!");

        while (true) {
            // This code runs during the entire lifespan of the robot, which is why it is in an infinite
            // loop. If we ever leave this loop and return from run(), the robot dies! At the end of the
            // loop, we call Clock.yield(), signifying that we've done everything we want to do.

            turnCount += 1;  // We have now been alive for one more turn!

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode.
            try {
                // The same run() function is called for every robot on your team, even if they are
                // different types. Here, we separate the control depending on the UnitType, so we can
                // use different strategies on different robots. If you wish, you are free to rewrite
                // this into a different control structure!
                if (rc.getRoundNum() < 200){
                    switch (rc.getType()){
                        case SOLDIER: runPre200Soldier(rc); break; 
                        case MOPPER: runMopper(rc); break;
                        case SPLASHER: runSplasher(rc); break;
                        default: runPre200Tower(rc); break;
                }} else {
                    switch (rc.getType()){
                        case SOLDIER: runSoldier(rc); break; 
                        case MOPPER: runMopper(rc); break;
                        case SPLASHER: runSplasher(rc); break;
                        default: runTower(rc); break;
                        }
                }
                }
             catch (GameActionException e) {
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

    /* Before Round 200, Tower Code. Will Spawn Based off Multiples of Rounds.  */
    public static void runPre200Tower(RobotController rc) throws GameActionException{
		spawning(rc);
    }
    public static void runPre200Soldier(RobotController rc) throws GameActionException{
        MapLocation[] robotRuinsArr = rc.senseNearbyRuins(1000); //no clue what the robot's vision radius is. 
		RobotInfo[] nearbyRobots = rc.senseNearbyRobots(1000);
		if (rc.getLocation().equals(robotRuinsArr[0].translate(0,1))){
			paintPattern(rc, robotRuinsArr[0]);
		} else if(robotRuinsArr.length > 0){
			moveTo(rc, robotRuinsArr[0]);
			rc.setIndicatorString("Moving to Ruins");
		} else{
			flee(rc, nearbyRobots[0].location);
			rc.setIndicatorString("Fleeing!");
		}
		//Start Painting the Pattern Around the Tower, Build Resource Tower if possible.

    }


	public static void spawning(RobotController rc) throws GameActionException {
		if ((rc.getPaint() >= 300 && rc.getMoney() >= 400) || (rc.getRoundNum() < rc.getPaint())) {
			for (Direction dir : directions) {
				MapLocation nextLoc = rc.getLocation().add(dir);
				if (turnCount % 9 == 0 && rc.canBuildRobot(UnitType.SPLASHER, nextLoc )){
					rc.buildRobot(UnitType.MOPPER, nextLoc);
				} else if (turnCount % 4 == 0 && rc.canBuildRobot(UnitType.MOPPER, nextLoc)){
					rc.buildRobot(UnitType.SPLASHER, nextLoc);
				}else{
					if(rc.canBuildRobot(UnitType.SOLDIER, nextLoc)){
						rc.buildRobot(UnitType.SOLDIER, nextLoc);
					}
				}
			}
		}
	}

	public static void paintPattern(RobotController rc, MapLocation center) throws GameActionException{
		// Center coordinate is (1,0)
		//MapLocation center = fuck.translate(0, -1);

		// Check if robot is at center
		if (rc.getLocation().equals(center)) {
			// Paint own tile first

			// Far North Row (y+2)
			MapLocation farNorthWest = center.translate(-2, 2);
			MapInfo farNorthWestPaint = rc.senseMapInfo(farNorthWest);
			if (farNorthWestPaint.getPaint() != PaintType.ALLY_SECONDARY) {
				rc.attack(farNorthWest, true);
			}

			MapLocation farNorthNorthWest = center.translate(-1, 2);
			MapInfo farNorthNorthWestPaint = rc.senseMapInfo(farNorthNorthWest);
			if (farNorthNorthWestPaint.getPaint() != PaintType.ALLY_PRIMARY) {
				rc.attack(farNorthNorthWest);
			}

			MapLocation farNorth = center.translate(0, 2);
			MapInfo farNorthPaint = rc.senseMapInfo(farNorth);
			if (farNorthPaint.getPaint() != PaintType.ALLY_PRIMARY) {
				rc.attack(farNorth);
			}

			MapLocation farNorthNorthEast = center.translate(1, 2);
			MapInfo farNorthNorthEastPaint = rc.senseMapInfo(farNorthNorthEast);
			if (farNorthNorthEastPaint.getPaint() != PaintType.ALLY_PRIMARY) {
				rc.attack(farNorthNorthEast);
			}

			MapLocation farNorthEast = center.translate(2, 2);
			MapInfo farNorthEastPaint = rc.senseMapInfo(farNorthEast);
			if (farNorthEastPaint.getPaint() != PaintType.ALLY_SECONDARY) {
				rc.attack(farNorthEast, true);
			}

			// North Row (y+1)
			MapLocation northWest = center.translate(-2, 1);
			MapInfo northWestPaint = rc.senseMapInfo(northWest);
			if (northWestPaint.getPaint() != PaintType.ALLY_PRIMARY) {
				rc.attack(northWest);
			}

			MapLocation northNorthWest = center.translate(-1, 1);
			MapInfo northNorthWestPaint = rc.senseMapInfo(northNorthWest);
			if (northNorthWestPaint.getPaint() != PaintType.ALLY_SECONDARY) {
				rc.attack(northNorthWest, true);
			}

			MapLocation north = center.translate(0, 1);
			MapInfo northPaint = rc.senseMapInfo(north);
			if (northPaint.getPaint() != PaintType.ALLY_PRIMARY) {
				rc.attack(north);
			}

			MapLocation northNorthEast = center.translate(1, 1);
			MapInfo northNorthEastPaint = rc.senseMapInfo(northNorthEast);
			if (northNorthEastPaint.getPaint() != PaintType.ALLY_SECONDARY) {
				rc.attack(northNorthEast, true);
			}

			MapLocation northEast = center.translate(2, 1);
			MapInfo northEastPaint = rc.senseMapInfo(northEast);
			if (northEastPaint.getPaint() != PaintType.ALLY_PRIMARY) {
				rc.attack(northEast);
			}

			// Center Row (y+0)
			MapLocation centerWest = center.translate(-2, 0);
			MapInfo centerWestPaint = rc.senseMapInfo(centerWest);
			if (centerWestPaint.getPaint() != PaintType.ALLY_PRIMARY) {
				rc.attack(centerWest);
			}

			MapLocation centerNorthWest = center.translate(-1, 0);
			MapInfo centerNorthWestPaint = rc.senseMapInfo(centerNorthWest);
			if (centerNorthWestPaint.getPaint() != PaintType.ALLY_PRIMARY) {
				rc.attack(centerNorthWest);
			}

			MapLocation centerNorthEast = center.translate(1, 0);
			MapInfo centerNorthEastPaint = rc.senseMapInfo(centerNorthEast);
			if (centerNorthEastPaint.getPaint() != PaintType.ALLY_PRIMARY) {
				rc.attack(centerNorthEast);
			}

			MapLocation centerEast = center.translate(2, 0);
			MapInfo centerEastPaint = rc.senseMapInfo(centerEast);
			if (centerEastPaint.getPaint() != PaintType.ALLY_PRIMARY) {
				rc.attack(centerEast);
			}

			// South Row (y-1)
			MapLocation southWest = center.translate(-2, -1);
			MapInfo southWestPaint = rc.senseMapInfo(southWest);
			if (southWestPaint.getPaint() != PaintType.ALLY_PRIMARY) {
				rc.attack(southWest);
			}

			MapLocation southSouthWest = center.translate(-1, -1);
			MapInfo southSouthWestPaint = rc.senseMapInfo(southSouthWest);
			if (southSouthWestPaint.getPaint() != PaintType.ALLY_SECONDARY) {
				rc.attack(southSouthWest, true);
			}

			MapLocation south = center.translate(0, -1);
			MapInfo southPaint = rc.senseMapInfo(south);
			if (southPaint.getPaint() != PaintType.ALLY_PRIMARY) {
				rc.attack(south);
			}

			MapLocation southSouthEast = center.translate(1, -1);
			MapInfo southSouthEastPaint = rc.senseMapInfo(southSouthEast);
			if (southSouthEastPaint.getPaint() != PaintType.ALLY_SECONDARY) {
				rc.attack(southSouthEast,true);
			}

			MapLocation southEast = center.translate(2, -1);
			MapInfo southEastPaint = rc.senseMapInfo(southEast);
			if (southEastPaint.getPaint() != PaintType.ALLY_PRIMARY) {
				rc.attack(southEast);
			}

			// Far South Row (y-2)
			MapLocation farSouthWest = center.translate(-2, -2);
			MapInfo farSouthWestPaint = rc.senseMapInfo(farSouthWest);
			if (farSouthWestPaint.getPaint() != PaintType.ALLY_SECONDARY) {
				rc.attack(farSouthWest, true);
			}

			MapLocation farSouthSouthWest = center.translate(-1, -2);
			MapInfo farSouthSouthWestPaint = rc.senseMapInfo(farSouthSouthWest);
			if (farSouthSouthWestPaint.getPaint() != PaintType.ALLY_PRIMARY) {
				rc.attack(farSouthSouthWest);
			}

			MapLocation farSouth = center.translate(0, -2);
			MapInfo farSouthPaint = rc.senseMapInfo(farSouth);
			if (farSouthPaint.getPaint() != PaintType.ALLY_PRIMARY) {
				rc.attack(farSouth);
			}

			MapLocation farSouthSouthEast = center.translate(1, -2);
			MapInfo farSouthSouthEastPaint = rc.senseMapInfo(farSouthSouthEast);
			if (farSouthSouthEastPaint.getPaint() != PaintType.ALLY_PRIMARY) {
				rc.attack(farSouthSouthEast);
			}

			MapLocation farSouthEast = center.translate(2, -2);
			MapInfo farSouthEastPaint = rc.senseMapInfo(farSouthEast);
			if (farSouthEastPaint.getPaint() != PaintType.ALLY_SECONDARY) {
				rc.attack(farSouthEast, true);
			}
		}

		int roundNum = rc.getRoundNum();
		if (roundNum > 200 && rc.canCompleteTowerPattern(UnitType.LEVEL_ONE_DEFENSE_TOWER , center)){
			rc.completeTowerPattern(UnitType.LEVEL_ONE_DEFENSE_TOWER, center);
		} else if(roundNum < 200 && rc.canCompleteTowerPattern(UnitType.LEVEL_ONE_MONEY_TOWER , center)){
			rc.completeTowerPattern(UnitType.LEVEL_ONE_MONEY_TOWER, center);
		}else if (rc.canCompleteResourcePattern(center)){
			rc.completeResourcePattern(center);
		}

 	}
    /**
     * Run a single turn for towers.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    public static void runTower(RobotController rc) throws GameActionException{
        spawning(rc);
        // Read incoming messages
        Message[] messages = rc.readMessages(-1);
        for (Message m : messages) {
            System.out.println("Tower received message: '#" + m.getSenderID() + " " + m.getBytes());
        }
    }

	
    /**
     * Run a single turn for a Soldier.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    public static void runSoldier(RobotController rc) throws GameActionException{
        MapLocation here = rc.getLocation();

		// Sense and label nearby robots
		rc.setIndicatorString("Scanning");
        RobotInfo[] nearbyRobots = rc.senseNearbyRobots(-1);
		RobotInfo nearestTower = null;
        int nTowerDist = 9999;
        RobotInfo nearestMopper = null;
        int nMopDist = 9999;
		RobotInfo nearestEnemyTower = null;
		int nETDist = 9999;
		boolean enemies = false;
        for (RobotInfo aBot : nearbyRobots) {
            int botDist = aBot.location.distanceSquaredTo(here);
			if (rc.getTeam() == aBot.team && botDist < nTowerDist && (aBot.type != UnitType.SOLDIER && aBot.type != UnitType.SPLASHER && aBot.type != UnitType.MOPPER)){
                nTowerDist = botDist;
                nearestTower = aBot;
            }
			if (rc.getTeam() == aBot.team && botDist < nMopDist && aBot.type == UnitType.MOPPER){
                nMopDist = botDist;
                nearestMopper = aBot;
            }
			if (rc.getTeam() != aBot.team && botDist < nETDist && (aBot.type != UnitType.SOLDIER && aBot.type != UnitType.SPLASHER && aBot.type != UnitType.MOPPER)){
                nETDist = botDist;
                nearestEnemyTower = aBot;
            }
			if (rc.getTeam() != aBot.team) {enemies = true;}
        }

		
		MapLocation[] nearbyRuins = rc.senseNearbyRuins(-1);
		MapLocation nearestRuin = null;
		int nRuinDist = 9999;

		for (MapLocation aLoc : nearbyRuins) {
			int ruinDist = here.distanceSquaredTo(aLoc);
			if (rc.senseRobotAtLocation(aLoc) == null && ruinDist < nRuinDist) {
				nearestRuin = aLoc;
				nRuinDist = ruinDist;
			}
		}
		
		// If not enough paint to safely attack/paint, go refill
		if (rc.getPaint() < 105 && nearestTower != null) {
			rc.setIndicatorString("Getting paint");
            refill(rc,nearestTower.location);
        } else if (rc.getPaint() < 105 && nearestMopper != null) {
			rc.setIndicatorString("Following mopper");
            moveTo(rc,nearestMopper.location);
        }

		// If see enemy tower, attack
		// TODO: Shoot and scoot, scoot and shoot
		if (nearestEnemyTower != null) {
			rc.setIndicatorString("Attack!");
			if (nETDist <= 20) {
				rc.attack(nearestEnemyTower.location);
			} else {
				moveTo(rc, nearestEnemyTower.location);
			}
		}

		// Building on ruins
		if (nearestRuin != null) {
			boolean northMark = rc.senseMapInfo(nearestRuin.add(Direction.NORTH)).getMark() == PaintType.ALLY_SECONDARY; //Defense
			boolean southMark = rc.senseMapInfo(nearestRuin.add(Direction.SOUTH)).getMark() == PaintType.ALLY_SECONDARY; //Paint
			boolean eastMark = rc.senseMapInfo(nearestRuin.add(Direction.EAST)).getMark() == PaintType.ALLY_SECONDARY; //Money
			boolean anyMark = northMark || southMark || eastMark;
			if (!anyMark) {
				if (enemies) {
					rc.mark(nearestRuin.add(Direction.NORTH), true);
					northMark = true;
					anyMark = true;
				} else if (rc.getMoney() < 500) {
					rc.mark(nearestRuin.add(Direction.EAST), true);
					eastMark = true;
					anyMark = true;
				} else {
					rc.mark(nearestRuin.add(Direction.SOUTH), true);
					southMark = true;
					anyMark = true;
				}

			}
			


			if (northMark) {
				//paintPattern(rc, nearestRuin, 3);
			}
			if (southMark) {
				//paintPattern(rc, nearestRuin, 2);
			}
			if (eastMark) {
				//paintPattern(rc, nearestRuin, 1);
			}
		}



        // Sense information about all visible nearby tiles.
        MapInfo[] nearbyTiles = rc.senseNearbyMapInfos();

		for (MapInfo anInfo : nearbyTiles) {
			if (anInfo.getMark() == PaintType.ALLY_SECONDARY) {
				//paintPattern(rc, anInfo.getMapLocation(), 4);
			}
		}

		

        
        // Try to paint beneath us as we walk to avoid paint penalties.
        // Avoiding wasting paint by re-painting our own tiles.
        MapInfo currentTile = rc.senseMapInfo(rc.getLocation());
        if (!currentTile.getPaint().isAlly() && rc.canAttack(rc.getLocation())){
            rc.attack(rc.getLocation());
        }
    }

    public static boolean refill(RobotController rc, MapLocation loc) throws GameActionException {
        // TODO: Follow increasing density to find tower
		if (rc.canTransferPaint(loc, rc.getPaint() - 200)) {
			rc.transferPaint(loc, rc.getPaint() - 200);
			return true;
		} 
		return moveTo(rc, loc);
    }

    /**
     * Run a single turn for a Mopper.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    public static void runMopper(RobotController rc) throws GameActionException{
        // Move and attack randomly.
        Direction dir = directions[rng.nextInt(directions.length)];
        MapLocation nextLoc = rc.getLocation().add(dir);
        moveTo(rc,nextLoc);
        if (rc.canMopSwing(dir)){
            rc.mopSwing(dir);
            System.out.println("Mop Swing! Booyah!");
        }
        else if (rc.canAttack(nextLoc)){
            rc.attack(nextLoc);
        }
        // We can also move our code into different methods or classes to better organize it!
        updateEnemyRobots(rc);
    }

    public static void runSplasher(RobotController rc) throws GameActionException{
        Direction dir = directions[rng.nextInt(directions.length)];
        MapLocation nextLoc = rc.getLocation().add(dir);
        moveTo(rc,nextLoc);
    }

    public static void updateEnemyRobots(RobotController rc) throws GameActionException{
        // Sensing methods can be passed in a radius of -1 to automatically 
        // use the largest possible value.
        RobotInfo[] enemyRobots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        if (enemyRobots.length != 0){
            rc.setIndicatorString("There are nearby enemy robots! Scary!");
            // Save an array of locations with enemy robots in them for possible future use.
            MapLocation[] enemyLocations = new MapLocation[enemyRobots.length];
            for (int i = 0; i < enemyRobots.length; i++){
                enemyLocations[i] = enemyRobots[i].getLocation();
            }
            RobotInfo[] allyRobots = rc.senseNearbyRobots(-1, rc.getTeam());
            // Occasionally try to tell nearby allies how many enemy robots we see.
            if (rc.getRoundNum() % 20 == 0){
                for (RobotInfo ally : allyRobots){
                    if (rc.canSendMessage(ally.location, enemyRobots.length)){
                        rc.sendMessage(ally.location, enemyRobots.length);
                    }
                }
            }
        }
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
		if (canFill(rc, rc.getLocation().add(Direction.NORTH))) {fill(rc, rc.getLocation().add(Direction.NORTH));}
    	else if (canFill(rc, rc.getLocation().add(Direction.NORTHEAST))) {fill(rc, rc.getLocation().add(Direction.NORTHEAST));}
    	else if (canFill(rc, rc.getLocation().add(Direction.NORTHWEST))) {fill(rc, rc.getLocation().add(Direction.NORTHWEST));}
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
    	if (canFill(rc, rc.getLocation().add(Direction.NORTH))) {fill(rc, rc.getLocation().add(Direction.NORTH));}
    	else if (canFill(rc, rc.getLocation().add(Direction.NORTHEAST))) {fill(rc, rc.getLocation().add(Direction.NORTHEAST));}
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
    	if (canFill(rc, rc.getLocation().add(Direction.NORTHEAST))) {fill(rc, rc.getLocation().add(Direction.NORTHEAST));}
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
    	if (canFill(rc, rc.getLocation().add(Direction.EAST))) {fill(rc, rc.getLocation().add(Direction.EAST));}
    	else if (canFill(rc, rc.getLocation().add(Direction.NORTHEAST))) {fill(rc, rc.getLocation().add(Direction.NORTHEAST));}
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
    	if (canFill(rc, rc.getLocation().add(Direction.EAST))) {fill(rc, rc.getLocation().add(Direction.EAST));}
    	else if (canFill(rc, rc.getLocation().add(Direction.NORTHEAST))) {fill(rc, rc.getLocation().add(Direction.NORTHEAST));}
    	else if (canFill(rc, rc.getLocation().add(Direction.SOUTHEAST))) {fill(rc, rc.getLocation().add(Direction.SOUTHEAST));}
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
    	if (canFill(rc, rc.getLocation().add(Direction.EAST))) {fill(rc, rc.getLocation().add(Direction.EAST));}
    	else if (canFill(rc, rc.getLocation().add(Direction.SOUTHEAST))) {fill(rc, rc.getLocation().add(Direction.SOUTHEAST));}
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
    	if (canFill(rc, rc.getLocation().add(Direction.SOUTHEAST))) {fill(rc, rc.getLocation().add(Direction.SOUTHEAST));}
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
    	if (canFill(rc, rc.getLocation().add(Direction.SOUTH))) {fill(rc, rc.getLocation().add(Direction.SOUTH));}
    	else if (canFill(rc, rc.getLocation().add(Direction.SOUTHEAST))) {fill(rc, rc.getLocation().add(Direction.SOUTHEAST));}
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
    	if (canFill(rc, rc.getLocation().add(Direction.SOUTH))) {fill(rc, rc.getLocation().add(Direction.SOUTH));}
    	else if (canFill(rc, rc.getLocation().add(Direction.SOUTHWEST))) {fill(rc, rc.getLocation().add(Direction.SOUTHWEST));}
    	else if (canFill(rc, rc.getLocation().add(Direction.SOUTHEAST))) {fill(rc, rc.getLocation().add(Direction.SOUTHEAST));}
    	return moveS2(rc);
    }
    
    public static boolean moveS2(RobotController rc) throws GameActionException {
    	if (rc.canMove(Direction.SOUTH)) {
    		rc.move(Direction.SOUTH); 
    		if (canFill(rc, rc.getLocation().add(Direction.SOUTH))) {fill(rc, rc.getLocation().add(Direction.SOUTH));};
    		return true;
    	} else if (rc.canMove(Direction.SOUTHWEST)) {
    		rc.move(Direction.SOUTHWEST); 
    		if (canFill(rc, rc.getLocation().add(Direction.SOUTHEAST))) {fill(rc, rc.getLocation().add(Direction.SOUTHEAST));};
    		return true;
    	} else if (rc.canMove(Direction.SOUTHEAST)) {
    		rc.move(Direction.SOUTHEAST); 
    		if (canFill(rc, rc.getLocation().add(Direction.SOUTHWEST))) {fill(rc, rc.getLocation().add(Direction.SOUTHWEST));};
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
    	if (canFill(rc, rc.getLocation().add(Direction.SOUTH))) {fill(rc, rc.getLocation().add(Direction.SOUTH));}
    	else if (canFill(rc, rc.getLocation().add(Direction.SOUTHWEST))) {fill(rc, rc.getLocation().add(Direction.SOUTHWEST));}
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
    	if (canFill(rc, rc.getLocation().add(Direction.SOUTHWEST))) {fill(rc, rc.getLocation().add(Direction.SOUTHWEST));}
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
    	if (canFill(rc, rc.getLocation().add(Direction.WEST))) {fill(rc, rc.getLocation().add(Direction.WEST));}
    	else if (canFill(rc, rc.getLocation().add(Direction.SOUTHWEST))) {fill(rc, rc.getLocation().add(Direction.SOUTHWEST));}
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
    	if (canFill(rc, rc.getLocation().add(Direction.WEST))) {fill(rc, rc.getLocation().add(Direction.WEST));}
    	else if (canFill(rc, rc.getLocation().add(Direction.SOUTHWEST))) {fill(rc, rc.getLocation().add(Direction.SOUTHWEST));}
    	else if (canFill(rc, rc.getLocation().add(Direction.NORTHWEST))) {fill(rc, rc.getLocation().add(Direction.NORTHWEST));}
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
    	if (canFill(rc, rc.getLocation().add(Direction.WEST))) {fill(rc, rc.getLocation().add(Direction.WEST));}
    	else if (canFill(rc, rc.getLocation().add(Direction.NORTHWEST))) {fill(rc, rc.getLocation().add(Direction.NORTHWEST));}
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
    	if (canFill(rc, rc.getLocation().add(Direction.NORTHWEST))) {fill(rc, rc.getLocation().add(Direction.NORTHWEST));}
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
    	if (canFill(rc, rc.getLocation().add(Direction.NORTH))) {fill(rc, rc.getLocation().add(Direction.NORTH));}
    	else if (canFill(rc, rc.getLocation().add(Direction.NORTHWEST))) {fill(rc, rc.getLocation().add(Direction.NORTHWEST));}
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
    	//rc.setIndicatorString("Ultra Greedy " + dir.toString());
    	if (rc.canMove(dir) || canFill(rc, rc.getLocation().add(dir))) {
    		fill(rc, rc.getLocation().add(dir));
    		rc.move(dir);
    		return true;
        } else if (rc.canMove(secDir) || canFill(rc, rc.getLocation().add(secDir))) {
        	fill(rc, rc.getLocation().add(secDir));
    		rc.move(secDir);
    		return true;
        } else if (dir.rotateLeft() == secDir) {
        	if (rc.canMove(dir.rotateRight()) || canFill(rc, rc.getLocation().add(dir.rotateRight()))) {
        		fill(rc, rc.getLocation().add(dir.rotateRight()));
        		rc.move(dir.rotateRight());
        		return true;
        	} else if (restrictive) {
        		return false;
        	} else if (rc.canMove(dir.rotateLeft().rotateLeft()) || canFill(rc, rc.getLocation().add(dir.rotateLeft().rotateLeft()))) {
        		fill(rc, rc.getLocation().add(dir.rotateLeft().rotateLeft()));
        		rc.move(dir.rotateLeft().rotateLeft());
        		return true;
        	} else if (rc.canMove(dir.rotateRight().rotateRight()) || canFill(rc, rc.getLocation().add(dir.rotateRight().rotateRight()))) {
        		fill(rc, rc.getLocation().add(dir.rotateRight().rotateRight()));
        		rc.move(dir.rotateRight().rotateRight());
        		return true;
        	} else if (rc.canMove(dir.rotateLeft().rotateLeft().rotateLeft()) || canFill(rc, rc.getLocation().add(dir.rotateLeft().rotateLeft().rotateLeft()))) {
        		fill(rc, rc.getLocation().add(dir.rotateLeft().rotateLeft().rotateLeft()));
        		rc.move(dir.rotateLeft().rotateLeft().rotateLeft());
        		return true;
        	} else if (rc.canMove(dir.rotateRight().rotateRight().rotateRight()) || canFill(rc, rc.getLocation().add(dir.rotateRight().rotateRight().rotateRight()))) {
        		fill(rc, rc.getLocation().add(dir.rotateRight().rotateRight().rotateRight()));
        		rc.move(dir.rotateRight().rotateRight().rotateRight());
        		return true;
        	}
        } else if (rc.canMove(dir.rotateLeft()) || canFill(rc, rc.getLocation().add(dir.rotateLeft()))) {
        	fill(rc, rc.getLocation().add(dir.rotateLeft()));
    		rc.move(dir.rotateLeft());
    		return true;
    	} else if (restrictive) {
    		return false;
    	} else if (rc.canMove(dir.rotateRight().rotateRight()) || canFill(rc, rc.getLocation().add(dir.rotateRight().rotateRight()))) {
    		fill(rc, rc.getLocation().add(dir.rotateRight().rotateRight()));
    		rc.move(dir.rotateRight().rotateRight());
    		return true;
    	} else if (rc.canMove(dir.rotateLeft().rotateLeft()) || canFill(rc, rc.getLocation().add(dir.rotateLeft().rotateLeft()))) {
    		fill(rc, rc.getLocation().add(dir.rotateLeft().rotateLeft()));
    		rc.move(dir.rotateLeft().rotateLeft());
    		return true;
    	} else if (rc.canMove(dir.rotateRight().rotateRight().rotateRight()) || canFill(rc, rc.getLocation().add(dir.rotateRight().rotateRight().rotateRight()))) {
    		fill(rc, rc.getLocation().add(dir.rotateRight().rotateRight().rotateRight()));
    		rc.move(dir.rotateRight().rotateRight().rotateRight());
    		return true;
    	} else if (rc.canMove(dir.rotateLeft().rotateLeft().rotateLeft()) || canFill(rc, rc.getLocation().add(dir.rotateLeft().rotateLeft().rotateLeft()))) {
    		fill(rc, rc.getLocation().add(dir.rotateLeft().rotateLeft().rotateLeft()));
    		rc.move(dir.rotateLeft().rotateLeft().rotateLeft());
    		return true;
    	}
    return false;
    }

	public static boolean canFill(RobotController rc, MapLocation loc) throws GameActionException {
		MapInfo locInfo = rc.senseMapInfo(loc);
		return rc.canAttack(loc) && locInfo.isPassable() && !locInfo.getPaint().isAlly();
	}

	public static void fill(RobotController rc, MapLocation loc) throws GameActionException {
		MapInfo locInfo = rc.senseMapInfo(loc);
		if (rc.canAttack(loc) && locInfo.isPassable() && !locInfo.getPaint().isAlly()) {
			rc.attack(loc);
		}
	}
}
