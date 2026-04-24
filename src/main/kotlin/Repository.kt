import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement

class Repository(private val dbFileName: String = "game_history.db") {
    private var connection: Connection

    init {
        Class.forName("org.sqlite.JDBC")
        connection = DriverManager.getConnection("jdbc:sqlite:$dbFileName")
        createTables()
    }

    private fun createTables() {
        val createPlayers = """
            CREATE TABLE IF NOT EXISTS players (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL UNIQUE
            )
        """.trimIndent()
        val createGames = """
            CREATE TABLE IF NOT EXISTS games (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                date TEXT NOT NULL,
                winner_id INTEGER
            )
        """.trimIndent()
        connection.createStatement().use { stmt ->
            stmt.execute(createPlayers)
            stmt.execute(createGames)
        }
    }

    fun saveGame(winnerName: String, playersNames: List<String>) {
        val playerIds = playersNames.map { name ->
            val select = connection.prepareStatement("SELECT id FROM players WHERE name = ?")
            select.setString(1, name)
            val rs = select.executeQuery()
            if (rs.next()) rs.getInt("id")
            else {
                val insert = connection.prepareStatement("INSERT INTO players(name) VALUES(?)", Statement.RETURN_GENERATED_KEYS)
                insert.setString(1, name)
                insert.executeUpdate()
                insert.generatedKeys.let { keys -> if (keys.next()) keys.getInt(1) else -1 }
            }
        }
        val winnerId = playerIds[playersNames.indexOf(winnerName)]
        val date = java.time.LocalDateTime.now().toString()
        val insertGame = connection.prepareStatement("INSERT INTO games(date, winner_id) VALUES(?, ?)")
        insertGame.setString(1, date)
        insertGame.setInt(2, winnerId)
        insertGame.executeUpdate()
    }

    fun readHistory(): List<Pair<String, String>> {
        val query = """
            SELECT g.date, p.name as winner 
            FROM games g 
            JOIN players p ON g.winner_id = p.id
            ORDER BY g.id DESC
        """.trimIndent()
        val result = mutableListOf<Pair<String, String>>()
        connection.createStatement().use { stmt ->
            val rs = stmt.executeQuery(query)
            while (rs.next()) {
                result.add(rs.getString("date") to rs.getString("winner"))
            }
        }
        return result
    }

    fun close() {
        connection.close()
    }
}