package cube;

import java.util.Observer;
import java.util.Observable;
import java.util.Random;

/** Main class for the Cube puzzle.
 *  @author P. N. Hilfinger. */
public class Main implements Observer {

    /** Present cube puzzles, according to options given in ARGS. */
    public static void main(String... args) {
        new Main().run();
    }

    /** Set up and monitor cube puzzles until exited. */
    private void run() {
        _model = new CubeModel();
        _board = new CubeGUI("Cube", _model);
        _side = 4;
        initPuzzle();
        _board.addObserver(this);
        _board.display(true);
    }

    /** Initialize model to a random configuration on a grid with SIDE
     *  rows. */
    private void initPuzzle() {

        int randomRow = _random.nextInt(_side);
        int randomColumn = _random.nextInt(_side);

        boolean[]facePainted = new boolean[6];
        boolean[][]painted = new boolean [_side][_side];

        _model.initialize(_side, randomRow, randomColumn, painted, facePainted);

        painted[_random.nextInt(_side)][_random.nextInt(_side)] = true;
        painted[_random.nextInt(_side)][_random.nextInt(_side)] = true;
        painted[_random.nextInt(_side)][_random.nextInt(_side)] = true;
        painted[_random.nextInt(_side)][_random.nextInt(_side)] = true;
        painted[_random.nextInt(_side)][_random.nextInt(_side)] = true;
        painted[_random.nextInt(_side)][_random.nextInt(_side)] = true;

        _done = false;

    }

    @Override
    public void update(Observable obs, Object arg) {
        switch ((String) arg) {
        case "click":
            if (_done) {
                return;
            }
            try {
                _model.move(_board.mouseRow(), _board.mouseCol());
                if (_model.allFacesPainted()) {
                    _done = true;
                    _board.message("", "Finished in %d moves.%n",
                                   _model.moves());
                }
            } catch (IllegalArgumentException excp) {
                /* Ignore IllegalArgumentException */
            }
            break;
        case "New":
            initPuzzle();
            break;
        case "Seed...":
            _random.setSeed((Long) _board.param());
            break;
        case "Size...":
            _side = (Integer) _board.param();
            initPuzzle();
            break;
        case "Quit":
            System.exit(0);
            break;
        default:
            break;
        }
    }

    /** Current board size. */
    private int _side;

    /** The current cube puzzle. */
    private CubeModel _model;

    /** GUI displaying puzzles. */
    private CubeGUI _board;

    /** True iff current puzzle is solved. */
    private boolean _done;

    /** PRNG for choosing initial positions. */
    private Random _random = new Random();

}
