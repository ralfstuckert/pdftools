package rst.pdftools.compare

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.io.InputStream

class PdfCompareTest {

    val expectedFileNameBase = "blubber"
    val expectedFileName = "$expectedFileNameBase.jpg"
    lateinit var original: InputStream
    lateinit var original2: InputStream
    lateinit var changed: InputStream
    @Rule
    @JvmField
    val folder = TemporaryFolder()
    lateinit var diffImageDir: File

    @Before
    fun setUp() {
        original = PdfCompareTest::class.java.getResource("original.pdf").openStream()
        original2 = PdfCompareTest::class.java.getResource("original.pdf").openStream()
        changed = PdfCompareTest::class.java.getResource("changed.pdf").openStream()

        diffImageDir = folder.newFolder()
    }

    @Test
    fun pdfWithIdenticalContent() {
        val comparator = PdfComparator(diffImageDir)
        val result = comparator.comparePdfs(expectedFileName, original, original2)
        assertNotNull(result)

        assertTrue(result is PdfCompareResult.Identical)
    }


    @Test
    fun pdfWithDifferentContent() {
        val comparator = PdfComparator(diffImageDir)
        val result = comparator.comparePdfs(expectedFileName, original, changed)
        assertNotNull(result)

        assertTrue(result is PdfCompareResult.ContentDiffers)
        val contentDiffers = result as PdfCompareResult.ContentDiffers

        assertEquals(5, contentDiffers.differentPages.size)

        assertPageContentDiffers(contentDiffers.differentPages[0], 0)
        assertPageContentDiffers(contentDiffers.differentPages[1], 1)
        assertPageContentDiffers(contentDiffers.differentPages[2], 2)
        assertPageContentDiffers(contentDiffers.differentPages[3], 3)
        assertPageContentDiffers(contentDiffers.differentPages[4], 4)
    }

    private fun assertPageContentDiffers(differentPage: PdfPageCompareResult, pageIndex: Int) {
        assertTrue(differentPage is PdfPageCompareResult.ContentDiffers)
        val contentDiffers = differentPage as PdfPageCompareResult.ContentDiffers
        assertEquals("page index", pageIndex, contentDiffers.pageIndex)
        assertEquals("${diffImageDir.absolutePath}/$expectedFileNameBase.page-$pageIndex-diff.png",
                contentDiffers.diffImageFile.absolutePath)
    }
}