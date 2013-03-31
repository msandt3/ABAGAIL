package project3;

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

public class PCATest{

	private static final String WINE_RES = "datasets/results/wine_pca_results.txt";
	private static final String SPAM_RES = "datasets/results/spam_pca_results.txt";

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

		//Now separate the labels
		//DataSetLabelBinarySeperator.seperateLabels(wine_set);
		//DataSetLabelBinarySeperator.seperateLabels(spam_set);


		//RUN PCA ON WINE
		System.out.println("Beginning PCA on wine data set");
		System.out.println("~~~~~~~~~~~ WINE STATS - Before PCA ~~~~~~~~~~~~");
		System.out.println(new DataSetDescription(wine_set));
		PrincipalComponentAnalysis w_filter = new PrincipalComponentAnalysis(wine_set);
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


        System.out.println("\n\n\n");
        //RUN PCA ON SPAM
        System.out.println("Beginning PCA on spam data set");
		System.out.println("~~~~~~~~~~~ SPAM STATS - Before PCA ~~~~~~~~~~~~");
		System.out.println(new DataSetDescription(spam_set));
		PrincipalComponentAnalysis s_filter = new PrincipalComponentAnalysis(spam_set);
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