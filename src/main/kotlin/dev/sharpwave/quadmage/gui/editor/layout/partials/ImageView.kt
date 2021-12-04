package dev.sharpwave.quadmage.gui.editor.layout.partials

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import dev.sharpwave.quadmage.gui.components.Draggable
import dev.sharpwave.quadmage.gui.components.Zoomable
import dev.sharpwave.quadmage.gui.core.LocalContentState

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ImageView() {
    val content = LocalContentState.current
    val onUpdate = remember { { content.updateDrawnImage() } }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Draggable(
            onUpdate = onUpdate,
            dragHandler = LocalContentState.current.drag,
            modifier = Modifier.fillMaxSize()
        ) {
            Zoomable(
                onUpdate = onUpdate,
                scaleHandler = LocalContentState.current.scale,
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    bitmap = content.drawnImage,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}