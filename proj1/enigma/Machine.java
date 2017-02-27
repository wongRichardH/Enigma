package enigma;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author rw
 */
class Machine {

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;
    /** int for number of Rotors */
    private int _numRotors;
    /** int for number of pawls */
    private int _pawls;
    /** my collection of all available Rotors */
    private Collection<Rotor> _allRotors;
    /** a Permutation object that represents the plugboard */
    private Permutation _plugboard;


    private ArrayList<Rotor> rotorsInUse = new ArrayList<>();


    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */

    Machine(Alphabet alpha, int numRotors, int pawls, Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors;
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        for (String rotorName : rotors) {
            for (Rotor rotor : _allRotors) {
                if (rotor.name().toUpperCase().equals(rotorName)) {
                    rotorsInUse.add(rotor);
                    break;
                }
            }
        }
    }

    void parseSettings(String settings) {
        String[] settingsTokens = settings.split(" ");

        /** splits tokens by their blank space for easy access */
        int initialPositionIndex = settingsTokens.length;

        String plugboardString = "";

        for (int i = settingsTokens.length - 1; i >= 0; i--) {
            initialPositionIndex = i;
            if (settingsTokens[i].charAt(0) != '(') {
                break;
            } else {
                plugboardString = settingsTokens[i] + plugboardString;
            }
        }

        String initialPositions = settingsTokens[initialPositionIndex];
        rotorsInUse = new ArrayList<Rotor>();
        this.insertRotors(Arrays.copyOfRange(settingsTokens, 1, initialPositionIndex));
        this.setRotors(initialPositions);
        this.setPlugboard(new Permutation(plugboardString, _alphabet));
    }

    /** Set my rotors according to SETTING, which must be a string of four
     *  upper-case letters. The first letter refers to the leftmost
     *  rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        for (int i = 0; i < setting.length(); i++) {
            rotorsInUse.get(i + 1).set(setting.charAt(i));
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
        this.rotate();

        int converted = c;

        converted = _plugboard.permute(converted);

        for (int i = rotorsInUse.size() - 1; i >= 1; i--) {
            Rotor currentRotor = rotorsInUse.get(i);
            converted = currentRotor.convertForward(converted);
        }

        Rotor reflector = rotorsInUse.get(0);
        converted = reflector.convertForward(converted);

        for (int i = 1; i < rotorsInUse.size(); i++) {
            Rotor currentRotor = rotorsInUse.get(i);
            converted = currentRotor.convertBackward(converted);
        }

        converted = _plugboard.permute(converted);

        return converted;
    }

    void rotate() {
        boolean atNotch = false;
        for (int i = rotorsInUse.size() - 1; i >= 0; i--) {
            Rotor currentRotor = rotorsInUse.get(i);
            if (currentRotor.getClass() != MovingRotor.class) {
                break;
            }

            if (i == rotorsInUse.size() - 1 || atNotch) {
                atNotch = currentRotor.atNotch();
                currentRotor.advance();
            }
        }
    }


    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String converted = "";
        for (int i = 0; i < msg.length(); i++) {
            converted += _alphabet.toChar(convert(_alphabet.toInt(msg.charAt(i))));
        }

        return converted;
    }

}
