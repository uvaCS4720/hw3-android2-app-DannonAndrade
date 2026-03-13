package edu.nd.pmcburne.hwapp.one.data

data class ApiResponse(
    val games: List<GameWrapper>
)

data class GameWrapper(
    val game: GameData
)

data class GameData(
    val gameID: String,
    val away: TeamData,
    val home: TeamData,
    val startTime: String,
    val gameState: String,
    val currentPeriod: String,
    val contestClock: String
)

data class TeamData(
    val score: String,
    val names: TeamNames,
    val winner: Boolean
)

data class TeamNames(
    val short: String
)
