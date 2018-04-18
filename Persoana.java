package com.google.gwt.bacterialresistance.server;
import java.util.*;

public class Persoana {

	private LinkedList<Legatura> edges = new LinkedList<Legatura>();
	private int nodeNum;
	private float edgeWeights = Math.round(100.0f) / 100.0f;
	private BacterialResistance br;
	private String condition;
	
	private int time;
	
	private static Random r = new Random();
	
	public Persoana(int nodeNum, int numberOfAntibiotics){
		this.nodeNum = nodeNum;
		br = new BacterialResistance(numberOfAntibiotics);
	}
	
	public void setCondition(String condition){
		this.condition = condition;
	}
	
	private float computeEdgeWeight(){
		
		float u = Math.round(r.nextFloat() * 100.0f) / 100.0f;
		
		return u;
	}
	
	public void addTime(int time){
		this.time = time;
	}
	
	public void decrementTime(int decrValue){
		time = (decrValue > time) ? 0 : (time - decrValue);
	}
	
	public void addEdgeWeight(){
		float edgeW;
		int pos = 0;
		int dim = edges.size() - 1;
		
		for(Legatura l : edges){
		
			do
			{
				edgeW = computeEdgeWeight();
			}
			while((edgeW > edgeWeights) && (pos != dim));
		
			if(pos != dim){
				edgeWeights -= edgeW;
				pos++;
			}
			else
				edgeW = edgeWeights;
			
			l.addWeight(edgeW);
		}
	}
	
	public void addEdge(Persoana y){
		
		Legatura l = new Legatura(this,y);
		
		edges.add(l);
	}
	
	public void removeEdge(Persoana y){
		
		for(Legatura l : edges){
			if(l.getY().equals(y)){
				edges.remove(y);
				break;
			}
		}
		
	}
	
	public boolean hasEdge(Persoana p){
		for(Legatura l : edges){
			if(l.isVertex(p))
				return true;
		}
		
		return false;
	}
	
	public Persoana getEdge(float chance){
		for(Legatura l : edges){
			
			if((l.getWeight() >  chance) && (l.getY().time == 0) && l.getY().condition.equals("susceptible"))
				return l.getY();
			
		}
		
		return null;
	}
	
	public int getNodeNum(){
		return nodeNum;
	}
	
	public int getDegree(){
		return edges.size();
	}
	
	public int getTime(){
		return time;
	}
	
	/*public void adjustResistance(float tau, float gamma, float[] resistanceFreq){
		float val;
		for(int i = 0; i<resistanceFreq.length; i++){
			if(br.getIndexOfResist() != i){
				val = tau * resistanceFreq[i] - gamma;
				if(val < 0.0f) val = 0.0f;
				br.addResistance(i, val);
			}
		}
	}
	
	public void adjustResistance(float tau, float gamma, int index){
		float val = tau - gamma;
		if(val < 0.0f) val = 0.0f;
		br.addResistance(index, val);
	}*/
	
	public void adjustResistance(float val, int index, boolean addResistance){
		if(addResistance == false)	val = val*(-1);
		br.addResistance(index, val);
	}
	
	public boolean isResistant(){
		return (br.maximumResistance() > 0.6f);
	}
	
	public boolean equals(Object o){
		return (o instanceof Persoana) && (((Persoana)o).nodeNum == this.nodeNum);
	}
	
	public String toString(){
		String s = "Node " + nodeNum + " time " + time + " numberOfAntibiotics " + br.getNumberOfAntibiotics() + "\n";
		
		/*for(Legatura l : edges)
			s += l.toString() + "\n";*/
		
		s += br.toString() + "\n";
		
		return s;
	}
	
}
