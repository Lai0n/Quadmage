package dev.sharpwave.quadmage.image.data

import dev.sharpwave.quadmage.image.data.quadtree.ITreeQuad
import dev.sharpwave.quadmage.image.data.quadtree.Node
import dev.sharpwave.quadmage.image.data.quadtree.Point
import dev.sharpwave.quadmage.image.data.quadtree.Quad
import java.awt.Color
import java.lang.ref.WeakReference
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sqrt

class ColorCompressorQuad (
    topLeft: Point,
    botRight: Point,
    parent: WeakReference<ITreeQuad<Color, SharedTreeMeta>>?,
    sharedTreeMeta: WeakReference<SharedTreeMeta>
) : Quad<Color, SharedTreeMeta, ColorCompressorQuad>(topLeft, botRight, parent, sharedTreeMeta) {
    private var disposableMeta: QuadDisposableMeta? = QuadDisposableMeta()

    var meta: QuadMeta = QuadMeta()
        private set

    override fun quadFactory(
        topLeft: Point,
        botRight: Point,
        parent: WeakReference<ITreeQuad<Color, SharedTreeMeta>>,
        sharedTreeMeta: WeakReference<SharedTreeMeta>
    ): ColorCompressorQuad {
        return ColorCompressorQuad(topLeft, botRight, parent, sharedTreeMeta)
    }

    override fun insert(node: Node<Color>) {
        if (notInBoundary(node)) {
            return
        }

        super.insert(node)

        if (isUnitQuad) {
            disposableMeta!!.avgColorChR = node.data.red
            disposableMeta!!.avgColorChG = node.data.green
            disposableMeta!!.avgColorChB = node.data.blue
            disposableMeta!!.avgColorDistance = 0.0
        }
        else {
            addColorToAvg(node.data)
        }
    }

    private fun addColorToAvg(color: Color) {
        if (disposableMeta!!.childNodeCount > 0) {
            disposableMeta!!.avgColorDistance = sqrt(
                (sqrt((disposableMeta!!.avgColorChR.toDouble() / disposableMeta!!.childNodeCount).absoluteValue) - color.red).pow(2.0) +
                (sqrt((disposableMeta!!.avgColorChG.toDouble() / disposableMeta!!.childNodeCount).absoluteValue) - color.green).pow(2.0) +
                (sqrt((disposableMeta!!.avgColorChB.toDouble() / disposableMeta!!.childNodeCount).absoluteValue) - color.blue).pow(2.0)
            )
        }
        disposableMeta!!.avgColorChR += color.red * color.red
        disposableMeta!!.avgColorChG += color.green * color.green
        disposableMeta!!.avgColorChB += color.blue * color.blue
        disposableMeta!!.childNodeCount++
    }

    override fun finalize() {
        processMeta()
        normalizeMeta()
        disposeMeta()
    }

    private fun processMeta() {
        if (disposableMeta!!.childNodeCount > 0) {
            disposableMeta!!.avgColorChR = sqrt(
                disposableMeta!!.avgColorChR.toDouble() /
                        disposableMeta!!.childNodeCount
            ).toInt()

            disposableMeta!!.avgColorChG = sqrt(
                disposableMeta!!.avgColorChG.toDouble() /
                        disposableMeta!!.childNodeCount
            ).toInt()

            disposableMeta!!.avgColorChB = sqrt(
                disposableMeta!!.avgColorChB.toDouble() /
                        disposableMeta!!.childNodeCount
            ).toInt()

            disposableMeta!!.avgColorDistance /= disposableMeta!!.childNodeCount

            sharedTreeMeta.get()?.let {
                it.maxAvgColorDistance = disposableMeta!!.avgColorDistance
                it.minAvgColorDistance = disposableMeta!!.avgColorDistance
            }
        }

        _topLeftTree?.processMeta()
        _topRightTree?.processMeta()
        _botLeftTree?.processMeta()
        _botRightTree?.processMeta()
    }

    private fun normalizeMeta() {
        sharedTreeMeta.get()?.let {
            if (it.maxAvgColorDistance > 0) {
                disposableMeta!!.avgColorDistance = (disposableMeta!!.avgColorDistance - it.minAvgColorDistance) /
                        (it.maxAvgColorDistance - it.minAvgColorDistance)
            }
        }

        _topLeftTree?.normalizeMeta()
        _topRightTree?.normalizeMeta()
        _botLeftTree?.normalizeMeta()
        _botRightTree?.normalizeMeta()
    }

    private fun disposeMeta() {
        meta.tileColor = Color(
            disposableMeta!!.avgColorChR,
            disposableMeta!!.avgColorChG,
            disposableMeta!!.avgColorChB
        )
        meta.avgColorDistance = disposableMeta!!.avgColorDistance

        disposableMeta = null

        _topLeftTree?.disposeMeta()
        _topRightTree?.disposeMeta()
        _botLeftTree?.disposeMeta()
        _botRightTree?.disposeMeta()
    }

    fun getAvgRGB(): Int {
        return meta.tileColor.rgb
    }

    private data class QuadDisposableMeta (
        var avgColorChR: Int = 0,
        var avgColorChG: Int = 0,
        var avgColorChB: Int = 0,
        var avgColorDistance: Double = 0.0,
        var childNodeCount: Int = 0
    )

    data class QuadMeta (
        var tileColor: Color = Color.BLACK,
        var avgColorDistance: Double = 0.0,
    )
}