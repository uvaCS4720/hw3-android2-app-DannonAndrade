package edu.nd.pmcburne.hwapp.one.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.nd.pmcburne.hwapp.one.data.GameEntity
import edu.nd.pmcburne.hwapp.one.data.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ScoreViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = Repository(application)
    private val df = SimpleDateFormat("MM/dd/yyyy", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private val _games = MutableStateFlow<List<GameEntity>>(emptyList())
    val games: StateFlow<List<GameEntity>> = _games.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _gender = MutableStateFlow("men")
    val gender: StateFlow<String> = _gender.asStateFlow()

    private val _date = MutableStateFlow(df.format(Date()))
    val date: StateFlow<String> = _date.asStateFlow()

    fun setGender(g: String) {
        _gender.value = g
        refresh()
    }

    fun setDate(d: String) {
        _date.value = d
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _loading.value = true
            repo.loadGames(_gender.value, _date.value, getApplication()).collect {
                _games.value = it
                _loading.value = false
            }
        }
    }
}
