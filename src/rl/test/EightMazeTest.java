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

import org.apache.commons.cli.*;

public class  EightMazeTest {

	private static double GAMMA;

	private static CommandLineParser parser = new BasicParser();
    private static Options options = new Options();

	public static void main(String[] args) throws Exception{
		/** Parse Command Line Args **/
		setUpArgs();
		parseArgs(args);

		MazeMarkovDecisionProcess maze = MazeMarkovDecisionProcess.load(new File("").getAbsolutePath() + 
            "/datasets/8x8maze.txt");
		MazeMarkovDecisionProcessVisualization mazeVis =
            new MazeMarkovDecisionProcessVisualization(maze);
		System.out.println(maze);

		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~");

		ValueIteration vi = new ValueIteration(GAMMA,maze);
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

        PolicyIteration pi = new PolicyIteration(GAMMA, maze);
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

	private static void setUpArgs(){
        //set up command line args
        options.addOption("g",true,"set gamma value for value & policy iteration");
    }

    private static void parseArgs(String[] args){
        CommandLine line = null;
        try{
            line = parser.parse(options,args);
        }catch(org.apache.commons.cli.ParseException e){
            System.err.println( "Parsing failed.  Reason: " + e.getMessage() );
        }

        //need to parse these as appropriate data types
        String gamma = line.getOptionValue("g");
        if(gamma == null){
        	GAMMA = 0.95;
        }
        else
        	GAMMA = Double.parseDouble(gamma);
    }
}