package dev.sharpwave.quadmage.gui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import dev.sharpwave.quadmage.gui.components.FileDialog
import dev.sharpwave.quadmage.gui.core.LocalContentState
import dev.sharpwave.quadmage.gui.core.misc.FileDropTarget
import dev.sharpwave.quadmage.gui.theme.ICON_DRAG_N_DROP
import dev.sharpwave.quadmage.image.ImageAnalyser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.io.path.pathString
import kotlin.math.pow

@Composable
fun StartScreen(
    frameScope: FrameWindowScope
) {
    val content = LocalContentState.current
    val coroutineScope = rememberCoroutineScope()
    var fileIsSelected by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0.0F) }

    if (fileIsSelected) {
        ProgressIndicatorPart(progress)
    }
    else {
        FileOpeningPart(
            frameScope
        ) { file ->
            fileIsSelected = true
            coroutineScope.launch {
                withContext(Dispatchers.Default) {
                    val image = ImageAnalyser(file)
                        .registerProgressHook { v -> progress = v }
                        .process()
                    content.image = image
                    content.pruningThreshold = 0.5.pow(6)
                    content.updateDrawnImage()
                }
            }
        }
    }
}

@Composable
private fun FileOpeningPart(frameScope: FrameWindowScope, processImage: (File) -> Unit) {
    val filterRegex = remember { Regex("^.*\\.(jpg|jpeg|JPG|JPEG|PNG|png)\$") }
    var fileDialogIsVisible by remember { mutableStateOf(false) }

    val target = remember {
        FileDropTarget { files ->
            run {
                val file = files.first() as File
                if (file.isFile && file.name.contains(filterRegex)) {
                    processImage(file)
                }
            }
        }
    }

    DisposableEffect(true) {
        frameScope.window.contentPane.dropTarget = target
        onDispose {
            frameScope.window.contentPane.dropTarget = null
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { fileDialogIsVisible = true }) {
            Text("Open image")
        }
        Column(
            modifier = Modifier.offset(y = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = ICON_DRAG_N_DROP.asPainterResource(),
                contentDescription = null,
                tint = MaterialTheme.colors.secondaryVariant,
                modifier = Modifier.size(50.dp)
            )
            Text(
                "Or drag and drop",
                color = MaterialTheme.colors.secondaryVariant,
                style = MaterialTheme.typography.subtitle2
            )
        }
    }

    if (fileDialogIsVisible) {
        FileDialog(
            scope = frameScope,
            title = "Pick a image to be Quadmaged",
            isLoad = true,
            onResult = {
                fileDialogIsVisible = false
                it?.let {
                    processImage(File(it.pathString))
                }
            },
            filter = { _, name -> name.contains(filterRegex) }
        )
    }
}

@Composable
private fun ProgressIndicatorPart(progress: Float) {
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TODO: add cool looking backdrop generated using this software
        // TODO: Make indicator with overlay of original image with randomly generated tree for some depth to have nice effect
        LinearProgressIndicator(
            progress = progress
        )
    }
}