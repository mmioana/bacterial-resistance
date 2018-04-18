package com.google.gwt.bacterialresistance.server;

import java.util.*;


public class BacterialResistance {
	
	private int numberOfAntibiotics;
	private ArrayList<Float> proportionOfResistance = new ArrayList<Float>();
	private int positionOfResist = 0;
	
	public BacterialResistance(int numberOfAntibiotics){
		this.numberOfAntibiotics = numberOfAntibiotics;
		
		for(int i = 0; i<numberOfAntibiotics; i++)
			proportionOfResistance.add(0.0f);
		
	}
	
	public void addResistance(int index, float value){
		float val = proportionOfResistance.get(index) + value;
		proportionOfResistance.set(index, Math.max(val,0));
	}
	
	public int getIndexOfResist(){
		return positionOfResist;
	}
	
	public float maximumResistance(){
		float max = 0.0f;
		int index = 0;
		for(Float x : proportionOfResistance){
			if(x > max){
				max = x;
				positionOfResist = index;
			}
			index++;
		}
		
		return max;
	}
	
	public int getNumberOfAntibiotics(){
		return proportionOfResistance.size();
	}
	
	public String toString(){
		String s = "Resistance of the human : ";
		
		for(Float x : proportionOfResistance){
			s += x + " ";
		}
		
		 s += "\n";
		 
		 return s;
	}

}
