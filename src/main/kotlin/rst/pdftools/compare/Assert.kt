package rst.pdftools.compare

import java.io.File
import java.io.InputStream
import kotlin.math.exp

fun assertPdfEquals(
    expected: File,
    actual: File,
    config: ComparatorConfig = ComparatorConfig()
) {
    assertPdfEquals(expected.inputStream(), actual.inputStream(), expected.name, config)
}

fun assertPdfEquals(
    expected: InputStream,
    actual: InputStream,
    expectedFileName: String,
    config: ComparatorConfig = ComparatorConfig()
) {
    val comparator = PdfComparator(config.diffImageDirectory, config.resolution, config.colorTolerance)
    val result = comparator.comparePdfs( expected, actual, expectedFileName)
    when (result) {
        is PdfCompareResult.ContentDiffers ->
            throw AssertionError("${result.reason}: ${result.differentPages}")
        is PdfCompareResult.PageCountDiffers ->
            throw AssertionError("${result.reason}: expected ${result.expectedPageCount} but is ${result.actualPageCount}")
        else -> return Unit
    }
}
