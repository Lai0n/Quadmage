package dev.sharpwave.quadmage.image.data.quadtree

class Point (
    val x: Int,
    val y: Int
) {
    fun getAABBox(p: Point): AxisAlignedBoundingBox {
        return AxisAlignedBoundingBox(this, p)
    }
}