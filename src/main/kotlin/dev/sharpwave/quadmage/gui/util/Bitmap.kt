package dev.sharpwave.quadmage.gui.util

/**
 * Credits: JetBrains https://github.com/JetBrains/compose-jb/blob/master/examples/imageviewer/common/src/desktopMain/kotlin/example/imageviewer/utils/GraphicsMath.kt
 */

import androidx.compose.ui.unit.DpSize
import dev.sharpwave.quadmage.gui.components.DragHandler
import java.awt.Rectangle
import java.awt.image.BufferedImage
import kotlin.math.pow
import kotlin.math.roundToInt

fun cropImage(bitmap: BufferedImage, crop: Rectangle) : BufferedImage {
    return bitmap.getSubimage(crop.x, crop.y, crop.width, crop.height)
}

fun cropBitmapByScale(
    bitmap: BufferedImage,
    size: DpSize,
    scale: Float,
    drag: DragHandler
): BufferedImage {
    val crop = cropBitmapByBounds(
        bitmap,
        getDisplayBounds(bitmap, size),
        size,
        scale,
        drag
    )
    return cropImage(
        bitmap,
        Rectangle(crop.x, crop.y, crop.width - crop.x, crop.height - crop.y)
    )
}

fun cropBitmapByBounds(
    bitmap: BufferedImage,
    bounds: Rectangle,
    size: DpSize,
    scaleFactor: Float,
    drag: DragHandler
): Rectangle {

    if (scaleFactor <= 1f) {
        return Rectangle(0, 0, bitmap.width, bitmap.height)
    }

    var scale = scaleFactor.toDouble().pow(1.4)

    var boundW = (bounds.width / scale).roundToInt()
    var boundH = (bounds.height / scale).roundToInt()

    scale *= size.width.value / bounds.width.toDouble()

    val offsetX = drag.getAmount().x / scale
    val offsetY = drag.getAmount().y / scale

    if (boundW > bitmap.width) {
        boundW = bitmap.width
    }
    if (boundH > bitmap.height) {
        boundH = bitmap.height
    }

    val invisibleW = bitmap.width - boundW
    var leftOffset = (invisibleW / 2.0 - offsetX).roundToInt()

    if (leftOffset > invisibleW) {
        leftOffset = invisibleW
        drag.getAmount().x = -((invisibleW / 2.0) * scale).roundToInt().toFloat()
    }
    if (leftOffset < 0) {
        drag.getAmount().x = ((invisibleW / 2.0) * scale).roundToInt().toFloat()
        leftOffset = 0
    }

    val invisibleH = bitmap.height - boundH
    var topOffset = (invisibleH / 2 - offsetY).roundToInt()

    if (topOffset > invisibleH) {
        topOffset = invisibleH
        drag.getAmount().y = -((invisibleH / 2.0) * scale).roundToInt().toFloat()
    }
    if (topOffset < 0) {
        drag.getAmount().y = ((invisibleH / 2.0) * scale).roundToInt().toFloat()
        topOffset = 0
    }

    return Rectangle(leftOffset, topOffset, leftOffset + boundW, topOffset + boundH)
}
