package rst.pdftools.compare

import java.awt.Dimension
import java.awt.Image
import java.awt.image.BufferedImage

sealed class ImageCompareResult {

    class Identical:ImageCompareResult()

    class SizeDifferent(val expectedWidth:Int, val expectedHeight:Int,
                        val otherWidth:Int, val otherHeight:Int):ImageCompareResult()

    class ContentDifferent(val diffCount:Int, val diffImage:BufferedImage):ImageCompareResult()
}