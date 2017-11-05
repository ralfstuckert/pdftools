package rst.pdftools.compare;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.InputStream;

import static org.junit.Assert.assertNotNull;

public class PdfCompareJavaTest {

    private String expectedFileNameBase = "blubber";
    private String expectedFileName = expectedFileNameBase + ".jpg";
    private InputStream original;
    private InputStream original2;
    private InputStream changed;
    private InputStream lessPages;
    private InputStream small;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private File diffImageDir;

    @Before
    public void setUp() throws Exception {
        original = PdfCompareTest.class.getResourceAsStream("original.pdf");
        original2 = PdfCompareTest.class.getResourceAsStream("original.pdf");
        changed = PdfCompareTest.class.getResourceAsStream("changed.pdf");
        lessPages = PdfCompareTest.class.getResourceAsStream("lessPages.pdf");
        small = PdfCompareTest.class.getResourceAsStream("small.pdf");

        diffImageDir = folder.newFolder();
    }

    @Test
    public void pdfWithIdenticalContent() throws Exception {
        PdfComparator comparator = new PdfComparator(diffImageDir);
        PdfCompareResult result = comparator.comparePdfs(expectedFileName, original, original2);
        assertNotNull(result);
    }


}
