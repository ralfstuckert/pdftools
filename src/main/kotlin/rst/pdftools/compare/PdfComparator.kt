package rst.pdftools.compare

import com.github.salomonbrys.kotson.jsonArray
import com.github.salomonbrys.kotson.jsonObject
import com.google.gson.JsonObject
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.ImageType
import org.apache.pdfbox.rendering.PDFRenderer
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.io.InputStream
import javax.imageio.ImageIO

typealias Resolution = Double
typealias ColorDistanceTolerance = Double
typealias PageIndex = Int

class PdfComparator(val diffImageDirectory: File,
                    val resolution: Resolution = 300.0,
                    val colorTolerance: ColorDistanceTolerance = 0.001) {


    @Throws(IOException::class)
    fun comparePdfs(expected: File, actual: File): PdfCompareResult =
            comparePdfs(expected.name, expected.inputStream(), actual.inputStream())


    @Throws(IOException::class)
    fun comparePdfs(expectedFileName: String, expected: InputStream, actual: InputStream): PdfCompareResult {

        PDDocument.load(actual).use { actualDoc ->
            PDDocument.load(expected).use { expectedDoc ->

                if (actualDoc.numberOfPages != expectedDoc.numberOfPages) {
                    return PdfCompareResult.PageCountDiffers(expectedDoc.numberOfPages, actualDoc.numberOfPages)
                }

                val pageCompareDifferences = mutableListOf<PdfPageCompareResult>()

                for (pageIndex in 0..expectedDoc.numberOfPages - 1) {
                    val imageCompareResult = expectedDoc.comparePageAsImage(actualDoc, pageIndex, resolution, colorTolerance)
                    when (imageCompareResult) {
                        is ImageCompareResult.SizeDiffers ->
                            pageCompareDifferences.add(PdfPageCompareResult.SizeDiffers(imageCompareResult))
                        is ImageCompareResult.ContentDiffers -> {
                            val diffImageFile = writeDiffImage(imageCompareResult.diffImage, diffImageDirectory,
                                    expectedFileName, pageIndex)
                            pageCompareDifferences.add(PdfPageCompareResult.ContentDiffers(pageIndex,
                                    imageCompareResult.diffPixelCount, diffImageFile))
                        }
                    }
                }

                if (pageCompareDifferences.isEmpty()) {
                    return PdfCompareResult.Identical
                }

                return PdfCompareResult.ContentDiffers(pageCompareDifferences)
            }
        }
    }


    private fun writeDiffImage(image: BufferedImage, directory: File, expectedFileName: String, pageIndex: PageIndex): File {
        val (name, ext) = expectedFileName.filename()
        val diffFile = File(directory, "${name}.page-${pageIndex}-diff.png")
        ImageIO.write(image, "png", diffFile)
        return diffFile
    }

}


fun String.filename(): Pair<String, String> {
    val matchResult = """(.+?)(\.[^.]*${'$'}|${'$'})""".toRegex().matchEntire(this)

    if (matchResult != null) {
        val (name, extension) = matchResult.destructured
        return Pair(name, extension)
    }
    return Pair(this, "")
}

fun PDDocument.comparePageAsImage(other: PDDocument, pageIndex: PageIndex, resolution: Resolution,
                                  colorDistanceTolerance: Double): ImageCompareResult {
    val thisImage = this.toImage(pageIndex, resolution)
    val otherImage = other.toImage(pageIndex, resolution)
    return compareImage(thisImage, otherImage, colorDistanceTolerance)
}

@Throws(IOException::class)
fun PDDocument.toImage(pageIndex: PageIndex, resolution: Resolution): BufferedImage {
    val pdfRenderer = PDFRenderer(this)
    return pdfRenderer.renderImageWithDPI(pageIndex, resolution.toFloat(), ImageType.RGB)
}


abstract sealed class PdfPageCompareResult() {

    abstract fun toJson(): JsonObject

    object Identical : PdfPageCompareResult() {
        override fun toJson(): JsonObject = jsonObject(
                "differs" to false
        )
    }

    data class SizeDiffers(val expectedWidth: Int, val expectedHeight: Int,
                           val actualWidth: Int, val actualHeight: Int) : PdfPageCompareResult() {
        constructor(diff: ImageCompareResult.SizeDiffers) : this(diff.expectedWidth, diff.expectedHeight,
                diff.actualWidth, diff.actualHeight)

        override fun toJson(): JsonObject = jsonObject(
                "differs" to true,
                "reason" to "size",
                "expected" to jsonObject(
                        "width" to expectedWidth,
                        "height" to expectedHeight
                ),
                "actual" to jsonObject(
                        "width" to actualWidth,
                        "height" to actualHeight
                )
        )
    }

    data class ContentDiffers(val pageIndex: PageIndex,
                              val diffPixelCount: Int,
                              val diffImageFile: File) : PdfPageCompareResult() {

        override fun toJson(): JsonObject = jsonObject(
                "differs" to true,
                "reason" to "content",
                "pageIndex" to pageIndex,
                "diffPixelCount" to diffPixelCount,
                "diffImageFile" to diffImageFile.absolutePath
        )
    }

}

abstract sealed class PdfCompareResult {

    abstract fun toJson(): JsonObject

    object Identical : PdfCompareResult() {
        override fun toJson(): JsonObject = jsonObject(
                "differs" to false
        )
    }

    class PageCountDiffers(val expectedPageCount: Int, val actualPageCount: Int) : PdfCompareResult() {
        override fun toJson(): JsonObject = jsonObject(
                "differs" to true,
                "reason" to "page count differs"
        )
    }

    class ContentDiffers(val differentPages: List<PdfPageCompareResult>) : PdfCompareResult() {
        override fun toJson(): JsonObject = jsonObject(
                "differs" to true,
                "reason" to "page content differs",
                "differentPages" to jsonArray(differentPages)
        )
    }
}
