package edu.nd.pmcburne.hwapp.one.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class Repository(context: Context) {
    private val dao = getDatabase(context).gameDao()

    fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val net = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(net) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun loadGames(gender: String, date: String, context: Context): Flow<List<GameEntity>> = flow {
        if (isOnline(context)) {
            val parts = date.split("/")
            if (parts.size == 3) {
                val month = parts[0].toIntOrNull() ?: 1
                val day = parts[1].toIntOrNull() ?: 1
                val year = parts[2].toIntOrNull() ?: 2026
                try {
                    val retrofit = Api.create()
                    val api = retrofit.create(ScoreApi::class.java)
                    val url = Api.buildUrl(gender, year, month, day)
                    val resp = withContext(Dispatchers.IO) { api.getScores(url) }
                    val entities = resp.games.map { wrapper ->
                        val g = wrapper.game
                        val winner = when {
                            g.gameState == "final" && g.home.winner -> g.home.names.short
                            g.gameState == "final" && g.away.winner -> g.away.names.short
                            else -> null
                        }
                        GameEntity(
                            gameId = g.gameID,
                            gender = gender,
                            date = date,
                            awayTeam = g.away.names.short,
                            homeTeam = g.home.names.short,
                            awayScore = g.away.score,
                            homeScore = g.home.score,
                            gameState = g.gameState,
                            startTime = g.startTime,
                            currentPeriod = g.currentPeriod,
                            contestClock = g.contestClock,
                            winner = winner
                        )
                    }
                    withContext(Dispatchers.IO) { dao.insertAll(entities) }
                } catch (_: Exception) {}
            }
        }
        emit(withContext(Dispatchers.IO) { dao.getGames(gender, date) })
    }
}
