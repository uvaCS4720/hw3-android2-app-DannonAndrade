package edu.nd.pmcburne.hwapp.one.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey val gameId: String,
    val gender: String,
    val date: String,
    val awayTeam: String,
    val homeTeam: String,
    val awayScore: String,
    val homeScore: String,
    val gameState: String,
    val startTime: String,
    val currentPeriod: String,
    val contestClock: String,
    val winner: String?
)
