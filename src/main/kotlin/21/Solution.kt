package `21`

fun run_a(input: List<String>): String {
    val players = mutableListOf(Player(1, input[0]), Player(2, input[1]))
    val dice = DeterministicDice()

    while (players[1].score < 1000) {
        players.add(players.removeFirst().moveSpaces(dice.getNext() + dice.getNext() + dice.getNext()))
    }

    return (players[0].score * dice.rolls).toString()
}

fun run_b(input: List<String>): String {
    val movementOptionsPerTurn = listOf(3, 4, 5, 4, 5, 6, 5, 6, 7, 4, 5, 6, 5, 6, 7, 6, 7, 8, 5, 6, 7, 6, 7, 8, 7, 8, 9)
    val gameStates = mutableMapOf(GameState(listOf(Player(1, input[0]), Player(2, input[1]))) to 1L)
    val wins = mutableMapOf(1 to 0L, 2 to 0L)

    while (gameStates.isNotEmpty()) {
        val gameState = gameStates.keys.first()
        val gamesInState = gameStates.remove(gameState)!!

        if (gameState.players[1].score >= 21) {
            wins[gameState.players[1].playerNumber] = wins[gameState.players[1].playerNumber]!! + gamesInState
        } else {
            movementOptionsPerTurn.forEach { movement ->
                val newGameState = GameState(listOf(gameState.players[1], gameState.players[0].moveSpaces(movement)))
                gameStates[newGameState] = (gameStates[newGameState] ?: 0) + gamesInState
            }
        }
    }

    return wins.maxOf { it.value }.toString()
}

data class GameState(val players: List<Player>)
data class Player(val playerNumber: Int, val position: Int, val score: Int) {
    constructor(playerNumber: Int, position: String) : this(playerNumber, position.toInt(), 0)

    fun moveSpaces(count: Int) =
        ((position + count) % 10).let { if (it == 0) 10 else it }
            .let { newPosition -> Player(playerNumber, newPosition, score + newPosition) }
}

class DeterministicDice(var rolls: Int = 0, private var nextValue: Int = 1) {
    fun getNext() = nextValue
        .also { nextValue = (nextValue + 1).let { if (it > 100) it - 100 else it } }
        .also { rolls += 1 }
}
