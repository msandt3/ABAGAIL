package rl.test;

import rl.EpsilonGreedyStrategy;
import rl.ExplorationStrategy;
import rl.DecayingEpsilonGreedyStrategy;
import rl.GreedyStrategy;
import rl.MazeMarkovDecisionProcess;
import rl.MazeMarkovDecisionProcessVisualization;
import rl.Policy;
import rl.QLambda;
import shared.FixedIterationTrainer;
import shared.ThresholdTrainer;
import shared.ConvergenceTrainer;
import shared.mazes.MazeGenerator;

import java.io.File;

import org.apache.commons.cli.*;

public class QLearningTest{


	private static int ITERATIONS;
	private static double LAMBDA,GAMMA,ALPHA,DECAY,EPSILON,EDECAY;
	private static ExplorationStrategy STRATEGY;

	private static CommandLineParser parser = new BasicParser();
    private static Options options = new Options();
    private static String strategy;


	public static void main(String[] args) throws Exception{

		setUpArgs();
		parseArgs(args);



		System.out.println("Setting up 4x4 maze");
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		MazeMarkovDecisionProcess maze = MazeMarkovDecisionProcess.load(new File("").getAbsolutePath()+"/datasets/4x4maze.txt");
		MazeMarkovDecisionProcessVisualization mazeVis = new MazeMarkovDecisionProcessVisualization(maze);
		System.out.println(maze);

		QLambda ql;
		if(strategy == null)
			ql = new QLambda(LAMBDA,GAMMA,ALPHA,DECAY,new GreedyStrategy(), maze);
        else if(strategy == "1")
        	ql = new QLambda(LAMBDA,GAMMA,ALPHA,DECAY,new EpsilonGreedyStrategy(EPSILON), maze);
        else if(strategy == "2")
        	ql = new QLambda(LAMBDA,GAMMA,ALPHA,DECAY
        		,new DecayingEpsilonGreedyStrategy(EPSILON,EDECAY), maze);
        else
        	ql = new QLambda(LAMBDA,GAMMA,ALPHA,DECAY,new  EpsilonGreedyStrategy(EPSILON), maze);

		
        FixedIterationTrainer fit = new FixedIterationTrainer(ql, ITERATIONS);
        ThresholdTrainer tt = new ThresholdTrainer(ql);
        ConvergenceTrainer ct = new ConvergenceTrainer(ql);
        long startTime = System.currentTimeMillis();
        fit.train();
        Policy p = ql.getPolicy();
        long finishTime = System.currentTimeMillis();
        System.out.println("Q lambda learned : " + p);
        System.out.println("in " + ITERATIONS + " iterations");
        System.out.println("and " + (finishTime - startTime) + " ms");
        System.out.println("Acquiring " + ql.getTotalReward() + " reward");
        System.out.println(mazeVis.toString(p));
	}

	private static void setUpArgs(){
        //set up command line args
        options.addOption("l",true,"set lambda value for value & q learning");
        options.addOption("g",true,"set gamma value for value & q learning");
        options.addOption("a",true,"set alpha value for value & q learning");
        options.addOption("d",true,"set decay value for value & q learning");
        options.addOption("s",true,"set exploration value for value & q learning");
        options.addOption("e",true,"set epsilon for exploration strategy");
        options.addOption("r",true,"set decay value for exploration strategy -- if needed");
        options.addOption("i",true,"set # of iterations to train for");
    }

    private static void parseArgs(String[] args){
        CommandLine line = null;
        try{
            line = parser.parse(options,args);
        }catch(org.apache.commons.cli.ParseException e){
            System.err.println( "Parsing failed.  Reason: " + e.getMessage() );
        }

        //need to parse these as appropriate data types
        String lambda = line.getOptionValue("l");
        String gamma = line.getOptionValue("g");
        String alpha = line.getOptionValue("a");
        String decay = line.getOptionValue("d");
        strategy = line.getOptionValue("s");
        String epsilon = line.getOptionValue("e");
        String edecay = line.getOptionValue("r");
        String iterations = line.getOptionValue("i");
 		

 		if(iterations == null)
 			ITERATIONS = 5000;
 		else
 			ITERATIONS = Integer.parseInt(iterations);

        if(lambda == null)
        	LAMBDA = 0.5;
        else
        	LAMBDA = Double.parseDouble(lambda);

        if(gamma == null)
        	GAMMA = 0.95;
        else
        	GAMMA = Double.parseDouble(gamma);

        if(alpha == null)
        	ALPHA = 0.2;
        else
        	ALPHA = Double.parseDouble(alpha);

        if(decay == null)
        	DECAY = 1.0;
        else
        	DECAY = Double.parseDouble(decay);

        if(epsilon == null)
        	EPSILON = 0.3;
        else
        	EPSILON = Double.parseDouble(epsilon);

        if(edecay == null)
        	EDECAY = 0.3;
        else
        	EDECAY = Double.parseDouble(edecay);
    }

}