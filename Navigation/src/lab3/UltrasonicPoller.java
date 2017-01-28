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
	private static final int FORWARD_SPEED = 250;
	private static final int ROTATE_SPEED = 150;
	private SampleProvider us;
	private float[] usData;
	private static Thread nav;
	private static UltrasonicController cont;
	
	public UltrasonicPoller(SampleProvider us, float[] usData, UltrasonicController cont, Thread nav) {
		this.us = us;
		this.usData = usData;
		this.nav = nav;
		this.cont = cont;
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
			cont.processUSData(distance);
			try { Thread.sleep(50); } catch(Exception e){}		// Poor man's timed sampling
	}
	
	
	
	}
}
