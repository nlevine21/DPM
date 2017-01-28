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
	private static final int MOTOR_HIGH = 150;
	private static final int MOTOR_LOW = 75;
	private SampleProvider us;
	private float[] usData;
	private static Thread nav;
	private static final int bandwidth = 2;
	private static final int bandCenter = 20;
	
	public UltrasonicPoller(SampleProvider us, float[] usData, Thread nav) {
		this.us = us;
		this.usData = usData;
		this.nav = nav;
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
				followWall();
				break;
			}
			try { Thread.sleep(50); } catch(Exception e){}		// Poor man's timed sampling
		}
	}
	
	private void followWall() {
		while (true) {
			us.fetchSample(usData,0);							// acquire data
			int distance=(int)(usData[0]*100.0);
			bangBang(distance);
			try { Thread.sleep(50); } catch(Exception e){}
		}
	}
	
	private void bangBang(int distance) {
		int distError = bandCenter - distance; 		//Measured error from desired distance
		
		
		if (Math.abs(distError) <= bandwidth) {
			Lab3.leftMotor.setSpeed(MOTOR_HIGH);				
			Lab3.rightMotor.setSpeed(MOTOR_HIGH);				
			Lab3.leftMotor.forward();
			Lab3.rightMotor.forward();
		}
		
		
		else if (distError > 0) {
			Lab3.leftMotor.setSpeed(MOTOR_HIGH+MOTOR_LOW);
			Lab3.rightMotor.setSpeed(MOTOR_LOW);
			Lab3.leftMotor.forward();
			Lab3.rightMotor.forward();
		}

		
		else if (distError < 0) { 
			Lab3.leftMotor.setSpeed(MOTOR_LOW);
			Lab3.rightMotor.setSpeed(MOTOR_HIGH+MOTOR_LOW);
			Lab3.leftMotor.forward();
			Lab3.rightMotor.forward();
		}
	}

}
