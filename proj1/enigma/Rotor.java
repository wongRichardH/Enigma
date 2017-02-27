package enigma;

import static enigma.EnigmaException.*;

/** Superclass that represents a rotor in the enigma machine.
 *  @author rw
 */
class Rotor {
    /** settings variable */
    public int _setting;

    /** My name. */
    private final String _name;

    /** The permutation implemented by this rotor in its 0 position. */
    public Permutation _permutation;

    /**variable for my notches */
    private String _notches;

    /** A rotor named NAME whose permutation is given by PERM. */
    Rotor(String name, Permutation perm, String notches) {
        _name = name;
        _permutation = perm;
        _notches = notches;
        _setting = 0;
    }

    /** Return my name. */
    String name() {
        return _name;
    }

    /** Return my alphabet. */
    Alphabet alphabet() {
        return _permutation.alphabet();
    }

    /** Return my permutation. */
    Permutation permutation() {
        return _permutation;
    }

    /** Return the size of my alphabet. */
    int size() {
        return _permutation.size();
    }

    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {
        return false;
    }

    /** Return true iff I reflect. */
    boolean reflecting() {
        return false;
    }

    /** Return my current setting. */
    int getSetting() {
        return _setting;
    }

    /** Set setting() to POSN.  */
    void set(int posn) {
        assert 0<= posn && posn < 26;
        _setting = posn;
    }

    /** Set setting() to character CPOSN. */
    void set(char cposn) {
        _setting = _permutation.alphabet().toInt(cposn);
    }

    /** Return the conversion of P (an integer in the range 0..size()-1)
     *  according to my permutation. */
    int convertForward(int p) {
        int added = (p + _setting) % _permutation.alphabet().size();
        int converted = _permutation.permute(added);
        int subtracted = (converted - _setting) % _permutation.alphabet().size();

        if (subtracted < 0) {
            subtracted += _permutation.alphabet().size();
        }

        return subtracted;
    }

    /** Return the conversion of E (an integer in the range 0..size()-1)
     *  according to the inverse of my permutation. */
    int convertBackward(int e) {
        int added = (e + _setting) % _permutation.alphabet().size();
        int converted = _permutation.invert(added);
        int subtracted = (converted - _setting) % _permutation.alphabet().size();

        if (subtracted < 0) {
            subtracted += _permutation.alphabet().size();
        }

        return subtracted;
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        return _notches.indexOf(_permutation.alphabet().toChar(_setting)) != -1;
    }

    /** Advance me one position, if possible. By default, does nothing. */
    void advance() {
        _setting = ((_setting + 1) % _permutation.alphabet().size());
    }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }

}
