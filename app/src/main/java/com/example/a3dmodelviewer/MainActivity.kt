package com.example.a3dmodelviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import io.github.sceneview.SceneView
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

// Predefined beautiful background colors mapping to Float4 for SceneView
private val backgroundColors = listOf(
    Color.White to dev.romainguy.kotlin.math.Float4(1f, 1f, 1f, 1f),
    Color(0xFFFFF0F5) to dev.romainguy.kotlin.math.Float4(1f, 0.94f, 0.96f, 1f), // Pastel Pink
    Color(0xFFF0F8FF) to dev.romainguy.kotlin.math.Float4(0.94f, 0.97f, 1f, 1f), // Alice Blue
    Color(0xFFFFFFE0) to dev.romainguy.kotlin.math.Float4(1f, 1f, 0.88f, 1f),    // Light Yellow
    Color(0xFFE6E6FA) to dev.romainguy.kotlin.math.Float4(0.9f, 0.9f, 0.98f, 1f), // Lavender
    Color(0xFF333333) to dev.romainguy.kotlin.math.Float4(0.2f, 0.2f, 0.2f, 1f)  // Dark Grey
)

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            com.example.a3dmodelviewer.ui.theme._3dModelViewerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val models = viewModel.models
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var showBottomSheet by remember { mutableStateOf(false) }
    var showInstructionsDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                SmallFloatingActionButton(
                    onClick = { showInstructionsDialog = true },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(Icons.Filled.Info, contentDescription = "Instructions")
                }
                FloatingActionButton(
                    onClick = { showBottomSheet = true },
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Model")
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            models.forEach { modelItem ->
                key(modelItem.id) {
                    ModelContainer(
                        item = modelItem,
                        onUpdatePosition = { id, dx, dy -> viewModel.updateModelPosition(id, dx, dy) },
                        onUpdateSize = { id, zoom -> viewModel.updateModelSize(id, zoom) },
                        onUpdateSizeAbsolute = { id, dw, dh -> viewModel.updateModelSizeAbsolute(id, dw, dh) },
                        onToggleInteraction = { id -> viewModel.toggleInteractionMode(id) },
                        onSetColor = { id, colorIndex -> viewModel.setBackgroundColor(id, colorIndex) },
                        onClose = { id -> viewModel.removeModel(id) }
                    )
                }
            }
        }

        if (showInstructionsDialog) {
            AlertDialog(
                onDismissRequest = { showInstructionsDialog = false },
                title = { Text("How to use") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("➕ Add Model: Tap the + button to spawn a 3D model.")
                        Text("🖐 Move: Drag anywhere inside a container to move it.")
                        Text("📐 Resize: Grab the bottom-right handle to scale.")
                        Text("👆 Interact: Tap the hand icon. The border will turn blue. Use 1 finger to rotate the model and 2 fingers to zoom.")
                        Text("🎨 Color: Tap the palette icon to change the background.")
                        Text("❌ Close: Tap the red X to remove the model.")
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showInstructionsDialog = false }) {
                        Text("Got it")
                    }
                }
            )
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = bottomSheetState
            ) {
                Column(modifier = Modifier.padding(16.dp).padding(bottom = 32.dp)) {
                    Text(
                        "Select a model to add", 
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    viewModel.availableModels.forEach { (path, name) ->
                        OutlinedButton(
                            onClick = {
                                viewModel.addModel(path)
                                coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                                    if (!bottomSheetState.isVisible) {
                                        showBottomSheet = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Text(name)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModelContainer(
    item: ModelItem,
    onUpdatePosition: (String, Float, Float) -> Unit,
    onUpdateSize: (String, Float) -> Unit,
    onUpdateSizeAbsolute: (String, Float, Float) -> Unit,
    onToggleInteraction: (String) -> Unit,
    onSetColor: (String, Int) -> Unit,
    onClose: (String) -> Unit
) {
    val currentColors = backgroundColors[item.bgColorIndex]
    val composeColor by animateColorAsState(targetValue = currentColors.first, label = "bgColor")
    val borderWidth by animateDpAsState(targetValue = if (item.isInteractionMode) 3.dp else 1.dp, label = "border")
    val borderColor by animateColorAsState(
        targetValue = if (item.isInteractionMode) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.5f),
        label = "borderColor"
    )
    var showColorMenu by remember { mutableStateOf(false) }

    ElevatedCard(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = if (item.isInteractionMode) 16.dp else 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = composeColor),
        modifier = Modifier
            .offset { IntOffset(item.offsetX.roundToInt(), item.offsetY.roundToInt()) }
            .size(width = item.width.dp, height = item.height.dp)
            .border(width = borderWidth, color = borderColor, shape = RoundedCornerShape(16.dp))
            .pointerInput(item.isInteractionMode) {
                if (!item.isInteractionMode) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        onUpdateSize(item.id, zoom)
                        onUpdatePosition(item.id, pan.x, pan.y)
                    }
                }
            }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
                factory = { context ->
                    SceneView(context).apply {
                        val lifecycle = (context as ComponentActivity).lifecycle
                        lifecycle.addObserver(this)
                        
                        // Performance improvements for low-end devices
                        this.dynamicResolution = com.google.android.filament.View.DynamicResolutionOptions().apply { enabled = true }
                        this.antiAliasing = com.google.android.filament.View.AntiAliasing.NONE
                        
                        // Set the opaque background based on the selected color to prevent ghosting
                        this.backgroundColor = currentColors.second
                        
                        // Load the model exactly once when the view is created
                        val modelNode = ModelNode().apply {
                            position = dev.romainguy.kotlin.math.Float3(x = 0.0f, y = 0.0f, z = 0.0f)
                            scale = dev.romainguy.kotlin.math.Float3(x = 1.0f, y = 1.0f, z = 1.0f)
                            loadModelGlbAsync(item.assetPath)
                        }
                        this.addChild(modelNode)
                    }
                },
                update = { sceneView ->
                    // Recomposition updates
                    sceneView.cameraNode.position = dev.romainguy.kotlin.math.Float3(x = 0.0f, y = 0.0f, z = 5.0f)
                    
                    // Force Filament renderer to update clear color
                    sceneView.backgroundColor = currentColors.second
                    sceneView.renderer?.clearOptions?.let { options ->
                        options.clearColor = floatArrayOf(
                            currentColors.second.x,
                            currentColors.second.y,
                            currentColors.second.z,
                            currentColors.second.w
                        )
                        options.clear = true
                        sceneView.renderer?.clearOptions = options
                    }
                }
            )

            // Overlay buttons (Top Right)
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                // Color Picker Button
                Box {
                    FilledIconButton(
                        onClick = { showColorMenu = true },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Filled.Palette, contentDescription = "Change Color", modifier = Modifier.size(20.dp))
                    }
                    DropdownMenu(
                        expanded = showColorMenu,
                        onDismissRequest = { showColorMenu = false }
                    ) {
                        backgroundColors.forEachIndexed { index, pair ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .background(pair.first, androidx.compose.foundation.shape.CircleShape)
                                                .border(1.dp, Color.Gray, androidx.compose.foundation.shape.CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text("Color ${index + 1}")
                                    }
                                },
                                onClick = {
                                    onSetColor(item.id, index)
                                    showColorMenu = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                // Interaction Mode Button
                FilledIconButton(
                    onClick = { onToggleInteraction(item.id) },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (item.isInteractionMode) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                        contentColor = if (item.isInteractionMode) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(Icons.Filled.TouchApp, contentDescription = "Toggle Interaction", modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                // Close Button
                FilledIconButton(
                    onClick = { onClose(item.id) },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f),
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Close", modifier = Modifier.size(20.dp))
                }
            }

            // Explicit Resize Handle (Bottom Right)
            if (!item.isInteractionMode) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f), RoundedCornerShape(12.dp))
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                onUpdateSizeAbsolute(item.id, dragAmount.x, dragAmount.y)
                            }
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.DragIndicator, 
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Drag to resize", 
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}