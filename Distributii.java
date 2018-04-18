package com.google.gwt.bacterialresistance.server;
import java.util.*;

public class Distributii {

	private int numInfectedNodes;
	private int numSusceptibleNodes;
	private int numTotalNodes;
	
	//parameters for erdosRenyi
	private float pOfInclusion;
	
	//parameters for smallWorld
	private float pW; //probability of rewiring
	private int kL; //num of edges between nodes
	
	//paramters for scaleFree
	private int m0; //size of initial network
	private int m; //usually m = m0, but never m>m0
	
	//parameters for scaleFreeSmallWorld
	private float pNiu;
	
	//parameters used for directed graph only
	private boolean isDirected;
	private float d; 
	
	private float exposurePeriod;
	private float infectionPeriodH, infectionPeriodR, infectionPeriodF;
	private float hospitalizationPeriodR, hospitalizationPeriodF;
	private float deadPeriod;
	private float immunePeriod;
	//private float infectionRate;
	
	//parameters for the network model
	private float alpha;
	private float betaI, betaH, betaF;
	private float delta1, delta2;
	private float gammaH, gammaDH, gammaF, gammaI, gammaIH, gammaD, gammaSR;
	private float theta1;
	
	//parameters for the bacterial resistance model
	private float beta, gamma, tau;
	private float [] resistanceFreq;
	private int	[]	rFreq;
	
	private int numOfAntibiotics;
	
	private static Random r = new Random();
	
	private LinkedList<Persoana> susceptibleNodes = new LinkedList<Persoana>(); 
	private LinkedList<Persoana> exposedNodes = new LinkedList<Persoana>(); 
	private LinkedList<Persoana> infectedNodes = new LinkedList<Persoana>(); 
	private LinkedList<Persoana> hospitalizedNodes = new LinkedList<Persoana>(); 
	private LinkedList<Persoana> deadNodes = new LinkedList<Persoana>(); //F
	private LinkedList<Persoana> deadImmuneNodes = new LinkedList<Persoana>(); //used to save the dead persons that cannot transmit the disease anymore
	private LinkedList<Persoana> immuneNodes = new LinkedList<Persoana>(); //R 

	
	public Distributii(int numInfectedNodes, int numTotalNodes, float pNiu,
			boolean isDirected, float exposurePeriod,
			float infectionPeriodH, float infectionPeriodR, float infectionPeriodF,
			float hospitalizationPeriodR, float hospitalizationPeriodF, 
			float deadPeriod, float immunePeriod
			/*, float infectionRate*/){
		
		
		this.numInfectedNodes = numInfectedNodes;
		this.numTotalNodes = numTotalNodes;
		this.numSusceptibleNodes = numTotalNodes - numInfectedNodes;
		this.pNiu = pNiu;
		this.isDirected = isDirected;
		
		
		this.exposurePeriod = exposurePeriod;
		this.alpha = 1.0f/exposurePeriod;
		
		this.infectionPeriodH = infectionPeriodH;
		this.gammaH = 1.0f / infectionPeriodH;
		
		this.infectionPeriodR = infectionPeriodR;
		this.gammaI = 1.0f / infectionPeriodR;
		
		this.infectionPeriodF = infectionPeriodF;
		this.gammaD = 1.0f / infectionPeriodF;
		
		this.hospitalizationPeriodF = hospitalizationPeriodF;
		this.gammaDH = 1.0f/hospitalizationPeriodF;
		
		this.hospitalizationPeriodR = hospitalizationPeriodR;
		this.gammaIH = 1.0f/hospitalizationPeriodR;
		
		this.deadPeriod = deadPeriod;
		this.gammaF = 1.0f / deadPeriod;
		
		this.immunePeriod = immunePeriod;
		this.gammaSR = 1.0f / immunePeriod;
		
		//this.infectionRate = infectionRate;
		
		
	}
	
	public void setNumOfAntibiotics(int numOfAntibiotics){
		this.numOfAntibiotics = numOfAntibiotics;
	}
	
	public void setBetaI(float betaI){
		this.betaI = betaI;
	}
	
	public void setBetaH(float betaH){
		this.betaH = betaH;
	}
	
	public void setBetaF(float betaF){
		this.betaF = betaF;
	}
	
	public void setTheta1(float theta1){
		this.theta1 = theta1;
	}
	
	public void setDelta1(float delta1){
		this.delta1 = delta1;
	}
	
	public void setDelta2(float delta2){
		this.delta2 = delta2;
	}
	
	//parameters for the bacterial resistance model
	public void setBeta(float beta){
		this.beta = beta;
	}
	
	public void setGamma(float gamma){
		this.gamma = gamma;
	}
	
	public void setTau(float tau){
		this.tau = tau;
	}
	
	public void setResistanceFreq(float[] resistanceFreq){
		this.resistanceFreq = resistanceFreq;	
		rFreq = new int[resistanceFreq.length];
		
		for(int i=0; i<resistanceFreq.length; i++) 
			rFreq[i] = 0;
	}
	
	private float computeChance(){
		
		float u = r.nextFloat();
		
		if(r.nextBoolean())
			return (1.0f - u);
		
		return u;
	}
	
	private Persoana getRandomDeactNodes(Persoana p){
		int index = r.nextInt(susceptibleNodes.size());
		Persoana q = susceptibleNodes.get(index);
		
		//System.out.println(p.getNodeNum() + " - " + q.getNodeNum());
		
		while(p.equals(q) || p.hasEdge(q)){
			index = r.nextInt(susceptibleNodes.size());
			q = susceptibleNodes.get(index);
			
			//System.out.println(p.getNodeNum() + " - " + q.getNodeNum());
		}
		
		return q;
		
	}
	
	private Persoana getRandomActNodes(){
		int index = r.nextInt(infectedNodes.size());

		return infectedNodes.get(index);
	}
	
	private Persoana getRandomActNodes(LinkedList<Persoana> activeState){
		
		for(Persoana q : activeState){
			if(q.getTime() == 0)	return q;
		}

		return null;
	}
	
	private Persoana getRandomHospNodes(){
		int index = r.nextInt(hospitalizedNodes.size());
		
		return hospitalizedNodes.get(index);
	}
	
	private int getDegreesDeactNodes(){
		int sum = 0;
		int i;
		
		for(i = 0; i<susceptibleNodes.size(); i++)
			sum += susceptibleNodes.get(i).getDegree();
		
		return sum;		
	}
	
	private void addEdge(Persoana x, Persoana y){
		
		if(isDirected)
			addDirectedEdge(x,y);
		else
			addUndirectedEdge(x,y);
		
	}
	
	private void addDirectedEdge(Persoana x, Persoana y){
			x.addEdge(y);
	}
	
	private void addUndirectedEdge(Persoana x, Persoana y){
			x.addEdge(y);
			y.addEdge(x);
	}
	
	private float sumInverseActNodesDegrees(){
		float sum = 0.0f;
		int i, n = infectedNodes.size();
		
			for(i = 0; i<n; i++)
				sum += 1.0f/(float) infectedNodes.get(i).getDegree();
		
		return sum;		
	}
	
	private int expDistribution(float theta){
		float u = r.nextFloat();
		return (int) Math.ceil((-1.0f) * theta * Math.log((double)u)); 
	}
	
	public void generateInitialGraph(){
		smallWorldScaleFree();
	}
	
	public void erdosRenyi(){
		int i,j;
		float chance;
		Persoana p, q, h;
		
		for(i = 0; i<numTotalNodes; i++)
			susceptibleNodes.add(new Persoana(i, numOfAntibiotics));
		
		for(i = 0; i<numTotalNodes - 1; i++){
			p = susceptibleNodes.get(i);
			for(j = i; j<numTotalNodes; j++){
				chance = computeChance();
				if(pOfInclusion > chance){
					
					q = susceptibleNodes.get(i); 
					
					if(!isDirected){
						
						addEdge(p,q);
						
					}
					else{
						chance = computeChance();
							if(d > chance){
								addEdge(p,q);
							}else{
								
								do{
									h = getRandomActNodes(susceptibleNodes);
								}while(h.equals(p));
								
								addEdge(h,p);
							}
					}
					
				}
			}
		}
		
		
		for(i = 0; i<numInfectedNodes; i++){
			p = susceptibleNodes.remove(i);
			infectedNodes.add(p);
		}
		
	}
	
	public void smallWorld(){
		int i,j;
		Persoana p, q, r, h;
		float chance;
		
		for(i = 0; i<numTotalNodes; i++)
			susceptibleNodes.add(new Persoana(i, numOfAntibiotics));
		
		//First create a ring lattice
		for(i = 0; i< numTotalNodes - 1; i++){
			p = susceptibleNodes.get(i);
			for(j = i + 1; j<(i + kL/2); j++){
				if(j > numTotalNodes){
					j = j-numTotalNodes;
				}	
				q = susceptibleNodes.get(j);	
				addEdge(p, q);		
			}
		}
		
		//Second rewire edges randomly with probability pW
		for(i = 0; i< numTotalNodes - 1; i++){
			p = susceptibleNodes.get(i);
			for(j = i + 1; j<(i + kL/2); j++){
				if(j > numTotalNodes){
					j = j-numTotalNodes;
				}	
				
				chance = computeChance();
				
				if(pW > chance){
					q = susceptibleNodes.get(j);
					p.removeEdge(q);
					do{
					r = getRandomActNodes(susceptibleNodes); //to check node is not i or connected already to i
					}while(r.equals(p));
					
					
						if(isDirected){
							chance = computeChance();
							if(d>chance){
								addEdge(p,r);
							}
							else{
								p.addEdge(r);
								do{
									h = getRandomActNodes(susceptibleNodes);
								}while(h.equals(p) || h.equals(r));
								h.addEdge(p);
							}
						}
						else{
							addEdge(p,r);
						}
					
				}
			}
		}
		
		for(i = 0; i<numInfectedNodes; i++){
			p = susceptibleNodes.remove(i);
			infectedNodes.add(p);
		}	
	}
	
	public void scaleFree(){
		
		int i,j;
		Persoana p, q, h;
		float chance;
		int currentDegree;
		float b;
		boolean noConnection;
		
		int E = 0; // number of edges
		
		
		for(i = 0; i<numTotalNodes; i++)
			susceptibleNodes.add(new Persoana(i, numOfAntibiotics));
		
		//First create the fully connected initial network
		for(i = 0; i<m0 - 1; i++){
			for(j = i + 1; j<m0; j++){
				p = susceptibleNodes.get(i);
				q = susceptibleNodes.get(j);
				addEdge(p,q);
				if(isDirected)	E += 2;
				else			E += 1;
			}
		}
		
		//Second add remaining nodes with a preferential attachment bias
		for(i = m0 + 1; i< numTotalNodes; i++){
			p = susceptibleNodes.get(i);
			currentDegree = 0;
			while(currentDegree < m){
				//node j = uniformly randomly chosen from the set of all nodes, excluding i and nodes adjacent to i
				q = getRandomActNodes(susceptibleNodes);
				b = q.getDegree() / E;
				chance = computeChance();
				
				if(b > chance){
					if(!isDirected){
						addEdge(p,q);
						E ++;
					}
					else{
						chance = computeChance();
						if(d > chance){
							addEdge(p,q);
							E += 2;
						}
						else{
							p.addEdge(q);
							E++;
							
							noConnection = true;
							
							while(noConnection){
								
								h = getRandomActNodes(susceptibleNodes);
								
								b = h.getDegree() / E;
								chance = computeChance();
								
								if(b > chance){
									h.addEdge(p);
									E++;
									noConnection = false;
								}
								
							}
							
						}
					}
				}
				
			}
		}
		
	}
	
	public void smallWorldScaleFree(){
		
		int i,j;
		int E;
		Persoana p;
		boolean isChosen;
		float prD;
		int jDegree;
		float chance;
		int numConnectionDeactNodes;
		
		for(i = 0; i<numInfectedNodes; i++)
			infectedNodes.add(new Persoana(i, numOfAntibiotics));
		
		for(i = numInfectedNodes; i<numTotalNodes; i++)
			susceptibleNodes.add(new Persoana(i, numOfAntibiotics));
		
		
		
		
		//generate connections for the infectedNodes
		for(i = 0; i<numInfectedNodes - 1; i++){
			
			//Persoana p = new Persoana(i);
			//infectedNodes.add(p);
			
			p = infectedNodes.get(i);
			
			
			for(j = i + 1; j< numInfectedNodes; j++){
			
					addEdge(p, infectedNodes.get(j));
				
			}
		}
		
		
		
		
		//generate rest of connections between nodes
		for(i = numInfectedNodes; i<numTotalNodes; i++){
			
			p = susceptibleNodes.get(i - numInfectedNodes);
			numConnectionDeactNodes = 0;
			
			
			for(j = 0; j<numInfectedNodes; j++){
				
				//System.out.println(" i = " + i + " j = " + j);
				
				chance = computeChance();
				
				if((chance < pNiu) || ((numTotalNodes - numInfectedNodes) == 1)){
					//System.out.println(p.getNodeNum() + " " + infectedNodes.get(j).getNodeNum());
					addEdge(p, infectedNodes.get(j));
					
				}
				else{
					boolean connected = false;
					
					numConnectionDeactNodes ++;
					
					if(numConnectionDeactNodes > (numSusceptibleNodes - (i - numInfectedNodes + 1))) connected = true;
					
					while(!connected){
						
						chance = computeChance();
						
						Persoana q = getRandomDeactNodes(p);
						
						E = getDegreesDeactNodes();
						
						jDegree = q.getDegree();
						
						//System.out.println("Degree distrib : " + E + " " + jDegree);
						
						if((E == 0) || (jDegree == 0) || ((jDegree/(float)E) > chance)){
							
							//System.out.println(p.getNodeNum() + " " + q.getNodeNum());
							
							addEdge(p, q);
							
							connected = true;
						}
						
					}
					
				}
			}
				
				 //Replace an active node with node i. Active nodes with lower degrees are more likely to be replaced.
				susceptibleNodes.remove(i - numInfectedNodes);
				
				infectedNodes.add(p);
				
				
				isChosen = false;
				
				while(!isChosen){
				
					Persoana q = getRandomActNodes();
					
					jDegree = q.getDegree();
					
					prD = (1.0f/(float)jDegree) / sumInverseActNodesDegrees(); 
					
					chance = computeChance();
					
					if(prD > chance){
						isChosen = true;
						
						infectedNodes.remove(q);
						
						susceptibleNodes.add(q);
						
					}
				}	
			
		}
		
		for(i = 0; i<numInfectedNodes; i++){
			p = infectedNodes.get(i);
			
			p.addEdgeWeight();
			p.addTime(expDistribution(infectionPeriodR));	
			p.setCondition("infected");
		}
		
		for(i = 0; i<numSusceptibleNodes; i++){
			susceptibleNodes.get(i).addEdgeWeight();
			susceptibleNodes.get(i).setCondition("susceptible");
		}
		
	}
	
	public void evaluateNetworkInTime(int period){
		int day;
		int numOfNewExposed, numOfNewInfected, numOfNewHospitalized,
		numOfNewDeadH, numOfNewDeadI, numOfNewImmuneH, numOfNewImmuneI, 
		numOfNewImmuneF, numOfNewSusceptible;
		
		//System.out.println("Period " + period);
		
		for(day = 0; day <period; day++){
			
			System.out.println("day " + day);
			
			numOfNewExposed = expDistribution(((betaI * infectedNodes.size() + 
												betaH * hospitalizedNodes.size() +
												betaF * deadNodes.size()) * susceptibleNodes.size()) / numTotalNodes);
			numOfNewInfected = expDistribution(alpha * exposedNodes.size());
			numOfNewHospitalized = expDistribution(gammaH * theta1 * infectedNodes.size());
			numOfNewDeadH = expDistribution(gammaDH * delta2 * hospitalizedNodes.size());
			numOfNewDeadI = expDistribution(delta1 * (1 - theta1) * gammaD * infectedNodes.size());
			numOfNewImmuneH = expDistribution(gammaIH * (1-delta2) * hospitalizedNodes.size());
			numOfNewImmuneI = expDistribution( gammaI * (1-theta1) * (1-delta1) * infectedNodes.size());
			numOfNewImmuneF = expDistribution(gammaF * deadNodes.size());
			numOfNewSusceptible = expDistribution(gammaSR * (1-delta1) * immuneNodes.size());
			
			//System.out.println(this);
			
			/*System.out.println(" numOfNewExposed : " + numOfNewExposed);
			System.out.println(" numOfNewInfected : " + numOfNewInfected);
			System.out.println(" numOfNewHospitalized : " + numOfNewHospitalized);
			System.out.println(" numOfNewDeadH : " + numOfNewDeadH);
			System.out.println(" numOfNewDeadI : " + numOfNewDeadI);
			System.out.println(" numOfNewImmuneH : " + numOfNewImmuneH);
			System.out.println(" numOfNewImmuneI : " + numOfNewImmuneI);
			System.out.println(" numOfNewImmuneF : " + numOfNewImmuneF);
			System.out.println(" numOfNewSusceptible : " + numOfNewSusceptible);*/
			
			adjustTime(1, numOfNewExposed, numOfNewInfected, numOfNewHospitalized,
					numOfNewDeadH, numOfNewDeadI, numOfNewImmuneH,
					numOfNewImmuneI, numOfNewImmuneF, numOfNewSusceptible);
			
			computeBacterialResistance();
			
		}
		
	}

	
	//
	//
	//function used to adjust the time the nodes remain in a certain state
	//
	//
	private void adjustTime(int decrTimeVal, int numOfNewExposed, int numOfNewInfected, 
						int numOfNewHospitalized, int numOfNewDeadH, int numOfNewDeadI, 
						int numOfNewImmuneH, int numOfNewImmuneI, int numOfNewImmuneF, 
						int numOfNewSusceptible){
		
		
		/*
		System.out.println("------------------------------------------------------------------------------------");
		System.out.println(this);
		System.out.println("------------------------------------------------------------------------------------");
		*/
		
		int i;
		Persoana p;
		
		for(i = 0; i<exposedNodes.size();){
			p = exposedNodes.get(i);
			p.decrementTime(decrTimeVal);
			
			if(p.getTime() <=0){
				if(numOfNewInfected > 0){
					exposedNodes.remove(p);
					p.addTime(expDistribution(infectionPeriodR));
					p.setCondition("infected");
					infectedNodes.add(p);
					numOfNewInfected--;
				}
				else	i++;
			}
			else 	i++;
		}
		
		for(i = 0; i<infectedNodes.size();){
			p = infectedNodes.get(i);
			p.decrementTime(decrTimeVal);
			
			//System.out.println("time : " + p.getTime());
			
			if(p.getTime() <=0){
				if(numOfNewImmuneI > 0){
					infectedNodes.remove(p);
					p.addTime(expDistribution(immunePeriod));
					p.setCondition("immune");
					immuneNodes.add(p);
					numOfNewImmuneI--;
				}
				else if(numOfNewHospitalized > 0){
					infectedNodes.remove(p);
					p.addTime(expDistribution(hospitalizationPeriodR));
					p.setCondition("hospitalized");
					hospitalizedNodes.add(p);
					numOfNewHospitalized--;
				}
				else if(numOfNewDeadI > 0){
					infectedNodes.remove(p);
					p.addTime(expDistribution(deadPeriod));
					p.setCondition("dead");
					deadNodes.add(p);
					numOfNewDeadI--;
				}
				else 	i++;
			}
			else	i++;
		}
		
		for(i = 0; i<hospitalizedNodes.size();){
			p = hospitalizedNodes.get(i);
			p.decrementTime(decrTimeVal);
			
			if(p.getTime() <=0){
				if(numOfNewImmuneH > 0){
					hospitalizedNodes.remove(p);
					p.addTime(expDistribution(immunePeriod));
					p.setCondition("immune");
					immuneNodes.add(p);
					numOfNewImmuneH--;
				}
				else if(numOfNewDeadH > 0){
					hospitalizedNodes.remove(p);
					p.addTime(expDistribution(deadPeriod));
					p.setCondition("dead");
					deadNodes.add(p);
					numOfNewDeadH--;
				}
				else 	i++;
			}
			else	i++;
		}
		
		for(i = 0; i<immuneNodes.size();){
			p = immuneNodes.get(i);
			p.decrementTime(decrTimeVal);
			
			if(p.getTime() <=0){
				if(numOfNewSusceptible>0){
					immuneNodes.remove(p);
					p.addTime(expDistribution(0.0f));
					p.setCondition("susceptible");
					susceptibleNodes.add(p);
					numOfNewSusceptible--;
				}
				else	i++;
			}
			else	i++;
		}
		
		for(i = 0; i<deadNodes.size();){
			p = deadNodes.get(i);
			p.decrementTime(decrTimeVal);
			
			if(p.getTime() <=0){
				if(numOfNewImmuneF>0){
					deadNodes.remove(p);
					p.addTime(expDistribution(0.0f));
					p.setCondition("immune");
					deadImmuneNodes.add(p);
					numOfNewImmuneF--;
				}
				else	i++;
			}
			else	i++;
		}
		
		
		/*
		System.out.println("------------------------------------------------------------------------------------");
		System.out.println(this);
		System.out.println("------------------------------------------------------------------------------------");
		*/
		
		
		adjustNetworkI(numOfNewExposed, exposurePeriod, susceptibleNodes, exposedNodes, "exposed");
		adjustNetwork(numOfNewInfected, infectionPeriodR, exposedNodes, infectedNodes, "infected");
		adjustNetwork(numOfNewImmuneI, immunePeriod, infectedNodes, immuneNodes, "immune");
		adjustNetwork(numOfNewHospitalized, hospitalizationPeriodR, infectedNodes, hospitalizedNodes, "hospitalized");
		adjustNetwork(numOfNewDeadI, deadPeriod, infectedNodes, deadNodes, "dead");
		adjustNetwork(numOfNewImmuneH, immunePeriod, hospitalizedNodes, immuneNodes, "immune");
		adjustNetwork(numOfNewDeadH, deadPeriod, hospitalizedNodes, deadNodes, "dead");
		adjustNetwork(numOfNewSusceptible, 0.0f, immuneNodes, susceptibleNodes, "susceptible");
		adjustNetwork(numOfNewImmuneF, 0.0f, deadNodes, deadImmuneNodes, "deadImmune");
	}
	
	//
	//
	//function used to distribute a number of nodes from a previous state to the new state
	//
	//
	private void adjustNetwork(int numOfNewCases, float periodOfInfection, LinkedList<Persoana> previousState, LinkedList<Persoana> newState, String condition){
		
		Persoana p;
		
		//System.out.println(numOfNewCases + " " + condition);
		
		while((numOfNewCases > 0) && (previousState.size() != 0)){
			
			p = getRandomActNodes(previousState);
			
			if(p != null){
				numOfNewCases--;
				previousState.remove(p);
				//the infection period should depend on the state in which one passes
				p.addTime(expDistribution(periodOfInfection));
				p.setCondition(condition);
				newState.add(p);
			}
			else break;
		}
		
	}
	
	//
	//
	// function used to get the susceptible nodes around the infected ones into exposed nodes
	//
	//
	private void adjustNetworkI(int numOfNewCases, float periodOfInfection, LinkedList<Persoana> previousState, LinkedList<Persoana> newState, String condition){
		
		Persoana p, q;
		float chance;
		
		//System.out.println(numOfNewCases + " " + condition);
		
		while((numOfNewCases > 0) && (previousState.size() != 0)){
			
			if(infectedNodes.size() > 0){
				p = getRandomActNodes();
				
				//System.out.println(p);
				
				chance = computeChance();
				
				q = p.getEdge(chance);
			}
			else
				q = getRandomActNodes(previousState);
			
			
			if(q == null) q = getRandomActNodes(previousState);
			
			if(q != null){
				numOfNewCases--;
				previousState.remove(q);
				//the infection period should depend on the state in which one passes
				q.addTime(expDistribution(periodOfInfection));				
				q.setCondition(condition);
				newState.add(q);
			}
			else break;
		}
		
	}
	
	
	
	//
	//
	//function used to compute the bacterial resistance developed by hospitalized ppl 
	//
	//
	private void computeBacterialResistance(){
		
		/*for(Persoana p : hospitalizedNodes){
			if(p.isResistant()){
				p.adjustResistance(tau, gamma, resistanceFreq);
			}
			else{
				
				int index = r.nextInt(numOfAntibiotics);
				
				p.adjustResistance(tau, gamma, index);
			}
		}*/
		
		int numOfNodesResistant, numOfNodesCleared;
		Persoana p;
		
		if(hospitalizedNodes.size() > 0){
		
			for(int i = 0; i<resistanceFreq.length; i++){
				
				if(isAnobodyResistant() == false){
					numOfNodesResistant = expDistribution(beta * hospitalizedNodes.size());
					numOfNodesCleared = 0;
					rFreq[i] += numOfNodesResistant;
				}
				else{
					numOfNodesResistant = expDistribution(beta * hospitalizedNodes.size() * rFreq[i]);
					numOfNodesCleared = expDistribution((tau * resistanceFreq[i] + gamma) * rFreq[i]);
					rFreq[i] += Math.max((numOfNodesResistant - numOfNodesCleared), 0); 
				}
				
				while(numOfNodesResistant>0){
					p = getRandomHospNodes();
					p.adjustResistance(beta * resistanceFreq[i], i, true);
					numOfNodesResistant--;
				}
				
				while(numOfNodesCleared>0){
					p = getRandomHospNodes();
					p.adjustResistance(beta * resistanceFreq[i], i, false);
					numOfNodesCleared--;
				}
			}
		}
		
	}
	
	private boolean isAnobodyResistant(){
		for(Persoana p : hospitalizedNodes)
			if(p.isResistant())
				return true;
		return false;
	}
	
	
	//
	//
	//function used to print the ppl from the entire network 
	//
	//
	public String toString(){
		String s="";
		
		/*
			s += "Susceptible nodes : \n";
			
			for(Persoana p : susceptibleNodes)
				s += p.toString();
			
			s += "Exposed nodes : \n";
			
			for(Persoana p : exposedNodes)
				s += p.toString();
		
		
			s += "Infected nodes : \n";
			
			for(Persoana p : infectedNodes)
				s += p.toString();
			*/
			s += "Hospitalized nodes : \n";
			
			for(Persoana p : hospitalizedNodes)
				s += p.toString();
			/*
			s += "Dead nodes : \n";
			
			for(Persoana p : deadNodes)
				s += p.toString();
			
			s += "Immune nodes : \n";
			
			for(Persoana p : immuneNodes)
				s += p.toString();
			
			s += "Immune and dead nodes : \n";
			
			for(Persoana p : deadImmuneNodes)
				s += p.toString();
			
		*/
			
			s += "Num of susceptible nodes : " + susceptibleNodes.size() + "\n";
			
			s += "Num of exposed nodes : " + exposedNodes.size() + "\n";
			
			s += "Num of infected nodes : " + infectedNodes.size() + "\n";
			
			s += "Num of hospitalized nodes : " + hospitalizedNodes.size() + "\n";
			
			s += "Num of dead nodes : " + deadNodes.size() + "\n";
			
			s += "Num of immune nodes : " + immuneNodes.size() + "\n";
			
			s += "Num of dead and immune nodes : " + deadImmuneNodes.size() + "\n";
			
		return s;
	}
	
}
