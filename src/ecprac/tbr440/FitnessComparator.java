package ecprac.tbr440;

import java.util.Comparator;

public class FitnessComparator implements Comparator{

	public int compare(Object genome1, Object genome2){
		double genome1Fitness = ( (Genome) genome1).fitness;
		double genome2Fitness = ( (Genome) genome2).fitness;
		
		if (genome1Fitness > genome2Fitness) return 1;
		else if (genome1Fitness < genome2Fitness) return -1;
		else return 0;
	}
}

