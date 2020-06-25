package getoutbot.genetic

object Gene {

    fun getGene(id: Int): GeneCode {
        return when (id) {
            GeneCode.SET_TURN_LEFT.value -> GeneCode.SET_TURN_LEFT
            GeneCode.SET_AHEAD.value -> GeneCode.SET_AHEAD
            GeneCode.SET_TURN_RIGHT.value -> GeneCode.SET_TURN_RIGHT
            GeneCode.NOTHING.value -> GeneCode.NOTHING
            else -> GeneCode.NOTHING
        }
    }

    fun getGene(geneCode: GeneCode, value: String): String {
        return when (geneCode) {
            GeneCode.SET_TURN_LEFT -> "setTurnLeft($value)"
            GeneCode.SET_TURN_RIGHT -> "setTurnRight($value)"
            GeneCode.SET_AHEAD -> "setAhead($value)"
            GeneCode.NOTHING -> ""
        }
    }
}

enum class GeneCode(val value: Int) {
    NOTHING(0),
    SET_TURN_LEFT(1),
    SET_TURN_RIGHT(2),
    SET_AHEAD(3);

    companion object {
        const val actionsAmount = 4
    }
}