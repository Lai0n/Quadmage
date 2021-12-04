package dev.sharpwave.quadmage.image

import dev.sharpwave.quadmage.image.data.ColorCompressorQuad
import dev.sharpwave.quadmage.image.data.SharedTreeMeta
import dev.sharpwave.quadmage.image.data.quadtree.QuadTree
import java.awt.Color
import java.awt.image.BufferedImage

class TreePainter {
    fun paint(
        tree: QuadTree<Color, SharedTreeMeta, ColorCompressorQuad>,
        imageType: Int,
        pruningThreshold: Double,
    ): BufferedImage {
        val box = tree.topLeft.getAABBox(tree.botRight)
        val buffer = BufferedImage(
            box.width,
            box.height,
            imageType
        )

        walker(tree.asQuad(), buffer, pruningThreshold)

        return buffer
    }

    private fun walker(quad: ColorCompressorQuad, buffer: BufferedImage, pruningThreshold: Double) {
        if (quad.isUnitQuad) {
            quad.node?.let {
                buffer.setRGB(
                    it.pos.x,
                    it.pos.y,
                    it.data.rgb
                )
            }
        }
        else {
            if (quad.meta.avgColorDistance <= pruningThreshold) {
                quad.topLeftTree?.let { walker(it, buffer, pruningThreshold) }
                quad.topRightTree?.let { walker(it, buffer, pruningThreshold) }
                quad.botLeftTree?.let { walker(it, buffer, pruningThreshold) }
                quad.botRightTree?.let { walker(it, buffer, pruningThreshold) }
            }
            else {
                val bbox = quad.topLeft.getAABBox(quad.botRight)

                buffer.setRGB(
                    bbox.a.x,
                    bbox.a.y,
                    bbox.width,
                    bbox.height,
                    IntArray(bbox.width * bbox.height) { quad.getAvgRGB() },
                    0,
                    1
                )
            }
        }
    }
}