import java.time.LocalDateTime
import logic.Player
data class Game(
    val id: Int,
    val date: LocalDateTime,
    val players: List<Player>,
    val winnerId: Int?,
    val scores: Map<Int, Int>
)

class StatisticsAndLeaderboard(private val gameHistory: List<Game>) {
    private lateinit var leaderboard: List<PlayerStats>

    data class PlayerStats(
        val playerId: Int,
        val name: String,
        val totalGames: Int,
        val wins: Int,
        val totalScore: Int,
        val rating: Double   // процент побед (wins / totalGames * 100)
    )

    fun ratingCalculation() {
        val statsMap = mutableMapOf<Int, MutableMap<String, Any>>()

        for (game in gameHistory) {
            val winnerId = game.winnerId

            for (player in game.players) {
                val playerId = player.id
                val stats = statsMap.getOrPut(playerId) {
                    mutableMapOf(
                        "name" to player.name,
                        "totalGames" to 0,
                        "wins" to 0,
                        "totalScore" to 0
                    )
                }
                stats["totalGames"] = stats["totalGames"] as Int + 1
                stats["totalScore"] = stats["totalScore"] as Int + (game.scores[playerId] ?: 0)
                if (winnerId == playerId) {
                    stats["wins"] = stats["wins"] as Int + 1
                }
            }
        }

        leaderboard = statsMap.map { (playerId, data) ->
            val totalGames = data["totalGames"] as Int
            val wins = data["wins"] as Int
            val rating = if (totalGames > 0) (wins.toDouble() / totalGames) * 100 else 0.0
            PlayerStats(
                playerId = playerId,
                name = data["name"] as String,
                totalGames = totalGames,
                wins = wins,
                totalScore = data["totalScore"] as Int,
                rating = rating
            )
        }.sortedByDescending { it.rating }
    }
}
