package shared;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * An instance reader reads instances from a file
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class DataSetReader {
    /** 
     * The files to read from 
     */
    private String file;
    
    /**
     * Make a new instance reader
     * @param file the file to read from
     */
    public DataSetReader(String file) {
        this.file = file;
    }
    
    /**
     * Read the thing
     * @return the data 
     */
    public DataSet read() throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        List data = new ArrayList();
        Pattern pattern = Pattern.compile("[ ,]+");
        while ((line = br.readLine()) != null) {
            String[] split = pattern.split(line.trim());
            double[] input = new double[split.length];
            for (int i = 0; i < input.length; i++) {
                input[i] = Double.parseDouble(split[i]);
            }
            Instance instance = new Instance(input);
            data.add(instance);
        }
        br.close();
        Instance[] instances = (Instance[]) data.toArray(new Instance[0]);
        DataSet set = new DataSet(instances);
        set.setDescription(new DataSetDescription(set));
        return set;
    }

    /**
     * 
     * @return DataSet created from specified arff file. Modified to ignore tags supported by
     * arff files such as @ etc. 
     * @throws Exception
     */
    public DataSet readarff() throws Exception{
        int numAttr = 0;
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        List data = new ArrayList();
        Pattern pattern = Pattern.compile("[ ,]+");
        while ((line = br.readLine()) != null) {
            //ensure the line is not empty and does not contain metadata
            if(!line.isEmpty() && !line.contains("@")){
                String[] split = pattern.split(line.trim());
                double[] input = new double[split.length];
                int label = -1;
                for (int i = 0; i < input.length; i++) {
                    //here we address the issue of handling good/bad labels
                    if(split[i].contains("good"))
                        label = 1;
                    else if(split[i].contains("bad"))
                        label = 0;
                    else
                        input[i] = Double.parseDouble(split[i]);
                }
                Instance instance = new Instance(input,label);
                data.add(instance);
            }
        }//end while
        
        
        
        br.close();
        Instance[] instances = (Instance[]) data.toArray(new Instance[0]);
        DataSet set = new DataSet(instances);
        set.setDescription(new DataSetDescription(set));
        return set;
    }

}
