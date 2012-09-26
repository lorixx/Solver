import java.util.TreeSet;
import java.util.Set;

public class Solver {
    
    private MinPQ<SearchNode> minQueue;  // used for the original Board
    private MinPQ<SearchNode> twinQueue; // used for the twin Board
    private int moves = 0; //keeping track of how many moves happened so far
    private SearchNode solutionNode = null;
    private Board boardToSolve;
    private boolean isSolvable;
    
    public Solver(Board board) {
        this.boardToSolve = board;
        this.minQueue = new MinPQ<SearchNode>();
        this.twinQueue = new MinPQ<SearchNode>();
        
        SearchNode firstNode = new SearchNode(board, null, this.moves);
        this.minQueue.insert(firstNode);
        
        SearchNode firstTwinNode = new SearchNode(board.twin(), null, this.moves);
        this.twinQueue.insert(firstTwinNode);
        
        this.isSolvable = this.solvePuzzle();
        
        
    }    
    
    private boolean solvePuzzle() {
        Set<Board> closedSet = new TreeSet<Board>();
        Set<Board> openSet = new TreeSet<Board>();
        openSet.add(this.boardToSolve);
        this.moves = 0;

        
        Set<Board> closedSetForTwin = new TreeSet<Board>();
        Set<Board> openSetForTwin = new TreeSet<Board>();
        openSetForTwin.add(this.boardToSolve.twin());
        int movesForTwin = 0;
        
        
        
        Queue<Board> neighbors; //will be used by original and twin
        SearchNode currentNode = this.minQueue.delMin();
        SearchNode currentTwinNode = this.twinQueue.delMin();
        while (true) {
            if (currentNode.searchBoard.isGoal()) {
                this.solutionNode = currentNode;
                return true;
            } 
            
            if (currentTwinNode.searchBoard.isGoal()) {
                this.solutionNode = null;
                return false;
            }
            
            
            openSet.remove(currentNode.searchBoard);
            closedSet.add(currentNode.searchBoard);
            neighbors = (Queue<Board>) currentNode.searchBoard.neighbors();
            this.moves = currentNode.movesSoFar + 1;
            for(Board board : neighbors) {
                if (closedSet.contains(board)) continue;
                if ( !openSet.contains(board) ) {
                    this.minQueue.insert(new SearchNode(board, currentNode, this.moves));
                    openSet.add(board);
                }
            }
            currentNode = this.minQueue.delMin();
            
            
            openSetForTwin.remove(currentTwinNode.searchBoard);
            closedSetForTwin.add(currentTwinNode.searchBoard);
            neighbors = (Queue<Board>) currentTwinNode.searchBoard.neighbors();
            movesForTwin = currentTwinNode.movesSoFar + 1;
            for(Board board : neighbors) {
                if (closedSetForTwin.contains(board)) continue;
                if ( !openSetForTwin.contains(board) ) {
                    this.twinQueue.insert(new SearchNode(board, currentTwinNode, movesForTwin));
                    openSetForTwin.add(board);
                }
            }
            currentTwinNode = this.twinQueue.delMin();
        } 
    }

    
    public boolean isSolvable() {
        return this.isSolvable;        
    }
    
    public int moves() {
        
        //return this.isSolvable ? this.moves : -1;
        if (!this.isSolvable)
            return -1;
        else {
            int moves = 0;
            SearchNode tempNode = this.solutionNode;
            while (tempNode.previousNode != null) {
                tempNode = tempNode.previousNode;
                moves++;
            }
            return moves;
            
        }
    }
        
    public Iterable<Board> solution() {
        if (this.isSolvable) {
            Stack<Board> solutionStack= new Stack<Board>();
            
            SearchNode tempNode = this.solutionNode;
            
            solutionStack.push(tempNode.searchBoard);
            
            while (tempNode.previousNode != null) {
                solutionStack.push(tempNode.previousNode.searchBoard);
                tempNode = tempNode.previousNode;
            }
            
            return solutionStack;
            
        } else
            return null;
    }
    
    private class SearchNode implements Comparable<SearchNode>{
        public SearchNode previousNode;
        public Board searchBoard;
        public int movesSoFar;
        private int priorityValue;
        
        
        public SearchNode(Board board, SearchNode previous, int moves) {
            this.searchBoard = board;
            this.previousNode = previous;   
            this.movesSoFar = moves;
            this.priorityValue = moves + board.hamming();
        }
        
        public int compareTo(SearchNode that) {
            if      (this.priorityValue < that.priorityValue) return -1;
            else if (this.priorityValue > that.priorityValue) return +1;
            else                                              return  0;
        }    

        // is this SearchNode equal to x?
        // only compare the two searchBoards for equality 
        public boolean equals(Object x) {
            if (x == this) return true;
            if (x == null) return false;
            if (x.getClass() != this.getClass()) return false;
            SearchNode that = (SearchNode) x;
            return //(this.priorityValue == that.priorityValue) 
                //&& (this.previousNode.equals(that.previousNode))
                //&& 
                (this.searchBoard.equals(that.searchBoard));
        }
    }
    
    
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int N = in.readInt();
        int[][] blocks = new int[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
            blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);
        
        // solve the puzzle
        Stopwatch timer = new Stopwatch();
        Solver solver = new Solver(initial);
        
        // print solution to standard output

        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);    
        }
//        StdOut.println("It took " + timer.elapsedTime() + " seconds to computer.");
//        
//
//        StdOut.println("Minimum number of moves = " + solver.moves());
//        StdOut.println("Minimum number of moves = " + solver.moves());
//        StdOut.println("Minimum number of moves = " + solver.moves());
//        StdOut.println("Minimum number of moves = " + solver.moves());
//
//        StdOut.println("Minimum number of moves = " + solver.moves());
//        
        

    }
}