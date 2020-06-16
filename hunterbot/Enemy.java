package hunterbot;

import robocode.ScannedRobotEvent;
import java.awt.geom.Point2D;
import java.util.AbstractMap;

public class Enemy {
	public double bearing;
	public double head;
	public double changehead;
	public double lastHead;
	public long ctime;
	public double speed;
	public double x, y;
	public double distance;
	public double energy;

	public int actualShot = 0;
	public double[] shots = { 0.0, 0.0 };
	public boolean shotDefined = false;
	public boolean lastShotType = false;

	public void fields(ScannedRobotEvent e, double x, double y, double absbearing_rad, long time) {
		this.x = x + Math.sin(absbearing_rad) * e.getDistance();
		this.y = y + Math.cos(absbearing_rad) * e.getDistance();
		this.bearing = e.getBearingRadians();
		this.lastHead = this.head;
		this.head = e.getHeadingRadians();
		this.changehead = head - lastHead;
		this.ctime = time;
		this.speed = e.getVelocity();
		this.distance = e.getDistance();
		this.energy = e.getEnergy();
	}

	public Point2D.Double guessPosition(long when) {
		double diff = when - ctime;
		double newX, newY;
		if (actualShot == 0) { // Math.abs(changehead) > 0.00001
			double radius = speed / (changehead / 2 + head);
			double tothead = diff * (changehead / 2 + head);
			newY = y + (Math.sin(head + tothead) * radius) - (Math.sin(head) * radius);
			newX = x + (Math.cos(head) * radius) - (Math.cos(head + tothead) * radius);
		} else {
			newY = y + Math.cos(head) * speed * diff;
			newX = x + Math.sin(head) * speed * diff;
		}
		return new Point2D.Double(newX, newY);
	}

	public void actualShotTypeUp(double power) {
		shots[actualShot] += power;
	}

	public void changeShot() {
		actualShot++;
		// reset
		if (actualShot == shots.length) {
			lastShotType = true;
		}
	}

	public void defineShot() {
		if (shots[0] > shots[1]) {
			actualShot = 0;
			shotDefined = true;
		} else if (shots[1] > shots[0]) {
			actualShot = 1;
			shotDefined = true;
		}else {
			// if equals, then retry
			actualShot = 0;
		}
		
		// if(shotDefined) BaseRobotControls.log(String.format("DEFINED Result shot one %s Result shot two %s", shots[0], shots[1]));
	}
}
