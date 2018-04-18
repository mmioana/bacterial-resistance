package com.google.gwt.bacterialresistance.server;
public class Legatura {
	
	private Persoana x;
	private Persoana y;
	private float weight;
	
	public Legatura(Persoana x, Persoana y){
		this.x = x;
		this.y = y;
	}
	
	public void addWeight(float weight){
		this.weight = weight;
	}
	
	public boolean isVertex(Persoana p){
		return p.equals(x) || p.equals(y); 
	}
	
	public Persoana getX(){
		return x;
	}
	
	public Persoana getY(){
		return y;
	}
	
	public float getWeight(){
		return weight;
	}
	
	public String toString(){
		String s = "";
		
			s += x.getNodeNum() + " " + y.getNodeNum() + " " + weight;
		
		return s;
	}

}
