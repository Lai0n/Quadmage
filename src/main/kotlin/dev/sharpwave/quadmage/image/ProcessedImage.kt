package dev.sharpwave.quadmage.image

import androidx.compose.ui.graphics.toComposeImageBitmap
import dev.sharpwave.quadmage.image.data.ColorCompressorQuad
import dev.sharpwave.quadmage.image.data.SharedTreeMeta
import dev.sharpwave.quadmage.image.data.quadtree.QuadTree
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class ProcessedImage(
    private val originalFile: File,
    private val originalImageType: Int,
    val generatedTree: QuadTree<Color, SharedTreeMeta, ColorCompressorQuad>
) {
    private val painter = TreePainter()

    fun readOriginal() = ImageIO.read(originalFile).toComposeImageBitmap()

    fun toBitmap(pruningThreshold: Double): BufferedImage = painter.paint(generatedTree, originalImageType, pruningThreshold)

    fun toComposableBitmap(pruningThreshold: Double) = toBitmap(pruningThreshold).toComposeImageBitmap()
}