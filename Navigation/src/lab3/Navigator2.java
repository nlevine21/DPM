package lab3;



import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class Navigator2 extends Thread{
	
	//Port needed for sensor
	private static final Port usPort = LocalEV3.get().getPort("S1");
	

	
	
	
	public static void drive(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
			double leftRadius, double rightRadius, double width) {
		

		//Set up sensor
		@SuppressWarnings("resource")							    // Because we don't bother to close this resource
		SensorModes usSensor = new EV3UltrasonicSensor(usPort);		// usSensor is the instance
		SampleProvider usDistance = usSensor.getMode("Distance");	// usDistance provides samples from this instance
		float[] usData = new float[usDistance.sampleSize()];		// usData is the buffer in which data are returned
		
		// reset the motors
		for (EV3LargeRegulatedMotor motor : new EV3LargeRegulatedMotor[] { leftMotor, rightMotor }) {
			motor.stop();
			motor.setAcceleration(3000);
		}

		// wait 5 seconds
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// there is nothing to be done here because it is not expected that
			// the odometer will be interrupted by another thread
		}

		 {
			 
			 	//Create a new ObjectAvoider thread which will check for objects obstructing the path. Start the thread
			 	 ObjectAvoider avoider = new ObjectAvoider(usDistance, usData,Thread.currentThread());
			 	 avoider.start();
			 	 
			 	//Travel to requested locations 
				Navigator.travelTo(0,60);	
				Navigator.travelTo(60,0);
				

		
		}
		
		
		}
}