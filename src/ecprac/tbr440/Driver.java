package ecprac.daniel;

import ecprac.torcs.client.Action;
import ecprac.torcs.client.SensorModel;
import ecprac.torcs.controller.GenomeDriver;
import ecprac.torcs.controller.extras.ABS;
import ecprac.torcs.controller.extras.AutomatedClutch;
import ecprac.torcs.controller.extras.AutomatedGearbox;
import ecprac.torcs.controller.extras.AutomatedRecovering;
import ecprac.torcs.genome.IGenome;

public class Driver extends GenomeDriver {

        NeuralNetwork nn;    

	public void init() {
		enableExtras(new AutomatedClutch());
		enableExtras(new AutomatedGearbox());
		enableExtras(new AutomatedRecovering());
		enableExtras(new ABS());
	}

	public void loadGenome(IGenome genome) {

		if (genome instanceof Genome) {
			Genome llgenome = (Genome) genome;
			nn = llgenome.nn;
		} else {
			System.err.println("Invalid Genome assigned");
		}

	}

	public void control(Action action, SensorModel sensors) {

	// ask neural network	
		
	}

	public String getDriverName() {
		return "";
	}

	public float[] initAngles() {
		return super.initAngles();

	}
}
