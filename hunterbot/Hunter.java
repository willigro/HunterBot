package hunterbot;

import robocode.*;
import java.awt.Color;
import java.util.ArrayList;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html
/*
 * 
 * - "bug" parando de disparar, checar se não é o flow de pausa que esta levando muito tempo
 * - movimentação travada, pode haver conflito de setAhead
 * - aplicar ML em:
 * 				- Esquiva, movimentação apropriada
 *  			- Tipo de previsão da posição do inimigo
 *  			- Tempo de pausa após X disparos, para isso é preciso uma esquiva bem trabalhada, para ganhar no desgaste do inimigo
 */
public class Hunter extends AdvancedRobot implements BaseRobotControls.IRecoverEvent {

	private final int TURNS_TO_STOP_AND_DODGE_HIT = 20;
	private final int TURNS_TO_CHANGE_MODE = 100;

	private ArrayList<Mode> modeList = new ArrayList<Mode>();
	private Mode _actualMode;
	private Knowledge knowledge;

	private double radarOffset;
	private int turnsToChange = 0;
	private boolean changed = false;
	private boolean foundEnemy = false;

	public void run() {
		knowledge = new Knowledge();
		modeList.add(new TaticMode(this, knowledge));
		modeList.add(new AgressiveMode(this, knowledge));
		_actualMode = modeList.get(0).initMode();

		setBodyColor(Color.black);
		setRadarColor(new Color(200, 200, 70));
		setGunColor(Color.white);
		setBulletColor(Color.green);

		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		turnRadarRightRadians(2 * Calculate.PI);

		BaseRobotControls.setWall(getBattleFieldHeight(), getBattleFieldWidth());

		BaseRobotControls.iRecoverEvent = this;
		while (true) {
			doScanner();
			if(!foundEnemy) {
				BaseRobotControls.parallelToEnemy(this, knowledge);
				BaseRobotControls.wall(this);
			}
			execute();
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		knowledge.enemyDetected(this, e);
		checkMode();

		if (BaseRobotControls.isHitting && !Calculate.ticksFinish(this, TURNS_TO_STOP_AND_DODGE_HIT)) {
			BaseRobotControls.predictFire(this, knowledge);
			return;
		}

		BaseRobotControls.isHitting = false;

		_actualMode.scannedEnemy(this);

		BaseRobotControls.predictFire(this, knowledge);
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		_actualMode.onHitByBullet(e);
	}

	/**
	 * onHitRobot: Set him as our new target
	 */
	public void onHitRobot(HitRobotEvent e) {
		_actualMode.onHitRobot(e);
	}

	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		_actualMode.onHitWall(e);
	}

	/**
	 * onWin: Do a victory dance
	 */
	public void onWin(WinEvent e) {
	}

	@Override
	public void onRoundEnded(RoundEndedEvent event) {
		BaseRobotControls.clear();
	}

	private void doScanner() {
		if (getTime() - knowledge.getTarget().ctime > 5) {
			radarOffset = 360;
			foundEnemy = false;
		} else {
			foundEnemy = true;
			radarOffset = getRadarHeadingRadians()
					- Calculate.absBearing(getX(), getY(), knowledge.getTarget().x, knowledge.getTarget().y);
			if (radarOffset < 0)
				radarOffset -= Calculate.SCANNER_ANGLE;
			else
				radarOffset += Calculate.SCANNER_ANGLE;
		}
		setTurnRadarLeftRadians(Calculate.normaliseBearing(radarOffset));
	}

	private void checkMode() {
//		checkModeByTurns();
		checkModeByEnergy();
	}

	private void checkModeByEnergy() {
		if (getEnergy() > 50) {
			_actualMode = getModeById(TaticMode.MODE);
			BaseRobotControls.actualMode = "TaticMode";
		} else {
			_actualMode = getModeById(AgressiveMode.MODE);
			BaseRobotControls.actualMode = "AgressiveMode";
		}
	}

	private void checkModeByTurns() {
		if (turnsToChange == TURNS_TO_CHANGE_MODE) {
			turnsToChange = 0;

			if (changed) {
				if (getModeById(AgressiveMode.MODE).getBulletTaken() > getModeById(TaticMode.MODE).getBulletTaken())
					_actualMode = getModeById(TaticMode.MODE);
				else
					_actualMode = getModeById(AgressiveMode.MODE);

				BaseRobotControls.log("Defined mode " + _actualMode.getModeId());
				BaseRobotControls
						.log("Agressive mode damage taked " + getModeById(AgressiveMode.MODE).getBulletTaken());
				BaseRobotControls.log("Tatic mode damage taked " + getModeById(TaticMode.MODE).getBulletTaken());

			} else {
				// simplificando o código, caso coloque mais modos, o deixarei mais completo
				_actualMode = getModeById(AgressiveMode.MODE);
				changed = true;
			}

			_actualMode.initMode();
		}

		turnsToChange++;
	}

	private Mode getModeById(int id) {
		if (_actualMode.getModeId() == id)
			return _actualMode;

		for (Mode mode : modeList) {
			if (mode.getModeId() == id)
				return mode;
		}

		return _actualMode;
	}

	@Override
	public void recover(double lastEnergy, double actualEnergy) {
		// BaseRobotControls.log(String.format("Last energy %s Actual energy %s",
		// lastEnergy, actualEnergy));
	}
}
