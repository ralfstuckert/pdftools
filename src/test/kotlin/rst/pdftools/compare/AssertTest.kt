package rst.pdftools.compare

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.containsSubstring
import com.natpryce.hamkrest.isA
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.io.InputStream
import kotlin.test.DefaultAsserter.fail

class AssertTest {

    val expectedFileNameBase = "blubber"
    val expectedFileName = "$expectedFileNameBase.jpg"
    lateinit var original: InputStream
    lateinit var original2: InputStream
    lateinit var changed: InputStream
    lateinit var lessPages: InputStream
    lateinit var small: InputStream
    lateinit var config: ComparatorConfig

    @Rule
    @JvmField
    val folder = TemporaryFolder()
    lateinit var diffImageDir: File

    @Before
    fun setUp() {
        original = PdfCompareTest::class.java.getResource("original.pdf").openStream()
        original2 = PdfCompareTest::class.java.getResource("original.pdf").openStream()
        changed = PdfCompareTest::class.java.getResource("changed.pdf").openStream()
        lessPages = PdfCompareTest::class.java.getResource("lessPages.pdf").openStream()
        small = PdfCompareTest::class.java.getResource("small.pdf").openStream()

        diffImageDir = folder.newFolder()
        config = ComparatorConfig(diffImageDir)
    }

    @Test
    fun assertPdfEquals() {
        assertPdfEquals(original, original2, expectedFileName, config)
    }


    @Test
    fun pdfWithDifferentContent() {
        try {
            assertPdfEquals(original, changed, expectedFileName, config)
            fail("expected assertion error")
        } catch (e: AssertionError) {
            assertThat(e.message!!, containsSubstring("content differs"))
            assertPageDifferFileExists(0, 1, 2, 3, 4)
        }
    }


    @Test
    fun pdfWithDifferentPageSize() {
        try {
            assertPdfEquals(original, small, expectedFileName, config)
            fail("expected assertion error")
        } catch (e: AssertionError) {
            e.printStackTrace()
            assertThat(e.message!!, containsSubstring("content differs"))
            assertThat(e.message!!, containsSubstring("SizeDiffers"))
        }
    }


    @Test
    fun pdfWithDifferentPageCount() {
        try {
            assertPdfEquals(original, lessPages, expectedFileName, config)
            fail("expected assertion error")
        } catch (e: AssertionError) {
            assertThat(e.message!!, containsSubstring("count differs"))
            assertThat(e.message!!, containsSubstring("expected 5 but is 2"))
        }
    }


    private fun assertPageDifferFileExists(vararg pages: Int) {
        for (pageIndex in pages) {
            val differenceFilePath =
                "${diffImageDir.invariantSeparatorsPath}/$expectedFileNameBase.page-$pageIndex-diff.png"
            Assert.assertTrue(
                "expected difference file $differenceFilePath does not exist",
                File(differenceFilePath).exists()
            )
        }
    }
}
