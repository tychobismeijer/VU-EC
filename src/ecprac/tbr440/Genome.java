package ecprac.tbr440;

import ecprac.torcs.genome.IGenome;

public class Genome implements IGenome {    
   
    public double fitness;
   	public int speed;
	public double steering;
	public double trackpos;
	
	Genome() {
		fitness = 1.0;
		speed = 15;
		steering = 0.1;
		trackpos = 0.01;
	}
}




