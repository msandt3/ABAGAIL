package shared.mazes;

import java.util.Random;
import java.util.Collections;
import java.util.Arrays;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.FileReader;

public class MazeGenerator{
	private final int x;
	private final int y;
	private final int[][] maze;
	private static final Random rand = new Random();
	private String filename = "";

	/**
	* Create a maze of xy dimensions
	**/
	public MazeGenerator(int x, int y) {
		this.x = x;
		this.y = y;
		maze = new int[this.x][this.y];
		generateMaze(0, 0);
		this.filename = new File("").getAbsolutePath() + "/datasets/" + x+"x"+y+"maze.txt";
	}

	public void display() {
		for (int i = 0; i < y; i++) {
			// draw the north edge
			for (int j = 0; j < x; j++) {
				System.out.print((maze[j][i] & 1) == 0 ? "##" : "# ");
			}
			System.out.println("#");
			// draw the west edge
			for (int j = 0; j < x; j++) {
				System.out.print((maze[j][i] & 8) == 0 ? "# " : "  ");
			}
			System.out.println("#");
		}
		// draw the bottom line
		for (int j = 0; j < x; j++) {
			System.out.print("##");
		}
		System.out.println("#");
	}

	public void write() throws IOException{
		PrintWriter pw = new PrintWriter(new FileWriter(filename));

		for (int i = 0; i < y; i++) {
			// draw the north edge
			for (int j = 0; j < x; j++) {
				pw.print((maze[j][i] & 1) == 0 ? "##" : "# ");
			}
			pw.println("#");
			// draw the west edge
			for (int j = 0; j < x; j++) {
				pw.print((maze[j][i] & 8) == 0 ? "# " : "  ");
			}
			pw.println("#");
		}
		// draw the bottom line
		for (int j = 0; j < x; j++) {
			pw.print("##");
		}
		pw.println("#");

		pw.close();

	}

	private void generateMaze(int cx, int cy) {
		DIR[] dirs = DIR.values();
		Collections.shuffle(Arrays.asList(dirs));
		for (DIR dir : dirs) {
			int nx = cx + dir.dx;
			int ny = cy + dir.dy;
			if (between(nx, x) && between(ny, y)
					&& (maze[nx][ny] == 0)) {
				maze[cx][cy] |= dir.bit;
				maze[nx][ny] |= dir.opposite.bit;
				generateMaze(nx, ny);
			}
		}
	}

	private static boolean between(int v, int upper) {
		return (v >= 0) && (v < upper);
	}

	private enum DIR {
		N(1, 0, -1), S(2, 0, 1), E(4, 1, 0), W(8, -1, 0);
		private final int bit;
		private final int dx;
		private final int dy;
		private DIR opposite;
 
		// use the static initializer to resolve forward references
		static {
			N.opposite = S;
			S.opposite = N;
			E.opposite = W;
			W.opposite = E;
		}
 
		private DIR(int bit, int dx, int dy) {
			this.bit = bit;
			this.dx = dx;
			this.dy = dy;
		}
	};
}