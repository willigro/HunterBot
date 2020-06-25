package getoutbot.main

import getoutbot.genetic.Genetic

class Run {
    private var myBattleRunner: BattleRunner = BattleRunner()

    init {
        startPopulation()
        runFirst()
    }

    private fun startPopulation() {
        Genetic.start()
    }

    private fun runFirst() {
        println("running")
        runPopulation()

        for (i in 0 until Genetic.GENERATIONS) {
            runPopulation()
            Genetic.evolveAndRunSons(myBattleRunner)
        }

        myBattleRunner.battleShutdown()
    }

    private fun runPopulation() {
        for (i in 0 until Genetic.POPULATION_SIZE) {
            myBattleRunner.runBattle(Genetic.chromosomes[i].apply {
                makeBot(i.toString())
            }, i.toString())
        }
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            Run()
        }
    }
}