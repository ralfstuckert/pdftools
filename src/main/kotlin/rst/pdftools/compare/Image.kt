package rst.pdftools.compare

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.IOException

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.ImageType
import org.apache.pdfbox.rendering.PDFRenderer
import java.util.stream.IntStream

typealias Resolution = Float
typealias PageIndex = Int

@Throws(IOException::class)
fun toImage(document: PDDocument, pageIndex: PageIndex, resolution:Resolution): BufferedImage {
    val pdfRenderer = PDFRenderer(document)
    return pdfRenderer.renderImageWithDPI(pageIndex, resolution, ImageType.RGB)
}

@Throws(IOException::class)
fun compareImage(expected: BufferedImage,
                 other: BufferedImage): ImageCompareResult {

    if (expected.width != other.width || expected.height != other.height) {
        return ImageCompareResult.SizeDifferent(expected.width, expected.height, other.width, other.height)
    }

    val width = expected.width
    val height = expected.height
    val expectedPixel = expected.getRGB(0, 0, width, height, null, 0, width)
    val otherPixel = other.getRGB(0, 0, width, height, null, 0, width)
    val diffPixel = IntArray(expectedPixel.size)
    var errorCount = 0

    IntStream.range(0, expectedPixel.size).parallel().forEach { index ->
        if (expectedPixel[index] == otherPixel[index]) {
            diffPixel[index] = expectedPixel[index]
        } else {
            ++errorCount
            diffPixel[index] = Color.red.rgb
        }
    }

    if (errorCount == 0) {
        return ImageCompareResult.Identical()
    }
    val diffImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    return null
}

