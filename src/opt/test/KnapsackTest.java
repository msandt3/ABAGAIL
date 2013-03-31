package opt.test;

import java.util.Arrays;
import java.util.Random;

import dist.DiscreteDependencyTree;
import dist.DiscreteUniformDistribution;
import dist.Distribution;

import opt.DiscreteChangeOneNeighbor;
import opt.EvaluationFunction;
import opt.GenericHillClimbingProblem;
import opt.HillClimbingProblem;
import opt.NeighborFunction;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.example.*;
import opt.ga.CrossoverFunction;
import opt.ga.DiscreteChangeOneMutation;
import opt.ga.GenericGeneticAlgorithmProblem;
import opt.ga.GeneticAlgorithmProblem;
import opt.ga.MutationFunction;
import opt.ga.StandardGeneticAlgorithm;
import opt.ga.UniformCrossOver;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.FixedIterationTrainer;

import org.apache.commons.cli.*;

/**
 * A test of the knap sack problem
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class KnapsackTest {
    /** Random number generator */
    private static final Random random = new Random();
    /** The number of items */
    private static final int NUM_ITEMS = 40;
    /** The number of copies each */
    private static final int COPIES_EACH = 4;
    /** The maximum weight for a single element */
    private static final double MAX_WEIGHT = 50;
    /** The maximum volume for a single element */
    private static final double MAX_VOLUME = 50;
    /** The volume of the knapsack */
    private static final double KNAPSACK_VOLUME = 
         MAX_VOLUME * NUM_ITEMS * COPIES_EACH * .4;


    private static CommandLineParser parser = new BasicParser();
    private static Options options = new Options();

    private static int trainingIterations,population,mutation,mate,experiments;
    private static double temp,cool;
    private static double[] scores = new double[4];
    private static double[] times = new double[4];
    private static double start = 0, end = 0, trainTime = 0;
    private static String[] oanames = {"RHC","SA","GA","MIMIC"};
    /**
     * The test main
     * @param args ignored
     */
    public static void main(String[] args) {
        setUpArgs();
        parseArgs(args);
        for(int q=0; q<experiments; q++){
            System.out.println("Experiment "+q);
            int[] copies = new int[NUM_ITEMS];
            Arrays.fill(copies, COPIES_EACH);
            double[] weights = new double[NUM_ITEMS];
            double[] volumes = new double[NUM_ITEMS];
            for (int i = 0; i < NUM_ITEMS; i++) {
                weights[i] = random.nextDouble() * MAX_WEIGHT;
                volumes[i] = random.nextDouble() * MAX_VOLUME;
            }
            int[] ranges = new int[NUM_ITEMS];
            Arrays.fill(ranges, COPIES_EACH + 1);
            EvaluationFunction ef = new KnapsackEvaluationFunction(weights, volumes, KNAPSACK_VOLUME, copies);
            Distribution odd = new DiscreteUniformDistribution(ranges);
            NeighborFunction nf = new DiscreteChangeOneNeighbor(ranges);
            MutationFunction mf = new DiscreteChangeOneMutation(ranges);
            CrossoverFunction cf = new UniformCrossOver();
            Distribution df = new DiscreteDependencyTree(.1, ranges); 
            HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
            GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);
            ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);
            

            System.out.println("Running RHC");
            RandomizedHillClimbing rhc = new RandomizedHillClimbing(hcp);      
            FixedIterationTrainer fit = new FixedIterationTrainer(rhc, trainingIterations);
            start = System.nanoTime();
            fit.train();
            end = System.nanoTime();


            trainTime = end - start;    
            trainTime /= Math.pow(10,9);
            times[0] += trainTime;

            scores[0] += ef.value(rhc.getOptimal());
            System.out.println("Score for iteration "+q+" : "+ef.value(rhc.getOptimal()));
            //System.out.println(ef.value(rhc.getOptimal()));
            
            System.out.println("Running SA");
            SimulatedAnnealing sa = new SimulatedAnnealing(temp, cool, hcp);
            fit = new FixedIterationTrainer(sa, trainingIterations);
            start = System.nanoTime();
            fit.train();
            end = System.nanoTime();

            trainTime = end - start;    
            trainTime /= Math.pow(10,9);
            times[1] += trainTime;

            scores[1] += ef.value(sa.getOptimal());
            System.out.println("Score for iteration "+q+" : "+ef.value(sa.getOptimal()));
            //System.out.println(ef.value(sa.getOptimal()));
            
            System.out.println("Running GA");
            StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(population, mate, mutation, gap);
            fit = new FixedIterationTrainer(ga, 1000);
            start = System.nanoTime();
            fit.train();
            end = System.nanoTime();

            trainTime = end - start;    
            trainTime /= Math.pow(10,9);
            times[2] += trainTime;

            scores[2] += ef.value(ga.getOptimal());
            System.out.println("Score for iteration "+q+" : "+ef.value(ga.getOptimal()));
            //System.out.println(ef.value(ga.getOptimal()));
            
            MIMIC mimic = new MIMIC(200, 100, pop);
            fit = new FixedIterationTrainer(mimic, 1000);
            start = System.nanoTime();
            fit.train();
            end = System.nanoTime();
            
            trainTime = end - start;    
            trainTime /= Math.pow(10,9);
            times[3] += trainTime;

            scores[3] += ef.value(mimic.getOptimal());
            System.out.println("Score for iteration "+q+" : "+ef.value(mimic.getOptimal()));
            //System.out.println(ef.value(mimic.getOptimal()));
        }

        printResults();

    }


    private static void setUpArgs(){
        //set up command line args
        options.addOption("n",true,"set number of training iterations -- default 1000");
        //args for GA
        options.addOption("p",true,"set genetic algorithm population size -- default 200");
        options.addOption("m",true,"set number of instances to mate each iteration -- default 100");
        options.addOption("u",true,"set number of instance to mutate each iteration -- default 10");
        //args for SA
        options.addOption("t",true,"set the starting temperature for simulated annealing -- default 1E11");
        options.addOption("c",true,"set the cooling rate for simulated annealing -- default 0.95");
        options.addOption("e",true,"the number of experiments to perform");
    }

    private static void parseArgs(String[] args){
        CommandLine line = null;
        try{
            line = parser.parse(options,args);
        }catch(org.apache.commons.cli.ParseException e){
            System.err.println( "Parsing failed.  Reason: " + e.getMessage() );
        }

        //need to parse these as appropriate data types
        String ti = line.getOptionValue("n");
        String pop = line.getOptionValue("p");
        String ma = line.getOptionValue("m");
        String mut = line.getOptionValue("u");
        String t = line.getOptionValue("t");
        String c = line.getOptionValue("c");
        String exp = line.getOptionValue("e");


        if(ti == null)
            trainingIterations = 20000;
        else
            trainingIterations = Integer.parseInt(ti);

        if(pop == null)
            population = 200;
        else
            population = Integer.parseInt(pop);

        if(ma == null)
            mate = 100;
        else
            mate = Integer.parseInt(ma);

        if(mut == null)
            mutation = 10;
        else
            mutation = Integer.parseInt(mut);

        if(t == null)
            temp = (Double)1E11;
        else
            temp = Double.parseDouble(t);

        if(c == null)
            cool = (Double)0.95;
        else
            cool = Double.parseDouble(c);

        if(exp == null)
            experiments = 1;
        else
            experiments = Integer.parseInt(exp);
    }

    private static void printResults(){
        String results = "";
        for(int i=0; i<oanames.length; i++){
            results += "~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n";
            results += "Results for "+oanames[i]+"\n"+
                        "Avg time "+(times[i]/(double)experiments)+"\n"+
                        "Avg score "+(scores[i]/(double)experiments)+"\n";

            results += "~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n";
        }
        System.out.println(results);
    }

}
