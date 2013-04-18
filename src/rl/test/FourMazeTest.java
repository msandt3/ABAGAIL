package rl.test;

import rl.EpsilonGreedyStrategy;
import rl.MazeMarkovDecisionProcess;
import rl.MazeMarkovDecisionProcessVisualization;
import rl.Policy;
import rl.PolicyIteration;
import rl.QLambda;
import rl.SarsaLambda;
import rl.ValueIteration;
import shared.FixedIterationTrainer;
import shared.ThresholdTrainer;
import shared.mazes.MazeGenerator;

import java.io.File;

public class  FourMazeTest {


	public static void main(String[] args) throws Exception{
		MazeMarkovDecisionProcess maze = MazeMarkovDecisionProcess.load(new File("").getAbsolutePath() + 
            "/datasets/4x4maze.txt");
		MazeMarkovDecisionProcessVisualization mazeVis =
            new MazeMarkovDecisionProcessVisualization(maze);
		System.out.println(maze);

		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~");

		ValueIteration vi = new ValueIteration(0.95,maze);
		ThresholdTrainer tt = new ThresholdTrainer(vi);

		/** Start Value Iteration **/
		System.out.println("Starting value iteration");
		long starttime = System.currentTimeMillis();
		tt.train();
		long finishtime = System.currentTimeMillis();
		Policy p = vi.getPolicy();

		System.out.println("Value iteration learned : " + p);
        System.out.println("in " + tt.getIterations() + " iterations");
        System.out.println("and " + (finishtime - starttime) + " ms");

        System.out.println(mazeVis.toString(p));

        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        PolicyIteration pi = new PolicyIteration(.95, maze);
        tt = new ThresholdTrainer(pi);
        starttime = System.currentTimeMillis();
        tt.train();
        finishtime = System.currentTimeMillis();
        p = pi.getPolicy();
        System.out.println("Policy iteration learned : " + p);
        System.out.println("in " + tt.getIterations() + " iterations");
        System.out.println("and " + (finishtime - starttime) + " ms");
        System.out.println(mazeVis.toString(p));

	}
}