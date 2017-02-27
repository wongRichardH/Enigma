package enigma;

import java.util.HashMap;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author rw
 */
class Permutation {

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** permuted is a dictionary that maps keys to its permutation */
    private HashMap<Character, Character> _permuted = new HashMap<>();
    /** inverted is also a dictionary, but maps keys to permutation coming back */
    private HashMap<Character, Character> _inverted = new HashMap<>();

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters not
     *  included in any cycle map to themselves. Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        String[] cyclesArray = cycles.split("\\)");
        for (String cycle : cyclesArray) {
            for (int i = 1; i < cycle.length(); i++) {
                char key = cycle.charAt(i);
                if (i + 1 >= cycle.length()) {
                    _permuted.put(key, cycle.charAt(1));
                } else {
                    _permuted.put(key, cycle.charAt(i + 1));
                }
            }
        }

        for (String cycle : cyclesArray) {
            for (int i = 1; i < cycle.length(); i++) {
                char key = cycle.charAt(i);
                if (i - 1 < 1) {
                    _inverted.put(key, cycle.charAt(cycle.length() - 1));
                } else {
                    _inverted.put(key, cycle.charAt(i - 1));
                }
            }
        }
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
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        if (_permuted.containsKey(_alphabet.toChar(p))) {
            return _alphabet.toInt(_permuted.get(_alphabet.toChar(p))) % _alphabet.size();
        } else {
            return p % _alphabet.size();
        }
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        if (_inverted.containsKey(_alphabet.toChar(c))) {
            return _alphabet.toInt(_inverted.get(_alphabet.toChar(c))) % _alphabet.size();
        } else {
            return c % _alphabet.size();
        }
    }


    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        return true;
    }
}

