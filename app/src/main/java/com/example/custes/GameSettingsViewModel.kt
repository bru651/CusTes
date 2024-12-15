package com.example.custes

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.runtime.*


class GameSettingsViewModel : ViewModel() {
    var blockColor by mutableStateOf(Color.Cyan)
    var emptyColor by mutableStateOf(Color.Black)
    var shadowColor by mutableStateOf(Color.Red)
    var minBlocks by mutableStateOf(3) // Minimum blocks for random shape bigger than 0
    var maxBlocks by mutableStateOf(7) // Maximum blocks for random shape not smaller than minBlocks
    var randomChance by mutableStateOf(50) // Chance of random shape 0-100
    var MinimumDelay by mutableStateOf(100L) // Minimum delay between block updates bigger than 0
    var StartingDelay by mutableStateOf(500L) // Starting delay between block updates bigger than MinimumDelay
    var DelayDecrese by mutableStateOf(5L) // How fast the game speeds-Up when placing shapes not smaller than 0
    var ButtonSlownes by mutableStateOf(100L) // Delay for holdable buttons bigger than 0
}
