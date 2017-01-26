package lab3;



import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class Navigator2 extends Thread{
	private static final int FORWARD_SPEED = 250;
	private static final int ROTATE_SPEED = 150;
	private static int i =1 ;
	private static Odometer odometer;
	
	private static final int bandCenter = 35;			// Offset from the wall (cm)
	private static final int bandWidth = 2;				// Width of dead band (cm)
	private static final Port usPort = LocalEV3.get().getPort("S1");

	public static void drive(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
			double leftRadius, double rightRadius, double width, Odometer odo) {
		
		odometer = odo;
		
	/* CONTROLLER
	 * 
	 * 	
		PController p = new PController(leftMotor, rightMotor, bandCenter, bandWidth);
		
		@SuppressWarnings("resource")							    // Because we don't bother to close this resource
		SensorModes usSensor = new EV3UltrasonicSensor(usPort);		// usSensor is the instance
		SampleProvider usDistance = usSensor.getMode("Distance");	// usDistance provides samples from this instance
		float[] usData = new float[usDistance.sampleSize()];		// usData is the buffer in which data are returned
		
		// Setup Ultrasonic Poller									// This thread samples the US and invokes
		UltrasonicPoller usPoller = null;							// the selected controller on each cycle
						
		usPoller = new UltrasonicPoller(usDistance, usData, p);
		
		usPoller.start();
		
		*/
		
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
				 travelTo(0,60);			 
				 travelTo(60,0);
	
		
		}
		
		
	}

	public static void travelTo (double x, double y){
		
		double initX = odometer.getX();
		double initY = odometer.getY();
		
		
		if (i==1)
			 turnTo (0);
		if (i==2)
			 turnTo(-135);
		i++;
		
		Lab3.leftMotor.setSpeed(FORWARD_SPEED);
		Lab3.rightMotor.setSpeed(FORWARD_SPEED);
		
		double distance;
		
		distance = Math.pow((x - initX),2) + Math.pow((y - initY),2);
		distance = Math.pow(distance, 0.5);

		Lab3.leftMotor.rotate(convertDistance(Lab3.WHEEL_RADIUS, distance), true);
		Lab3.rightMotor.rotate(convertDistance(Lab3.WHEEL_RADIUS, distance), false);
		
	}
	
	
	public static void turnTo (double theta){
		
		double abs = Math.abs(theta);
		
		Lab3.leftMotor.setSpeed(ROTATE_SPEED);
		Lab3.rightMotor.setSpeed(ROTATE_SPEED);
		
		if (theta >=0){
			Lab3.leftMotor.rotate(-convertAngle(Lab3.WHEEL_RADIUS, Lab3.TRACK, abs), true);
			Lab3.rightMotor.rotate(convertAngle(Lab3.WHEEL_RADIUS, Lab3.TRACK, abs), false);
		}
		else {
			Lab3.leftMotor.rotate(convertAngle(Lab3.WHEEL_RADIUS, Lab3.TRACK, abs), true);
			Lab3.rightMotor.rotate(-convertAngle(Lab3.WHEEL_RADIUS, Lab3.TRACK, abs), false);
			
		}
		
		
	}
	
	
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
}