package ecprac.tbr440;

import java.io.File;
import java.io.IOException;
import java.util.*;

import ecprac.torcs.client.Controller.Stage;
import ecprac.torcs.genome.Utilities;
import ecprac.torcs.race.Race;
import ecprac.torcs.race.RaceResults;
import ecprac.torcs.race.Race.Termination;
import ecprac.torcs.race.Race.Track;

public class EA {	 	

    final static int POPULATION_SIZE = 4,
                     EVALUATIONS = 400,
                     NR_OF_CHILDREN = 2;
    final static double ALPHA = 0.45,                 //must be between 0 and 1.
    					MUTATION_PROBABILITY = 0.1;   //must be between 0 and 1.
    
    
    Random r;
    Genome[] population;
    int evals;
    double globalFitness,
    	   alpha;	
    FitnessComparator c;
   	
    EA() {
    	r = new Random(); 
    	population = new Genome[POPULATION_SIZE];    	
    	evals = 0;
    	globalFitness = 0.0;
    	c = new FitnessComparator();
    }
    
    public void run() {
    	//TODO: parent selection
		
    	// initialize population
		initialize();		
		evaluateAll();
		evals += POPULATION_SIZE;
		Arrays.sort(population, c); //genomes sorted from worst to best fitness
		
		//debug
		System.out.println("-------------Genomes------------------");
		for (int i = 0; i < POPULATION_SIZE; i++){
			System.out.println("Fitness is: " + population[i].fitness);
			System.out.println("Percentage is: " + (double)evals/EVALUATIONS);
			System.out.println("Weights: ");
			population[i].nn.debugPrintWeights();
			System.out.println("");
		}
		System.out.println("-------------/Genomes-----------------");
		
		try {
			Utilities.saveGenome(population[POPULATION_SIZE-1], "bestInitialization.genome");
			
		
			// the evolutionary loop
			while (evals < EVALUATIONS) {
				//recombineAndMutate(population[POPULATION_SIZE-1], population[POPULATION_SIZE-2], ALPHA, MUTATION_PROBABILITY); //recombine best two parents
				mutate(population[0], MUTATION_PROBABILITY); //debug: mutate worst
				evaluateAll();	
				evals += POPULATION_SIZE;
				Arrays.sort(population, c); //genomes sorted from worst to best fitness
				
				//debug
				System.out.println("-------------Genomes------------------");
				for (int i = 0; i < POPULATION_SIZE; i++){
					System.out.println("Fitness is: " + population[i].fitness);
					System.out.println("Percentage is: " + (double)evals/EVALUATIONS);
					System.out.println("Weights: ");
					population[i].nn.debugPrintWeights();
					System.out.println("");
				}
				System.out.println("-------------/Genomes-----------------");
			
				if ((double)evals/EVALUATIONS == 0.05) Utilities.saveGenome(population[POPULATION_SIZE-1], "best5%.genome");
				else if ((double)evals/EVALUATIONS == 0.10) Utilities.saveGenome(population[POPULATION_SIZE-1], "best10%.genome");
				else if ((double)evals/EVALUATIONS == 0.25) Utilities.saveGenome(population[POPULATION_SIZE-1], "best25%.genome");
				else if ((double)evals/EVALUATIONS == 0.50) Utilities.saveGenome(population[POPULATION_SIZE-1], "best50%.genome");				
			}		
			Utilities.saveGenome(population[POPULATION_SIZE-1], "best.genome");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    //whole arithmetic crossover followed with mutation 
    // -alpha must be between 0 and 1
    // -mutationProbability must be between 0 and 1
    private void recombineAndMutate(Genome parent1, Genome parent2, double alpha, double mutationProbability) {
    	Genome child1 = new Genome();
    	Genome child2 = new Genome();
    	
    	child1.nn = parent1.nn.crossOver(parent2.nn, ALPHA);
    	mutate(child1, mutationProbability);    	
    	child2.nn = parent2.nn.crossOver(parent1.nn, ALPHA);
    	mutate(child2, mutationProbability);
    	    	    	
    	population[0] = child1; //the childs replace the genomes with worst fitnesses
    	population[1] = child2; //the childs replace the genomes with worst fitnesses 
    	Arrays.sort(population, c);
    }
    

    // -mutationProbability must be between 0 and 1
    private void mutate(Genome genome, double mutationProbability) {
        genome.nn.mutate(mutationProbability); 
    }
	
	private void initialize() {
		for (int i = 0; i < POPULATION_SIZE; i++) {
			Genome genome = new Genome();
			genome.fitness = 1000000000.0;
			population[i] = genome;
		}
	}
	
	public void evaluateAll() {

		Race race1 = new Race();
		race1.setTrack(Track.michigan);
		race1.setStage(Stage.QUALIFYING);
		race1.setTermination(Termination.TICKS, 1000);	
		
		Race race2 = new Race();
		race2.setTrack(Track.michigan);
		race2.setStage(Stage.QUALIFYING);
		race2.setTermination(Termination.TICKS, 1000);
		
		Race race3 = new Race();
		race3.setTrack(Track.michigan);
		race3.setStage(Stage.QUALIFYING);
		race3.setTermination(Termination.TICKS, 1000);	
		
		Race race4 = new Race();
		race4.setTrack(Track.michigan);
		race4.setStage(Stage.QUALIFYING);
		race4.setTermination(Termination.TICKS, 1000);
		
		Driver driver1 = new ecprac.tbr440.Driver();
		driver1.init();
		driver1.loadGenome(population[0]);
		race1.addCompetitor(driver1);
				
		Driver driver2 = new ecprac.tbr440.Driver();
		driver2.init();
		driver2.loadGenome(population[1]);
		race2.addCompetitor(driver2);
		
		
		Driver driver3 = new ecprac.tbr440.Driver();
		driver3.init();
		driver3.loadGenome(population[2]);
		race3.addCompetitor(driver3);
		
		Driver driver4 = new ecprac.tbr440.Driver();
		driver4.init();
		driver4.loadGenome(population[3]);
		race4.addCompetitor(driver4);

		// Run in Text Mode
		System.out.println("Race1!");
		RaceResults results1 = race1.run();		
		System.out.println("Race2!");
		RaceResults results2 = race2.run();		
		System.out.println("Race3!");
		RaceResults results3 = race3.run();
		System.out.println("Race4!");
		RaceResults results4 = race4.run();
		
		// Fitness = BestLap, except if both did not do at least one lap
		if( Double.isInfinite(results1.get(driver1).bestLapTime) &&  Double.isInfinite(results2.get(driver2).bestLapTime) &&
			Double.isInfinite(results3.get(driver3).bestLapTime) &&  Double.isInfinite(results4.get(driver4).bestLapTime)){
			  population[0].fitness = results1.get(driver1).distance;
			  population[1].fitness = results2.get(driver2).distance;
			  population[2].fitness = results3.get(driver3).distance;
			  population[3].fitness = results4.get(driver4).distance;
		} else {
			  population[0].fitness = -1 * results1.get(driver1).bestLapTime;
              population[1].fitness = -1 * results2.get(driver2).bestLapTime;
			  population[2].fitness = -1 * results3.get(driver3).bestLapTime;
              population[3].fitness = -1 * results4.get(driver4).bestLapTime;
		}
		
		/*Race race = new Race();
		
		//  One of the two ovals
		race.setTrack( Track.fromIndex(  (evals / 2) % 2 ));
		race.setStage(Stage.RACE);
		race.setTermination(Termination.LAPS, 1);
		
		// Add driver 1
		Driver driver1 = new Driver();
		driver1.init();
		driver1.loadGenome(population[0]);
		race.addCompetitor(driver1);
		
		// Add driver 2
		Driver driver2 = new Driver();
		driver2.init();
		driver2.loadGenome(population[1]);
		race.addCompetitor(driver2);
		
		// Add driver 3
		Driver driver3 = new Driver();
		driver3.init();
		driver3.loadGenome(population[2]);
		race.addCompetitor(driver3);
		
		// Add driver 4
		Driver driver4 = new Driver();
		driver4.init();
		driver4.loadGenome(population[3]);
		race.addCompetitor(driver4);
		
				// Add driver 5
		Driver driver5 = new Driver();
		driver5.init();
		driver5.loadGenome(population[4]);
		race.addCompetitor(driver5);
		
				// Add driver 6
		Driver driver6 = new Driver();
		driver6.init();
		driver6.loadGenome(population[5]);
		race.addCompetitor(driver6);
		
				// Add driver 7
		Driver driver7 = new Driver();
		driver7.init();
		driver7.loadGenome(population[6]);
		race.addCompetitor(driver7);
		
				// Add driver 8
		Driver driver8 = new Driver();
		driver8.init();
		driver8.loadGenome(population[7]);
		race.addCompetitor(driver8);
		
				// Add driver 9
		Driver driver9 = new Driver();
		driver9.init();
		driver9.loadGenome(population[8]);
		race.addCompetitor(driver9);
		
				// Add driver 10
		Driver driver10 = new Driver();
		driver10.init();
		driver10.loadGenome(population[9]);
		race.addCompetitor(driver10);
		
		// Run in Text Mode
		RaceResults results = race.run();
		
		// Fitness = BestLap, except if both did not do at least one lap
		if( Double.isInfinite(results.get(driver1).bestLapTime) &&  Double.isInfinite(results.get(driver2).bestLapTime) &&
			Double.isInfinite(results.get(driver3).bestLapTime) &&  Double.isInfinite(results.get(driver4).bestLapTime) &&
		    Double.isInfinite(results.get(driver5).bestLapTime) &&  Double.isInfinite(results.get(driver6).bestLapTime) &&
		    Double.isInfinite(results.get(driver7).bestLapTime) &&  Double.isInfinite(results.get(driver8).bestLapTime) &&
		    Double.isInfinite(results.get(driver9).bestLapTime) &&  Double.isInfinite(results.get(driver10).bestLapTime)){
			  population[0].fitness = results.get(driver1).distance;
			  population[1].fitness = results.get(driver2).distance;
			  population[2].fitness = results.get(driver3).distance;
			  population[3].fitness = results.get(driver4).distance;
			  population[4].fitness = results.get(driver5).distance;
			  population[5].fitness = results.get(driver6).distance;
			  population[6].fitness = results.get(driver7).distance;
			  population[7].fitness = results.get(driver8).distance;
			  population[8].fitness = results.get(driver9).distance;
			  population[9].fitness = results.get(driver10).distance;
		} else {
			  population[0].fitness = -1 * results.get(driver1).bestLapTime;
			  population[1].fitness = -1 * results.get(driver2).bestLapTime;
			  population[2].fitness = -1 * results.get(driver3).bestLapTime;
			  population[3].fitness = -1 * results.get(driver4).bestLapTime;
			  population[4].fitness = -1 * results.get(driver5).bestLapTime;
			  population[5].fitness = -1 * results.get(driver6).bestLapTime;
			  population[6].fitness = -1 * results.get(driver7).bestLapTime;
			  population[7].fitness = -1 * results.get(driver8).bestLapTime;
			  population[8].fitness = -1 * results.get(driver9).bestLapTime;
			  population[9].fitness = -1 * results.get(driver10).bestLapTime;
		}*/
		
	}
	
	public void show(){
		try {
			
			Race race = new Race();
			race.setTrack(Track.michigan);
			race.setStage(Stage.QUALIFYING);
			race.setTermination(Termination.LAPS, 3);			
			
			Driver driver = new ecprac.tbr440.Driver();
			driver.init();
			driver.loadGenome(Utilities.loadGenome("best.genome"));
			race.addCompetitor(driver);

			race.runWithGUI();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

    public static void main( String[] args ) {
		
		/*
		 * 
		 * Start without arguments to run the EA, start with -show to show the best found
		 * 
		 */
		
		if(args.length > 0 && args[0].equals("-show")){
			new EA().show();
		} else {
			new EA().run();
		}
	}
}
