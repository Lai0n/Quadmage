package dev.sharpwave.quadmage.gui.components

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Zoomable(
    scaleHandler: ScaleHandler,
    modifier: Modifier = Modifier,
    onUpdate: (() -> Unit)? = null,
    children: @Composable () -> Unit
) {
    val focusRequester = FocusRequester()

    Surface(
        color = Color.Transparent,
        modifier = modifier.onPreviewKeyEvent {
            if (it.type == KeyEventType.KeyUp) {
                when (it.key) {
                    Key.I -> {
                        scaleHandler.onScale(1.2f)
                        onUpdate?.invoke()
                    }
                    Key.O -> {
                        scaleHandler.onScale(0.8f)
                        onUpdate?.invoke()
                    }
                    Key.R -> {
                        scaleHandler.reset()
                        onUpdate?.invoke()
                    }
                }
            }
            false
        }
            .focusRequester(focusRequester)
            .focusable()
            .pointerInput(Unit) {
                detectTapGestures(onDoubleTap = { scaleHandler.reset() }) {
                    focusRequester.requestFocus()
                }
            }
    ) {
        children()
    }

    DisposableEffect(Unit) {
        focusRequester.requestFocus()
        onDispose { }
    }
}