import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import logic.Edge
import rules.IGameRule
import shapes.RectangularShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.geometry.Size
import rules.ClassicRule
import rules.NoBonusRule
import rules.PenaltyRule
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.platform.LocalDensity
import kotlin.math.abs

@Composable
fun StartScreen() {
    // все настройки для самой игры
    var showGameScreen by remember { mutableStateOf(false) }
    var selectedRule by remember { mutableStateOf("") }
    var playersInput by remember { mutableStateOf("") }
    var widthInput by remember { mutableStateOf("") }
    var heightInput by remember { mutableStateOf("") }
    var shapeType by remember { mutableStateOf("") }
    var customBoxesInput by remember { mutableStateOf("") }

    if (showGameScreen) { // срабатывает при нажатии "Start Game"
        val rule = when (selectedRule) {
            "Classic" -> ClassicRule()
            "NoBonus" -> NoBonusRule()
            else -> PenaltyRule()
        }
        GameScreen(
            playersInput.split(",").map { it },
            heightInput.toIntOrNull() ?: 3,
            widthInput.toIntOrNull() ?: 3,
            rule,
            { showGameScreen = false }
        ) // отображение поля и промежуточных результатов
    } else {
        StartScreenInterface(
            selectedRule,
            { selectedRule = it},
            playersInput,
            { playersInput = it },
            widthInput,
            { widthInput = it },
            heightInput,
            { heightInput = it },
            shapeType,
            { shapeType = it },
            customBoxesInput,
            { customBoxesInput = it },
            {
                if (shapeType == "Custom") {
                    parseBoxes(customBoxesInput)
                }
                showGameScreen = true
            }
        ) // отображается экран с выбором настроек игры
    }
}

@Composable
fun StartScreenInterface(
    selectedRule: String,
    onRuleChange: (String) -> Unit,

    playersInput: String,
    onPlayersInputChange: (String) -> Unit,

    widthInput: String,
    onWidthChange: (String) -> Unit,

    heightInput: String,
    onHeightChange: (String) -> Unit,

    shapeType: String,
    onShapeTypeChange: (String) -> Unit,

    customBoxesInput: String,
    onCustomBoxesInputChange: (String) -> Unit,

    onGameScreen: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text("Dots and Boxes Assistant", fontSize = 30.sp)
        Spacer(Modifier.height(24.dp))

        Text("Select game mode:")
        Row {
            listOf("Classic", "NoBonus", "Penalty").forEach { mode ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedRule == mode,
                        onClick = { onRuleChange(mode) }
                    )
                    Text(mode)
                    Spacer(Modifier.width(8.dp))
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("Player names (comma separated):")
        TextField(
            playersInput,
            onPlayersInputChange,
            Modifier.fillMaxWidth(0.6f),
            placeholder = { Text("Ivan,Max") }
        )

        Spacer(Modifier.height(16.dp))
        Text("Field shape:")
        Row {
            listOf("Rectangular", "Custom").forEach { shape ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = shapeType == shape,
                        onClick = { onShapeTypeChange(shape) }
                    )
                    Text(shape)
                    Spacer(Modifier.width(8.dp))
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        if (shapeType == "Rectangular") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    widthInput,
                    onWidthChange,
                    Modifier.width(100.dp),
                    placeholder = { Text("Width") }
                )
                TextField(
                    heightInput,
                    onHeightChange,
                    Modifier.width(100.dp),
                    placeholder = { Text("Height") }
                )
            }
        } else {
            Text("Enter boxes (top-left corners) as 'x,y' separated by semicolons:")
            TextField(
                customBoxesInput,
                onCustomBoxesInputChange,
                Modifier.fillMaxWidth(0.7f),
                placeholder = { Text("0,0;1,0;0,1;1,1") }
            )
        }

        Spacer(Modifier.height(24.dp))
        Button(onGameScreen, Modifier.fillMaxWidth(0.3f).height(48.dp)) {
            Text("Start Game", color = Color.White)
        }
    }
}

fun parseBoxes(input: String): List<Pair<Int, Int>> {
    return input.split(";").map<String, Pair<Int, Int>> { pair ->
        val coordinates = pair.split(",").map { it }
        (if (coordinates.size == 2) {
            val x = coordinates[0].toIntOrNull()
            val y = coordinates[1].toIntOrNull()
            if (x != null && y != null) x to y else null
        } else null) as Pair<Int, Int>
    }
}

fun detectEdge(
    position: Offset,
    canvasSizePx: Float,
    widthSquares: Int,
    heightSquares: Int
): Edge? {
    val stepX = canvasSizePx / widthSquares
    val stepY = canvasSizePx / heightSquares
    val tolerance = 30f

    // Горизонтальные рёбра
    for (y in 0..heightSquares) {
        for (x in 0 until widthSquares) {
            val leftX = x * stepX // левая x-координата ребра
            val rightX = (x + 1) * stepX // правая x-координата ребра
            val lineY = y * stepY // y-координата всей горизонтальной линии
            if (abs(position.y - lineY) < tolerance &&
                position.x >= leftX - tolerance &&
                position.x <= rightX + tolerance) {
                return Edge(x, y, true)
            }
        }
    }
    // Вертикальные рёбра
    for (x in 0..widthSquares) {
        for (y in 0 until heightSquares) {
            val topY = y * stepY // верхняя у-координата ребра
            val downY = (y + 1) * stepY // нижняя у-координата ребра
            val lineX = x * stepX // х-координата всей горизонтальной линии
            if (abs(position.x - lineX) < tolerance &&
                position.y >= topY - tolerance &&
                position.y <= downY + tolerance) {
                return Edge(x, y, false)
            }
        }
    }
    return null
}

@Composable
fun GameScreen(
    playerNames: List<String>,
    height: Int,
    width: Int,
    rule: IGameRule,
    onStartScreen: () -> Unit
) {
    var gameControl by remember { mutableStateOf(createGameControl(playerNames, width, height, rule)) }
    var updateTrigger by remember { mutableStateOf(0) } // триггер перерисовки

    val sizePx = LocalDensity.current.run { 600.dp.toPx() }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val players = gameControl.getPlayers()
        val currentPlayer = gameControl.getCurrentPlayer()
        Row(Modifier.fillMaxWidth().padding(16.dp), Arrangement.SpaceEvenly) {
            players.forEach { player ->
                Text(
                    "${player.name}: ${player.score}",
                    fontSize = if (player.id == currentPlayer.id) 20.sp else 16.sp,
                    color = if (player.id == currentPlayer.id) Color.Green else Color.Black
                )
            }
        }

        Canvas(
            modifier = Modifier
                .size(600.dp)
                .pointerInput(updateTrigger) {  // зависимость от updateTrigger
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Main)
                            if (event.type == PointerEventType.Release) {
                                val position = event.changes.first().position
                                val edge = detectEdge(position, sizePx, width, height)
                                edge?.let {
                                    try {
                                        gameControl.makeMove(it)
                                        updateTrigger++  // вызывает перерисовку
                                    } catch (e: Exception) { }
                                }
                            }
                        }
                    }
                }
        ) {
            drawGameField(gameControl, width, height)
        }

        Spacer(Modifier.height(8.dp))
        Button(onStartScreen) { Text("Back to Menu") }
    }
}

private fun DrawScope.drawGameField(
    gameControl: GameControl,
    width: Int,
    height: Int
) {
    val board = gameControl.gameLogic.board
    val shape = gameControl.gameLogic.shape

    val allBoxes = shape.getAllBoxes()
    val stepX = size.width / width
    val stepY = size.height / height

    for (box in allBoxes) {
        val owner = board.getBoxOwner(box)
        if (owner != null) {
            drawRect(
                color = when (owner) {
                    1 -> Color.Blue
                    2 -> Color.Green
                    3 -> Color.Yellow
                    else -> Color.Gray
                },
                Offset(box.x * stepX, box.y * stepY),
                Size(stepX, stepY)
            )
        }
    }

    // Горизонтальные линии
    for (y in 0..height) {
        for (x in 0 until width) {
            val edge = Edge(x, y, true)
            if (board.isEdge(edge)) {
                val startX = x * stepX
                val endX = (x + 1) * stepX
                val lineY = y * stepY
                drawLine(
                    Color.Black,
                    Offset(startX, lineY),
                    Offset(endX, lineY),
                    8f
                )
            }
        }
    }

    // Вертикальные линии
    for (x in 0..width) {
        for (y in 0 until height) {
            val edge = Edge(x, y, false)
            if (board.isEdge(edge)) {
                val startY = y * stepY
                val endY = (y + 1) * stepY
                val lineX = x * stepX
                drawLine(
                    Color.Black,
                    Offset(lineX, startY),
                    Offset(lineX, endY),
                    8f
                )
            }
        }
    }

    // Точки
    for (x in 0..width) {
        for (y in 0..height) {
            drawCircle(
                Color.Black,
                10f,
                Offset(x * stepX, y * stepY)
            )
        }
    }
}

private fun createGameControl(
    playerNames: List<String>,
    width: Int,
    height: Int,
    rule: IGameRule
): GameControl {
    val shape = RectangularShape(mutableListOf(height, width))
    val configuration = Configuration(
        rule,
        shape,
        listOf(height, width),
        playerNames)
    val gameControl = GameControl()
    gameControl.startGame(configuration)
    return gameControl
}

fun main() = application {
    Window(::exitApplication, title = "Dots and Boxes") {
        StartScreen()
    }
}