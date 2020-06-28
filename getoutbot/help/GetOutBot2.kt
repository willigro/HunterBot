package getoutbot.formatbot

import robocode.AdvancedRobot
import robocode.ScannedRobotEvent
import java.awt.Color
import kotlin.math.cos
import kotlin.math.sin

class GetOutBot2 : AdvancedRobot() {

    private var enemy: Enemy = Enemy()
    private var action: IntArray = intArrayOf()

    override fun run() {
        setBodyColor(Color.black)
        setRadarColor(Color.black)
        setGunColor(Color.black)
        setScanColor(Color.RED)
        setBulletColor(Color.black)

        var a = false
        var t = 0
        while (true) {
//            action.forEach {
//                when (it) {
//                    0 -> setTurnRight(10.0)
//                    1 -> setAhead(100.0)
//                }
//            }
//
//            execute()
//            if (t > 5){
//                a = if (a) {
//                    setTurnRight(30.0)
//                    false
//                } else {
//                    setTurnLeft(30.0)
//                    true
//                }
//                t = 0
//            }
//            t++
//
//            setAhead(100.0)
            execute()
        }
    }

    override fun onScannedRobot(event: ScannedRobotEvent?) {
//        if (event == null) return
//
//        val absBearingRad: Double = (headingRadians + event.bearingRadians) % (2 * Math.PI)
//        enemy.fields(event, x, y, absBearingRad)
        /*onScannedRobot*/
    }
}

class Enemy {

    var bearing = 0.0
    var head = 0.0
    var speed = 0.0
    var x = 0.0
    var y = 0.0
    var distance = 0.0
    var energy = 0.0

    fun fields(e: ScannedRobotEvent, x: Double, y: Double, absBearingRad: Double) {
        this.x = x + sin(absBearingRad) * e.distance
        this.y = y + cos(absBearingRad) * e.distance
        bearing = e.bearingRadians
        head = e.headingRadians
        speed = e.velocity
        distance = e.distance
        energy = e.energy
    }
}