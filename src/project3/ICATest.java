package project3;

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


public class ICATest{
	private static final String WINE_RES = "datasets/results/wine_ica_results.txt";
	private static final String SPAM_RES = "datasets/results/spam_ica_results.txt";

	public static void main(String[] args) throws Exception{
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
        
		IndependentComponentAnalysis w_filter = new IndependentComponentAnalysis(wine_set,9);
		w_filter.filter(wine_set);
		System.out.println(new DataSetDescription(wine_set));



		//RUN ICA ON SPAM
		System.out.println("Beginning ICA on wine data set");
		System.out.println("~~~~~~~~~~~ SPAM STATS - Before Randomizing ~~~~~~~~~~~~");
		System.out.println(new DataSetDescription(spam_set));


        System.out.println("\n\n\n");
        System.out.println("~~~~~~~~~~~ SPAM STATS - Before ICA ~~~~~~~~~~~~");
        System.out.println(new DataSetDescription(spam_set));


        System.out.println("\n\n\n");
        System.out.println("~~~~~~~~~~~ SPAM STATS - After ICA ~~~~~~~~~~~~");
        
		IndependentComponentAnalysis s_filter = new IndependentComponentAnalysis(spam_set,10);
		s_filter.filter(spam_set);
		System.out.println(new DataSetDescription(spam_set));

		//WRITING DATA SETS
        //Write the wine results to a file
		System.out.println("Writing wine data results....");
		DataSetWriter w_writer = new DataSetWriter(wine_set,WINE_RES);
		w_writer.write();
		System.out.println("Finished writing wine results to - "+WINE_RES);

		//Write the spam results to a file
		System.out.println("Writing spam data results....");
		DataSetWriter s_writer = new DataSetWriter(spam_set,SPAM_RES);
		w_writer.write();
		System.out.println("Finished writing spam results to - "+SPAM_RES);
	}
}