package com.example.custes

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey

import kotlinx.coroutines.flow.map


class ScoresViewModel(private val dataStore: DataStore<Preferences>) : ViewModel() {
    private val _scores = MutableStateFlow<List<Pair<String, Int>>>(emptyList())
    val scores: StateFlow<List<Pair<String, Int>>> = _scores

    init {
        loadScores()
    }

    private fun loadScores() {
        viewModelScope.launch {
            dataStore.data.map { preferences ->
                preferences.asMap().entries
                    .map { (key, value) ->
                        Pair(key.name, value as Int)
                    }
                    .sortedByDescending { it.second }
            }.collect { loadedScores ->
                _scores.value = loadedScores
            }
        }
    }

    suspend fun addScore(name: String, score: Int) {
        dataStore.edit { preferences ->
            preferences[intPreferencesKey(name)] = score
        }
        loadScores()
    }
}



