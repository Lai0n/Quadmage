package dev.sharpwave.quadmage.gui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import dev.sharpwave.quadmage.gui.editor.layout.partials.ImageView
import dev.sharpwave.quadmage.gui.editor.layout.partials.RightMenu
import dev.sharpwave.quadmage.gui.editor.layout.partials.StatusBar
import dev.sharpwave.quadmage.gui.editor.layout.partials.Toolbar
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState
import java.awt.Cursor

@OptIn(ExperimentalComposeUiApi::class)
private fun Modifier.cursorForHorizontalResize(): Modifier =
    pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun EditorScreen() {
    val hSplitState = rememberSplitPaneState(0.85F)

    Column(modifier = Modifier.fillMaxSize()) {
        Toolbar()
        HorizontalSplitPane(
            splitPaneState = hSplitState,
        ) {
            first(100.dp) { ImageView() }
            second(100.dp) { RightMenu() }
            splitter {
                visiblePart {
                    Box(
                        Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                    )
                }
                handle {
                    Box(
                        Modifier
                            .markAsHandle()
                            .cursorForHorizontalResize()
                            .width(9.dp)
                            .fillMaxHeight()
                    )
                }
            }
        }
        StatusBar()
    }
}