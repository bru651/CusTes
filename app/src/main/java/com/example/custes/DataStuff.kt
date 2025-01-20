package com.example.custes

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

import kotlinx.serialization.json.Json

// Define the DataStore
private val Context.dataStore by preferencesDataStore(name = "game_records")

class DataStoreManager(private val context: Context) {
    companion object {
        private val RECORDS_KEY = stringPreferencesKey("records")
    }

    // Save records as a JSON string
    suspend fun saveRecords(records: List<Pair<String, Int>>) {
        val recordsJson = Json.encodeToString(records)
        context.dataStore.edit { preferences ->
            preferences[RECORDS_KEY] = recordsJson
        }
    }

    // Load records and parse the JSON string
    val recordsFlow: Flow<List<Pair<String, Int>>> = context.dataStore.data.map { preferences ->
        preferences[RECORDS_KEY]?.let {
            Json.decodeFromString(it)
        } ?: emptyList()
    }
}

