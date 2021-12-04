package dev.sharpwave.quadmage.image.data.quadtree

import java.lang.ref.WeakReference

/**
 * Implementation of the quad tree was inspired by this
 * https://www.geeksforgeeks.org/quad-tree/ post
 * (which means it's basically the same, but psst)
 */
class QuadTree<T, M, D : ITreeQuad<T, M>>(
    private val delegate: D,
    private var sharedTreeMeta: M? = null
) : ITreeQuad<T, M> by delegate {

    override fun finalize() {
        delegate.finalize()
        sharedTreeMeta = null
    }

    fun asQuad(): D {
        return delegate
    }

    companion object {
        inline fun <T, reified M, reified D : ITreeQuad<T, M>> make(
            topLeft: Point,
            botRight: Point,
        ): QuadTree<T, M, D> {
            val sharedMeta = M::class.java.getConstructor().newInstance()
            return QuadTree(
                D::class.java.getConstructor(
                    Point::class.java,
                    Point::class.java,
                    WeakReference::class.java,
                    WeakReference::class.java
                ).newInstance(
                    topLeft,
                    botRight,
                    null,
                    WeakReference(sharedMeta)
                ),
                sharedMeta
            )
        }
    }
}