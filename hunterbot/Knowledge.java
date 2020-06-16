package hunterbot;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

public class Knowledge {

	private final static int SHOT_TIME_LIMIT = 4;
	private double energyOfEnemy = 100;
	private Enemy enemy = new Enemy();
	public int shotTimes = 0;

	public void enemyDetected(AdvancedRobot me, ScannedRobotEvent e) {
		double absbearing_rad = (me.getHeadingRadians() + e.getBearingRadians()) % (2 * Calculate.PI);
		enemy.fields(e, me.getX(), me.getY(), absbearing_rad, me.getTime());
	}

	public Enemy getTarget() {
		return enemy;
	}

	public boolean enemyDropEnergy() {
		double changeInEnergy = energyOfEnemy - getTarget().energy;
		energyOfEnemy = getTarget().energy;
		return changeInEnergy > 0 && changeInEnergy <= 3;
	}
	
	public void shoted() {
		if (enemy.shotDefined) return;
		
		shotTimes++;
		
		if(shotTimes == SHOT_TIME_LIMIT) {
			enemy.changeShot();	
			shotTimes = 0;
			
			if(enemy.lastShotType) {
				enemy.defineShot();
			}
		}
	}
	
	public void hittedShot(double power) {
		enemy.actualShotTypeUp(power);
	}
}
