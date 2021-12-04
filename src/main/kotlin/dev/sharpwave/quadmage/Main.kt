package dev.sharpwave.quadmage

import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.sharpwave.quadmage.gui.screen.EditorScreen
import dev.sharpwave.quadmage.gui.screen.StartScreen
import dev.sharpwave.quadmage.gui.core.LocalContentState
import dev.sharpwave.quadmage.gui.core.model.ContentState
import dev.sharpwave.quadmage.gui.theme.AppTheme

fun main() = application {
    val state = rememberWindowState()
    val content = remember { ContentState().applyContent(state) }

    Window(
        state = state,
        title = "Quadmage",
        onCloseRequest = ::exitApplication
    ) {
        AppTheme {
            CompositionLocalProvider(LocalContentState provides content) {
                if (content.image == null) {
                    StartScreen(this)
                } else {
                    EditorScreen()
                }
            }
        }
    }
}