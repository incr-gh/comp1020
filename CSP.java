package termProject;

import java.util.*;
import java.util.stream.Collectors;

class VarIntCP {
	Set<Integer> domain;
	public String name;

	public VarIntCP(String name, Collection<Integer> domain) {
		this.domain = new HashSet<Integer>(domain);
		this.name = name;
	}
	
	public VarIntCP(String name, Integer[] arr) {
		this(name, Arrays.asList(arr));
	}
	public VarIntCP(String name, int min, int max) {
		this.domain = new HashSet<>();
		for (int i = min; i <= max; i++) {
			this.domain.add(i);
		}

		this.name = name;
	}
	
	public VarIntCP(String name, int v) {
		this.domain = new HashSet<>();
		this.domain.add(v);
		this.name= name;
	}
	public VarIntCP(VarIntCP x) {
		this.domain = new HashSet<>(x.domain);
		this.name = x.name;
	}
	
	public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        VarIntCP var= (VarIntCP) o;
        return name==var.name;
	}
	
	public String toString() {
		return name + domain.toString();
	}
	
	@Override
	public int hashCode() {
	    return name.hashCode();
	}

}

abstract class Constraint{
	VarIntCP x;
	VarIntCP y;
	int a;
	int b;
	int v;

	abstract public boolean valid(Map<VarIntCP, Integer> assignments);

	abstract public Constraint clone();

	public Constraint(int a, VarIntCP x, int b, VarIntCP y, int v) {
		this.a = a;
		this.x = x;
		this.b = b;
		this.y = y;
		this.v = v;
	}

	public Constraint(Constraint c) {
		this(c.getA(), c.getX(), c.getB(), c.getY(), c.getV());
	}

	public Constraint(VarIntCP x, VarIntCP y, int v) {
		this(1, x, 1, y, v);
	}

	public Constraint(VarIntCP x, VarIntCP y) {
		this(1, x, 1, y, 0);
	}

	public VarIntCP getX() {
		return new VarIntCP(this.x);
	}

	public VarIntCP getY() {
		return new VarIntCP(this.y);
	}

	public int getA() {
		return a;
	}

	public int getB() {
		return b;
	}

	public int getV() {
		return v;
	}
	
	@Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Constraint con= (Constraint) o;
        return x.equals(con.getX()) && y.equals(con.getY()) && a == con.getA() && 
        		b == con.getB() && v==con.getV() ;
    }
}

class Leq extends Constraint {
	public Leq(int a, VarIntCP x, int b, VarIntCP y, int v) {
		super(a, x, b, y, v);
	}

	public Leq(VarIntCP x, VarIntCP y, int v) {
		super(x, y, v);
	}

	public Leq(VarIntCP x, VarIntCP y) {
		super(x, y);
	}

	public Leq(Constraint c) {
		super(c);
	}

	@Override
	public Leq clone() {
		return new Leq(this);
	}

	@Override
	public boolean valid(Map<VarIntCP, Integer> assignments) {
		assignments = new HashMap<VarIntCP, Integer>(assignments);
		Set<VarIntCP> names = assignments.keySet();
		VarIntCP x = getX();
		VarIntCP y = getY();
		if (!names.contains(x) || !names.contains(y)) {
			return true;
		}

		return a * assignments.get(x) <= b * assignments.get(y) + v;
	}
}

class Eq extends Constraint {

	public Eq(Constraint c) {
		super(c);

	}

	public Eq(int a, VarIntCP x, int b, VarIntCP y, int v) {
		super(a, x, b, y, v);

	}

	public Eq(VarIntCP x, VarIntCP y, int v) {
		super(x, y, v);

	}

	public Eq(VarIntCP x, VarIntCP y) {
		super(x, y);
	}

	@Override
	public Eq clone() {
		return new Eq(this);
	}

	@Override
	public boolean valid(Map<VarIntCP, Integer> assignments) {
		assignments = new HashMap<VarIntCP, Integer>(assignments);
		Set<VarIntCP> names = assignments.keySet();
		VarIntCP x = getX();
		VarIntCP y = getY();
		if (!names.contains(x) || !names.contains(y)) {
			return true;
		}


		return a * assignments.get(x) == b * assignments.get(y) + v;
	}
}

class Neq extends Constraint {
	// x != y + v

	public Neq(Constraint c) {
		super(c);
	}

	public Neq(int a, VarIntCP x, int b, VarIntCP y, int v) {
		super(a, x, b, y, v);
	}

	public Neq(VarIntCP x, VarIntCP y, int v) {
		super(x, y, v);
	}

	public Neq(VarIntCP x, VarIntCP y) {
		super(x, y);
	}

	@Override
	public Neq clone() {
		return new Neq(this);
	}

	@Override
	public boolean valid(Map<VarIntCP, Integer> assignments) {
		assignments = new HashMap<VarIntCP, Integer>(assignments);
		Set<VarIntCP> names = assignments.keySet();
		VarIntCP x = getX();
		VarIntCP y = getY();
		if (!names.contains(x) || !names.contains(y)) {
			return true;
		}


		return a * assignments.get(x) != b * assignments.get(y) + v;
	}

}

class CPModel {
	ArrayList<Constraint> constraints;

	public CPModel() {
		this.constraints = new ArrayList<Constraint>();
	}
	
	public CPModel(Collection<Constraint> constraints) {
		this.constraints = new ArrayList<Constraint>(constraints);
	}

	public CPModel(CPModel cp) {
		this();
		for (Constraint c : cp.getConstraints()) {
			this.addConstraint(c.clone());
		}
	}

	public void addConstraint(Constraint c) {
		constraints.add(c);
	}
	
	public void addAllConstraint(List<Constraint> cs) {
		constraints.addAll(cs);
	}

	public List<VarIntCP> getVariables() {
		HashSet<String> hash = new HashSet<>();
		ArrayList<VarIntCP> variables = new ArrayList<VarIntCP>();
		for (Constraint c : constraints) {
			VarIntCP x = c.getX();
			VarIntCP y = c.getY();
			if (!hash.contains(x.name))
				variables.add(x);
			if (!hash.contains(y.name))
				variables.add(y);
			hash.add(x.name);
			hash.add(y.name);
		}
		// System.out.println(variables);
		return variables;
	}

	public ArrayList<Constraint> getConstraints() {
		return constraints;
	}
	
	public List<Constraint> allDifferentConstraint() {
		return allDifferentConstraint(getVariables());
	}
	
	static public List<Constraint> allDifferentConstraint(List<VarIntCP> vars){
		List<Constraint> alldiff= new ArrayList<Constraint>();
		for (int i=0; i<vars.size(); i++) {
			for (int j=i+1; j<vars.size(); j++) {
				alldiff.add(new Neq(vars.get(i), vars.get(j)));
			}
		}
		return alldiff;
	}
	public boolean validAssignment(Map<VarIntCP, Integer> assignments) {
		boolean valid = true;
		for (Constraint c : constraints) {
			valid &= c.valid(assignments);
		}
		return valid;
	}
	
	
	public void close() {
		// this.constraints.clear();
	}

}

class CPSearch {
	CPModel m;
	
	public CPSearch() {
	}

	public List<Map<VarIntCP, Integer>> search(CPModel m) {
		this.m = m;
		return search(m.getVariables(), new HashMap<VarIntCP, Integer>());
	}

	private List<Map<VarIntCP, Integer>> search(List<VarIntCP> variables, Map<VarIntCP, Integer> assignments) {
		List<Map<VarIntCP, Integer>> results= new ArrayList<Map<VarIntCP, Integer>>();
		if (assignments.size() >= variables.size()) {
			System.out.printf("Assignment found: %s\n", assignments.toString());
			results.add(assignments);
			return results;
		}

		List<VarIntCP> unassigned = getUnassigned(variables, assignments);
		VarIntCP mrv = MRV(unassigned);
		for (int possibleValue : mrv.domain) {
			List<VarIntCP> propagatedVariables = propagateConstraints(variables, mrv, possibleValue);
			assignments.put(new VarIntCP(mrv.name, possibleValue), possibleValue);
			results.addAll(search(propagatedVariables, assignments));
			assignments.remove(mrv);
		}
		return results;
	}

	private VarIntCP MRV(List<VarIntCP> variables) {
		VarIntCP var = null;
		for (VarIntCP i : variables)
			if (var == null || i.domain.size() < var.domain.size())
				var = i;
		return var;
	}

	private List<VarIntCP> getUnassigned(List<VarIntCP> variables, Map<VarIntCP, Integer> assignments) {
		Set<VarIntCP> assigned = assignments.keySet();
		List<VarIntCP> unassigned = new ArrayList<VarIntCP>();
		for (VarIntCP var : variables)
			if (!assigned.contains(var))
				unassigned.add(var);
		return unassigned;
	}

	private List<VarIntCP> propagateConstraints(List<VarIntCP> variables, VarIntCP assigned, int possibleValue) {
		List<VarIntCP> propagatedVars = new ArrayList<VarIntCP>();
		Map<VarIntCP, Integer> assignments = new HashMap<VarIntCP, Integer>();
		for (VarIntCP var : variables) {
			VarIntCP n;
			if (var.equals(assigned)) {
				n = new VarIntCP(assigned.name, possibleValue);
			} else {
				n = new VarIntCP(var);
				for (int posValueN : var.domain) {
					assignments.put(assigned, possibleValue);
					assignments.put(var, posValueN);
					if (!m.validAssignment(assignments))
						n.domain.remove(posValueN);
					assignments.clear();
				}
			}
			propagatedVars.add(n);
		}
		return propagatedVars;
	}
}

public class CSP {
	/*
	 * Demo Constraint Satisfaction Problem : - Variables X , Y , Z with domain ( X
	 * ) = {1 ,... ,10} , domain ( Y ) = ←- ,... ,7} , domain ( Z ) = {4 ,... ,10}
	 *
	 * - Constraints : X != Y + 3 Y <= Z - 3 X = Z - 1
	 */
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		System.out.println("Problem 1. solutions");
		Problem1();
        //System.out.println("Time taken to solve Problem 1: " + String.valueOf(System.currentTimeMillis() - start)+"ms");
        start = System.currentTimeMillis();
		System.out.println("Problem 2. solutions");
		Problem2();
        //System.out.println("Time taken to solve Problem 2: " + String.valueOf(System.currentTimeMillis() - start)+"ms");
		System.out.println("Sudoku. solutions");
		//Sudoku();
		CPModel sudoku= SudokuModel(); 
	}
	public static void example() {
		VarIntCP X = new VarIntCP("X", 1, 10);
		VarIntCP Y = new VarIntCP("Y", 1, 7);
		VarIntCP Z = new VarIntCP("Z", 4, 10);
		CPModel m = new CPModel();
		m.addConstraint(new Neq(X, Y, 3));
		m.addConstraint(new Leq(Y, Z, -3));
		m.addConstraint(new Eq(X, Z, -1));
		// System.out.println(new Neq(X, Y, 5).clone());
		CPModel cp = new CPModel(m);
		m.close();
		CPSearch se = new CPSearch();
		se.search(m);
	}
/*
Problem 1.
The goal is to schedule three tasks (Task1, Task2, and Task3) within a set of four available time 
slots (1, 2, 3, and 4) such that no two tasks overlap in time and specific constraints for each 
task's allowable time slots are met.
 */
	public static void Problem1() {
		VarIntCP Task1= new VarIntCP("T1", 1,3);
		VarIntCP Task2= new VarIntCP("T2", 3,4);
		VarIntCP Task3= new VarIntCP("T3", new Integer[] {1,3});
		CPModel m = new CPModel();
		m.addConstraint(new Neq(Task1, Task2));
		m.addConstraint(new Neq(Task2, Task3));
		m.addConstraint(new Neq(Task1, Task3));
		// System.out.println(new Neq(X, Y, 5).clone());
		CPModel cp = new CPModel(m);
		m.close();
		CPSearch se = new CPSearch();
		se.search(m);
	}
/*
Problem 2:
The goal is to schedule three lectures (LectureA, LectureB, and LectureC) within a set of
four available time slots and two available rooms, such that no two lectures overlap in the same 
room and specific dependency constraints between the lectures are met.
*/
	public static void Problem2() {
		VarIntCP LecA= new VarIntCP("A", 1,8);// (9,10) (10,11) ... corresponds to 1, 3, 5....
		VarIntCP LecB= new VarIntCP("B", 1,8);// If room 1 +0, else if room 2 +1. Room 1 (9,10) = 1; Room 2 (9,10) = 2 and so on
		VarIntCP LecC1= new VarIntCP("C1", 1,8);
		VarIntCP LecC2= new VarIntCP("C2", 1,8);
		CPModel m = new CPModel();
		m.addConstraint(new Neq(LecA, LecB));
		m.addConstraint(new Neq(LecC1, LecB));
		m.addConstraint(new Neq(LecA, LecC1));
		m.addConstraint(new Neq(LecC2, LecB));
		m.addConstraint(new Neq(LecA, LecC2));
		m.addConstraint(new Leq(LecA, LecB));
		m.addConstraint(new Leq(LecB, LecC1));
		m.addConstraint(new Eq(LecC2, LecC1, 2)); //Same room next hour
		// System.out.println(new Neq(X, Y, 5).clone());
		CPModel cp = new CPModel(m);
		m.close();
		CPSearch se = new CPSearch();
		se.search(m);
	}
	
	public static CPModel SudokuModel(int[][] board) {
		VarIntCP[][] mat= new VarIntCP[board.length][board.length];
		for (int i=0; i<board.length;i++) {
			for (int j=0; j<board.length; j++) {
				String nm= String.valueOf(i)+String.valueOf(j);
				if (1<= board[j][i] && 9<=board[j][i]) mat[i][j]= new VarIntCP(nm, board[j][i]);
				mat[i][j]= new VarIntCP(nm, 1, 9);
			}
		}
		
		List<Constraint> constraints= new ArrayList<Constraint>();
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				List<VarIntCP> vars= new ArrayList<VarIntCP>();
				for (int k=0; k<9; k++) {
					vars.add(mat[3*i+k%3][3*j+k/3]);
				}
				constraints.addAll(CPModel.allDifferentConstraint(vars));
			}
		}
		
		for (int i=0; i<9; i++) {
			List<VarIntCP> vars= new ArrayList<VarIntCP>();
			for (int j=0; j<9; j++) {
				vars.add(mat[i][j]);
			}
			constraints.addAll(CPModel.allDifferentConstraint(vars));
		}
		
		for (int j=0; j<9; j++) {
			List<VarIntCP> vars= new ArrayList<VarIntCP>();
			for (int i=0; i<9; i++) {
				vars.add(mat[i][j]);
			}
			constraints.addAll(CPModel.allDifferentConstraint(vars));
		}
		
		CPModel mod= new CPModel(constraints);
		return mod;
	}
	public static CPModel SudokuModel() {
		int[][] board= new int[9][9];
		return SudokuModel(board);
	}
}