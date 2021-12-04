package dev.sharpwave.quadmage.image.data.quadtree

import java.lang.ref.WeakReference

interface ITreeQuad<T, M> {
    val topLeft: Point
    val botRight: Point
    val parent: WeakReference<ITreeQuad<T, M>>?
    val node: Node<T>?
    val topLeftTree: ITreeQuad<T, M>?
    val topRightTree: ITreeQuad<T, M>?
    val botLeftTree: ITreeQuad<T, M>?
    val botRightTree: ITreeQuad<T, M>?
    val isUnitQuad: Boolean

    fun inBoundary(p: Point): Boolean

    fun inBoundary(node: Node<T>): Boolean

    fun notInBoundary(p: Point): Boolean

    fun notInBoundary(node: Node<T>): Boolean

    fun insert(node: Node<T>)

    fun finalize()
}