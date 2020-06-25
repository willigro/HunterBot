package getoutbot.main

import getoutbot.genetic.Chromosome
import robocode.control.BattleSpecification
import robocode.control.BattlefieldSpecification
import robocode.control.RobocodeEngine
import robocode.control.events.BattleAdaptor
import robocode.control.events.BattleCompletedEvent
import robocode.control.events.BattleErrorEvent
import robocode.control.events.BattleMessageEvent
import java.io.File
import kotlin.system.exitProcess

class BattleRunner {
    companion object {
        const val ROBOCODE_PATH = "D:\\Rittmann\\Projetos\\eclipse-workspace\\hunterbot\\robocode"
        private const val trackFire = "sample.TrackFire,getoutbot.genetic.GetOutBot"
        private const val snippet = "snippet.SnippetBot*,getoutbot.genetic.GetOutBot"
        private const val hunter = "hunterbot.Hunter*,getoutbot.genetic.GetOutBot"
        private const val rochedo = "rochedo.Rochedo*,getoutbot.genetic.GetOutBot"
    }

    private lateinit var engine: RobocodeEngine
    private var battleObserver: BattleObserver = BattleObserver()
    private var battleSpec: BattleSpecification? = null
    private lateinit var battlefield: BattlefieldSpecification
    private var opponents = trackFire // choose opponent

    init {
        prepareBattlefield()
    }

    private fun prepareBattlefield() {
        RobocodeEngine.setLogMessagesEnabled(false)
        engine = RobocodeEngine(File(ROBOCODE_PATH)) // Run from default dir
        engine.addBattleListener(battleObserver)
        engine.setVisible(false)
        battlefield = BattlefieldSpecification(800, 600) // 800x600
    }

    fun battleShutdown() {
        // Cleanup our RobocodeEngine
        engine.close()

        // Make sure that the Java VM is shut down properly
        exitProcess(0)
    }

    fun runBattle(chromosome: Chromosome, id: String) {
        val numberOfRounds = 1
        battleObserver.actualCromossome = chromosome

        val selectedRobots = engine.getLocalRepository("$opponents$id*")
        battleSpec = BattleSpecification(numberOfRounds, battlefield, selectedRobots)
        engine.runBattle(battleSpec, true)
    }
}

class BattleObserver : BattleAdaptor() {

    lateinit var actualCromossome: Chromosome

    override fun onBattleCompleted(e: BattleCompletedEvent) {
//        println("-- Battle has completed --")
        for (result in e.sortedResults) {
//            println("  " + result.teamLeaderName + ": " + result.score)
            if (result.teamLeaderName.contains("GetOutBot"))
                actualCromossome.fitness = FileBot().getActualFitness()
        }
    }

    // Called when the game sends out an information message during the battle
    override fun onBattleMessage(e: BattleMessageEvent) {
//        println("Msg> " + e.message)
    }

    // Called when the game sends out an error message during the battle
    override fun onBattleError(e: BattleErrorEvent) {
        println("Err> " + e.error)
    }
}