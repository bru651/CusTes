package com.example.custes

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


class MainActivity : ComponentActivity() {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "scores_preferences")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gameSettingsViewModel = GameSettingsViewModel()
        val scoresViewModel = ScoresViewModel(dataStore = dataStore)
        setContent {
            val navController: NavHostController = rememberNavController()
            //Surface(color = MaterialTheme.colors.background) {
                NavHost(navController = navController, startDestination = "menu") {
                    composable("menu") { MainMenu(navController, gameSettingsViewModel) }
                    composable("game") { TetrisGame(navController, gameSettingsViewModel, scoresViewModel) }
                    composable("settings") { SettingsMenu(navController, gameSettingsViewModel) }
                    composable("records") { RecordMenu(navController, scoresViewModel) }
                }
        }
    }
}


/*class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TetrisGame()
            /*CustesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }*/
        }
    }
}*/

/*@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CustesTheme {
        Greeting("Android")
    }
}*/