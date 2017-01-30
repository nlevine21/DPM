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
	private static double previousAngle = 0;
	private boolean onFirstPath;

	
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
				goAroundWall(distance);
				break;
			}
			try { Thread.sleep(50); } catch(Exception e){}		// Poor man's timed sampling
		}
		
		travelTo(60,0);
		
		
	}
	

	private void goAroundWall(int distance) {
		turnTo(15);
		turnTo(90);
		
		for (int i=0; i<3; i++) {
			
		int travelDist = 30;
		
		if (i==1) {
			travelDist =  15 + 2*distance;
		}
			
		Lab3.leftMotor.setSpeed(FORWARD_SPEED);
		Lab3.rightMotor.setSpeed(FORWARD_SPEED);

		Lab3.leftMotor.rotate(convertDistance(Lab3.WHEEL_RADIUS, travelDist), true);
		Lab3.rightMotor.rotate(convertDistance(Lab3.WHEEL_RADIUS, travelDist), false);
		
		if (i<2) 
			turnTo(-90);
		
		if (i==2) {
			turnTo(90);
		}
	
		
		}
		
		

		}
	

	
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
	

	public static void travelTo (double x, double y){
		
		double initX = odo.getX();
		double initY = odo.getY();
		double previousAngle = odo.getTheta() * 180/Math.PI;
		
		double angle = Math.atan2(x - initX, y - initY);
		angle = angle*180/Math.PI;

		/*if (Math.abs(previousAngle) >=180){
			previousAngle = 360 - Math.abs(previousAngle);
		}*/
		
		turnTo(-previousAngle+angle);
		
		Lab3.leftMotor.setSpeed(FORWARD_SPEED);
		Lab3.rightMotor.setSpeed(FORWARD_SPEED);
		
		
		double distance;
		
		distance = Math.pow((x - initX),2) + Math.pow((y - initY),2);
		distance = Math.pow(distance, 0.5);

		Lab3.leftMotor.rotate(convertDistance(Lab3.WHEEL_RADIUS, distance), true);
		Lab3.rightMotor.rotate(convertDistance(Lab3.WHEEL_RADIUS, distance), false);
		
	}
	
	
	public static void turnTo (double theta){
		
	
		
		Lab3.leftMotor.setSpeed(ROTATE_SPEED);
		Lab3.rightMotor.setSpeed(ROTATE_SPEED);
		
		
		Lab3.leftMotor.rotate(convertAngle(Lab3.WHEEL_RADIUS, Lab3.TRACK, theta), true);
		Lab3.rightMotor.rotate(-convertAngle(Lab3.WHEEL_RADIUS, Lab3.TRACK, theta), false);
			
		
		
		
	}
}
