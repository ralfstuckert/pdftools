package rst.pdftools.compare

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import us.jimschubert.kopper.typed.BooleanArgument
import us.jimschubert.kopper.typed.NumericArgument
import us.jimschubert.kopper.typed.StringArgument
import us.jimschubert.kopper.typed.TypedArgumentParser
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {

    val pdfArgs = PdfCompareArgs(args)

    if (pdfArgs.help) {
        println(pdfArgs.printHelp())
        return
    }

    val diffDirectory = checkDirectoryParameter(pdfArgs, pdfArgs.diffDirectory, "diffDirectory")
    val expected = checkFileParameter(pdfArgs, pdfArgs.expected, "expected")
    val actual = checkFileParameter(pdfArgs, pdfArgs.actual, "actual")
    val resolution = checkParameter(pdfArgs, pdfArgs.resolution, "resolution")
    val tolerance = checkParameter(pdfArgs, pdfArgs.tolerance, "tolerance")

    val comparator = PdfComparator(diffDirectory, resolution, tolerance)
    val compareResult = comparator.comparePdfs(expected, actual)

    when (compareResult) {
        is PdfCompareResult.Identical -> {
            println("PDFs are identical")
            exitProcess(0)
        }
        else -> {
            println(jacksonObjectMapper().writeValueAsString(compareResult))
            exitProcess(-1)
        }
    }

}


fun replaceVariables(path: String): String {
    if (path.startsWith("__DOWNLOAD__")) {
        val home = System.getProperty("user.home")
        val downloadPath = "$home/Downloads"
        return path.replace("__DOWNLOAD__", downloadPath)
    }
    return path
}

fun checkDirectoryParameter(pdfArgs: PdfCompareArgs, parameter: String?, name: String): File {
    val file = checkFileParameter(pdfArgs, parameter, name)
    if (!file.isDirectory()) {
        println("file given as parameter $name is not a directory")
        println(pdfArgs.printHelp())
        exitProcess(-1)
    }
    return file
}

fun checkFileParameter(pdfArgs: PdfCompareArgs, parameter: String?, name: String): File {
    val path = replaceVariables(checkParameter(pdfArgs, parameter, name))
    val file = File(path)
    if (!file.exists()) {
        println("file given as parameter $name does not exist")
        println(pdfArgs.printHelp())
        exitProcess(-1)
    }
    return file
}

fun <T> checkParameter(pdfArgs: PdfCompareArgs, parameter: T?, name: String): T {
    if (parameter == null || parameter == "") {
        println("parameter $name is missing")
        println(pdfArgs.printHelp())
        exitProcess(-1)
    } else {
        return parameter
    }
}

class PdfCompareArgs(args: Array<String>) : TypedArgumentParser(args,
        "pdfcompare", "compares two given PDFs by comparing images rendered from each page") {

    val help by BooleanArgument(self, "h",
            description = "Prints this help"
    )

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
