package dev.sharpwave.quadmage.gui.editor.layout.partials


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import dev.sharpwave.quadmage.gui.components.DescribedInputWrapper
import dev.sharpwave.quadmage.gui.core.LocalContentState
import kotlin.math.pow

@Composable
fun RightMenu() {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            PruningThreshold()
        }
    }
}

@Composable
fun PruningThreshold() {
    val content = LocalContentState.current
    var localPruningThreshold by remember { mutableStateOf(0.5F) }

    DescribedInputWrapper("Pruning threshold") {
        Slider(
            value = localPruningThreshold,
            onValueChange = { v -> localPruningThreshold = v },
            onValueChangeFinished = {
                content.pruningThreshold = localPruningThreshold.toDouble().pow(6)
                content.updateDrawnImage()
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}