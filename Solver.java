import java.util.TreeSet;
import java.util.HashMap;
import java.util.Comparator;

public class Solver {
    
    private MinPQ<SearchNode> minQueue;  // used for the original Board
    private MinPQ<SearchNode> twinQueue; // used for the twin Board
    private int moves = 0; //keeping track of how many moves happened so far
    private SearchNode solutionNode = null;
    private Board boardToSolve;
    private boolean isSolvable;
    
    public Solver(Board board) {
        this.boardToSolve = board;
        this.minQueue = new MinPQ<SearchNode>(new PriorityComparator());
        this.twinQueue = new MinPQ<SearchNode>(new PriorityComparator());
        
        SearchNode firstNode = new SearchNode(board, null, this.moves);
        this.minQueue.insert(firstNode);
        
        SearchNode firstTwinNode = new SearchNode(board.twin(), null, this.moves);
        this.twinQueue.insert(firstTwinNode);
        
        this.isSolvable = this.solvePuzzle();  
    }   
    
    private boolean solvePuzzle() {
        

        SearchNode minNode = this.minQueue.min();
        int moves = 0;

        SearchNode minNodeForTwin = this.twinQueue.min();
        int movesForTwin = 0;
        
        Queue<Board> neighbors; //will be used by original and twin
        SearchNode currentNode = this.minQueue.delMin();
        SearchNode currentTwinNode = this.twinQueue.delMin();
        
        
        while (true) {
            if (currentNode.searchBoard.isGoal()) { //TODO: refactor isGoal() for performance
                this.solutionNode = currentNode;
                return true;
            } 
            if (currentTwinNode.searchBoard.isGoal()) {
                this.solutionNode = null;
                return false;
            }  
            
            currentNode = this.getNextNode(this.minQueue, currentNode);
            currentTwinNode = this.getNextNode(this.twinQueue, currentTwinNode);
        } 
    }
    
    private SearchNode getNextNode(MinPQ<SearchNode> minQueue, SearchNode currentNode) {
        Queue<Board> neighbors = (Queue<Board>) currentNode.searchBoard.neighbors();
        int moves = currentNode.movesSoFar + 1;
        for(Board board : neighbors) {
            
            if (currentNode.previousNode != null && currentNode.previousNode.searchBoard.equals(board)) continue;               
            
            SearchNode newNode = new SearchNode(board, currentNode, moves);
            minQueue.insert(newNode);
        }
        return minQueue.delMin();
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
    
    private class PriorityComparator implements Comparator<SearchNode> {
        
        public int compare(SearchNode s1, SearchNode s2) {
            int v1 = s1.searchBoard.hamming() + s1.movesSoFar;
            int v2 = s2.searchBoard.hamming() + s2.movesSoFar;
            if      (v1 < v2) return -1;
            else if (v1 > v2) return +1;
            else              return  0;
        } 
    }
    
    private class SearchNode {
        public SearchNode previousNode;
        public Board searchBoard;
        public int movesSoFar;
                
        public SearchNode(Board board, SearchNode previous, int moves) {
            this.searchBoard = board;
            this.previousNode = previous;   
            this.movesSoFar = moves;           
        }
        

        // is this SearchNode equal to x?
        // only compare the two searchBoards for equality 
//        public boolean equals(Object x) {
//            if (x == this) return true;
//            if (x == null) return false;
//            if (x.getClass() != this.getClass()) return false;
//            SearchNode that = (SearchNode) x;
//            return this.hashCode() == that.hashCode();
//        }
        
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
        StdOut.println("It took " + timer.elapsedTime() + " seconds to computer.");
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