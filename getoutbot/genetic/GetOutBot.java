package getoutbot.genetic;

import robocode.*;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GetOutBot extends AdvancedRobot {

    /**
     * Genetic and file vars
     */
    private int fit = 0;
    private final String path = "D:\\Rittmann\\Projetos\\eclipse-workspace\\hunterbot\\robocode\\robots\\getoutbot\\genetic";
    private String log = "";

    /**
     * Robot vars
     */
    private double fullEnergy = 0.0;
    private double lastEnemyEnergy = 0.0;

    @Override
    public void run() {
        setBodyColor(Color.black);
        setRadarColor(Color.black);
        setGunColor(Color.black);
        setScanColor(Color.RED);
        setBulletColor(Color.black);

        fullEnergy = getEnergy();

        //noinspection InfiniteLoopStatement
        while (true) {
            doMove();
            setTurnRadarRight(Double.POSITIVE_INFINITY);
            execute();
        }
    }

    private void doMove() {
        /*doMove*/
    }

    public boolean enemyDropEnergy(ScannedRobotEvent event) {
        double changeInEnergy = lastEnemyEnergy - event.getEnergy();
        lastEnemyEnergy = event.getEnergy();
        return changeInEnergy > 0 && changeInEnergy <= 3;
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        /*onScannedRobot*/
        if (enemyDropEnergy(event)) {
            /*enemyDropEnergy*/
        }
    }

    @Override
    public void onHitByBullet(HitByBulletEvent event) {
        fit += 10;
        log += "onHitByBullet " + fit + "\n";
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        fit += 5;
        log += "onHitWall " + fit + "\n";
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void onRoundEnded(RoundEndedEvent event) {
        fit += (fullEnergy - getEnergy()) * 2;
        fit -= event.getTurns();
        log += "\nonRoundEnded " + fit + "\n";
        File file = new File(path);
        file.mkdirs();
        try {
            FileWriter fileWriter = new FileWriter(path + "\\fit.txt");
            BufferedWriter out = new BufferedWriter(fileWriter);
            out.write(String.valueOf(fit));
            out.write("\n" + log);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
