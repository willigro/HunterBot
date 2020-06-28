package getoutbot.genetic

import getoutbot.cultural.BeliefSpace
import getoutbot.main.FileBot
import kotlin.random.Random

class Chromosome(var generation: Int) {

    var id = ""
    var genes: ArrayList<Pair<GeneCode, String>> = arrayListOf()
    var fitness: Int = 0

    fun initGenes(): Chromosome {
        for (i in 0 until Genetic.ACTIONS_SIZE) {
            genes.add(randomGene())
        }
        return this
    }

    fun makeBot(id: String): Chromosome {
        this.id = id
        FileBot().load(id).handleEnergyDrop(translateGenes()).write()
        return this
    }

    private fun randomGene(): Pair<GeneCode, String> {
        val r = Random.nextInt(0, Genetic.ACTIONS_SIZE)
        return Pair(Gene.getGene(r), Random.nextInt(0, 100).toString())
    }

    private fun translateGenes(): ArrayList<String> = arrayListOf<String>().apply {
        for (i in genes.indices) {
            genes[i].also { gene ->
                add(Gene.getGene(gene.first, gene.second))
            }
        }
    }

    fun canBeRelative(totalFitness: Int): Boolean {
        if (totalFitness == 0) return false
        return Random.nextDouble() > fitness / totalFitness
    }

    fun canReproduce(): Boolean {
        return Random.nextDouble() < Genetic.CROSSOVER_FEE
    }

    fun tryMutate() {
        for (i in 0 until genes.size) {
            if (Random.nextDouble() < Genetic.MUTATION_FEE) {
                randomGene().apply {
                    val v = newValue(second, i)
                    genes[i] = Pair(first, v)
                }
            }
        }
    }

    private fun newValue(oldValue: String, i: Int): String {
        return toDecimal(toBinary(oldValue.toInt()).let { binary ->
            val cutPoint = Random.nextInt(1, binary.length)

            var newBinary = ""
            for (c in binary.indices) {
                newBinary += if (c < cutPoint) {
                    if (binary[c] == '0') '1' else '0'
                } else {
                    binary[c]
                }
            }
            println("old $binary new $newBinary")
            newBinary
        }).toString()
    }

    private fun pow(base: Int, exponent: Int) = Math.pow(base.toDouble(), exponent.toDouble()).toInt()

    private fun toDecimal(binaryNumber: String): Int {
        var sum = 0
        binaryNumber.reversed().forEachIndexed { k, v ->
            sum += v.toString().toInt() * pow(2, k)
        }
        return sum
    }

    private fun toBinary(decimalNumber: Int, binaryString: String = ""): String {
        while (decimalNumber > 0) {
            val temp = "${binaryString}${decimalNumber % 2}"
            return toBinary(decimalNumber / 2, temp)
        }

        return binaryString.reversed().let {
            var res = ""
            val diff = 8 - it.length
            for (i in 0 until diff) {
                res += "0"
            }
            res + it
        }
    }

    fun isBetterThan(chromosome: Chromosome): Boolean {
        return fitness < chromosome.fitness
    }

    fun log() {
        println("Chromosome $id Generation $generation Fitness $fitness")
    }
}