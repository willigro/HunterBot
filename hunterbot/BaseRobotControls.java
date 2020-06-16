package hunterbot;

import java.awt.geom.Point2D;

import robocode.AdvancedRobot;

public class BaseRobotControls {

	private static final int FACTOR_TO_FIRE_DITANCE = 500;
	private static final int CHANGE_DIRECTION_ON_FOLLOW_TURNS = 10;
	public static final double MAX_MOVEMENT = 90;
	public static final double MIN_MOVEMENT = 50;
	public static int direction = 1;
	public static boolean directionOnFollow = false;
	public static int firedCount = 0;
	public static boolean canFire = true;
	private static double gunTurnAmt;
	private static long nextTime;
	private static Point2D.Double p;
	public static boolean isHitting = false;
	private static double lastEnergy = 0.0;
	public static IRecoverEvent iRecoverEvent;
	private static double maxXWall = 0;
	private static double minXWall = 100;
	private static double maxYWall = 0;
	private static double minYWall = 100;
	public static String actualMode = "";

	public static void doDodgeIn(AdvancedRobot me, Knowledge knowledge, int ticks) {
		if (Calculate.ticksFinish(me, ticks)) {
			doDodge(me, knowledge);
		}
	}

	public static void doDodge(AdvancedRobot me, Knowledge knowledge) {
		direction = -direction;
		me.setAhead((knowledge.getTarget().distance / 4 + 27) * BaseRobotControls.direction);
	}

	public static void getOut(AdvancedRobot me) {
		me.setBack(MIN_MOVEMENT);
	}

	public static void predictFire(AdvancedRobot me, Knowledge knowledge) {
		p = new Point2D.Double(knowledge.getTarget().x, knowledge.getTarget().y);
		for (int i = 0; i < 5; i++) {
			nextTime = Math
					.round(Calculate.getRange(me.getX(), me.getY(), p.x, p.y) / (20 - (3 * getFirePower(knowledge))));
			p = knowledge.getTarget().guessPosition(me.getTime() + nextTime);
		}

		gunTurnAmt = me.getGunHeadingRadians() - Calculate.absBearing(me.getX(), me.getY(), p.x, p.y);
		me.setTurnGunLeftRadians(Calculate.normaliseBearing(gunTurnAmt));
		tryFire(me, knowledge);
	}

	public static void tryFire(AdvancedRobot me, Knowledge knowledge) {
		if (canFire && me.getGunHeat() == 0) {
			// O problema desse algoritmo é que eu nao tenho o poder do disparo realizado, apenas um aproximado
			double power = getFirePower(knowledge);
			me.setFire(power);

			if (iRecoverEvent != null && me.getEnergy() >= lastEnergy) {
				iRecoverEvent.recover(lastEnergy, me.getEnergy());
				knowledge.hittedShot(power);
			} 
			
			knowledge.shoted();

			lastEnergy = me.getEnergy();

			firedCount++;
		}
	}

	private static double getFirePower(Knowledge knowledge) {
		return Math.min(FACTOR_TO_FIRE_DITANCE / knowledge.getTarget().distance, 3);
	}

	public static void followTarget(AdvancedRobot me, Knowledge knowledge, double movement) {
		if (Calculate.ticksFinish(me, CHANGE_DIRECTION_ON_FOLLOW_TURNS)) {
			directionOnFollow = !directionOnFollow;
			if (directionOnFollow)
				me.setTurnRightRadians(knowledge.getTarget().bearing + (Calculate.PI / 16));
			else
				me.setTurnRightRadians(knowledge.getTarget().bearing - (Calculate.PI / 16));
		}
		me.setAhead(movement);
	}

	public static void parallelToEnemy(AdvancedRobot me, Knowledge knowledge) {
		if (Calculate.ticksFinish(me,  20)) {
			direction *= -1; 
			me.setAhead(direction * 300);
		}
		me.setTurnRightRadians(knowledge.getTarget().bearing + (Calculate.PI / 3));
	}

	public static void wall(AdvancedRobot me) {
		if(me.getX() < minXWall || me.getX() > maxXWall ){
        	direction = -direction;
			isHitting = true;
			me.setAhead(MAX_MOVEMENT * 2);
		}else if(me.getY() < minYWall || me.getY() > maxYWall ){
			direction = -direction;	
			isHitting = true;
			me.setAhead(MAX_MOVEMENT * 2);
        }
		if(isHitting) BaseRobotControls.log("wall");
	}
	
	public static void log(String s) {
		// System.out.println(String.format("%s %s",actualMode,s));
	}

	interface IRecoverEvent {
		void recover(double lastEnergy, double actualEnergy);
	}

	public static void setWall(double battleFieldHeight, double battleFieldWidth) {
		maxYWall = battleFieldHeight - minYWall;
		maxXWall = battleFieldWidth - minXWall;
	}
	
	public static void clear() {
		lastEnergy = 0.0;
		firedCount = 0;
		canFire = true;
	}
}
