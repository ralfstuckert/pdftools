package rst.pdftools.compare

import org.apache.pdfbox.preflight.PreflightDocument
import org.apache.pdfbox.preflight.ValidationResult
import org.apache.pdfbox.preflight.exception.SyntaxValidationException
import org.apache.pdfbox.preflight.parser.PreflightParser
import org.apache.pdfbox.preflight.utils.ByteArrayDataSource
import java.io.File
import java.io.InputStream


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

fun assertPdfA(pdf:InputStream) {
    val parser = PreflightParser(ByteArrayDataSource(pdf))
    val result:ValidationResult = try {
        parser.parse()
        val document: PreflightDocument = parser.getPreflightDocument()
        document.use {
            document.validate()
            document.getResult()
        }
    } catch (e: SyntaxValidationException) {
         e.getResult()
    }

    if (!result.isValid()) {
        val errorMsg = result.errorsList
            .map { "${it.errorCode}: ${it.details}" }
            .joinToString("\n")
        throw AssertionError("not PDF/A:\n ${errorMsg}")
    }
}