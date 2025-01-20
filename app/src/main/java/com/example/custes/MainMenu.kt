package com.example.custes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.navigation.NavController

import androidx.compose.material3.TextField
import androidx.compose.ui.graphics.Color


import androidx.compose.runtime.*



@Composable
fun MainMenu(navController: NavController, viewModel: GameSettingsViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "CusTes", fontSize = 32.sp)
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = { navController.navigate("game") }) {
            Text(text = "Start Game")
        }
        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = { navController.navigate("settings") }) {
            Text("Settings")
        }
        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = { navController.navigate("records") }) {
            Text("Records")
        }
    }
}

@Composable
fun SettingsMenu(navController: NavController, viewModel: GameSettingsViewModel) {
    var selectedColorType by remember { mutableStateOf("Block") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Text("Settings", fontSize = 17.sp )

        // Dropdown to select which color to customize
        Text("Choose which color to customize:")
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            RadioButtonGroup(
                options = listOf("Block", "Empty", "Shadow"),
                selectedOption = selectedColorType,
                onOptionSelected = { selectedColorType = it }
            )
        }


        // Show the currently selected color preview
        val currentColor = when (selectedColorType) {
            "Block" -> viewModel.blockColor
            "Empty" -> viewModel.emptyColor
            "Shadow" -> viewModel.shadowColor
            else -> Color.Black
        }
        Text("Current Color:")
        Box(
            modifier = Modifier
                .size(45.dp)
                .background(currentColor)
        )

        // Color Picker
        ColorPicker(
            selectedColor = currentColor,
            onColorSelected = { color ->
                when (selectedColorType) {
                    "Block" -> viewModel.blockColor = color
                    "Empty" -> viewModel.emptyColor = color
                    "Shadow" -> viewModel.shadowColor = color
                }
            }
        )


        // Numeric Inputs
        NumberSetting(
            label = "Minimum Blocks",
            value = viewModel.minBlocks,
            onValueChange = { if (it in 0..viewModel.maxBlocks) viewModel.minBlocks = it }
        )

        NumberSetting(
            label = "Maximum Blocks",
            value = viewModel.maxBlocks,
            onValueChange = { if (it >= viewModel.minBlocks) viewModel.maxBlocks = it }
        )

        NumberSetting(
            label = "Random Shape Chance (%)",
            value = viewModel.randomChance,
            onValueChange = { if (it in 0..100) viewModel.randomChance = it }
        )

        NumberSetting(
            label = "Starting Delay (ms)",
            value = viewModel.StartingDelay.toInt(),
            onValueChange = { if (it >= viewModel.MinimumDelay) viewModel.StartingDelay = it.toLong() }
        )

        NumberSetting(
            label = "Minimum Delay (ms)",
            value = viewModel.MinimumDelay.toInt(),
            onValueChange = { if (it in 0..viewModel.StartingDelay.toInt()) viewModel.MinimumDelay = it.toLong() }
        )

        NumberSetting(
            label = "Delay Decrease (ms)",
            value = viewModel.DelayDecrese.toInt(),
            onValueChange = { if (it >= 0) viewModel.DelayDecrese = it.toLong() }
        )

        NumberSetting(
            label = "Button Hold Delay (ms)",
            value = viewModel.ButtonSlownes.toInt(),
            onValueChange = { if (it > 0) viewModel.ButtonSlownes = it.toLong() }
        )

        // Back Button
        Button(onClick = { navController.popBackStack() }) {
            Text("Back")
        }
    }
}


@Composable
fun ColorPicker(selectedColor: Color, onColorSelected: (Color) -> Unit) {
    Row(horizontalArrangement = Arrangement.Center) {
        listOf(Color.Cyan, Color.Red, Color.Green, Color.Yellow, Color.Magenta,
            Color.Blue, Color.Black, Color.White).forEach { color ->
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color)
                    .clickable { onColorSelected(color) }
            )
        }
    }
}

@Composable
fun NumberSetting(label: String, value: Int, onValueChange: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 15.sp)

        Row(verticalAlignment = Alignment.CenterVertically) {
            HoldableButton(onPress = { if (value > 0) onValueChange(value - 1) }) {
                Text("-")
            }
            TextField(
                value = value.toString(),
                onValueChange = { it.toIntOrNull()?.let(onValueChange) },
                modifier = Modifier.width(65.dp),
                singleLine = true
            )
            HoldableButton(onPress = { onValueChange(value + 1) }) {
                Text("+")
            }
        }
    }
}



@Composable
fun RadioButtonGroup(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    options.forEach { option ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(1.dp)
        ) {
            RadioButton(
                selected = (selectedOption == option),
                onClick = { onOptionSelected(option) }
            )
            Spacer(modifier = Modifier.width(1.dp))
            Text(text = option)
        }
    }
}
