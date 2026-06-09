package com.example.a3dmodelviewer

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import java.util.UUID

data class ModelItem(
    val id: String = UUID.randomUUID().toString(),
    val assetPath: String,
    var offsetX: Float = 0f,
    var offsetY: Float = 0f,
    var width: Float = 500f,
    var height: Float = 500f,
    var isInteractionMode: Boolean = false,
    var bgColorIndex: Int = 0
)

class MainViewModel : ViewModel() {
    private val _models = mutableStateListOf<ModelItem>()
    val models: List<ModelItem> get() = _models

    val availableModels = listOf(
        "models/Fox.glb" to "Fox",
        "models/Duck.glb" to "Duck",
        "models/Box.glb" to "Box",
        "models/Avocado.glb" to "Avocado",
        "models/Lantern.glb" to "Lantern",
        "models/WaterBottle.glb" to "Water Bottle",
        "models/ToyCar.glb" to "Toy Car"
    )

    fun addModel(assetPath: String) {
        // Position new models slightly offset from center
        val offset = (_models.size * 50).toFloat()
        _models.add(
            ModelItem(
                assetPath = assetPath,
                offsetX = 100f + offset,
                offsetY = 100f + offset
            )
        )
    }

    fun removeModel(id: String) {
        _models.removeAll { it.id == id }
    }

    fun updateModelPosition(id: String, deltaX: Float, deltaY: Float) {
        val index = _models.indexOfFirst { it.id == id }
        if (index != -1) {
            val item = _models[index]
            _models[index] = item.copy(
                offsetX = item.offsetX + deltaX,
                offsetY = item.offsetY + deltaY
            )
        }
    }

    fun updateModelSize(id: String, zoom: Float) {
        val index = _models.indexOfFirst { it.id == id }
        if (index != -1) {
            val item = _models[index]
            val newWidth = (item.width * zoom).coerceIn(200f, 1500f)
            val newHeight = (item.height * zoom).coerceIn(200f, 1500f)
            _models[index] = item.copy(
                width = newWidth,
                height = newHeight
            )
        }
    }

    fun updateModelSizeAbsolute(id: String, deltaW: Float, deltaH: Float) {
        val index = _models.indexOfFirst { it.id == id }
        if (index != -1) {
            val item = _models[index]
            val newWidth = (item.width + deltaW).coerceIn(200f, 1500f)
            val newHeight = (item.height + deltaH).coerceIn(200f, 1500f)
            _models[index] = item.copy(
                width = newWidth,
                height = newHeight
            )
        }
    }

    fun toggleInteractionMode(id: String) {
        val index = _models.indexOfFirst { it.id == id }
        if (index != -1) {
            val item = _models[index]
            _models[index] = item.copy(isInteractionMode = !item.isInteractionMode)
        }
    }

    fun setBackgroundColor(id: String, colorIndex: Int) {
        val index = _models.indexOfFirst { it.id == id }
        if (index != -1) {
            val item = _models[index]
            _models[index] = item.copy(bgColorIndex = colorIndex)
        }
    }
}
