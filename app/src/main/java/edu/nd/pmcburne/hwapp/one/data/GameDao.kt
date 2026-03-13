package edu.nd.pmcburne.hwapp.one.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GameDao {
    @Query("SELECT * FROM games WHERE gender = :gender AND date = :date ORDER BY gameId")
    suspend fun getGames(gender: String, date: String): List<GameEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(games: List<GameEntity>)
}
