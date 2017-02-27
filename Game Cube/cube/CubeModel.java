package cube;

import java.util.*;
import java.util.Observable;

import static java.lang.System.arraycopy;

/** Models an instance of the Cube puzzle: a cube with color on some sides
 *  sitting on a cell of a square grid, some of whose cells are colored.
 *  Any object may register to observe this model, using the (inherited)
 *  addObserver method.  The model notifies observers whenever it is modified.
 *  @author P. N. Hilfinger
 */
class CubeModel extends Observable {

    /** variable for holding the size of the CubeModel */
    int size;

    /** Contains the 0th index row */
    int row0;
    /** Contains the 0th index col  */
    /**  */
    int col0;

    /** Boolean that tells me whether or a grid on CubeModel is painted */
    boolean[][] painted;

    /**Boolean that tells me whether or not the cube face is painted */
    boolean[] facePainted;

    /** Counts the amount of times the cube has moved across the board  */
    int moveCount;

    Map<String, Integer> diceFaces = new HashMap<>();

    /**
     * A blank cube puzzle of size 4.
     */
    CubeModel() {

        initialize(4, 0, 0, new boolean[4][4], new boolean[6]);

    }

    /**
     * A copy of CUBE.
     */
    CubeModel(CubeModel cube) {
        initialize(cube);
    }

    /**
     * Initialize puzzle of size SIDExSIDE with the cube initially at
     * ROW0 and COL0, with square r, c painted iff PAINTED[r][c], and
     * with face k painted iff FACEPAINTED[k] (see isPaintedFace).
     * Assumes that
     * * SIDE > 2.
     * * PAINTED is SIDExSIDE.
     * * 0 <= ROW0, COL0 < SIDE.
     * * FACEPAINTED has length 6.
     */
    void initialize(int side, int row0, int col0,
                    boolean[][] painted,
                    boolean[] facePainted) {

        this.size = side;
        this.row0 = row0;
        this.col0 = col0;
        this.painted = painted;
        this.facePainted = facePainted;

        diceFaces.put("Close", 0);
        diceFaces.put("Far", 1);
        diceFaces.put("Left", 2);
        diceFaces.put("Right", 3);
        diceFaces.put("Bottom", 4);
        diceFaces.put("Top", 5);


        setChanged();
        notifyObservers();
    }

    /**
     * Initialize puzzle of size SIDExSIDE with the cube initially at
     * ROW0 and COL0, with square r, c painted iff PAINTED[r][c].
     * The cube is initially blank.
     * Assumes that
     * * SIDE > 2.
     * * PAINTED is SIDExSIDE.
     * * 0 <= ROW0, COL0 < SIDE.
     */
    void initialize(int side, int row0, int col0, boolean[][] painted) {
        initialize(side, row0, col0, painted, new boolean[6]);
    }

    /**
     * Initialize puzzle to be a copy of CUBE.
     */
    void initialize(CubeModel cube) {

        this.size = cube.size;
        this.row0 = cube.row0;
        this.col0 = cube.col0;
        this.painted = cube.painted;
        this.facePainted = cube.facePainted;

        this.moveCount = cube.moves();
        this.diceFaces = new HashMap<String, Integer>(cube.diceFaces);

        setChanged();
        notifyObservers();
    }

    /**
     * Move the cube to (ROW, COL), if that position is on the board and
     * vertically or horizontally adjacent to the current cube position.
     * Transfers colors as specified by the rules.
     * Throws IllegalArgumentException if preconditions are not met.
     */
    void move(int row, int col) {

        if(Math.abs(cubeRow() - row) == 1 && Math.abs(cubeCol()- col) == 1) {
            throw new IllegalArgumentException("Can't move diagonally, dude");
        }

        if (((row < 0 || row >= this.size) ||
                (col < 0 || col >= this.size)) ||
                ((cubeCol() - col != 1) &&
                        (cubeCol() - col != -1) &&
                        (cubeRow() - row != 1) &&
                        (cubeRow() - row != -1))) {
            throw new IllegalArgumentException("Not within the parameters of the Grid " +
                    "or can't move to squares that are non-adjacent values!!");

            } else {

                if (cubeRow() - row == -1) {
                    this.row0 = row;
                    this.col0 = col;
                    int temp = diceFaces.get("Close");

                    diceFaces.put("Close", diceFaces.get("Bottom"));
                    diceFaces.put("Bottom", diceFaces.get("Far"));
                    diceFaces.put("Far", diceFaces.get("Top"));
                    diceFaces.put("Top", temp);

                    if ((!facePainted[diceFaces.get("Bottom")] && (painted[cubeRow()][cubeCol()]))) {
                        System.out.print("Transferring from painted Grid to unpainted Bottom Dice Face A");

                        facePainted[diceFaces.get("Bottom")] = true;
                        painted[cubeRow()][cubeCol()] = false;

                    } else if ((facePainted[diceFaces.get("Bottom")] && (!painted[cubeRow()][cubeCol()]))) {
                        System.out.println("Transferring paint dice to grid B");
                        System.out.println(diceFaces.get("Bottom"));

                        facePainted[diceFaces.get("Bottom")] = false;
                        painted[cubeRow()][cubeCol()] = true;

                    }

                }
                if (cubeRow() - row == 1) {
                    this.row0 = row;
                    this.col0 = col;
                    int temp = diceFaces.get("Close");

                    diceFaces.put("Close", diceFaces.get(("Top")));
                    diceFaces.put("Top", diceFaces.get(("Far")));
                    diceFaces.put("Far", diceFaces.get(("Bottom")));
                    diceFaces.put("Bottom", temp);

                    if ((!facePainted[diceFaces.get("Bottom")] &&
                            (painted[cubeRow()][cubeCol()]))) {
                        System.out.print("Transferring from painted Grid to unpainted Bottom Dice Face A2");

                        facePainted[diceFaces.get("Bottom")] = true;
                        painted[cubeRow()][cubeCol()] = false;
                    } else if ((facePainted[diceFaces.get("Bottom")] &&
                            (!painted[cubeRow()][cubeCol()]))) {
                        System.out.println("Transferring paint dice to grid A3");

                        facePainted[diceFaces.get("Bottom")] = false;
                        painted[cubeRow()][cubeCol()] = true;

                    }

                }

                if (cubeCol() - col == -1) {
                    this.row0 = row;
                    this.col0 = col;
                    int temp = diceFaces.get("Top");

                    diceFaces.put("Top", diceFaces.get("Left"));
                    diceFaces.put("Left", diceFaces.get("Bottom"));
                    diceFaces.put("Bottom", diceFaces.get("Right"));
                    diceFaces.put("Right", temp);




                    if ((!facePainted[diceFaces.get("Bottom")] &&
                            (painted[cubeRow()][cubeCol()]))) {
                        System.out.print("Transferring from painted Grid to unpainted Bottom Dice Face");

                        facePainted[diceFaces.get("Bottom")] = true;
                        painted[cubeRow()][cubeCol()] = false;

                    } else if ((facePainted[diceFaces.get("Bottom")] &&
                            (!painted[cubeRow()][cubeCol()]))) {
                        System.out.println("Transferring paint dice to grid");

                        facePainted[diceFaces.get("Bottom")] = false;
                        painted[cubeRow()][cubeCol()] = true;

                    }


                }

                if (cubeCol() - col == 1) {
                    this.row0 = row;
                    this.col0 = col;
                    int temp = diceFaces.get("Top");


                    diceFaces.put("Top", diceFaces.get("Right"));
                    diceFaces.put("Right", diceFaces.get("Bottom"));
                    diceFaces.put("Bottom", diceFaces.get("Left"));
                    diceFaces.put("Left", temp);

                    System.out.println("Am I moving LEFT");
                    System.out.println(diceFaces.get("Bottom"));
                    System.out.println(Arrays.toString(facePainted));


                    if ((!facePainted[diceFaces.get("Bottom")] &&
                            (painted[cubeRow()][cubeCol()]))) {
                        System.out.println("Transferring from painted Grid to unpainted Bottom Dice Face");

                        facePainted[diceFaces.get("Bottom")] = true;
                        painted[cubeRow()][cubeCol()] = false;

                    } else if ((facePainted[diceFaces.get("Bottom")] &&
                            (!painted[cubeRow()][cubeCol()]))) {
                        System.out.println("Transferring paint dice to grid");

                        facePainted[diceFaces.get("Bottom")] = false;
                        painted[cubeRow()][cubeCol()] = true;

                    }

                }

                moveCount++;
                setChanged();
                notifyObservers();

            }
        }

    /**
     * Return the number of squares on a side.
     */
    int side() {
        return this.size;
    }

    /**
     * Return true iff square ROW, COL is painted.
     * Requires 0 <= ROW, COL < board size.
     */
    boolean isPaintedSquare(int row, int col) {
        if ((painted[row][col])) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Return current row of cube.
     */
    int cubeRow() {
        return this.row0;
    }

    /**
     * Return current column of cube.
     */
    int cubeCol() {
        return this.col0;
    }

    /**
     * Return the number of moves made on current puzzle.
     */
    int moves() {
        return moveCount;
    }

    /**
     * Return true iff face #FACE, 0 <= FACE < 6, of the cube is painted.
     * Faces are numbered as follows:
     * 0: Vertical in the direction of row 0 (nearest row to player).
     * 1: Vertical in the direction of last row.
     * 2: Vertical in the direction of column 0 (left column).
     * 3: Vertical in the direction of last column.
     * 4: Bottom face.
     * 5: Top face.
     */
    boolean isPaintedFace(int face) {

        if (face < 0 || face > 6) {
            throw new IllegalArgumentException();
        }

        if (face >= 0 && face < 6) {
            if (face == 0) {
                return facePainted[this.diceFaces.get("Close")];
            } else if (face == 1) {
                return facePainted[this.diceFaces.get("Far")];
            } else if (face == 2) {
                return facePainted[this.diceFaces.get("Left")];
            } else if (face == 3) {
                return facePainted[this.diceFaces.get("Right")];
            } else if (face == 4) {
                return facePainted[this.diceFaces.get("Bottom")];
            } else if (face == 5) {
                return facePainted[this.diceFaces.get("Top")];
            }
        }
        return false;
    }


    /**
     * Return true iff all faces are painted.
     */
    boolean allFacesPainted() {
        int i = 0;

        while (i < facePainted.length) {
            System.out.println (Arrays.toString(facePainted));

            if (!facePainted[i]) {
                return false;
            }
            i++;
        }
        return true;

    }
}
