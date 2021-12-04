package dev.sharpwave.quadmage.image.data.quadtree

import java.lang.ref.WeakReference
import kotlin.math.abs

open class Quad<T, M, Q : ITreeQuad<T, M>> (
    final override val topLeft: Point,
    final override val botRight: Point,
    override val parent: WeakReference<ITreeQuad<T, M>>?,
    protected val sharedTreeMeta: WeakReference<M>
) : ITreeQuad<T, M> {
    protected var _node: Node<T>? = null
    protected var _topLeftTree: Q? = null
    protected var _topRightTree: Q? = null
    protected var _botLeftTree: Q? = null
    protected var _botRightTree: Q? = null

    override val node: Node<T>?
        get() = _node
    override val topLeftTree: Q?
        get() = _topLeftTree
    override val topRightTree: Q?
        get() = _topRightTree
    override val botLeftTree: Q?
        get() = _botLeftTree
    override val botRightTree: Q?
        get() = _botRightTree

    override val isUnitQuad: Boolean = abs(topLeft.x - botRight.x) <= 1 && abs(topLeft.y - botRight.y) <= 1

    override fun inBoundary(p: Point): Boolean {
        return (p.x >= topLeft.x &&
                p.x <= botRight.x &&
                p.y >= topLeft.y &&
                p.y <= botRight.y)
    }

    override fun inBoundary(node: Node<T>): Boolean {
        return inBoundary(node.pos)
    }

    override fun notInBoundary(p: Point): Boolean {
        return ! inBoundary(p)
    }

    override fun notInBoundary(node: Node<T>): Boolean {
        return ! inBoundary(node)
    }

    @Suppress("UNCHECKED_CAST")
    protected open fun quadFactory(
        topLeft: Point,
        botRight: Point,
        parent: WeakReference<ITreeQuad<T, M>>,
        sharedTreeMeta: WeakReference<M>
    ): Q {
        return Quad(topLeft, botRight, parent, sharedTreeMeta) as Q
    }

    override fun insert(node: Node<T>) {
        if (notInBoundary(node)) {
            return
        }

        if (isUnitQuad) {
            this._node = node
            return
        }

        if ((topLeft.x + botRight.x) / 2 > node.pos.x) {
            // Indicates topLeftTree
            if ((topLeft.y + botRight.y) / 2 > node.pos.y) {
                if (_topLeftTree == null)
                    _topLeftTree = quadFactory(
                        Point(topLeft.x, topLeft.y),
                        Point((topLeft.x + botRight.x) / 2, (topLeft.y + botRight.y) / 2),
                        WeakReference(this),
                        sharedTreeMeta
                    )
                _topLeftTree!!.insert(node)
            } else {
                if (_botLeftTree == null)
                    _botLeftTree = quadFactory(
                        Point(topLeft.x, (topLeft.y + botRight.y) / 2),
                        Point((topLeft.x + botRight.x) / 2, botRight.y),
                        WeakReference(this),
                        sharedTreeMeta
                    )
                _botLeftTree!!.insert(node)
            }
        } else {
            // Indicates topRightTree
            if ((topLeft.y + botRight.y) / 2 > node.pos.y) {
                if (_topRightTree == null)
                    _topRightTree = quadFactory(
                        Point((topLeft.x + botRight.x) / 2, topLeft.y),
                        Point(botRight.x, (topLeft.y + botRight.y) / 2),
                        WeakReference(this),
                        sharedTreeMeta
                    )
                _topRightTree!!.insert(node)
            } else {
                if (_botRightTree == null)
                    _botRightTree = quadFactory(
                        Point((topLeft.x + botRight.x) / 2, (topLeft.y + botRight.y) / 2),
                        Point(botRight.x, botRight.y),
                        WeakReference(this),
                        sharedTreeMeta
                    )
                _botRightTree!!.insert(node)
            }
        }
    }

    override fun finalize() {}
}