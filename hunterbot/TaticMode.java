package hunterbot;

import java.awt.Color;

import robocode.AdvancedRobot;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.WinEvent;

public class TaticMode implements Mode{

	public static final int MODE = 2;

	private final int DANGER_DISTANCE = 200;
	private final float CHANCE_TO_FOLLOW_TARGET = 0.3f;

	private AdvancedRobot me;
	private Knowledge knowledge;

	private int demageTaken = 0;
	
	public TaticMode(AdvancedRobot me, Knowledge knowledge) {
		this.me = me;
		this.knowledge = knowledge;
	}

	@Override
	public int getModeId() {
		return MODE;
	}

	@Override
	public Mode initMode() {
		me.setScanColor(Color.white);
		BaseRobotControls.log("Init tatic mode");
		return this;
	}
	
	@Override
	public int getBulletTaken() {
		return demageTaken;
	}

	@Override
	public void scannedEnemy(AdvancedRobot me) {
		BaseRobotControls.parallelToEnemy(me, knowledge);
		BaseRobotControls.log("parallel");
		if(knowledge.enemyDropEnergy()) {
			// BaseRobotControls.doDodge(me, knowledge);
		} else if (knowledge.getTarget().distance < DANGER_DISTANCE) {
			BaseRobotControls.log("getOut");
			BaseRobotControls.getOut(me);
		}
		
		BaseRobotControls.wall(me);

		checkIfNeedStopFire();
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
		me.setTurnRight(20);
		BaseRobotControls.getOut(me);
	}

	@Override
	public void onHitByBullet(HitByBulletEvent e) {
		if (Math.random() < CHANCE_TO_FOLLOW_TARGET)
			BaseRobotControls.followTarget(me, knowledge, BaseRobotControls.MIN_MOVEMENT);
		else
			BaseRobotControls.getOut(me);
		demageTaken++;
	}

	@Override
	public void onWin(WinEvent e) {
		for(int i = 0; i < 100; i ++) {
			me.turnRight(30);
			me.turnLeft(30);
		}
	}
	
	private void checkIfNeedStopFire() {
		if (BaseRobotControls.firedCount > 60 && BaseRobotControls.canFire) {
			if (me.getEnergy() > knowledge.getTarget().energy && Math.random() > .1) {
				BaseRobotControls.canFire = false;
				BaseRobotControls.log("stop fire");
			} else if (me.getEnergy() < knowledge.getTarget().energy && Math.random() > .2) {
				BaseRobotControls.canFire = false;
				BaseRobotControls.log("stop fire");
				BaseRobotControls.getOut(me);
			}
		}

		if (!BaseRobotControls.canFire && Calculate.ticksFinish(me, 10)) {
			BaseRobotControls.log("can fire");
			BaseRobotControls.canFire = true;
		}
	}
}
