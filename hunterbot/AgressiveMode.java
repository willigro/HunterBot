package hunterbot;

import java.awt.Color;

import robocode.AdvancedRobot;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.WinEvent;

public class AgressiveMode implements Mode{

	public static final int MODE = 1;
	private final int AWAY_DISTANCE = 200;
	private final int DANGER_DISTANCE = 90;
	private final float CHANCE_TO_FOLLOW_TARGET = 0.6f;

	private AdvancedRobot me;
	private Knowledge knowledge;
	
	private int demageTaked = 0;

	public AgressiveMode(AdvancedRobot me, Knowledge knowledge) {
		this.me = me;
		this.knowledge = knowledge;
	}
	
	@Override
	public int getModeId() {
		return MODE;
	}

	@Override
	public Mode initMode() {
		me.setScanColor(Color.red);
		// Apenas um giro de ódio!!
		me.turnRight(360);
		
		BaseRobotControls.canFire = true;
		BaseRobotControls.log("Init agressive mode");
		return this;
	}
	
	@Override
	public int getBulletTaken() {
		return demageTaked;
	}

	@Override
	public void scannedEnemy(AdvancedRobot me) {
		if (knowledge.getTarget().distance > AWAY_DISTANCE) {
			BaseRobotControls.log("follow");
			BaseRobotControls.followTarget(me, knowledge, getSafeDistance());
		} else if (knowledge.getTarget().distance < DANGER_DISTANCE) {
			BaseRobotControls.log("getOut");
			BaseRobotControls.getOut(me);
		} else if(knowledge.enemyDropEnergy()) {
//			BaseRobotControls.log("parallel");
//			 BaseRobotControls.parallelToEnemy(me, knowledge);
			BaseRobotControls.log("dodge");
			BaseRobotControls.doDodge(me, knowledge);	
		}
		
		BaseRobotControls.wall(me);
	}

	@Override
	public void onHitWall(HitWallEvent e) {
		me.setTurnRightRadians(knowledge.getTarget().bearing + (Calculate.PI / 8));
		BaseRobotControls.followTarget(me, knowledge, BaseRobotControls.MIN_MOVEMENT);
		BaseRobotControls.isHitting = true;
	}

	@Override
	public void onHitRobot(HitRobotEvent e) {
		BaseRobotControls.isHitting = true;
		me.setTurnRight(45);
		BaseRobotControls.getOut(me);
	}

	@Override
	public void onHitByBullet(HitByBulletEvent e) {
		if (Math.random() > CHANCE_TO_FOLLOW_TARGET)
			BaseRobotControls.followTarget(me, knowledge, BaseRobotControls.MIN_MOVEMENT);
		else
			BaseRobotControls.getOut(me);
		demageTaked++;
	}

	@Override
	public void onWin(WinEvent e) {
		me.setTurnRight(10000);
		me.setTurnGunLeft(10000);
	}
	
	private double getSafeDistance() {
		return knowledge.getTarget().distance - DANGER_DISTANCE;
	}
}
