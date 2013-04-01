package project3;

import java.util.Arrays;
import java.io.File;
import shared.DataSet;
import shared.Instance;
import shared.filt.IndependentComponentAnalysis;
import util.linalg.Matrix;
import shared.DataSetWriter;
import shared.DataSetDescription;
import shared.reader.ArffDataSetReader;
import shared.reader.DataSetReader;
import shared.filt.LabelSplitFilter;
import shared.reader.DataSetLabelBinarySeperator;
import util.linalg.RectangularMatrix;
import shared.EuclideanDistance;
import func.KMeansClusterer;

import org.apache.commons.cli.*;


public class ICATest{
	private static final String W_HEADER = "datasets/wineheader.txt";
	private static final String S_HEADER = "datasets/spamheader.txt";
	private static final String WINE_RES = "datasets/results/wine_ica_results";
	private static final String SPAM_RES = "datasets/results/spam_ica_results";

	private static int WINE_K;
	private static int SPAM_K;

	private static double W_PER;
	private static double S_PER;

	private static CommandLineParser parser = new BasicParser();
    private static Options options = new Options();

	public static void main(String[] args) throws Exception{

		setUpArgs();
        parseArgs(args);

		//create a data set reader
		//System.out.println("Reading wine set");
		DataSetReader wine_dsr = new ArffDataSetReader(new File("").getAbsolutePath() + 
			"/datasets/wine.arff");
		DataSet wine_set = wine_dsr.read();
		//System.out.println("Reading spam set");
		DataSetReader spam_dsr = new ArffDataSetReader(new File("").getAbsolutePath() + 
			"/datasets/spambase.arff");
		DataSet spam_set = spam_dsr.read();


		LabelSplitFilter lsf = new LabelSplitFilter();
		//filter the datasets
		lsf.filter(wine_set);
		lsf.filter(spam_set);



		//RUN ICA ON WINE
		System.out.println("Beginning PCA on wine data set");
		System.out.println("~~~~~~~~~~~ WINE STATS - Before Randomizing ~~~~~~~~~~~~");
		System.out.println(new DataSetDescription(wine_set));


        System.out.println("\n\n\n");
        System.out.println("~~~~~~~~~~~ WINE STATS - Before ICA ~~~~~~~~~~~~");
        System.out.println(new DataSetDescription(wine_set));


		System.out.println("\n\n\n");
        System.out.println("~~~~~~~~~~~ WINE STATS - After ICA ~~~~~~~~~~~~");
        
        int w_comp = (int)(wine_set.get(0).size() * W_PER);
		IndependentComponentAnalysis w_filter = new IndependentComponentAnalysis(wine_set,w_comp);
		w_filter.filter(wine_set);
		System.out.println(new DataSetDescription(wine_set));

		System.out.println("\n\n\n");
		System.out.println("~~~~~~~~~~~ WINE STATS - After Reconstruction ~~~~~~~~~~~~");
		w_filter.reverse(wine_set);
		System.out.println(new DataSetDescription(wine_set));



		//RUN ICA ON SPAM
		System.out.println("Beginning ICA on spam data set");

        System.out.println("\n\n\n");
        System.out.println("~~~~~~~~~~~ SPAM STATS - Before ICA ~~~~~~~~~~~~");
        System.out.println(new DataSetDescription(spam_set));


        System.out.println("\n\n\n");
        System.out.println("~~~~~~~~~~~ SPAM STATS - After ICA ~~~~~~~~~~~~");
        
        int s_comp = (int)(spam_set.get(0).size() * S_PER);
		IndependentComponentAnalysis s_filter = new IndependentComponentAnalysis(spam_set,s_comp);
		s_filter.filter(spam_set);
		System.out.println(new DataSetDescription(spam_set));


		System.out.println("\n\n\n");
		System.out.println("~~~~~~~~~~~ SPAM STATS - After Reconstruction ~~~~~~~~~~~~");
		s_filter.reverse(spam_set);
		System.out.println(new DataSetDescription(spam_set));

		//WRITING DATA SETS
        //Write the wine results to a file
		System.out.println("Writing wine data results....");
		String wname = WINE_RES + "_"+(int)(W_PER*100)+".arff";
		DataSetWriter w_writer = new DataSetWriter(wine_set,wname);
		w_writer.writeWithHeader(W_HEADER);
		System.out.println("Finished writing wine results to - "+wname);

		//Write the spam results to a file
		System.out.println("Writing spam data results....");
		String sname = SPAM_RES +"_"+(int)(S_PER*100)+".arff";
		DataSetWriter s_writer = new DataSetWriter(spam_set,sname);
		s_writer.writeWithHeader(S_HEADER);
		System.out.println("Finished writing spam results to - "+sname);
	}

	private static void setUpArgs(){
        //set up command line args
        options.addOption("w",true,"set k value for wine clustering -- default 2");
        options.addOption("s",true,"set k value for spam clustering -- default 2");
        options.addOption("p",true,"set percentage of wine components to keep -- default 1.0");
        options.addOption("q",true,"set percentage of spam components to keep -- default 1.0");
    }

    private static void parseArgs(String[] args){
        CommandLine line = null;
        try{
            line = parser.parse(options,args);
        }catch(org.apache.commons.cli.ParseException e){
            System.err.println( "Parsing failed.  Reason: " + e.getMessage() );
        }

        //need to parse these as appropriate data types
        String wk = line.getOptionValue("w");
        String sk = line.getOptionValue("s");
        String per = line.getOptionValue("p");
        String per2 = line.getOptionValue("q");
        if(wk == null){
        	WINE_K = 2;
        }
        else
        	WINE_K = Integer.parseInt(wk);

        if(sk == null){
        	SPAM_K = 2;
        }
        else
        	SPAM_K = Integer.parseInt(sk);

        if(per == null){
        	W_PER = 0.7;
        }
        else
        	W_PER = Double.parseDouble(per);

        if(per2 == null){
        	S_PER = 0.6;
        }
        else
        	S_PER = Double.parseDouble(per);
    }
}