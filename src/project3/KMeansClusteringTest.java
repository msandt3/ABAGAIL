package project3;

import java.util.Arrays;
import java.io.File;
import dist.Distribution;
import dist.MultivariateGaussian;
import func.KMeansClusterer;
import shared.DataSet;
import shared.DataSetWriter;
import shared.DataSetDescription;
import shared.reader.ArffDataSetReader;
import shared.reader.DataSetReader;
import shared.filt.ContinuousToDiscreteFilter;
import shared.filt.LabelSplitFilter;
import shared.reader.DataSetLabelBinarySeperator;
import shared.EuclideanDistance;


import org.apache.commons.cli.*;

/**
* Running K-means clustering on the abalone and spambase data sets
**/
public class KMeansClusteringTest{

	private static final String WINE_RES = "datasets/results/wine_km_results.txt";
	private static final String SPAM_RES = "datasets/results/spam_km_results.txt";

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
		//System.out.println(new DataSetDescription(spam_set));

		System.out.println("~~~~~~~~~~~~ WINE SET ~~~~~~~~~~~~~~");
		//System.out.println(new DataSetDescription(wine_set));


		System.out.println("\n\n\n");
		System.out.println("~~~~~~~~~~~~~~~ RUNNING KMC ~~~~~~~~~~~~~~~~");
		KMeansClusterer w_kmc = new KMeansClusterer(WINE_K);
		System.out.println("~~~~~~~~~~~~ WINE SET ~~~~~~~~~~~~~~");
		w_kmc.estimate(wine_set);
		//System.out.println(w_kmc);

		System.out.println("\n\n\n");
		System.out.println("~~~~~~~~~~~~ SPAM SET ~~~~~~~~~~~~~~");
		KMeansClusterer s_kmc = new KMeansClusterer(SPAM_K);
		s_kmc.estimate(spam_set);
		//System.out.println(s_kmc);


		System.out.println("~~~~~~~~~~~~~~~ WINE SET STATISTICS ~~~~~~~~~~~~~~~~");
		Double tot_w_avg_correct = (double)0;
		Double tot_s_avg_correct = (double)0;
		Double tot_w_var = (double)0;
		Double tot_s_var = (double)0;
		Double tot_w_dist = (double)0;
		Double tot_s_dist = (double)0;
		//10 fold cross val
		for(int n=0; n<10; n++){

			Double[] w_clusters = new Double[WINE_K];
			Double[] w_dists = new Double[WINE_K];
			Arrays.fill(w_clusters,(double)0);
			Arrays.fill(w_dists,(double)0);
			Double avg_dist = (double)0;
			int cluster;


			for(int i=0; i<wine_set.size(); i++){		

				cluster = (int)Double.parseDouble(w_kmc.value(wine_set.get(i)).toString());
				w_clusters[cluster]++;

			}
			System.out.println("~~~~~~ Composition by percentage for clusters ~~~~~~~");
			for(int i=0; i<w_clusters.length; i++){
				//System.out.println("Cluster "+i+" -- "+w_clusters[i]/(double)wine_set.size());
			}

			System.out.println("~~~~~~~ Mean distance by cluster classification ~~~~~~~");
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
			//System.out.println(ret);


			System.out.println("~~~~~~~ AVERAGE DIST OVER ALL CLUSTERS ~~~~~~~~");
			avg_dist = running_total_dist/WINE_K;
			tot_w_dist += avg_dist;
			System.out.println(avg_dist);

			System.out.println("~~~~~~~ Average variance in distance measure ~~~~~~~");
			double total_var = 0;
			for(int i=0; i<w_dists.length; i++){
				total_var += Math.pow((w_dists[i] - avg_dist),2);
			}
			tot_w_var += total_var/WINE_K;
			System.out.println(total_var/WINE_K);


			System.out.println("~~~~~~~ CORRECT CLASSIFICATIONS ~~~~~~~");
			tot_w_avg_correct += correct/wine_set.size() * (double)100;
			System.out.println(correct/wine_set.size() * (double)100);
			System.out.println("WINE SET SIZE -- "+wine_set.size());


			System.out.println("~~~~~~~~~~~~~~~ SPAM SET STATISTICS ~~~~~~~~~~~~~~~~");


			Double[] s_clusters = new Double[SPAM_K];
			Double[] s_dists = new Double[SPAM_K];
			avg_dist = (double)0;
			Arrays.fill(s_dists,(double)0);
			Arrays.fill(s_clusters,(double)0);


			//populate the number of elements for each cluster
			for(int i=0; i<spam_set.size(); i++){		
				cluster = (int)Double.parseDouble(s_kmc.value(spam_set.get(i)).toString());
				s_clusters[cluster]++;
			}


			System.out.println("~~~~~~ Composition by percentage for clusters ~~~~~~~");
			for(int i=0; i<s_clusters.length; i++){
				//System.out.println("Cluster "+i+" -- "+s_clusters[i]/(double)spam_set.size());
			}

			System.out.println("~~~~~~~ Mean distance by cluster classification ~~~~~~~");
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

			System.out.println("~~~~~~~ AVERAGE DIST OVER ALL CLUSTERS ~~~~~~~~");
			avg_dist = running_total_dist/SPAM_K;
			tot_s_dist += avg_dist;
			System.out.println(avg_dist);

			System.out.println("~~~~~~~ Average variance in distance measure ~~~~~~~");
			total_var = 0;
			for(int i=0; i<s_dists.length; i++){
				total_var += Math.pow((s_dists[i] - avg_dist),2);
			}
			tot_s_var += total_var/SPAM_K;
			System.out.println(total_var/SPAM_K);
			System.out.println("~~~~~~~ CORRECT CLASSIFICATIONS ~~~~~~~");
			tot_s_avg_correct += correct/spam_set.size() * (double)100;
			System.out.println(correct/spam_set.size() * (double)100);
			System.out.println("SPAM SET SIZE - "+spam_set.size());
		}
		System.out.println("##############################################");
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