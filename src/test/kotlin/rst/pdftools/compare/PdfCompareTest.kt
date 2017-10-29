package rst.pdftools.compare

import org.junit.Before
import org.junit.Test
import java.io.InputStream

class PdfCompareTest {

    lateinit var original: InputStream
    lateinit var changed: InputStream

    @Before
    fun setUp() {
//        original = ClassLoader.getSystemResource("original.pdf").openStream()
//        changed = this.javaClass.classLoader.getResourceAsStream(("changed.pdf"))
    }

    @Test
    fun pdfWithDifferentContent() {

    }
}