package project3;

import java.io.File;
import dist.Distribution;
import dist.MultivariateGaussian;
import shared.DataSet;
import func.EMClusterer;
import shared.DataSetWriter;
import shared.DataSetDescription;
import shared.reader.ArffDataSetReader;
import shared.reader.DataSetReader;
import shared.filt.ContinuousToDiscreteFilter;
import shared.filt.LabelSplitFilter;
import shared.reader.DataSetLabelBinarySeperator;
import org.apache.commons.cli.*;

public class ExMinimizationTest{

	private static final String WINE_RES = "datasets/results/wine_em_results.txt";
	private static final String SPAM_RES = "datasets/results/spam_em_results.txt";

	private static int WINE_K;
	private static int SPAM_K;

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

		//split the data set into data and labels
		LabelSplitFilter lsf = new LabelSplitFilter();
		//not sure if we'll need this
		ContinuousToDiscreteFilter cdf = new ContinuousToDiscreteFilter(10);

		//filter the datasets
		lsf.filter(wine_set);
		lsf.filter(spam_set);

		//Now separate the labels
		//DataSetLabelBinarySeperator.seperateLabels(wine_set);
		//DataSetLabelBinarySeperator.seperateLabels(spam_set);

		System.out.println("~~~~~~~~~~~~ SPAM SET ~~~~~~~~~~~~~~");
		System.out.println(new DataSetDescription(spam_set));

		System.out.println("~~~~~~~~~~~~ WINE SET ~~~~~~~~~~~~~~");
		System.out.println(new DataSetDescription(wine_set));


		System.out.println("~~~~~~ EXPECTATION MINIMIZATION ~~~~~");
		EMClusterer s_em = new EMClusterer();
		System.out.println("~~~~~~~~~~~~ SPAM SET ~~~~~~~~~~~~~~");
		s_em.estimate(spam_set);
		System.out.println(new DataSetDescription(spam_set));
		EMClusterer w_em = new EMClusterer();
		System.out.println("~~~~~~~~~~~~ WINE SET ~~~~~~~~~~~~~~");
		w_em.estimate(wine_set);
		System.out.println(new DataSetDescription(wine_set));



	}

	private static void setUpArgs(){
        //set up command line args
        options.addOption("w",true,"set k value for wine clustering -- default 10");
        options.addOption("s",true,"set k value for spam clustering -- default 50");
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
        if(wk == null){
        	WINE_K = 10;
        }
        else
        	WINE_K = Integer.parseInt(wk);

        if(sk == null){
        	SPAM_K = 50;
        }
        else
        	SPAM_K = Integer.parseInt(sk);
    }
}