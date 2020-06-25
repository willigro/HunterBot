package getoutbot.main

import getoutbot.main.BattleRunner.Companion.ROBOCODE_PATH
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class FileBot {

    private val robotPath = "$ROBOCODE_PATH\\robots\\getoutbot\\genetic"
    private val baseBotFilePath = "$robotPath\\GetOutBot"
    private val jars = "$ROBOCODE_PATH\\libs\\robocode.jar"
    private val fitPath = "$robotPath\\fit.txt"
    lateinit var file: FileWriter

    /**
     * Tips ->
     * write code in GetOutBot
     * copy and paste code in baseBotUnformated.txt
     * run format_baseBot.py
     * go to baseBot.txt and copy and past the code here
     *
     * or
     *
     * Use the extractGetOutBotFile
     * */

    private var _id: String = ""
    private var onScannedRobot = ""
    private var doMove = ""
    private var enemyDropEnergy = ""

    fun load(id: String): FileBot {
        _id = id
        File(robotPath).apply {
            mkdirs()
            file = FileWriter("$baseBotFilePath$_id.java")
        }
        return this
    }

    fun write(): String {
        val getOutBotFile = extractGetOutBotFile()

        val out = BufferedWriter(file)
        out.write(getOutBotFile)
        out.close()

        try {
            execute("javac -cp $jars $baseBotFilePath$_id.java")
        } catch (e: Exception) {
            e.printStackTrace()
            println(e.message)
        }
        return "$baseBotFilePath$_id.class"
    }

    private fun extractGetOutBotFile(): String {
        File("D:\\Rittmann\\Projetos\\eclipse-workspace\\hunterbot\\src\\getoutbot\\genetic\\GetOutBot.java").apply {
            val fileReader = FileReader(this)
            val out = BufferedReader(fileReader)
            var f = ""
            out.readLines().also { list ->
                list.forEach {
                    f += when {
                        it.contains("/*enemyDropEnergy*/") -> it.replace("/*enemyDropEnergy*/", enemyDropEnergy)
                        it.contains("/*doMove*/") -> it.replace("/*doMove*/", doMove)
                        it.contains("/*onScannedRobot*/") -> it.replace("/*onScannedRobot*/", onScannedRobot)
                        it.contains("GetOutBot") -> it.replace("GetOutBot", "GetOutBot$_id")
                        else -> it
                    }
                    f += "\n"
                }
            }
            out.close()
            return f
        }
    }

    fun getActualFitness(): Int {
        File(fitPath).apply {
            val fileReader = FileReader(this)
            val out = BufferedReader(fileReader)
            var fit = 0
            out.readLine()?.also {
                fit = it.toInt()
            }
            out.close()
            return fit
        }
    }

    @Throws(Exception::class)
    fun execute(command: String) {
        val process = Runtime.getRuntime().exec(command)
        process.waitFor()
        if (process.exitValue() != 0) println(command + "exited with value " + process.exitValue())
    }

    private fun translateGenes(genes: ArrayList<String>): String {
        val stringBuilder = StringBuilder()
        genes.forEach {
            if (it.isNotEmpty()) stringBuilder.append("\n$it;")
        }
        return stringBuilder.toString()
    }

    fun handleEnergyDrop(genes: ArrayList<String>): FileBot {
        enemyDropEnergy = translateGenes(genes)
        return this
    }

    fun doMove(genes: ArrayList<String>): FileBot {
        doMove = translateGenes(genes)
        return this
    }

    fun onScannedRobot(genes: ArrayList<String>): FileBot {
        onScannedRobot = translateGenes(genes)
        return this
    }
}