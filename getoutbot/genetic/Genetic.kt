package getoutbot.genetic

import getoutbot.main.BattleRunner
import kotlin.random.Random

object Genetic {

    const val GENERATIONS = 25
    const val POPULATION_SIZE = 10
    const val MUTATION_FEE = 0.2
    const val CROSSOVER_FEE = 0.7
    const val ACTIONS_SIZE = GeneCode.actionsAmount

    private var totalFitness = 0
    val chromosomes: ArrayList<Chromosome> = arrayListOf()

    fun start() {
        println("Starting population, writing files")
        for (i in 0 until POPULATION_SIZE) {
            chromosomes.add(Chromosome().initGenes().makeBot(i.toString()))
        }
    }

    fun evolveAndRunSons(battleRunner: BattleRunner) {
        println("It's the new generation")

        val newPopulation = arrayListOf<Chromosome>()
        val chromosomesToCrossover = arrayListOf<Chromosome>()

        totalFitness = 0
        chromosomes.forEach { totalFitness += it.fitness }

        getChromosomeOrderedByBest().apply {
            this.forEach {
                if (it.canBeRelative(totalFitness))
                    chromosomesToCrossover.add(it)

                newPopulation.add(it)
            }

            chromosomesToCrossover.forEach { chromosomeToCrossover ->
                if (chromosomeToCrossover.canReproduce()) {
                    val anotherChromosome = chromosomesToCrossover.random()

                    // testing one son, after i will test with two sons
                    val son: Chromosome = makeSonWith(chromosomeToCrossover, anotherChromosome)
                    son.apply {
                        val id = "9999"
                        battleRunner.runBattle(makeBot(id), id)
                    }

                    for (i in POPULATION_SIZE - 1 downTo 0) {
                        if (newPopulation[i].fitness > son.fitness) {
                            newPopulation[i] = (son)
                            break
                        }
                    }
                }
            }

            newPopulation.forEach { println("Fitness ${it.id} ${it.fitness}") }
            chromosomes.clear()
            chromosomes.addAll(newPopulation)
        }
    }

    private fun getChromosomeOrderedByBest(): List<Chromosome> {
        return chromosomes.sortedBy { it.fitness }
    }

    private fun makeSonWith(chromosomeToCrossover: Chromosome, anotherChromosome: Chromosome): Chromosome {
        return makeSon(chromosomeToCrossover.genes, anotherChromosome.genes)
    }

    private fun makeSon(genesP: ArrayList<Pair<GeneCode, String>>, genesM: ArrayList<Pair<GeneCode, String>>): Chromosome {
        val cutPoint = Random.nextInt(1, ACTIONS_SIZE)
        return Chromosome().also {
            val influent = if (Random.nextDouble() > .5) genesM else genesP

            for (i in 0 until cutPoint) {
                val g = genesP[i].first
                val v = influent[i].second
                it.genes.add(Pair(g, v))
            }

            for (i in cutPoint until genesM.size) {
                val g = genesM[i].first
                val v = influent[i].second
                it.genes.add(Pair(g, v))
            }

            it.tryMutate()
        }
    }

    fun makeBots() {
        for (i in 0 until chromosomes.size) chromosomes[i].makeBot(i.toString())
    }
}