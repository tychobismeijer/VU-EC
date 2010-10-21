package ecprac.tbr440;

import ecprac.torcs.genome.IGenome;

public class Genome implements IGenome {    
   
    public double fitness,
    			  damage;
    public EvoNN nn;
  
    Genome() {
    	damage = 0.0;
    	fitness = 0.0;
    	nn = new EvoNN();
    }
}




