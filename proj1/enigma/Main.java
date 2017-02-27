package enigma;


import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

import java.util.*;

/** Enigma simulator.
 *  @author rw
 */
public final class Main {

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

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
        _alphabet = new Alphabet(_config.nextLine());
        int rotorCount = Integer.parseInt(_config.next());
        int pawlCount = Integer.parseInt(_config.next());

        HashMap<String, String> rotorToPermutationString = new HashMap<String, String>();

        String rotorKey = "";
        String rotorValue = "";
        List<String> keys = new ArrayList<String>();
        while(_config.hasNext()) {
            String currentToken = _config.next();
            if (currentToken.charAt(0) == '(') {
                rotorValue = rotorValue + currentToken;
            } else {
                if (rotorValue.length() != 0) {
                    rotorToPermutationString.put(rotorKey, rotorValue);
                    keys.add(rotorKey);
                    rotorKey = "";
                    rotorValue = "";
                }
                if (rotorKey.length() != 0) {
                    rotorKey = rotorKey + "," + currentToken;
                } else {
                    rotorKey = currentToken;
                }
            }
        }

        rotorToPermutationString.put(rotorKey, rotorValue);
        keys.add(rotorKey);

        List<Rotor> rotorCollection = new ArrayList<>();

        for (String key : keys) {
            String[] splitKey = key.split(",");
            String rotorName = splitKey[0];
            String rotorDescription = splitKey[1];

            String notches = "";
            Permutation permutation = new Permutation(rotorToPermutationString.get(key), _alphabet);
            if (rotorDescription.length() > 1) {
                notches = rotorDescription.substring(1);
                Rotor movingRotor = new MovingRotor(rotorName, permutation, notches);
                rotorCollection.add(movingRotor);
            } else {
                if (rotorDescription.equals("N")) {
                    FixedRotor fixedRotor = new FixedRotor(rotorName, permutation, notches);
                    rotorCollection.add(fixedRotor);
                } else {
                    Reflector reflector = new Reflector(rotorName, permutation, notches);
                    rotorCollection.add(reflector);
                }
            }
        }
        Machine M = new Machine(_alphabet, rotorCount, pawlCount, rotorCollection);


        while (_input.hasNextLine()) {
            String inputLine = _input.nextLine();
            if (inputLine.length() == 0) {
                _output.println();
                continue;
            }
            if (inputLine.charAt(0) == '*') {
                setUp(M, inputLine);
            } else {
                inputLine = inputLine.replaceAll("\\s+","");
                inputLine = inputLine.toUpperCase();
                String output = "";
                String outputLine = "";
                for (int i = 0; i < inputLine.length(); i++) {
                    if (i % 5 == 0) {
                        output = M.convert(output);
                        if (outputLine.length() == 0) {
                            outputLine += output;
                        } else {
                            outputLine += " " + output;
                        }
                        output = "";
                    }
                    output += inputLine.charAt(i);
                }
                output = M.convert(output);
                if (outputLine.length() == 0) {
                    outputLine += output;
                } else {
                    outputLine += " " + output;
                }
                _output.println(outputLine);
            }
        }

    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            _alphabet = new UpperCaseAlphabet();
            return new Machine(_alphabet, 2, 1, null);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            return null;
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        M.parseSettings(settings);
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
    }

}
