import edu.princeton.cs.algs4.Queue;
import java.util.Arrays;


public class Board {
    private final int[][] blocks;
    private final int[][] goal;
    private final int manhattan;
    private final int hamming;
    private final int dim;
    private final Queue<Board> neighbours;

    public Board(int[][] blocks) {
        checkInput(blocks);
        this.blocks = blocks;
        this.dim = dimension();
        this.goal = new int[dim][dim];
        //calculate goal
        for (int i = 0, val = 1; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                if (i == dim - 1 && j == dim - 1) {
                    goal[i][j] = 0;
                } else {
                    goal[i][j] = val;
                    val++;
                }
            }
        }
        //calculate scores
        this.hamming = calcHamming();
        this.manhattan = calcManhattan();
        Queue<Board> temp = new Queue<>();

        this.neighbours = temp;

    }

    private class coords {
        public final int x;
        public final int y;

        public coords(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public int dimension() {
        return this.blocks.length;
    }

    private void checkInput(int[][] blocks) {
        if (!isSquare(blocks)) throw new IllegalArgumentException("Board is not square");
    }

    private boolean isSquare(int[][] blocks) {
        return blocks.length == blocks[0].length;
    }

    public boolean isGoal() {
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                if (this.blocks[i][j] != this.goal[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(dim + "\n");
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                s.append(String.format("%2d ", this.blocks[i][j]));
            }
            s.append("\n");
        }
        return s.toString();
    }

    public int hamming() {
        return this.hamming;
    }

    public int manhattan() {
        return this.manhattan;
    }

    private int calcHamming() {
        int score = 0;
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                if (this.blocks[i][j] != this.goal[i][j] && this.blocks[i][j] != 0) {
                    score++;
                }
            }
        }
        return score;
    }

    private int calcManhattan() {
        int score = 0;
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                if (this.blocks[i][j] != 0) {
                    int targetRow = (this.blocks[i][j] - 1) / dim;
                    int targetCol = (this.blocks[i][j] - 1) % dim;
                    int distance = Math.abs(i - targetRow) + Math.abs(j - targetCol);
                    //System.out.println("real row: "+i + " Real col: "+ j);
                    //System.out.println("target row: " + targetRow + " target col: " + targetCol + " Value: " + this.blocks[i][j] + "  Distance: " + distance);
                    score += distance;
                }
            }
        }
        return score;
    }

    public Board twin() {
        int i = 0, j = 0;
        while (this.blocks[i][j] == 0 || this.blocks[i][j + 1] == 0) {
            if (j == dim - 2) {
                j = 0;
                i++;
            }
            j++;
            if (i >= dim - 1 && j >= dim - 2) throw new IllegalArgumentException("wtf");
        }
        return new Board(exchange(i, j, i, j + 1));
    }

    public static void main(String[] args) {
        int[][] solved3 = {{1, 2, 3}, {4, 5, 6}, {7, 8, 0}};
        int[][] unsloved3 = {{1, 2, 4}, {3, 0, 6}, {7, 8, 5}};
        int[][] reverse = {{0, 8, 7}, {5, 6, 4}, {3, 2, 1}};
        int[][] exampleMan = {{8, 1, 3}, {4, 0, 2}, {7, 6, 5}};
        int[][] twinTest = {{8, 0, 7}, {5, 6, 4}, {3, 2, 1}};

        Board solved3b = new Board(solved3);
        Board unsloved3b = new Board(unsloved3);
        Board reverseb = new Board(reverse);
        Board scores = new Board(exampleMan);
        Board twinTestb = new Board(twinTest);
        Board twinTestb2 = new Board(twinTest);

        String str_board = unsloved3b.toString();
        System.out.println(str_board);

        System.out.println(Arrays.deepToString(solved3b.goal));
        System.out.println(Arrays.deepToString(unsloved3b.goal));
        assert !unsloved3b.isGoal();
        assert solved3b.isGoal();
        System.out.println("----");
        assert reverseb.hamming() == 8;
        assert solved3b.hamming() == 0;
        assert unsloved3b.hamming() == 3;
        assert scores.hamming() == 5;
        assert scores.manhattan() == 10;
        System.out.println(twinTestb.twin());
        assert twinTestb2.equals(twinTestb);
        System.out.println(twinTestb.calcNeighbors());

    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other == null) return false;
        if (other.getClass() != this.getClass()) return false;
        Board that = (Board) other;
        return Arrays.deepEquals(this.blocks, that.blocks);
    }

    private Queue<Board> calcNeighbors() {
        Queue<Board> neig = new Queue<>();
        int x = 0, y = 0;
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                if (this.blocks[i][j] == 0) {
                    x = i;
                    y = j;
                }
            }
        }
        System.out.println("x: " + x + " y: " + y);
        if (x - 1 >= 0) {
            neig.enqueue(new Board(exchange(x, y, x - 1, y)));
        }
        if (x + 1 < dim) {
            neig.enqueue(new Board(exchange(x, y, x + 1, y)));
        }
        if (y - 1 >= 0) {
            neig.enqueue(new Board(exchange(x, y, x, y - 1)));
        }
        if (y + 1 < dim) {
            neig.enqueue(new Board(exchange(x, y, x, y + 1)));
        }
        return neig;
    }

    public Iterable<Board> neighbors() {
        return this.neighbours;
    }

    private int[][] exchange(int x1, int y1, int x2, int y2) {
        int[][] copy = new int[dim][dim];
        for (int i = 0; i < dim; i++) {
            copy[i] = this.blocks[i].clone();
        }
        int temp = copy[x1][y1];
        copy[x1][y1] = copy[x2][y2];
        copy[x2][y2] = temp;
        return copy;
    }

}
