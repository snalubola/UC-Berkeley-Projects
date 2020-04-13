package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Swadhin Nalubola
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;
    }

    /** Return the notches of this moving rotor. */
    String notches() {
        return _notches;
    }

    /** This is a moving rotor, so it rotates. */
    @Override
    boolean rotates() {
        return true;
    }

    /** Returns true iff this rotor is at a notch. */
    @Override
    boolean atNotch() {
        char cSetting = permutation().alphabet().toChar(setting());
        return notches().contains("" + cSetting);
    }

    /** Advances this rotor one setting forward. */
    @Override
    void advance() {
        if (setting() == alphabet().size() - 1) {
            set(0);
        } else {
            set(setting() + 1);
        }
    }

    /** The notches that this rotor has. */
    private String _notches;

}
