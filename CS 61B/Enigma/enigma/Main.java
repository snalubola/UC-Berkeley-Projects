package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Swadhin Nalubola
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        _machine = readConfig();
        String message = "";
        String setting = _input.nextLine().toUpperCase();
        if (!setting.contains("*")) {
            throw new EnigmaException("Setting must start with '*'");
        }
        while (_input.hasNext()) {
            if (!setting.contains("*")) {
                throw new EnigmaException("Setting must start with '*'");
            }
            setUp(_machine, setting);
            message = _input.nextLine().toUpperCase();
            String result = "";
            while (!message.contains("*")) {
                message = message.replaceAll(" ", "");
                result += _machine.convert(message);
                result += "\n";
                if (_input.hasNext()) {
                    message = _input.nextLine().toUpperCase();
                } else {
                    break;
                }
            }
            setting = message;
            printMessageLine(result);
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {

            String alpha = _config.nextLine().toUpperCase();
            _alphabet = new Alphabet(alpha);

            _numRotors = _config.nextInt();
            _numPawls = _config.nextInt();
            if (_numPawls < 0 || _numRotors <= _numPawls) {
                throw new EnigmaException("Must be of the form S > P >= 0");
            }
            _config.nextLine();

            _allRotors = new ArrayList<>();
            while (_config.hasNext()) {
                _allRotors.add(readRotor());
            }

            return new Machine(_alphabet, _numRotors, _numPawls, _allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String rotorName = _config.next().toUpperCase();
            String rotorType = _config.next().toUpperCase();
            String permString = "";
            while (_config.hasNext("(?s)\\(.*")) {
                permString += _config.next("(?s)\\(.*").toUpperCase();
            }
            Permutation rotorPerm = new Permutation(permString, _alphabet);

            if (rotorType.charAt(0) == 'R') {
                return new Reflector(rotorName, rotorPerm);
            } else if (rotorType.charAt(0) == 'N') {
                return new FixedRotor(rotorName, rotorPerm);
            } else if (rotorType.charAt(0) == 'M') {
                String rotorNotches = rotorType.substring(1);
                return new MovingRotor(rotorName, rotorPerm, rotorNotches);
            } else {
                throw new EnigmaException("Motor type must be R, N, or M");
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        int spaceCounter = 0;
        boolean namingRotors = true;
        String currentRotorName = "";
        String[] rotorsToInsert = new String[_numRotors];
        String setting = "";
        int plugIndex = 0;

        for (char ch : settings.toCharArray()) {
            if (ch == '*') {
                assert true;
            } else if (ch == ' ') {
                spaceCounter += 1;
                if (spaceCounter == 1) {
                    assert true;
                } else if (spaceCounter <= _numRotors) {
                    rotorsToInsert[spaceCounter - 2] = currentRotorName;
                    currentRotorName = "";
                } else if (spaceCounter == _numRotors + 1) {
                    rotorsToInsert[spaceCounter - 2] = currentRotorName;
                    currentRotorName = "";
                    namingRotors = false;
                } else if (spaceCounter == _numRotors + 2) {
                    plugIndex = settings.indexOf(setting)
                            + setting.length() + 1;
                    break;
                }
            } else {
                if (namingRotors) {
                    currentRotorName += ch;
                } else {
                    setting += ch;
                }
            }
        }

        if (plugIndex != 0) {
            Permutation plugboard =
                    new Permutation(settings.substring(plugIndex), _alphabet);
            M.setPlugboard(plugboard);
        }
        M.clearRotors();
        M.insertRotors(rotorsToInsert);
        M.setRotors(setting);
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        int counter = -1;
        for (char ch : msg.toCharArray()) {
            if (ch == '\n') {
                _output.print(ch);
                counter = -1;
            } else if (counter == 4) {
                _output.print(" " + ch);
                counter = 0;
            } else {
                _output.print(ch);
                counter += 1;
            }
        }
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** Number of rotors. */
    private int _numRotors;

    /** Number of pawls or moving rotors. */
    private int _numPawls;

    /** All of the rotors created by this file. */
    private Collection<Rotor> _allRotors;

    /** This machine. */
    private Machine _machine;
}
