package rst.pdftools.compare

import us.jimschubert.kopper.typed.NumericArgument
import us.jimschubert.kopper.typed.StringArgument
import us.jimschubert.kopper.typed.TypedArgumentParser
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {

    val pdfArgs = PdfCompareArgs(args)

    val diffDirectory = checkDirectoryParameter(pdfArgs, pdfArgs.diffDirectory, "diffDirectory")
    val expected = checkFileParameter(pdfArgs, pdfArgs.expected, "expected")
    val actual = checkFileParameter(pdfArgs, pdfArgs.actual, "actual")
    val resolution = checkParameter(pdfArgs, pdfArgs.resolution, "resolution")
    val tolerance = checkParameter(pdfArgs, pdfArgs.resolution, "tolerance")

    val comparator = PdfComparator(diffDirectory, resolution, tolerance)
    val compareResult = comparator.comparePdfs(expected, actual)

    when (compareResult) {
        is PdfCompareResult.Identical -> {
            println("PDFs are identical")
            exitProcess(0)
        }
        else -> {
            println(compareResult.toJson())
            exitProcess(-1)
        }
    }

}

fun PdfCompareResult.toJson() {

}

fun checkDirectoryParameter(args: PdfCompareArgs, parameter: String?, name: String): File {
    val file = checkFileParameter(args, parameter, name)
    if (!file.isDirectory()) {
        println("file given as parameter $name is not a directory")
        args.printHelp()
        exitProcess(-1)
    }
    return file
}

fun checkFileParameter(args: PdfCompareArgs, parameter: String?, name: String): File {
    val path = checkParameter(args, parameter, name)
    val file = File(path)
    if (!file.exists()) {
        println("file given as parameter $name does not exist")
        args.printHelp()
        exitProcess(-1)
    }
    return file
}

fun <T> checkParameter(args: PdfCompareArgs, parameter: T?, name: String): T {
    if (parameter == null || parameter == "") {
        println("parameter $name is missing")
        args.printHelp()
        exitProcess(-1)
    } else {
        return parameter
    }
}

class PdfCompareArgs(args: Array<String>) : TypedArgumentParser(args,
        "pdfcompare", "compares two given PDFs by comparing images rendered from each page") {

    val diffDirectory by StringArgument(self, "d",
            description = "The directory to write the diff images to"
    )

    val expected by StringArgument(self, "e",
            description = "The path of the expected PDF file"
    )

    val actual by StringArgument(self, "a",
            description = "The path of the actual PDF file"
    )

    val resolution by NumericArgument<Double>(self, "r",
            default = 300.0,
            description = "The resolution to render the page as image"
    )

    val tolerance by NumericArgument<Double>(self, "t",
            default = 0.0001,
            description = "The tolerance of the RGB color distance, normalized to 0..1"
    )
}
