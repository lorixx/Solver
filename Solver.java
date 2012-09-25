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

        this.isSolvable = this.isSolvable();
    }    
    
    public boolean isSolvable() {
        
        while (!this.minQueue.isEmpty())
            this.minQueue.delMin();
        while (!this.twinQueue.isEmpty())
            this.twinQueue.delMin();
        
        SearchNode firstNode = new SearchNode(this.boardToSolve, null, this.moves);
        this.minQueue.insert(firstNode);
        
        SearchNode firstTwinNode = new SearchNode(this.boardToSolve.twin(), null, this.moves);
        this.twinQueue.insert(firstTwinNode);
        
        
        SearchNode minNode = this.minQueue.delMin();
        SearchNode minNodeFromTwinQueue = this.twinQueue.delMin();
        this.solutionNode = null;
        int moveForTwinBoard = 0;
        this.moves = 0;
        Queue<Board> neighbors;
        
        while (true) {
            
            if (minNode.searchBoard.isGoal()) {
                this.solutionNode = minNode;
                return true;
            } else if (minNodeFromTwinQueue.searchBoard.isGoal()) {
                this.moves = -1;
                return false;
            }
            
            // process for original queue
            neighbors = (Queue<Board>) minNode.searchBoard.neighbors();   
            for ( Board neighborBoard : neighbors) {
                if (minNode.previousNode == null) {
                    SearchNode newSearchNode = 
                        new SearchNode(neighborBoard, minNode, this.moves);
                    this.minQueue.insert(newSearchNode);
                } else {
                    if (!neighborBoard.equals(minNode.previousNode.searchBoard)) {
                        SearchNode newSearchNode = 
                        new SearchNode(neighborBoard, minNode, this.moves);
                        this.minQueue.insert(newSearchNode);
                    }                    
                }
            }
            
            // process for twin queue
            neighbors = (Queue<Board>) minNodeFromTwinQueue.searchBoard.neighbors();   
            for ( Board neighborBoard : neighbors) {
                if (minNodeFromTwinQueue.previousNode == null) {
                    SearchNode newSearchNode = 
                        new SearchNode(neighborBoard, minNodeFromTwinQueue, moveForTwinBoard);
                    this.twinQueue.insert(newSearchNode);
                } else {
                    if (!neighborBoard.equals(minNodeFromTwinQueue.previousNode.searchBoard)) {
                        SearchNode newSearchNode = 
                        new SearchNode(neighborBoard, minNodeFromTwinQueue, moveForTwinBoard);
                        this.twinQueue.insert(newSearchNode);
                    }                    
                }
            }
            
            minNode = this.minQueue.delMin();
            this.moves = minNode.movesSoFar + 1; 
            
            minNodeFromTwinQueue = this.twinQueue.delMin();
            moveForTwinBoard = minNodeFromTwinQueue.movesSoFar + 1;
        }        
    }
    
    public int moves() {
        
        return this.isSolvable ? this.moves : -1;
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
        public boolean equals(Object x) {
            if (x == this) return true;
            if (x == null) return false;
            if (x.getClass() != this.getClass()) return false;
            SearchNode that = (SearchNode) x;
            return (this.priorityValue == that.priorityValue) 
                && (this.previousNode.equals(that.previousNode))
                && (this.searchBoard.equals(that.searchBoard));
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
        Solver solver = new Solver(initial);
        
        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);    
        } 
        
//
//        StdOut.println("Minimum number of moves = " + solver.moves());
//        StdOut.println("Minimum number of moves = " + solver.moves());
//        StdOut.println("Minimum number of moves = " + solver.moves());
//        StdOut.println("Minimum number of moves = " + solver.moves());
//
//        StdOut.println("Minimum number of moves = " + solver.moves());
        
        

    }
}