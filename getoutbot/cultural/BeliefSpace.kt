package getoutbot.cultural

import getoutbot.genetic.Chromosome
import getoutbot.genetic.Genetic
import kotlin.random.Random

object BeliefSpace {

    private var situational: Chromosome? = null

    fun best(): Chromosome? {
        return situational?.also {
            print("The best is ")
        }
    }

    fun influence(chromosome: Chromosome) {
        if (Random.nextDouble() > 0.25)
            influenceBySituational(chromosome)
    }

    fun acceptSituational(chromosome: Chromosome) {
        updateSituational(chromosome)
    }

    private fun updateSituational(chromosome: Chromosome) {
        if (situational == null)
            situational = chromosome
        else if (chromosome.isBetterThan(situational!!)) {
            situational = chromosome
        }
    }

    private fun influenceBySituational(chromosome: Chromosome) {
        if (situational == null) return

        val selectedAction = Random.nextInt(0, situational!!.genes.size)

        if (chromosome.genes.size < Genetic.ACTIONS_SIZE && Random.nextDouble() > .5) {
            chromosome.genes.add(situational!!.genes[selectedAction])
        } else {
            val actionToReplace = Random.nextInt(0, chromosome.genes.size)
            chromosome.genes[actionToReplace] = situational!!.genes[selectedAction]
        }
    }
}