package ecprac.tbr440;

import ecprac.torcs.client.Action;
import ecprac.torcs.client.SensorModel;
import ecprac.torcs.controller.GenomeDriver;
import ecprac.torcs.controller.extras.ABS;
import ecprac.torcs.controller.extras.AutomatedClutch;
import ecprac.torcs.controller.extras.AutomatedGearbox;
import ecprac.torcs.controller.extras.AutomatedRecovering;
import ecprac.torcs.genome.IGenome;

public class Driver extends GenomeDriver {

	private int maxSpeed;
	private double steering;
	private double trackpos;


	public void init() {
		enableExtras(new AutomatedClutch());
		enableExtras(new AutomatedGearbox());
		enableExtras(new AutomatedRecovering());
		enableExtras(new ABS());
	}

	public void loadGenome(IGenome genome) {

		if (genome instanceof Genome) {
			Genome llgenome = (Genome) genome;
			maxSpeed = llgenome.speed;
			steering = llgenome.steering;
			trackpos = llgenome.trackpos;
		} else {
			System.err.println("Invalid Genome assigned");
		}

	}

	public void control(Action action, SensorModel sensors) {

		if (sensors.getSpeed() < maxSpeed) {
			action.accelerate = 1;
		}
		action.steering = 0;
		
		if(sensors.getTrackPosition() < -1 * trackpos){
			action.steering = steering * Math.abs(sensors.getTrackPosition()); 		// steer right
		} 
		if(sensors.getTrackPosition() > trackpos){
			action.steering =  -1 *  steering * Math.abs(sensors.getTrackPosition());  // steer left
		}
		
		
	}

	public String getDriverName() {
		return "Kai";
	}

	public float[] initAngles() {
		return super.initAngles();

	}
}
