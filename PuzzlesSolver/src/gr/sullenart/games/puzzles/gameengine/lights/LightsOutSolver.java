package gr.sullenart.games.puzzles.gameengine.lights;



public class LightsOutSolver {
	 
    private LightsOutBoard lightsOutBoard;
    private int colorsCount;
    @SuppressWarnings("unused")
	private int endColor;

    public LightsOutSolver(LightsOutBoard lightsOutBoard,
                           int colorsCount,
                           int endColor) {
        this.lightsOutBoard = lightsOutBoard;
        this.colorsCount = colorsCount;
        this.endColor = endColor;
       
        init();
    }
  
    public boolean solve() {
        return doSolve();
    }   
   
 
    private int colcount;   // integer, number of columns
    private int rowcount;   // integer, number of rows
    private int imgcount;   // integer, number of states of a tile
    private int [] cells;   // integer[row][col], current states of tiles

    private int [][] mat;    // integer[i][j]
    private int [] cols;   // integer[]
    private int m;      // count of rows of the matrix
    private int n;      // count of columns of the matrix
    private int np;     // count of columns of the enlarged matrix
    private int r;      // minimum rank of the matrix
    private int maxr;   // maximum rank of the matrix 
   
    private void init() {
        imgcount = colorsCount;
        colcount = lightsOutBoard.getSizeX();
        rowcount = lightsOutBoard.getSizeY();
        cells = lightsOutBoard.getBoard();
    }
   
    // --- finite field algebra solver
    private int modulate(int x) {
        // returns z such that 0 <= z < imgcount and x == z (mod imgcount)
        if (x >= 0) return x % imgcount;
        x = (-x) % imgcount;
        if (x == 0) return 0;
        return imgcount - x;
    }
   
    private int gcd(int x, int y) { // call when: x >= 0 and y >= 0
        if (y == 0) return x;
        if (x == y) return x;
        if (x > y)  x = x % y; // x < y
        while (x > 0) {
            y = y % x; // y < x
            if (y == 0) return x;
            x = x % y; // x < y
        }
        return y;
    }
    private int invert(int value) { // call when: 0 <= value < imgcount
        // returns z such that value * z == 1 (mod imgcount), or 0 if no such z
        if (value <= 1) return value;
        int seed = gcd(value,imgcount);
        if (seed != 1) return 0;
        int a = 1, b = 0, x = value;    // invar: a * value + b * imgcount == x
        int c = 0, d = 1, y = imgcount; // invar: c * value + d * imgcount == y
        while (x > 1) {
            int tmp = y / x; //Math.floor(y / x);
            y -= x * tmp;
            c -= a * tmp;
            d -= b * tmp;
            tmp = a;  a = c;  c = tmp;
            tmp = b;  b = d;  d = tmp;
            tmp = x;  x = y;  y = tmp;
        }
        return a;
    }
   
    // --- finite field matrix solver   
    private int a(int i, int j)   { return mat[i][cols[j]]; }
    private void setmat(int i, int j, int val) { mat[i][cols[j]] = modulate(val); }

    private boolean doSolve() {
        int col;
        int row;
        for (int goal = 0; goal < imgcount; goal++) {
            if (solveProblem(goal)) { // found an integer solution
                int [] anscols = new int[n];
                int j;
                for (j = 0; j < n; j++)  {
                    anscols[cols[j]] = j;
                }
                for (col = 0; col < colcount; col++) {
                    for (row = 0; row < rowcount; row++) {
                        int value;
                        j = anscols[row * colcount + col];
                        if (j < r)
                            value = a(j,n);
                        else
                            value = 0;
                        lightsOutBoard.getSolution()[row * colcount + col] = value;
                    }
                }
                return true;
            }
        }
        return false;
    }

    private void initMatrix() {
        maxr = Math.min(m,n);
        mat = new int [colcount*rowcount][];
        for (int col = 0; col < colcount; col++) {
            for (int row = 0; row < rowcount; row++) {
                int i = row * colcount + col;
                int [] line = new int[colcount*rowcount + 1];
                mat[i] = line;
                for (int j = 0; j < n; j++) line[j] = 0;
                line[i] = 1;
                if (col > 0)            line[i - 1]        = 1;
                if (row > 0)            line[i - colcount] = 1;
                if (col < colcount - 1) line[i + 1]        = 1;
                if (row < rowcount - 1) line[i + colcount] = 1;
            }
        }
        cols = new int [np];
        for (int j = 0; j < np; j++)
            cols[j] = j;
    }
   
    private boolean solveProblem(int goal) {
        int size = colcount * rowcount;
        m = size;
        n = size;
        np = n + 1;
        initMatrix();
        for (int col = 0; col < colcount; col++) {
        	for (int row = 0; row < rowcount; row++) {
        		mat[row * colcount + col][n] = 
        			modulate(goal - cells[row * colcount + col]);
        	}
        }
        return sweep();
    }   
   
    public boolean checkNormal() {
        int size = colcount * rowcount;
        m = size;
        n = size;
        np = n + size;
        initMatrix();
        for (int col = 0; col < colcount; col++) {
            for (int row = 0; row < rowcount; row++) {
                int i = row * colcount + col;
                int [] line = mat[i];
                for (int j = n; j < np; j++)  line[j] = 0;
                line[n + i] = 1;
            }
        }
        return sweep();
        //if (sweep())
        //    alert("Always solvable");
        //else alert("Not always solvable ( "
        //    + Math.pow(imgcount,n-r) + " identity patterns )");
    }

    private boolean sweep() {
        for (r = 0; r < maxr; r++) {
            if (!sweepStep()) return false; // failed in founding a solution
            if (r == maxr) break;
        }
        return true; // successfully found a solution
    }
    private boolean sweepStep() {
        int i;
        int j;
        boolean finished = true;
        for (j = r; j < n; j++) {
            for (i = r; i < m; i++) {
                int aij = a(i,j);
                if (aij != 0)  finished = false;
                int inv = invert(aij);
                if (inv != 0) {
                    for (int jj = r; jj < np; jj++)
                        setmat(i,jj, a(i,jj) * inv);
                    doBasicSweep(i,j);
                    return true;
                }
            }
        }
        if (finished) { // we have: 0x = b (every matrix element is 0)
            maxr = r;   // rank(A) == maxr
            for (j = n; j < np; j++)
                for (i = r; i < m; i++)
                    if (a(i,j) != 0)  return false; // no solution since b != 0
            return true;    // 0x = 0 has solutions including x = 0
        }
        //alert("Internal error - contact the author to obtain a full solver");
        return false;   // failed in finding a solution
    }

    private void swap(int [] array, int x, int y) {
        int tmp  = array[x];
        array[x] = array[y];
        array[y] = tmp;
    }

    private void swap2(int [][] array, int x, int y) {
        int [] tmp  = array[x];
        array[x] = array[y];
        array[y] = tmp;
    }    
    
    private void doBasicSweep(int pivoti, int pivotj) {
    	if (r != pivoti) swap2(mat,r,pivoti);
        if (r != pivotj) swap(cols,r,pivotj);
        for (int i = 0; i < m; i++) {
            if (i != r) {
                int air = a(i,r);
                if (air != 0)
                    for (int j = r; j < np; j++)
                        setmat(i,j, a(i,j) - a(r,j) * air);
            }
        }
    }   
 
}