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
import shared.filt.RandomizedProjectionFilter;

public class RandomizedProjectionTest{

	private static final String WINE_RES = "datasets/results/wine_rp_results.txt";
	private static final String SPAM_RES = "datasets/results/spam_rp_results.txt";

	//static values for initial component sizes
	private static final int WINE_COMP = 11;
	private static final int SPAM_COMP = 57;

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

		lsf.filter(wine_set);
		lsf.filter(spam_set);

		//TODO allow for multiple runs of randomized projection

		//RUN RANDOMIZED PROJECTION ON WINE
		System.out.println("~~~~~~~~~~~~ WINE SET - Before RP ~~~~~~~~~~~~~~");
		System.out.println(new DataSetDescription(wine_set));

		System.out.println("\n\n\n");
		System.out.println("~~~~~~~~~~~~ WINE SET - After RP ~~~~~~~~~~~~~~");
		//TODO change desired components to command line set
		RandomizedProjectionFilter w_filter = new RandomizedProjectionFilter(8,WINE_COMP);
		w_filter.filter(wine_set);
		System.out.println(new DataSetDescription(wine_set));

		System.out.println("\n\n\n");
		System.out.println("~~~~~~~~~~~~ WINE SET PROJECTION ~~~~~~~~~~~~~~");
		System.out.println(w_filter.getProjection());

		System.out.println("\n\n\n");
		System.out.println("~~~~~~~~~~~~ WINE SET - After Reconstruction ~~~~~~~~~~~~~~");
		w_filter.reverse(wine_set);
		System.out.println(new DataSetDescription(wine_set));

		//RUN RANDOMIZED PROJECTION ON WINE
		System.out.println("~~~~~~~~~~~~ SPAM SET - Before RP ~~~~~~~~~~~~~~");
		System.out.println(new DataSetDescription(spam_set));

		System.out.println("\n\n\n");
		System.out.println("~~~~~~~~~~~~ SPAM SET - After RP ~~~~~~~~~~~~~~");
		//TODO change desired components to command line set
		RandomizedProjectionFilter s_filter = new RandomizedProjectionFilter(40,SPAM_COMP);
		s_filter.filter(spam_set);
		System.out.println(new DataSetDescription(spam_set));


		System.out.println("\n\n\n");
		System.out.println("~~~~~~~~~~~~ SPAM SET PROJECTION ~~~~~~~~~~~~~~");
		System.out.println(s_filter.getProjection());

		System.out.println("\n\n\n");
		System.out.println("~~~~~~~~~~~~ WINE SET - After Reconstruction ~~~~~~~~~~~~~~");
		w_filter.reverse(spam_set);
		System.out.println(new DataSetDescription(spam_set));


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