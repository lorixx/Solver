import java.util.Arrays;

public class Board {
    
    private int[][] tiles; //immutable data
    private int hammingValue = -1; //has to start from -1 so that we calculate correctly
    private int manhattanValue = 0;
    private int xForZero;
    private int yForZero;
    
    public Board(int[][] blocks) {
        
        //this.tiles = new int[blocks.length][blocks.length];
        this.tiles = blocks;
        
        int tempNumber = 0;
        int referenceNumber = 1;
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks.length; j++) {
                this.tiles[i][j] = blocks[i][j];
                tempNumber = blocks[i][j];
                if (tempNumber != 0) {
                    this.manhattanValue += Math.abs(i - this.getXForNumber(tempNumber)) 
                            + Math.abs(j - this.getYForNumber(tempNumber)); 
                } else {
                    xForZero = i;
                    yForZero = j;
                }
                
                if (tempNumber != referenceNumber) {
                    this.hammingValue++;
                }
                referenceNumber++; 
            }
        }
    }
    
    public int dimension() {
        return this.tiles.length;
    }
    
    public int hamming() {   
        return this.hammingValue;
    }
    
    public int manhattan() {
        
        return this.manhattanValue;
    }
    
    // return x index for goal position of a number
    private int getXForNumber(int number) {
        int result = number / this.dimension();
        if (number == result * this.dimension())
            result--;
        return result;    
    }
    
    // return y index for goal position of a number
    private int getYForNumber(int number) {
        int remainder = number % this.dimension();
        if (remainder == 0)
            return this.dimension() - 1;
        else
            return remainder - 1; // since index start from 0 thus minus 1
    }
    
    public boolean isGoal() {
        return this.manhattan() == 0;
    }
    
    public Board twin() {
        
        int[][] newArray = new int[this.dimension()][this.dimension()];
        for (int i = 0; i < this.dimension(); i++) {
            for (int j = 0; j < this.dimension(); j++) {
                newArray[i][j] = this.tiles[i][j];
            }
        }
        if (newArray.length > 1) {
            
            // Fixed a bug for twin function
            if (newArray[0][0] == 0 || newArray[0][1] == 0) {
                int temp = newArray[1][0];
                newArray[1][0] = newArray[1][1];
                newArray[1][1] = temp;   
            } else {
                int temp = newArray[0][0];
                newArray[0][0] = newArray[0][1];
                newArray[0][1] = temp;
                
            }
        }
        
        return new Board(newArray);   
    }
    
    public boolean equals(Object y) {
        if (this == y) return true;
        if (y == null) return false;
        if (this.getClass() != y.getClass()) return false;
        Board that = (Board) y;
        
        for (int i = 0; i < this.dimension(); i++) {
            for (int j = 0; j < this.dimension(); j++) {
                if (this.tiles[i][j] != that.tiles[i][j])
                    return false;
            }
        }
        return true;
        
        //return Arrays.deepEquals(this.tiles, that.tiles);
    }
    
    public Iterable<Board> neighbors() {
        Queue<Board> queue = new Queue<Board>();

        int i = xForZero;
        int j = yForZero;
        if (i > 0) {
            int[][] newArray = this.cloneTiles();
            newArray[i][j] = newArray[i - 1][j];
            newArray[i - 1][j] = 0;
            queue.enqueue(new Board(newArray));
        }
        
        if (i < this.dimension() - 1) {
            int[][] newArray = this.cloneTiles(); 
            newArray[i][j] = newArray[i + 1][j];
            newArray[i + 1][j] = 0;
            queue.enqueue(new Board(newArray));
        }
        
        if (j > 0) {
            int[][] newArray = this.cloneTiles();
            newArray[i][j] = newArray[i][j - 1];
            newArray[i][j - 1] = 0;
            queue.enqueue(new Board(newArray));
        }
        
        if (j < this.dimension() - 1) {
            int[][] newArray = this.cloneTiles();    //this.cloneTiles();
            newArray[i][j] = newArray[i][j + 1];
            newArray[i][j + 1] = 0;
            queue.enqueue(new Board(newArray));
        }

        return queue;
    }
    
    private int[][] cloneTiles () {
        int length = this.dimension();
        int[][] newArray = new int[length][]; 
        for(int i = 0; i < length; i++)
            newArray[i] = this.tiles[i].clone();
        
        return newArray;
    }
    
    public String toString() {
        int N = this.dimension();
        StringBuilder s = new StringBuilder();
        s.append(N + "\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                s.append(String.format("%2d ", this.tiles[i][j]));
            }
            s.append("\n");
        }
        return s.toString();    
    }
    
    public static void main(String[] args) {
        int[][] goalArray = {{1, 2, 3}, {4, 5, 6}, {7, 8, 0}};
        
        // test isGoal()
        Board b1 = new Board(goalArray);
        assert b1.isGoal() : "array is not a goal board";
        
        // test equals() function
        Board b3 = new Board(goalArray);
        assert b1.equals(b3) : "Boards are not equal";
        
        int[][] testArray = {{1, 4, 3}, {2, 0, 6}, {8, 7, 5}};
        Board b2 = new Board(testArray);
        assert !b2.isGoal() : "board should not be a goal board";
        
        // test dimension() and hamming() function
        assert b2.dimension() == 3 : "Dimension for the current board is 3";
        assert b2.hamming() == 5 : "hamming is not correctly calculated.";
        
        int[][] testArray2 = {{1, 2, 3}, {4, 5, 6}, {8, 7, 0}};
        Board b4 = new Board(testArray2);
        assert b4.hamming() == 2 : "hamming is not correctly calculated for b4";
        
        // test manhattan result
        assert b1.manhattan() == 0 : "manhattan not equal to 0 for goal board";
        assert b2.manhattan() == 8 : "manhattan not equal to 0 for goal board";
        assert b4.manhattan() == 2 : "manhattan not equal to 0 for goal board";
        
        StdOut.println(b1);StdOut.println(b1.twin());
        StdOut.println(b2);StdOut.println(b2.twin());
        StdOut.println(b3);StdOut.println(b3.twin());
        StdOut.println(b4);StdOut.println(b4.twin());
        
        
        /*
        // test getXForNumber(int number) function, this will be removed
        assert b4.getXForNumber(3) == 0 : "wrong x calculation";
        assert b4.getXForNumber(2) == 0 : "wrong x calculation";        
        assert b4.getXForNumber(4) == 1 : "wrong x calculation";
        assert b4.getXForNumber(6) == 1 : "wrong x calculation";       
        assert b4.getXForNumber(8) == 2 : "wrong x calculation";
        assert b4.getXForNumber(7) == 2 : "wrong x calculation";
        
        // test getYForNumber(int number) function, this will be removed
        assert b4.getYForNumber(8) == 1 : "wrong y calculation";
        assert b4.getYForNumber(3) == 2 : "wrong y calculation";
        assert b4.getYForNumber(6) == 2 : "wrong y calculation";
        assert b4.getYForNumber(4) == 0 : "wrong y calculation";
        assert b4.getYForNumber(7) == 0 : "wrong y calculation";
        assert b4.getYForNumber(1) == 0 : "wrong y calculation";
        */
        
        // test neighbors
        
        StdOut.println("==========The following are neighbots of b1==========");
        //StdOut.println(b1);
        Queue<Board> queue = (Queue<Board>) b2.neighbors();
        while (!queue.isEmpty()) {
            Board board = queue.dequeue();
            StdOut.println(board);
        }
    }
}