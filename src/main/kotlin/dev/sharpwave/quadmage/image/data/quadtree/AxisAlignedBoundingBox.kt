package dev.sharpwave.quadmage.image.data.quadtree

import kotlin.math.absoluteValue

class AxisAlignedBoundingBox internal constructor(
    val a: Point,
    val b: Point,
    val width: Int,
    val height: Int
) {
    constructor(a: Point, b: Point) : this(
        a,
        b,
        (a.x - b.x).absoluteValue,
        (a.y - b.y).absoluteValue
    )
}