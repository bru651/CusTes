package com.example.custes


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay


import androidx.compose.ui.platform.LocalConfiguration

//import kotlin.math.pow
import androidx.compose.material3.Text

import androidx.compose.material3.Button
//import androidx.compose.material.Button
//import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.navigation.NavController

//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color


import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

// Game configuration
const val ROWS = 40
const val COLUMNS = 26

// Define the Tetriminos (L, T, I, etc.)
val SHAPES = listOf(
    listOf(Pair(0, 1), Pair(1, 1), Pair(2, 1), Pair(3, 1)),  // I shape
    listOf(Pair(1, 0), Pair(1, 1), Pair(1, 2), Pair(2, 2)),  // L shape
    listOf(Pair(1, 0), Pair(1, 1), Pair(0, 1), Pair(0, 2)),  // Z shape
    listOf(Pair(0, 1), Pair(1, 0), Pair(1, 1), Pair(1, 2)),  // T shape
    listOf(Pair(0, 0), Pair(0, 1), Pair(1, 0), Pair(1, 1))   // O shape
)

// Cell colors
//val COLORS = listOf(Color.Cyan, Color.Blue, Color.Red, Color.Green, Color.Yellow)

@Composable
fun TetrisGame(navController: NavController) {
    var grid by remember { mutableStateOf(Array(ROWS) { Array(COLUMNS) { Color.Black } }) }
    var currentShape by remember { mutableStateOf(SHAPES.random()) }
    var shapePosition by remember { mutableStateOf(Pair(0, COLUMNS / 2)) }
    var gameOver by remember { mutableStateOf(false) }
    var pause by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }

    val scope = rememberCoroutineScope() // Coroutine scope for navigation

    // Get screen height and dynamically calculate cell size
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val canvasHeight = screenHeight * 0.75f // 4/5 of the screen height
    val cellSize = canvasHeight / ROWS

    // Function to move the shape
    fun moveShape(dx: Int, dy: Int) {
        val newPosition = shapePosition.first + dy to shapePosition.second + dx
        if (canMove(currentShape, newPosition, grid)) {
            shapePosition = newPosition
        }
    }

    // Function to rotate the shape
    fun rotateShape() {
        val rotatedShape = currentShape.map { (dr, dc) -> Pair(dc, -dr) }
        if (canMove(rotatedShape, shapePosition, grid)) {
            currentShape = rotatedShape
        }
    }

    // Main game loop
    LaunchedEffect(Unit) {
        while (!gameOver) {
            delay(500)
            if(!pause){
            val newPosition = shapePosition.first + 1 to shapePosition.second
            if (canMove(currentShape, newPosition, grid)) {
                shapePosition = newPosition
            } else {
                placeShape(currentShape, shapePosition, grid)
                score += clearRows(grid)
                currentShape = SHAPES.random()
                shapePosition = 0 to COLUMNS / 2
                if (!canMove(currentShape, shapePosition, grid)) gameOver = true
            }
            }
        }
        // Navigate back to main menu on game over
        scope.launch {
            navController.popBackStack() // Go back to the previous screen (Main Menu)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text("Score: $score", fontSize = 24.sp, modifier = Modifier.padding(8.dp))

        // Game Canvas taking 4/5 of the screen height
        Box(
            Modifier
                .background(Color.DarkGray)
                .padding(8.dp)
                .weight(4f) // 4/5 of the available height
        ) {
            Canvas(
                Modifier
                    .fillMaxWidth()
                    .height(canvasHeight)
            ) {
                for (row in 0 until ROWS) {
                    for (col in 0 until COLUMNS) {
                        drawRect(
                            color = grid[row][col],
                            topLeft = androidx.compose.ui.geometry.Offset(col * cellSize.toPx(), row * cellSize.toPx()),
                            size = androidx.compose.ui.geometry.Size(cellSize.toPx(), cellSize.toPx())
                        )
                    }
                }
                // Draw current falling shape
                for ((dr, dc) in currentShape) {
                    drawRect(
                        color = Color.Cyan,
                        topLeft = androidx.compose.ui.geometry.Offset(
                            (shapePosition.second + dc) * cellSize.toPx(),
                            (shapePosition.first + dr) * cellSize.toPx()
                        ),
                        size = androidx.compose.ui.geometry.Size(cellSize.toPx(), cellSize.toPx())
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .background(Color.DarkGray)
                .fillMaxWidth()
                .weight(1f) // 1/5 of the screen height
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // First Row for movement and rotation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                Button(onClick = {if(!pause) moveShape(dx = -1, dy = 0) }) {
                    Text("<")
                }
                Button(onClick = {if(!pause) rotateShape() }) {
                    Text("⟳")
                }

                Button(onClick = {if(!pause) moveShape(dx = 1, dy = 0) }) {
                    Text(">")
                }

            }

            Spacer(modifier = Modifier.height(8.dp)) // Add some spacing between rows

            // Second Row for Pause and Hard Drop
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                Button(onClick = { pause = !pause }) {
                    Text("Pause")
                }
                Button(onClick = {if(!pause) moveShape(dx = 0, dy = 1) }) {
                    Text("v")
                }
                Button(onClick = {
                    while (canMove(currentShape, shapePosition.first + 1 to shapePosition.second, grid)) {
                        shapePosition = shapePosition.first + 1 to shapePosition.second
                    }
                }) {
                    Text("V")
                }
            }
        }

    }
}

fun placeShape(shape: List<Pair<Int, Int>>, pos: Pair<Int, Int>, grid: Array<Array<Color>>) {
    for ((dr, dc) in shape) {
        val row = pos.first + dr
        val col = pos.second + dc
        if (row in grid.indices && col in grid[row].indices) grid[row][col] = Color.Cyan
    }
}

fun canMove(shape: List<Pair<Int, Int>>, pos: Pair<Int, Int>, grid: Array<Array<Color>>): Boolean {
    return shape.all { (dr, dc) ->
        val row = pos.first + dr
        val col = pos.second + dc
        row in 0 until ROWS && col in 0 until COLUMNS && grid[row][col] == Color.Black
    }
}

/*fun clearRows(grid: Array<Array<Color>>) {
    val nonFullRows = grid.filter { row -> row.any { it == Color.Black } }
    val fullRows = ROWS - nonFullRows.size
    repeat(fullRows) {
        grid[it] = Array(COLUMNS) { Color.Black }
    }
}*/

fun clearRows(grid: Array<Array<Color>>): Int {
    var clearedRows = 0

    // Iterate from bottom to top to clear full rows
    for (row in ROWS - 1 downTo 0) {
        if (grid[row].all { it != Color.Black }) { // Check if the row is full
            clearedRows++

            // Shift rows down by copying the row above
            for (r in row downTo 1) {
                grid[r] = grid[r - 1].copyOf()
            }

            // Clear the top row after shifting
            grid[0] = Array(COLUMNS) { Color.Black }

            // After clearing a row, check the current row again (in case of multiple full rows)
            //row++
            clearedRows += clearRows(grid)
        }
    }
    return clearedRows
}

