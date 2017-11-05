package rst.pdftools.compare

import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.IntStream

@Throws(IOException::class)
fun compareImage(expected: BufferedImage,
                 other: BufferedImage, colorDistanceTolerance:Double): ImageCompareResult {

    if (expected.width != other.width || expected.height != other.height) {
        return ImageCompareResult.SizeDiffers(expected.width, expected.height, other.width, other.height)
    }

    val width = expected.width
    val height = expected.height
    val diffImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val diffPixel = ( diffImage.raster.dataBuffer as DataBufferInt).data
    val expectedPixel = expected.getRGB(0, 0, width, height, null, 0, width)
    val otherPixel = other.getRGB(0, 0, width, height, null, 0, width)
    var errorCount = AtomicInteger(0)

    IntStream.range(0, expectedPixel.size).parallel().forEach { index ->

        if (expectedPixel[index].normalizedRgbDistanceTo(otherPixel[index]) > colorDistanceTolerance) {
            errorCount.incrementAndGet()
            diffPixel[index] = Color.red.rgb
        } else {
            diffPixel[index] = expectedPixel[index]
        }
    }

    if (errorCount.get() == 0) {
        return ImageCompareResult.Identical
    }

    return ImageCompareResult.ContentDiffers(errorCount.get(), diffImage)
}


sealed class ImageCompareResult() {

    object Identical : ImageCompareResult()

    data class SizeDiffers(val expectedWidth:Int, val expectedHeight:Int,
                           val actualWidth:Int, val actualHeight:Int):ImageCompareResult()

    data class ContentDiffers(val diffPixelCount:Int, val diffImage:BufferedImage):ImageCompareResult()
}
