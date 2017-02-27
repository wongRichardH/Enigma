package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotor that has no ratchet and does not advance.
 *  @author
 */
class FixedRotor extends Rotor {

    /** A non-moving rotor named NAME whose permutation at the 0 setting
     * is given by PERM. */

    /** Notches is passed in to represent which notch the rotor is currently at. */

    FixedRotor(String name, Permutation perm, String notches) {
        super(name, perm, notches);
    }
}
