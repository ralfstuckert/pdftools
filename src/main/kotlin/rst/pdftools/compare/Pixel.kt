package rst.pdftools.compare

data class Pixel(val r: Int, val g: Int, val b: Int) {

    constructor(rgb:Int): this(rgb and 0xff0000 shr 16, rgb and 0xff00 shr 8, rgb and 0xff)

    companion object {
        val MAX_VECTOR_LENGTH:Double = Math.sqrt(3.0* 255.0*255.0)
    }

    fun getColorDistanceTo(other: Pixel): Double {
        val distanceR = r - other.r
        val distanceG = g - other.g
        val distanceB = b - other.b

        return Math.sqrt((distanceR * distanceR + distanceG * distanceG + distanceB * distanceB).toDouble())
    }

    fun getNormalizedColorDistanceTo(other: Pixel): Double {
        return getColorDistanceTo(other) / MAX_VECTOR_LENGTH
    }
}
