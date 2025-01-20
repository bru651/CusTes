package com.example.custes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.material3.Button
import androidx.compose.material3.Text


import androidx.navigation.NavController
import androidx.compose.foundation.lazy.items

import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp

@Composable
fun RecordMenu(navController: NavController, scoresViewModel: ScoresViewModel) {
    val records by scoresViewModel.scores.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Recorded Scores",
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterHorizontally)
        )

        Box(
            modifier = Modifier.weight(1f) // Let the LazyColumn take up remaining space
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(records) { (name, score) -> // Use destructuring to handle Pair<String, Int>
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = name)
                        Text(text = score.toString())
                    }
                }
            }
        }

        Button(
            onClick = { navController.navigate("menu") },
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterHorizontally) // Center the button horizontally
        ) {
            Text("Back to Main Menu")
        }
    }
}

