package enigma;

import java.util.ArrayList;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Swadhin Nalubola
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = new ArrayList<String>();
        String alphaCopy = alphabet.chars();
        int startIndex = 0;
        int endIndex;
        String currCycle = "";
        for (char ch : cycles.toCharArray()) {
            if (ch == ' ') {
                assert true;
            } else if (ch == '(') {
                startIndex = cycles.indexOf(ch);
            } else if (ch == ')') {
                endIndex = cycles.indexOf(ch);
                addCycle(currCycle);
                currCycle = "";
                if (endIndex - startIndex == 2) {
                    _derangement = false;
                }
            } else {
                currCycle += ch;
                alphaCopy = alphaCopy.substring(0, alphaCopy.indexOf(ch))
                        + alphaCopy.substring(alphaCopy.indexOf(ch) + 1);
            }
        }
        if (alphaCopy.length() > 0) {
            _derangement = false;
            for (int i = 0; i < alphaCopy.length(); i++) {
                addCycle(alphaCopy.substring(i, i + 1));
            }
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        _cycles.add(cycle);
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return alphabet().size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        char before = alphabet().toChar(wrap(p));
        char after = permute(before);
        return alphabet().toInt(after);
    }

    /** Return the result of applying
     * the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        char before = alphabet().toChar(wrap(c));
        char after = invert(before);
        return alphabet().toInt(after);
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        for (String str : cycles()) {
            if (str.contains("" + p)) {
                if (str.indexOf(p) == str.length() - 1) {
                    return str.charAt(0);
                } else {
                    return str.charAt(str.indexOf(p) + 1);
                }
            }
        }
        throw new EnigmaException("Character not in alphabet or cycles");
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        for (String str : cycles()) {
            if (str.contains("" + c)) {
                if (str.indexOf(c) == 0) {
                    return str.charAt(str.length() - 1);
                } else {
                    return str.charAt(str.indexOf(c) - 1);
                }
            }
        }
        throw new EnigmaException("Character not in alphabet or cycles");
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return the list of cycles in this permutation. */
    ArrayList<String> cycles() {
        return _cycles;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        return _derangement;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** Cycles in this permutation. */
    private ArrayList<String> _cycles;

    /** Boolean representing if this is a derangement. */
    private boolean _derangement = true;

}
