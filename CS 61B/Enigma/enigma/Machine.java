package enigma;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Swadhin Nalubola
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls. ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = new ArrayList<>(allRotors);
        _currentRotors = new ArrayList<>(numRotors);
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Reset my current rotors to an empty ArrayList of appropriate length. */
    void clearRotors() {
        _currentRotors = new ArrayList<>(numRotors());
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        if (rotors.length != numRotors()) {
            throw new EnigmaException("Incorrect number of rotors for machine");
        }
        for (int i = 0; i < rotors.length; i++) {
            String currentName = (String) Array.get(rotors, i);
            Rotor currentRotor = null;
            for (Rotor rotor : _allRotors) {
                if (rotor.name().equals(currentName)) {
                    currentRotor = rotor;
                    break;
                }
            }
            if (currentRotor == null) {
                throw new EnigmaException("Rotor name is not"
                        + "in available rotors");
            }
            _currentRotors.add(currentRotor);
        }
        if (!_currentRotors.get(0).reflecting()) {
            throw new EnigmaException("First rotor must be a reflector");
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != numRotors() - 1) {
            throw new EnigmaException("Setting list not compatible with"
                    + "number of Rotors");
        }
        int index = 0;
        for (char ch : setting.toCharArray()) {
            Rotor rotorToSet = _currentRotors.get(index + 1);
            rotorToSet.set(ch);
            index += 1;
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {

        boolean currentRotorNotch =
                _currentRotors.get(numRotors() - 1).atNotch();
        boolean nextRotorNotch = _currentRotors.get(numRotors() - 2).atNotch();
        for (int i = numRotors() - 1; i > 0; i -= 1) {
            if (currentRotorNotch && _currentRotors.get(i - 1).rotates()) {
                _currentRotors.get(i).advance();
                if (!nextRotorNotch) {
                    _currentRotors.get(i - 1).advance();
                } else if (nextRotorNotch && i == 2) {
                    _currentRotors.get(i - 1).advance();
                }
            } else if (i == numRotors() - 1) {
                _currentRotors.get(i).advance();
            }
            currentRotorNotch = nextRotorNotch;
            if (i != 1) {
                nextRotorNotch = _currentRotors.get(i - 2).atNotch();
            }
        }

        int result = c;
        if (_plugboard != null) {
            result = _plugboard.permute(result);
        }
        for (int i = numRotors() - 1; i > 0; i -= 1) {
            result = _currentRotors.get(i).convertForward(result);
        }

        for (int i = 0; i < numRotors(); i++) {
            result = _currentRotors.get(i).convertBackward(result);
        }
        if (_plugboard != null) {
            result = _plugboard.invert(result);
        }
        return result;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String msgCleaned = msg.replaceAll(" ", "");
        char[] msgCharacters = msgCleaned.toCharArray();
        String result = "";
        for (int i = 0; i < msgCharacters.length; i++) {
            int toInt = _alphabet.toInt(msgCharacters[i]);
            int temp = convert(toInt);
            char resultingChar = _alphabet.toChar(temp);

            result += Character.toString(resultingChar);
        }
        return result;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of rotor slots I have. */
    private int _numRotors;

    /** Number of pawls (rotating rotors) I have. */
    private int _pawls;

    /** Contains all the available rotors. */
    private ArrayList<Rotor> _allRotors;

    /** Contains the rotors I am currently using. */
    private ArrayList<Rotor> _currentRotors;

    /** My plugboard. */
    private Permutation _plugboard;

}
