package hunterbot;

import robocode.AdvancedRobot;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.WinEvent;

public interface Mode {
	int getModeId();
	Mode initMode();
	int getBulletTaken();
	void scannedEnemy(AdvancedRobot me);
	void onHitWall(HitWallEvent e);
	void onHitRobot(HitRobotEvent e);
	void onHitByBullet(HitByBulletEvent e);
	void onWin(WinEvent e);
}
