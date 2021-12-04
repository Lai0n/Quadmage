package dev.sharpwave.quadmage.gui.util


import androidx.compose.ui.unit.DpSize
import java.awt.Rectangle
import java.awt.image.BufferedImage

/**
 * Credits: JetBrains https://github.com/JetBrains/compose-jb/blob/master/examples/imageviewer/common/src/desktopMain/kotlin/example/imageviewer/utils/GraphicsMath.kt
 */
fun getDisplayBounds(bitmap: BufferedImage, windowSize: DpSize): Rectangle {

    val boundW: Float = windowSize.width.value
    val boundH: Float = windowSize.height.value

    val ratioX: Float = bitmap.width / boundW
    val ratioY: Float = bitmap.height / boundH

    val ratio: Float = if (ratioX > ratioY) ratioX else ratioY

    val resultW = (boundW * ratio)
    val resultH = (boundH * ratio)

    return Rectangle(0, 0, resultW.toInt(), resultH.toInt())
}