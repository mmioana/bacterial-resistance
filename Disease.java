package com.google.gwt.bacterialresistance.server;

import java.util.*;

public class Disease {

	private String diseaseName;
	private LinkedList<String> diseaseRes;
	private float[] vals;
	private int len = 0;
	private static int numOfResGenes;
	
	public Disease(String diseaseName, LinkedList<String> diseaseRes){
		this.diseaseName = diseaseName;
		this.diseaseRes = diseaseRes;
		vals = new float[diseaseRes.size()];
	}
	
	public void addResistenceVal(float value){
		
		if(len < vals.length)
			vals[len++] = value;
	}
	
	public static void setNumOfResGenes(int x){
		numOfResGenes = x;
	}
	
	public static int getNumOfResGenes(){
		return numOfResGenes;
	}
	
	public float[] getRes(){
			return vals;
	}
	
	public String toString(){
		
		String s = "Disease : " + diseaseName + "\n";
		
		int pos = 0;
		
		for(String resName : diseaseRes){
			s += diseaseRes.get(pos) + " " + vals[pos] + "\n";
			pos++;
		}
		
		return s;
		
	}
	
}
