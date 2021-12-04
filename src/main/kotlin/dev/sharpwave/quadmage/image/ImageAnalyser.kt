package dev.sharpwave.quadmage.image

import dev.sharpwave.quadmage.image.data.ColorCompressorQuad
import dev.sharpwave.quadmage.image.data.SharedTreeMeta
import dev.sharpwave.quadmage.image.data.quadtree.Node
import dev.sharpwave.quadmage.image.data.quadtree.Point
import dev.sharpwave.quadmage.image.data.quadtree.QuadTree
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class ImageAnalyser(private val file: File) {
    private var progressHook: ((v: Float) -> Unit)? = null

    fun registerProgressHook(hook: (v: Float) -> Unit): ImageAnalyser {
        progressHook = hook
        return this
    }

    fun process() : ProcessedImage {
        val imageBuffer = ImageIO.read(file)
        val tree = buildTree(imageBuffer)

        return ProcessedImage(file, imageBuffer.type, tree)
    }

    private fun buildTree(imageBuffer: BufferedImage): QuadTree<Color, SharedTreeMeta, ColorCompressorQuad> {
        val tree = QuadTree.make<Color, SharedTreeMeta, ColorCompressorQuad>(
            Point(0,0),
            getBotRightPoint(imageBuffer.width, imageBuffer.height)
        )

        val totalPixels = imageBuffer.height * imageBuffer.width

        for (y in 0 until imageBuffer.height) {
            for (x in 0 until imageBuffer.width) {
                tree.insert(Node(Point(x,y), Color(imageBuffer.getRGB(x, y))))
                progressHook?.let { it((((x+1) + (y * imageBuffer.width).toFloat()) / totalPixels)) }
            }
        }

        tree.finalize()
        return tree
    }

    private fun getBotRightPoint(w: Int, h: Int) =
        Point(
            if (w % 2 == 0) w else w + 1,
            if (h % 2 == 0) h else h + 1
        )
}