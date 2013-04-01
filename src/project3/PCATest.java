package project3;

import java.util.Arrays;
import java.io.File;
import shared.DataSet;
import shared.Instance;
import shared.filt.PrincipalComponentAnalysis;
import util.linalg.Matrix;
import shared.DataSetWriter;
import shared.DataSetDescription;
import shared.reader.ArffDataSetReader;
import shared.reader.DataSetReader;
import shared.filt.LabelSplitFilter;
import shared.reader.DataSetLabelBinarySeperator;
import shared.EuclideanDistance;
import func.KMeansClusterer;

import org.apache.commons.cli.*;


public class PCATest{


	private static final String W_HEADER = "datasets/wineheader.txt";
	private static final String S_HEADER = "datasets/spamheader.txt";

	private static String WINE_RES = "datasets/results/wine_pca_results";
	private static String SPAM_RES = "datasets/results/spam_pca_results";

	private static int WINE_K;
	private static int SPAM_K;

	private static CommandLineParser parser = new BasicParser();
    private static Options options = new Options();

    private static double C_PER;

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

		//Now separate the labels
		//DataSetLabelBinarySeperator.seperateLabels(wine_set);
		//DataSetLabelBinarySeperator.seperateLabels(spam_set);


		//RUN PCA ON WINE
		System.out.println("Beginning PCA on wine data set");
		System.out.println("~~~~~~~~~~~ WINE STATS - Before PCA ~~~~~~~~~~~~");
		System.out.println(new DataSetDescription(wine_set));
		int w_comp = (int)(wine_set.get(0).size() * C_PER);
		System.out.println("Using - "+w_comp+" components ");
		PrincipalComponentAnalysis w_filter = new PrincipalComponentAnalysis(wine_set,w_comp);
		//System.out.println("Eigenvals -- "+w_filter.getEigenValues());
        //System.out.println("Transpose -- "+w_filter.getProjection().transpose());

        System.out.println("\n\n\n");
        System.out.println("~~~~~~~~~~~ WINE STATS - After PCA ~~~~~~~~~~~~");
        w_filter.filter(wine_set);
        System.out.println(new DataSetDescription(wine_set));
        //reconstruct
        Matrix w_reverse = w_filter.getProjection().transpose();
        for (int i = 0; i < wine_set.size(); i++) {
            Instance instance = wine_set.get(i);
            instance.setData(w_reverse.times(instance.getData()).plus(w_filter.getMean()));
        }

        System.out.println("\n\n\n");
        System.out.println("~~~~~~~~~~~ WINE STATS - After Reconstruction ~~~~~~~~~~~~~~~~");
        System.out.println(new DataSetDescription(wine_set));
        //System.out.println(w_filter.getProjection().toString());


        System.out.println("\n\n\n");
        //RUN PCA ON SPAM
        System.out.println("Beginning PCA on spam data set");
		System.out.println("~~~~~~~~~~~ SPAM STATS - Before PCA ~~~~~~~~~~~~");
		System.out.println(new DataSetDescription(spam_set));
		int s_comp = (int)(spam_set.get(0).size() * C_PER);
		System.out.println("Using - "+s_comp+" components ");
		PrincipalComponentAnalysis s_filter = new PrincipalComponentAnalysis(spam_set,s_comp);
		//System.out.println("Eigenvals -- "+w_filter.getEigenValues());
        //System.out.println("Transpose -- "+w_filter.getProjection().transpose());
        System.out.println("\n\n\n");

        System.out.println("~~~~~~~~~~~ SPAM STATS - After PCA ~~~~~~~~~~~~");
        s_filter.filter(spam_set);
        System.out.println(new DataSetDescription(spam_set));
        //reconstruct
        Matrix s_reverse = s_filter.getProjection().transpose();
        for (int i = 0; i < spam_set.size(); i++) {
            Instance instance = spam_set.get(i);
            instance.setData(s_reverse.times(instance.getData()).plus(s_filter.getMean()));
        }

        System.out.println("\n\n\n");
        System.out.println("~~~~~~~~~~~ SPAM STATS - After Reconstruction ~~~~~~~~~~~~~~~~");
        System.out.println(new DataSetDescription(spam_set));
        //System.out.println(s_filter.getEigenValues().toString());

        //WRITING DATA SETS
		System.out.println("Writing wine data results....");
		WINE_RES += "_"+(int)(C_PER*100)+".arff";
		DataSetWriter w_writer = new DataSetWriter(wine_set,WINE_RES);
		w_writer.writeWithHeader(W_HEADER);
		System.out.println("Finished writing wine results to - "+WINE_RES);

		//Write the spam results to a file
		System.out.println("Writing spam data results....");
		SPAM_RES += "_"+(int)(C_PER*100)+".arff";
		DataSetWriter s_writer = new DataSetWriter(spam_set,SPAM_RES);
		s_writer.writeWithHeader(S_HEADER);
		System.out.println("Finished writing spam results to - "+SPAM_RES);



		KMeansClusterer w_kmc = new KMeansClusterer(WINE_K);
		KMeansClusterer s_kmc = new KMeansClusterer(SPAM_K);

		Double tot_w_avg_correct = (double)0;
		Double tot_s_avg_correct = (double)0;
		Double tot_w_var = (double)0;
		Double tot_s_var = (double)0;
		Double tot_w_dist = (double)0;
		Double tot_s_dist = (double)0;
	}

	private static void setUpArgs(){
        //set up command line args
        options.addOption("w",true,"set k value for wine clustering -- default 2");
        options.addOption("s",true,"set k value for spam clustering -- default 2");
        options.addOption("p",true,"set percentage of components to keep -- default 1.0");

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
        	C_PER = 1.0;
        }
        else
        	C_PER = Double.parseDouble(per);
    }
}