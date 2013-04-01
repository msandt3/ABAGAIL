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


public class PCATestCrossVal{


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
		//10 fold cross val
		for(int n=0; n<10; n++){

			/** REPRODUCING CLUSTERING EXPERIMENTS **/
			//System.out.println("\n\n\n");
			//System.out.println("~~~~~~~~~~~~~ RUNNING CLUSTERING ON REDUCED WINE SET ~~~~~~~~~~~~~~~~");
			
			w_kmc.estimate(wine_set);

			Double[] w_clusters = new Double[WINE_K];
			Double[] w_dists = new Double[WINE_K];
			Arrays.fill(w_clusters,(double)0);
			Arrays.fill(w_dists,(double)0);
			Double avg_dist = (double)0;
			int cluster;

			//System.out.println("~~~~~~~ Mean distance by cluster classification ~~~~~~~");
			double running_total_dist = 0;
			double total_dist = 0;
			double total_items = 0;
			double correct = 0;

			EuclideanDistance d_measure = new EuclideanDistance();
			//for every cluster center
			String ret = "";
			for(int i=0; i<w_kmc.getClusterCenters().length; i++){


				ret += "Mean distance from cluster - "+i;
				//for every instance


				for(int j=0; j<wine_set.size(); j++){
					
					cluster = (int)Double.parseDouble(w_kmc.value(wine_set.get(j)).toString());

					//if the instance belongs to the cluster
					if(cluster == i){
						//increment total distance by dist and increment total items
						double expected = Double.parseDouble(w_kmc.getClusterCenters()[i].getLabel().toString());
						double actual = Double.parseDouble(wine_set.get(j).getLabel().toString());
						if(expected == actual)
							correct++;
						total_dist += d_measure.value(wine_set.get(j),w_kmc.getClusterCenters()[i]);
						total_items++;
					}
				}


				double avg = 0;
				if(total_items != 0){
					avg = total_dist/total_items;
				}
				ret += " is - "+(avg);
				running_total_dist += avg;
				w_dists[i] = avg;
				total_dist = 0;
				total_items = 0;
				ret += "\n\n";
			}


			//System.out.println("~~~~~~~ AVERAGE DIST OVER ALL CLUSTERS ~~~~~~~~");
			avg_dist = running_total_dist/WINE_K;
			tot_w_dist += avg_dist;
			//System.out.println(avg_dist);

			//System.out.println("~~~~~~~ Average variance in distance measure ~~~~~~~");
			double total_var = 0;
			for(int i=0; i<w_dists.length; i++){
				total_var += Math.pow((w_dists[i] - avg_dist),2);
			}
			tot_w_var += total_var/WINE_K;
			//System.out.println(total_var/WINE_K);


			//System.out.println("~~~~~~~ CORRECT CLASSIFICATIONS ~~~~~~~");
			tot_w_avg_correct += correct/wine_set.size() * (double)100;
			//System.out.println(correct/wine_set.size() * (double)100);
			//System.out.println("WINE SET SIZE -- "+wine_set.size());






			/** ~~~~~~~~~~~~~~~~~~~~~~~ SPAM SET ~~~~~~~~~~~~~~~~~~~~ **/
			//System.out.println("\n\n\n");
			//System.out.println("~~~~~~~~~~~~~ RUNNING CLUSTERING ON REDUCED SPAM SET ~~~~~~~~~~~~~~~~");
			s_kmc.estimate(spam_set);
			Double[] s_clusters = new Double[SPAM_K];
			Double[] s_dists = new Double[SPAM_K];
			avg_dist = (double)0;
			Arrays.fill(s_dists,(double)0);
			Arrays.fill(s_clusters,(double)0);

			//System.out.println("\n\n\n");
			//System.out.println("~~~~~~~ Mean distance by cluster classification ~~~~~~~");
			running_total_dist = 0;
			total_dist = 0;
			total_items = 0;
			correct = 0;

			ret = "";
			for(int i=0; i<s_kmc.getClusterCenters().length; i++){


				ret += "Mean distance from cluster - "+i;
				//for every instance


				for(int j=0; j<spam_set.size(); j++){
					
					cluster = (int)Double.parseDouble(s_kmc.value(spam_set.get(j)).toString());

					//if the instance belongs to the cluster
					if(cluster == i){
						//increment total distance by dist and increment total items
						double expected = Double.parseDouble(s_kmc.getClusterCenters()[i].getLabel().toString());
						double actual = Double.parseDouble(spam_set.get(j).getLabel().toString());
						if(expected == actual)
							correct++;

						total_dist += d_measure.value(spam_set.get(j),s_kmc.getClusterCenters()[i]);
						total_items++;
					}
				}
				double avg = 0;
				if(total_items != 0){
					avg = total_dist/total_items;
				}
				ret += " is - "+(avg);
				running_total_dist += avg;
				s_dists[i] = avg;
				total_dist = 0;
				total_items = 0;
				ret += "\n\n";
			}
			//System.out.println(ret);
			

			//System.out.println("\n\n\n");
			//System.out.println("~~~~~~~ AVERAGE DIST OVER ALL CLUSTERS ~~~~~~~~");
			avg_dist = running_total_dist/SPAM_K;
			tot_s_dist += avg_dist;
			//System.out.println(avg_dist);


			//System.out.println("\n\n\n");
			//System.out.println("~~~~~~~ Average variance in distance measure ~~~~~~~");
			total_var = 0;
			for(int i=0; i<s_dists.length; i++){
				total_var += Math.pow((s_dists[i] - avg_dist),2);
			}
			tot_s_var += total_var/SPAM_K;
			//System.out.println(total_var/SPAM_K);

			//System.out.println("\n\n\n");
			//System.out.println("~~~~~~~ CORRECT CLASSIFICATIONS ~~~~~~~");
			tot_s_avg_correct += correct/spam_set.size() * (double)100;
			//System.out.println(correct/spam_set.size() * (double)100);
			//System.out.println("\n\n\n");
			//System.out.println("SPAM SET SIZE - "+spam_set.size());
		}

		System.out.println("##############################################");
		System.out.println("################ USING K CLUSTERS ############");
		System.out.println("FINAL CROSS VALIDATED STATS - WINE SET");
		System.out.println("AVERAGE DISTANCE - "+tot_w_dist/(double)10);
		System.out.println("AVERAGE VARIANCE - "+tot_w_var/(double)10);
		System.out.println("AVERAGE CORRECT - "+tot_w_avg_correct/(double)10);
		System.out.println("\n\n\n");
		System.out.println("FINAL CROSS VALIDATED STATS - SPAM SET");
		System.out.println("AVERAGE DISTANCE - "+tot_s_dist/(double)10);
		System.out.println("AVERAGE VARIANCE - "+tot_s_var/(double)10);
		System.out.println("AVERAGE CORRECT - "+tot_s_avg_correct/(double)10);
		System.out.println("\n\n\n");
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