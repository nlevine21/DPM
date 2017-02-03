package Lab4;


import lejos.robotics.SampleProvider;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	public static int ROTATION_SPEED = 75;

	

	private Odometer odo;
	private SampleProvider usSensor;
	private float[] usData;
	private LocalizationType locType;

	public USLocalizer(Odometer odo,  SampleProvider usSensor, float[] usData, LocalizationType locType) {
		this.odo = odo;
		this.usSensor = usSensor;
		this.usData = usData;
		this.locType = locType;
	}
	
	
	public void doLocalization() {
		double [] pos = new double [3];
		double angleA, angleB;
		double anglePrime = 10;
		double angleTemp=0;
		
		if (locType == LocalizationType.FALLING_EDGE) {
			// rotate the robot until it sees no wall
			
			if ((getFilteredData()*100) < 40){
				Rotate (180);
				
			}
			
			while (true){
				
				Rotate (anglePrime);
				if ((getFilteredData()*100) < 40) {break;}
				angleTemp = anglePrime + angleTemp;
					
			}
			
			// keep rotating until the robot sees a wall, then latch the angle
			
			angleA = odo.getAng() - 180;
			System.out.print("");
			System.out.print("");
			System.out.print("");
			System.out.print("");

			
			System.out.println("ANGLEA =" + angleA);
		
			
			// switch direction and wait until it sees no wall
			Rotate (-angleTemp);
			
			while (true){
				Rotate ( -anglePrime);
				if ((getFilteredData()*100) < 30) { break;}
		
			}
			
			// keep rotating until the robot sees a wall, then latch the angle
			angleB = odo.getAng() - 180;
			System.out.println("ANGLEB =" + angleB);

			
			// angleA is clockwise from angleB, so assume the average of the
			// angles to the right of angleB is 45 degrees past 'north'
			
			// update the odometer position (example to follow:)
			odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});
		} else {
			//
			// The robot should turn until it sees the wall, then look for the
			 // "rising edges:" the points where it no longer sees the wall.
			 //This is very similar to the FALLING_EDGE routine, but the robot
			 //will face toward the wall for most of it.
			 //
			
			//
			// FILL THIS IN
			//
		} 
	} 
	

	private float getFilteredData() {
		usSensor.fetchSample(usData, 0);
		float distance = usData[0];
				
		return distance;
	}

	public static void Rotate (double theta){
		
		Lab4.leftMotor.setSpeed(ROTATION_SPEED);
		Lab4.rightMotor.setSpeed(ROTATION_SPEED);
		Lab4.leftMotor.setAcceleration(150);
		Lab4.leftMotor.setAcceleration(150);

		
		
		
		Lab4.leftMotor.rotate(convertAngle(Lab4.WHEEL_RADIUS, Lab4.TRACK, theta), true);
		Lab4.rightMotor.rotate(-convertAngle(Lab4.WHEEL_RADIUS, Lab4.TRACK, theta), false);
		
	}
	
	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
	
	public static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}
	

}