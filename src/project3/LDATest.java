package project3;
import java.io.File;
import shared.DataSet;
import shared.Instance;
import shared.filt.LinearDiscriminantAnalysis;
import shared.DataSetWriter;
import shared.DataSetDescription;
import shared.reader.ArffDataSetReader;
import shared.reader.DataSetReader;
import shared.filt.ContinuousToDiscreteFilter;
import shared.filt.LabelSplitFilter;
import shared.reader.DataSetLabelBinarySeperator;

public class LDATest{

	private static final String W_HEADER = "datasets/wineheader.txt";
	private static final String S_HEADER = "datasets/spamheader.txt";
	private static final String WINE_RES = "datasets/results/wine_lda_results";
	private static final String SPAM_RES = "datasets/results/spam_lda_results";

	public static void main(String[] args) throws Exception{
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
		for(int i=0; i<10; i++){
			System.out.println("~~~~~~~~~~~~ WINE SET - Before LDA ~~~~~~~~~~~~~~");
			System.out.println(new DataSetDescription(wine_set));

			System.out.println("\n\n\n");
			System.out.println("~~~~~~~~~~~~ WINE SET - After LDA ~~~~~~~~~~~~~~");
			LinearDiscriminantAnalysis w_filter = new LinearDiscriminantAnalysis(wine_set);
			w_filter.filter(wine_set);
			System.out.println(new DataSetDescription(wine_set));


			System.out.println("\n\n\n");
			System.out.println("~~~~~~~~~~~~ WINE SET PROJECTION ~~~~~~~~~~~~~~");
			System.out.println(w_filter.getProjection());


			System.out.println("\n\n\n");
			System.out.println("~~~~~~~~~~~~ WINE SET - After Reconstruction ~~~~~~~~~~~~~~");
			w_filter.reverse(wine_set);
			System.out.println(new DataSetDescription(wine_set));



			System.out.println("\n\n\n");
			System.out.println("~~~~~~~~~~~~ SPAM SET - Before LDA ~~~~~~~~~~~~~~");
			System.out.println(new DataSetDescription(spam_set));

			System.out.println("\n\n\n");
			System.out.println("~~~~~~~~~~~~ SPAM SET - After LDA ~~~~~~~~~~~~~~");
			LinearDiscriminantAnalysis s_filter = new LinearDiscriminantAnalysis(spam_set);
			s_filter.filter(spam_set);
			System.out.println(new DataSetDescription(spam_set));


			System.out.println("\n\n\n");
			System.out.println("~~~~~~~~~~~~ SPAM SET PROJECTION ~~~~~~~~~~~~~~");
			System.out.println(s_filter.getProjection());


			System.out.println("\n\n\n");
			System.out.println("~~~~~~~~~~~~ SPAM SET - After Reconstruction ~~~~~~~~~~~~~~");
			s_filter.reverse(spam_set);
			System.out.println(new DataSetDescription(spam_set));


			//Write the wine results to a file
			System.out.println("Writing wine data results....");
			String wname = WINE_RES + "_" + (i+1) + ".arff";
			DataSetWriter w_writer = new DataSetWriter(wine_set,wname);
			w_writer.writeWithHeader(W_HEADER);
			System.out.println("Finished writing wine results to - "+wname);

			//Write the spam results to a file
			System.out.println("Writing spam data results....");
			String sname = SPAM_RES + "_" + (i+1) + ".arff";
			DataSetWriter s_writer = new DataSetWriter(spam_set,sname);
			s_writer.writeWithHeader(S_HEADER);
			System.out.println("Finished writing spam results to - "+sname);
		}



	}

}