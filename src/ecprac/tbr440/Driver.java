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

	private double steering;
	private double trackpos;
    private EvoNN nn;


	public void init() {
		enableExtras(new AutomatedClutch());
		enableExtras(new AutomatedGearbox());
		enableExtras(new AutomatedRecovering());
		enableExtras(new ABS());
	}

	public void loadGenome(IGenome genome) {

		if (genome instanceof Genome) {
			Genome llgenome = (Genome) genome;
            steering = 0.1;
			trackpos = 0.2;
            nn = llgenome.nn;
		} else {
			System.err.println("Invalid Genome assigned");
		}

	}

	public void control(Action action, SensorModel sensors) {
        action.steering = 0.0;

        nn.setSpeedX(sensors.getSpeed());
        nn.setSpeedY(sensors.getLateralSpeed());
        nn.setTrack(sensors.getTrackEdgeSensors());
        nn.setAngle(sensors.getAngleToTrackAxis());
        nn.setTrackPosition(sensors.getTrackPosition());
        nn.setOpponent(sensors.getOpponentSensors());
        nn.calculate();

        action.steering = nn.getSteering();
        action.accelerate = nn.getAccelerate();
 
        // We can leave this here to steer stupid drivers back on track
		if(sensors.getTrackPosition() < -1 * trackpos) { // from the track
			action.steering = steering * Math.abs(sensors.getTrackPosition()); 		// steer right
		} 
		if(sensors.getTrackPosition() > trackpos){ // from the track
			action.steering =  -1 *  steering * Math.abs(sensors.getTrackPosition());  // steer left
		}
	}

	public String getDriverName() {
		return "Kai, Andrea and Tycho";
	}

	public float[] initAngles() {
		return super.initAngles();

	}
}
