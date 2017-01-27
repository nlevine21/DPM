package lab3;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
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
	private SampleProvider us;
	private float[] usData;
	private static EV3LargeRegulatedMotor leftMotor; 
	private static EV3LargeRegulatedMotor rightMotor;
	private static Thread nav;
	private static final int FORWARD_SPEED = 250;
	private static final int ROTATE_SPEED = 150;
	
	public UltrasonicPoller(SampleProvider us, float[] usData,EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, Thread nav) {
		this.us = us;
		this.usData = usData;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.nav = nav;
	}

//  Sensors now return floats using a uniform protocol.
//  Need to convert US result to an integer [0,255]
	
	public void run() {
		int distance;
		while (true) {
			us.fetchSample(usData,0);							// acquire data
			distance=(int)(usData[0]*100.0);					// extract from buffer, cast to int						// now take action depending on value
			correction(distance, 10);
			try { Thread.sleep(50); } catch(Exception e){} // Poor man's timed sampling
		}
		
	}
	
	public void correction (int distance, int bandwidth) {
		int distError = distance - bandwidth;
		
		if (Math.abs(distance) <= 2)
			return;
		else if (distError > 0)
			return;
		else if (distError < 0) {
			nav.interrupt();
			
			rightMotor.stop();
			leftMotor.stop();
			
			turnClockwise();
			for (int i=0; i<2; i++) {
				turnCounterClockwise();
			}
			
			leftMotor.rotate(convertAngle(Lab3.WHEEL_RADIUS, Lab3.TRACK, 90.0), true);
			rightMotor.rotate(-convertAngle(Lab3.WHEEL_RADIUS, Lab3.TRACK, 90.0), false);

		}
	}
	
	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
	
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}
	
	private static void turnClockwise() {
		// turn 90 degrees clockwise
		Sound.beep();
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);

		leftMotor.rotate(convertAngle(Lab3.WHEEL_RADIUS, Lab3.TRACK, 90.0), true);
		rightMotor.rotate(-convertAngle(Lab3.WHEEL_RADIUS, Lab3.TRACK, 90.0), false);
		
		// drive forward two tiles
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);

		leftMotor.rotate(convertDistance(Lab3.WHEEL_RADIUS, 25), true);
		rightMotor.rotate(convertDistance(Lab3.WHEEL_RADIUS, 25), false);
	}
	
	private static void turnCounterClockwise() {
		// turn 90 degrees counter-clockwise
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);

		leftMotor.rotate(-convertAngle(Lab3.WHEEL_RADIUS, Lab3.TRACK, 90.0), true);
		rightMotor.rotate(convertAngle(Lab3.WHEEL_RADIUS, Lab3.TRACK, 90.0), false);
		
		// drive forward two tiles
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);

		leftMotor.rotate(convertDistance(Lab3.WHEEL_RADIUS, 25), true);
		rightMotor.rotate(convertDistance(Lab3.WHEEL_RADIUS, 25), false);
		
	}

}
