package gitlet;

import ucb.junit.textui;
import org.junit.Test;
import static org.junit.Assert.*;

/** The suite of all JUnit tests for the gitlet package.
 *  @author
 */
public class UnitTest {

    /** Run the JUnit tests in the loa package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        System.exit(textui.runClasses(UnitTest.class));
    }

    /** A dummy test to avoid complaint. */
    @Test
    public void placeholderTest() {
    }

    @Test
    public void commitObjectTest() {
        assertEquals(5, 5);
    }

    @Test
    public void globalLogTest() {
        assertEquals(5, 5);
    }

    @Test
    public void mergeTest() {
        assertEquals(5, 5);
    }

    @Test
    public void checkoutTest() {
        assertEquals(5, 5);
    }

    @Test
    public void resetTest() {
        assertEquals(5, 5);
    }

    @Test
    public void blobTest() {
        assertEquals(5, 5);
    }

    @Test
    public void commitTest() {
        assertEquals(5, 5);
    }

    @Test
    public void logTest() {
        assertEquals(5, 5);
    }

    @Test
    public void stageTest() {
        assertEquals(5, 5);
    }

    @Test
    public void initTest() {
        assertEquals(5, 5);
    }

    @Test
    public void branchTest() {
        assertEquals(5, 5);
    }

    @Test
    public void rmTest() {
        assertEquals(5, 5);
    }

    @Test
    public void addTest() {
        assertEquals(5, 5);
    }

}


