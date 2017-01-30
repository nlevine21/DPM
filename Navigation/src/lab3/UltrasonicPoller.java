package lab3;
import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

//
//  Control of the wall follower is applied periodically by the 
//  UltrasonicPoller thread.  The while loop at the bottom executes
//  in a loop.  Assuming that the us.fetchSample, and cont.processUSData
//  methods operate in about 20mS, and that the thread sleeps for
//  50 mS at the end of each loop, then one cycle through the loop
//  is approximately 70 mS.  This corresponds to a sampling rate
//  of 1/70mS or about 14 Hz.
//


public class UltrasonicPoller extends Thread{
	private static final int FORWARD_SPEED = 200;
	private static final int ROTATE_SPEED = 100;
	private SampleProvider us;
	private float[] usData;
	private static Thread nav;

	
	private static Odometer odo;
	
	public UltrasonicPoller(SampleProvider us, float[] usData, Odometer odo, Thread nav) {
		this.us = us;
		this.usData = usData;
		this.nav = nav;
		this.odo = odo;
	}

//  Sensors now return floats using a uniform protocol.
//  Need to convert US result to an integer [0,255]
	
	public void run() {
		int distance;
		while (true) {
			us.fetchSample(usData,0);							// acquire data
			distance=(int)(usData[0]*100.0);					// extract from buffer, cast to int
			
			if (distance <= 20) {
				
				Lab3.leftMotor.stop(); Lab3.rightMotor.stop();
				nav.interrupt();
				followWall(distance);
				break;
			}
			try { Thread.sleep(50); } catch(Exception e){}		// Poor man's timed sampling
		}
	}
	
	private void followWall(int distance) {
		
			goAroundWall(distance);
		
		
		if (Navigator2.onFirstPath) {
			Navigator2.travelTo(0, 60);
			Navigator2.travelTo(60, 0);
		}
		else {
			Navigator2.travelTo(60, 0);
		}
	}
	
	private void goAroundWall(int distance) {
		Lab3.leftMotor.rotate(convertAngle(Lab3.WHEEL_RADIUS, Lab3.TRACK, 15.0), true);
		Lab3.rightMotor.rotate(-convertAngle(Lab3.WHEEL_RADIUS, Lab3.TRACK, 15.0), false);
		turnClockwise();
		
		for (int i=0; i<3; i++) {
			
		
		while (true) {
		int travelDist = 30;
		
		if (i==2) {
			travelDist = travelDist + 2*distance;
		}
			
		Lab3.leftMotor.setSpeed(FORWARD_SPEED);
		Lab3.rightMotor.setSpeed(FORWARD_SPEED);

		Lab3.leftMotor.rotate(convertDistance(Lab3.WHEEL_RADIUS, travelDist), true);
		Lab3.rightMotor.rotate(convertDistance(Lab3.WHEEL_RADIUS, travelDist), false);
		
		turnCounterClockwise();
		
		us.fetchSample(usData,0);							// acquire data
		int distance2=(int)(usData[0]*100.0);
		
		if (distance2 < 20 && i<2) {
			turnClockwise();
			i--;
		}
		
		if (i==3) {
			turnClockwise();
			break;
		}
	}
		
		}
		
		

		}
	
	
	private boolean isBackOnPath (double x, double y) {
	
		if (Navigator2.onFirstPath) {
			
			if (Math.abs(x) <= 1) 
				return true;
			else 
				return false;
			
		}
		else {
			int a = 1;
			int b = 60;
			
			if(Math.abs(y-((a*x)+b)) <= 1)
				return true;
			else
				return false;
		}
	
	}
	
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
	
	private static void turnClockwise() {

		// turn 90 degrees clockwise
		Lab3.leftMotor.setSpeed(ROTATE_SPEED);
		Lab3.rightMotor.setSpeed(ROTATE_SPEED);

		Lab3.leftMotor.rotate(convertAngle(Lab3.WHEEL_RADIUS, Lab3.TRACK, 90.0), true);
		Lab3.rightMotor.rotate(-convertAngle(Lab3.WHEEL_RADIUS, Lab3.TRACK, 90.0), false);
	}
	
	private static void turnCounterClockwise() {

		// turn 90 degrees clockwise
		Lab3.leftMotor.setSpeed(ROTATE_SPEED);
		Lab3.rightMotor.setSpeed(ROTATE_SPEED);

		Lab3.leftMotor.rotate(-convertAngle(Lab3.WHEEL_RADIUS, Lab3.TRACK, 90.0), true);
		Lab3.rightMotor.rotate(convertAngle(Lab3.WHEEL_RADIUS, Lab3.TRACK, 90.0), false);
	}
}
