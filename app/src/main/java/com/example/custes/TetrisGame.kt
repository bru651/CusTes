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

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.graphics.toArgb

// Game configuration
const val ROWS = 40
const val COLUMNS = 26

// Define standard shapes
val SHAPES = listOf(
    listOf(Pair(0, 1), Pair(1, 1), Pair(2, 1), Pair(3, 1)),  // I shape
    listOf(Pair(1, 0), Pair(1, 1), Pair(1, 2), Pair(2, 2)),  // L shape
    listOf(Pair(1, 0), Pair(1, 1), Pair(0, 1), Pair(0, 2)),  // Z shape
    listOf(Pair(0, 1), Pair(1, 0), Pair(1, 1), Pair(1, 2)),  // T shape
    listOf(Pair(0, 0), Pair(0, 1), Pair(1, 0), Pair(1, 1))   // O shape
)

// Cell colors
//val COLORS = listOf(Color.Cyan, Color.Blue, Color.Red, Color.Green, Color.Yellow)
const val EMPTY = 0
const val FILLED = 1
const val SHADOW = 2

@Composable
fun TetrisGame(navController: NavController, viewModel: GameSettingsViewModel) {
    val currentBackStackEntry = navController.currentBackStackEntry

    val colorMap = mapOf(
        EMPTY to viewModel.emptyColor,
        FILLED to viewModel.blockColor,//Color.Cyan,
        SHADOW to viewModel.shadowColor
    )
    //var grid by remember { mutableStateOf(Array(ROWS) { Array(COLUMNS) { Color.Black } }) }
    var grid by remember { mutableStateOf(Array(ROWS) { Array(COLUMNS) { EMPTY } }) }
    var currentShape by remember { mutableStateOf(generateNextShape(viewModel)) }
    var shapePosition by remember { mutableStateOf(Pair(0, COLUMNS / 2)) }
    var gameOver by remember { mutableStateOf(false) }
    var pause by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }
    var currentDelay by remember { mutableStateOf(viewModel.StartingDelay) }

    val scope = rememberCoroutineScope() // Coroutine scope for navigation

    // Get screen height and dynamically calculate cell size
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val canvasHeight = screenHeight * 0.75f // 3/4 of the screen height
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
        // Calculate the bounding box of the shape
        val minRow = currentShape.minOf { it.first }
        val maxRow = currentShape.maxOf { it.first }
        val minCol = currentShape.minOf { it.second }
        val maxCol = currentShape.maxOf { it.second }

        // Dimensions of the bounding box
        val boxHeight = maxRow - minRow
        val boxWidth = maxCol - minCol

        // Rotate each block within the bounding box
        val rotatedShape = currentShape.map { (row, col) ->
            // Normalize the position within the bounding box
            val normalizedRow = row - minRow
            val normalizedCol = col - minCol

            // Perform 90-degree clockwise rotation
            val rotatedRow = normalizedCol
            val rotatedCol = boxHeight - normalizedRow

            // Map back to the grid position
            Pair(rotatedRow + minRow, rotatedCol + minCol)
        }

        // Update the shape only if the rotated position is valid
        if (canMove(rotatedShape, shapePosition, grid)) {
            currentShape = rotatedShape
        }
    }


    // Main game loop
    LaunchedEffect(Unit) {
        while (!gameOver) {
            delay(currentDelay)
            if(!pause){
            val newPosition = shapePosition.first + 1 to shapePosition.second
            if (canMove(currentShape, newPosition, grid)) {
                shapePosition = newPosition
            } else {
                placeShape(currentShape, shapePosition, grid)
                score += clearRows(grid)
                currentDelay -= viewModel.DelayDecrese
                if(currentDelay < viewModel.MinimumDelay) currentDelay = viewModel.MinimumDelay
                currentShape = generateNextShape(viewModel)
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
                        val color = colorMap[grid[row][col]] ?: Color.Black // Default to black if state is missing
                        drawRect(
                            color = color,
                            topLeft = androidx.compose.ui.geometry.Offset(col * cellSize.toPx(), row * cellSize.toPx()),
                            size = androidx.compose.ui.geometry.Size(cellSize.toPx(), cellSize.toPx())
                        )
                    }
                }
                // Draw the shadow first
                val shadowPosition = calculateShadowPosition(currentShape, shapePosition, grid)
                for ((dr, dc) in currentShape) {
                    val row = shadowPosition.first + dr
                    val col = shadowPosition.second + dc
                    if (row in 0 until ROWS && col in 0 until COLUMNS) {
                        drawRect(
                            color = colorMap[SHADOW] ?: Color.Red, // Semi-transparent color for shadow
                            topLeft = androidx.compose.ui.geometry.Offset(col * cellSize.toPx(), row * cellSize.toPx()),
                            size = androidx.compose.ui.geometry.Size(cellSize.toPx(), cellSize.toPx())
                        )
                    }
                }
                // Draw current falling shape
                for ((dr, dc) in currentShape) {
                    val row = shapePosition.first + dr
                    val col = shapePosition.second + dc
                    drawRect(
                        color = colorMap[FILLED] ?: Color.Cyan, // Use the filled color for shapes
                        topLeft = androidx.compose.ui.geometry.Offset(col * cellSize.toPx(), row * cellSize.toPx()),
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
                HoldableButton(onPress = {if(!pause) moveShape(dx = -1, dy = 0) }, repeatInterval = viewModel.ButtonSlownes) {
                    Text("<")
                }
                MyButton(onClick = {if(!pause) rotateShape() }) {
                    Text("⟳")
                }

                HoldableButton(onPress = {if(!pause) moveShape(dx = 1, dy = 0) }, repeatInterval = viewModel.ButtonSlownes) {
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
                MyButton(onClick = { pause = !pause }) {
                    Text("Pause")
                }
                HoldableButton(onPress = {if(!pause) moveShape(dx = 0, dy = 1) }, repeatInterval = viewModel.ButtonSlownes) {
                    Text("v")
                }
                MyButton(onClick = {
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

fun placeShape(shape: List<Pair<Int, Int>>, pos: Pair<Int, Int>, grid: Array<Array<Int>>) {
    for ((dr, dc) in shape) {
        val row = pos.first + dr
        val col = pos.second + dc
        if (row in grid.indices && col in grid[row].indices) grid[row][col] = FILLED
    }
}


fun canMove(shape: List<Pair<Int, Int>>, pos: Pair<Int, Int>, grid: Array<Array<Int>>): Boolean {
    return shape.all { (dr, dc) ->
        val row = pos.first + dr
        val col = pos.second + dc
        row in 0 until ROWS && col in 0 until COLUMNS && grid[row][col] == EMPTY
    }
}

/*fun clearRows(grid: Array<Array<Color>>) {
    val nonFullRows = grid.filter { row -> row.any { it == Color.Black } }
    val fullRows = ROWS - nonFullRows.size
    repeat(fullRows) {
        grid[it] = Array(COLUMNS) { Color.Black }
    }
}*/

fun clearRows(grid: Array<Array<Int>>): Int {
    var clearedRows = 0

    // Iterate from bottom to top to clear full rows
    for (row in ROWS - 1 downTo 0) {
        if (grid[row].all { it != EMPTY }) { // Check if the row is full
            clearedRows++

            // Shift rows down by copying the row above
            for (r in row downTo 1) {
                grid[r] = grid[r - 1].copyOf()
            }

            // Clear the top row after shifting
            grid[0] = Array(COLUMNS) { EMPTY }

            // After clearing a row, check the current row again (in case of multiple full rows)
            //row++
            clearedRows += clearRows(grid)
        }
    }
    return clearedRows
}

fun calculateShadowPosition(
    shape: List<Pair<Int, Int>>,
    position: Pair<Int, Int>,
    grid: Array<Array<Int>>
): Pair<Int, Int> {
    var shadowPosition = position
    while (canMove(shape, shadowPosition.first + 1 to shadowPosition.second, grid)) {
        shadowPosition = shadowPosition.first + 1 to shadowPosition.second
    }
    return shadowPosition
}

fun generateRandomShape(minBlocks: Int, maxBlocks: Int): List<Pair<Int, Int>> {
    val blockCount = (minBlocks..maxBlocks).random()
    val shape = mutableSetOf<Pair<Int, Int>>()

    shape.add(0 to 0) // Start at the origin

    while (shape.size < blockCount) {
        val randomBlock = shape.random()
        val newBlock = when ((0..3).random()) {
            0 -> randomBlock.first to randomBlock.second + 1 // Right
            1 -> randomBlock.first to randomBlock.second - 1 // Left
            2 -> randomBlock.first + 1 to randomBlock.second // Down
            else -> randomBlock.first - 1 to randomBlock.second // Up
        }

        // Ensure the new block is within bounds and not self-colliding
        if (newBlock !in shape &&
            newBlock.first in 0 until ROWS &&
            newBlock.second in 0 until COLUMNS
        ) {
            shape.add(newBlock)
        }
    }


    return shape.toList()
}


fun generateNextShape(viewModel: GameSettingsViewModel): List<Pair<Int, Int>> {
    return if ((1..100).random() <= viewModel.randomChance) {
        generateRandomShape(viewModel.minBlocks, viewModel.maxBlocks)
    } else {
        SHAPES.random()
    }
}


@Composable
fun HoldableButton(
    onPress: () -> Unit,
    modifier: Modifier = Modifier,
    repeatInterval: Long = 150L, // Time between repeated actions
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    LaunchedEffect(isPressed) {
        if (isPressed) {
            while (isPressed) {
                onPress()
                delay(repeatInterval)
            }
        }
    }

    // Styled Button
    MyButton(
        onClick = {}, // No single-click action for holdable buttons
        interactionSource = interactionSource,
        modifier = modifier,
        content = content
    )
}

@Composable
fun MyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) {

    Button(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = modifier,

        content = content
    )
}