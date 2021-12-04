package dev.sharpwave.quadmage.image.data

class SharedTreeMeta {
    var minAvgColorDistance: Double = Double.NaN
        set(value) {
            if (field.isNaN()) {
                field = value
            }
            else if (value > 0.0 && value < field) {
                field = value
            }
        }
    var maxAvgColorDistance: Double = Double.NaN
        set(value) {
            if (field.isNaN()) {
                field = value
            }
            if (value > field) {
                field = value
            }
        }
}
