package dev.sharpwave.quadmage.gui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.AwtWindow
import androidx.compose.ui.window.FrameWindowScope
import java.awt.FileDialog
import java.io.File
import java.io.FilenameFilter
import java.nio.file.Path


@Composable
fun FileDialog(
    scope: FrameWindowScope,
    title: String,
    isLoad: Boolean,
    onResult: (result: Path?) -> Unit,
    filter: FilenameFilter = { _: File, _: String -> true } as FilenameFilter
) = AwtWindow(
    create = {
        object : FileDialog(scope.window, "Choose a file", if (isLoad) LOAD else SAVE) {
            override fun setVisible(value: Boolean) {
                super.setVisible(value)
                if (value) {
                    if (file != null) {
                        onResult(File(directory).resolve(file).toPath())
                    } else {
                        onResult(null)
                    }
                }
            }
        }.apply {
            this.title = title
            filenameFilter = filter
        }
    },
    dispose = FileDialog::dispose
)