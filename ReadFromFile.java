package com.google.gwt.bacterialresistance.server;
import java.io.BufferedReader;
import java.io.*;
import java.util.*;

public class ReadFromFile {	
	
	private String csvFile;
	
	private ArrayList<Disease> dis = new ArrayList<Disease>();
	
	
	public ReadFromFile(String csvFile){
		this.csvFile = csvFile;
	}
	
	public ArrayList<Disease> readFromCSVFile(){
		String cvsSplitBy = ",";
		
		try{
			BufferedReader br = new BufferedReader(new FileReader(csvFile));
			String line;
			LinkedList<String> geneResName = new LinkedList<String>();
			int numOfVals = 0;
			
			
			if((line = br.readLine()) != null){
				String[] geneRes = line.split(cvsSplitBy);
				
				for(int i = 0; i<geneRes.length; i++)
					if(geneRes[i].equals("") == false)
						geneResName.add(geneRes[i]);
				
			}
			
			//System.out.println(geneResName.size());
			Disease.setNumOfResGenes(geneResName.size());
			
			if((line = br.readLine()) != null){
				String[] numYears = line.split(cvsSplitBy);
				//System.out.println(numYears.length);
				numOfVals = (numYears.length - 1) / geneResName.size();
			}
			
			//System.out.println("numofvals " + numOfVals);
			
			while ((line = br.readLine()) != null) {
				String[] values = line.split(cvsSplitBy);
				for(int i = 0; i<numOfVals; i++){
					Disease d = new Disease(values[0], geneResName);
					for(int j = i + 1; j<values.length; j+=numOfVals){
						String[] value = values[j].split("\"");
						
						String actualValue = value[value.length - 1];
						
						//System.out.println(actualValue);
						
						if(actualValue.equals(""))
							d.addResistenceVal(0.0f);
						else
							d.addResistenceVal(Float.parseFloat(actualValue) / 1000.0f);
						
						//System.out.println(value[value.length - 1]);
					}
					
					//System.out.println(d);
					//System.out.println("----------------------------");
					dis.add(d);
				}
			}
			
			br.close();
			
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		return dis;
		
	}
	
}
