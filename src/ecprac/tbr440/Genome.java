package ecprac.tbr440;

import ecprac.torcs.genome.IGenome;

public class Genome implements IGenome {    
   
    public double fitness;
    public EvoNN nn;
  
    Genome() {
	fitness = 1.0;
	nn = new EvoNN();
    }
}




