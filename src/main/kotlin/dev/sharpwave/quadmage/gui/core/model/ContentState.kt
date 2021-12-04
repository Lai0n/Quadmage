package dev.sharpwave.quadmage.gui.core.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.window.WindowState
import dev.sharpwave.quadmage.gui.components.DragHandler
import dev.sharpwave.quadmage.gui.components.ScaleHandler
import dev.sharpwave.quadmage.gui.util.cropBitmapByScale
import dev.sharpwave.quadmage.image.ProcessedImage

class ContentState {
    val drag = DragHandler()
    val scale = ScaleHandler()
    lateinit var windowState: WindowState
        private set
    var image by mutableStateOf<ProcessedImage?>(null)
    var drawnImage by mutableStateOf(ImageBitmap(1,1))
    var pruningThreshold by mutableStateOf(0.0)

    fun applyContent(state: WindowState): ContentState {
        windowState = state

        return this
    }

    fun updateDrawnImage() {
        image?.let {
            drawnImage = cropBitmapByScale(
                it.toBitmap(pruningThreshold),
                windowState.size,
                scale.factor.value,
                drag
            ).toComposeImageBitmap()
        }
    }
}