package opt.test;

import dist.*;
import opt.*;
import opt.example.*;
import opt.ga.*;
import shared.*;
import func.nn.backprop.*;


import java.util.*;
import java.io.*;
import java.text.*;

import org.apache.commons.cli.*;
/**
* Implementation of RHC, Simulated Annealing, and Genetic Algorithms for use in determining
* neural network weights. Binary classification problem.
* @Author - Michael Sandt - based on original class AbaloneTest by Hannah Lau
**/
public class WineTest{
	//set up our data set
	private static DataSetReader reader = new DataSetReader("datasets/wine.arff");
	private static DataSet set;

	private static int inputLayer = 11, hiddenLayer = 5, outputLayer = 1;
    private static int trainingIterations,population,mutation,mate,experiments;
    private static double temp,cool;
    private static BackPropagationNetworkFactory factory = new BackPropagationNetworkFactory();

    private static ErrorMeasure measure = new SumOfSquaresError();
    //arrays for networks and their respective optimization problems
    private static BackPropagationNetwork networks[] = new BackPropagationNetwork[3];
    private static NeuralNetworkOptimizationProblem[] nnop = new NeuralNetworkOptimizationProblem[3];

    private static OptimizationAlgorithm[] oa = new OptimizationAlgorithm[3];
    private static String[] oaNames = {"RHC", "SA", "GA"};
    private static String results = "";
    private static String avgResults = "";

    private static DecimalFormat df = new DecimalFormat("0.000");

    private static CommandLineParser parser = new BasicParser();
    private static Options options = new Options();

    private static double[] avgCorrect = new double[3];
    private static double[] avgIncorrect = new double[3];
    private static double[] avgTrain = new double[3];
    private static double[] avgTest = new double[3];


    public static void main(String[] args){
    	//read in data set file
        setUpArgs();
        parseArgs(args);
    	try{
    		set = reader.readarff();
    	}catch(Exception e){
    		System.out.println("Error reading in file, make sure the"+
    			"arff file is in the appropriate directory");
    		e.printStackTrace();
    	}
        for(int k=0; k<experiments; k++){
    	//set up the networks and optimization problems
            results = "";
        	for(int i = 0; i < oa.length; i++) {
                networks[i] = factory.createClassificationNetwork(
                    new int[] {inputLayer, hiddenLayer, outputLayer});
                nnop[i] = new NeuralNetworkOptimizationProblem(set, networks[i], measure);
            }
            //set up the optimization algorithms
            oa[0] = new RandomizedHillClimbing(nnop[0]);
            oa[1] = new SimulatedAnnealing(temp, cool, nnop[1]);
            oa[2] = new StandardGeneticAlgorithm(population, mate, mutation, nnop[2]);
            //train the networks and run them on the data set
            for(int i = 2; i < oa.length; i++) {
                double start = System.nanoTime(), end, trainingTime, testingTime, correct = 0, incorrect = 0;
                train(oa[i], networks[i], oaNames[i]); //trainer.train();
                end = System.nanoTime();
                trainingTime = end - start;
                trainingTime /= Math.pow(10,9);

                Instance optimalInstance = oa[i].getOptimal();
                networks[i].setWeights(optimalInstance.getData());

                double predicted, actual;
                start = System.nanoTime();
                for(int j = 0; j < set.size(); j++) {
                    networks[i].setInputValues(set.get(j).getData());
                    networks[i].run();

                    predicted = Double.parseDouble(set.get(j).getLabel().toString());
                    actual = Double.parseDouble(networks[i].getOutputValues().toString());

                    double trash = Math.abs(predicted - actual) < 0.5 ? correct++ : incorrect++;

                }
                end = System.nanoTime();
                testingTime = end - start;
                testingTime /= Math.pow(10,9);

                avgCorrect[i] += correct;
                avgIncorrect[i] += incorrect;
                avgTrain[i] += trainingTime;
                avgTest[i] += testingTime;

                results +=  "\nResults for " + oaNames[i] + ": \nCorrectly classified " + correct + " instances." +
                            "\nIncorrectly classified " + incorrect + " instances.\nPercent correctly classified: "
                            + df.format(correct/(correct+incorrect)*100) + "%\nTraining time: " + df.format(trainingTime)
                            + " seconds\nTesting time: " + df.format(testingTime) + " seconds\n";
            }

            System.out.println(results);
        }
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        //print out average results from all experiments
        for(int q=2; q<3; q++){
            avgResults = "";
            String avgCor = df.format(avgCorrect[q]/(double)experiments);
            String avgIncor = df.format(avgIncorrect[q]/(double)experiments);
            String avgPctCor = df.format(avgCorrect[q]/(avgCorrect[q]+avgIncorrect[q])*100);
            String avgTrainTime = df.format(avgTrain[q]/(double)experiments);
            String avgTestTime = df.format(avgTest[q]/(double)experiments);
            avgResults +=  "\nAvg Results for " + oaNames[q] + ": \nAvg Correctly classified " + avgCor + " instances." +
                            "\nAvg Incorrectly classified " + avgIncor + " instances.\nPercent correctly classified: "
                            + avgPctCor + "%\nTraining time: " + avgTrainTime
                            + " seconds\nTesting time: " + avgTestTime + " seconds\n";

            System.out.println(avgResults);
        }
    }


    private static void train(OptimizationAlgorithm oa, BackPropagationNetwork network, String oaName) {
        System.out.println("\nError results for " + oaName + "\n---------------------------");

        for(int i = 0; i < trainingIterations; i++) {
            oa.train();

            double error = 0;
            for(int j = 0; j < set.size(); j++) {
                network.setInputValues(set.get(j).getData());
                network.run();

                Instance output = set.get(j).getLabel(), example = new Instance(network.getOutputValues());
                example.setLabel(new Instance(Double.parseDouble(network.getOutputValues().toString())));
                error += measure.value(output, example);
            }

            //System.out.println(df.format(error));
        }
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
            trainingIterations = 1000;
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
}