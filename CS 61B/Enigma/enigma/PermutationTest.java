package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }

    @Test
    public void checkCyclesList() {
        perm = new Permutation("(ABCD) (EFGH)(JKL) (M) (NOPQRSTUVWX)",
                new Alphabet("NOPQRSTUVWXYZABCDEFGHIJKLM"));
        assertEquals("ABCD", perm.cycles().get(0));
        assertEquals("EFGH", perm.cycles().get(1));
        assertEquals("JKL", perm.cycles().get(2));
        assertEquals("M", perm.cycles().get(3));
        assertEquals("NOPQRSTUVWX", perm.cycles().get(4));
        assertEquals("Y", perm.cycles().get(5));
        assertEquals("Z", perm.cycles().get(6));
        assertEquals("I", perm.cycles().get(7));
    }

    @Test
    public void checkPermute() {
        perm = new Permutation("(ABCD) (EFGH)(JKL) (M) (NOPQRSTUVWX)",
                new Alphabet("NOPQRSTUVWXYZABCDEFGHIJKLM"));
        assertEquals('B', perm.permute('A'));
        assertEquals(1, perm.permute(0));
        assertEquals('A', perm.permute('D'));
        assertEquals(4, perm.permute(29));
        assertEquals('M', perm.permute('M'));
        assertEquals(12, perm.permute(12));
    }

    @Test
    public void checkInvert() {
        perm = new Permutation("(ABCD) (EFGH)(JKL) (M) (NOPQRSTUVWX)",
                new Alphabet("NOPQRSTUVWXYZABCDEFGHIJKLM"));
        assertEquals('A', perm.invert('B'));
        assertEquals(0, perm.invert(1));
        assertEquals('D', perm.invert('A'));
        assertEquals(10, perm.invert(26));
        assertEquals('M', perm.invert('M'));
        assertEquals(12, perm.invert(12));
    }

    @Test
    public void checkDerangement() {
        Permutation perm1 = new Permutation("(ABCD) (EFGH)(JKL)"
                + "(M) (NOPQRSTUVWX)", new Alphabet());
        Permutation perm2 =
                new Permutation("(ABCDEFGHIJKLMNOPQRSTUVWXYZ)", new Alphabet());
        assertEquals(false, perm1.derangement());
        assertEquals(true, perm2.derangement());
    }

}
