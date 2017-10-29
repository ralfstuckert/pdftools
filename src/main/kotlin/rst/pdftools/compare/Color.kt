package rst.pdftools.compare

import java.awt.Color

val MAX_VECTOR_LENGTH:Double = Math.sqrt(3.0* 255.0*255.0)


fun Color.normalizedDistanceTo(other:Color): Double {
    val distanceR = red- other.red
    val distanceG = green - other.green
    val distanceB = blue - other.blue

    return Math.sqrt((distanceR * distanceR +
            distanceG * distanceG +
            distanceB * distanceB).toDouble())  / MAX_VECTOR_LENGTH
}

fun Int.asColor(): Color = Color(this)

fun Int.normalizedRgbDistanceTo(other:Int):Double = Color(this).normalizedDistanceTo(Color(other))

