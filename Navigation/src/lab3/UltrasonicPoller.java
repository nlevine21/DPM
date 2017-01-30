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
	private SampleProvider us;
	private float[] usData;
	private static Thread nav;
	

	
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
				goAroundWall(distance);
				break;
			}
			try { Thread.sleep(50); } catch(Exception e){}		// Poor man's timed sampling
		}
		
		Navigator.travelTo(60,0);
		
		
	}
	

	private void goAroundWall(int distance) {
		Navigator.turnTo(15);
		Navigator.turnTo(90);
		
		for (int i=0; i<3; i++) {
			
		int travelDist = 30;
		
		if (i==1) {
			travelDist =  15 + 2*distance;
		}
			
		travelForward(travelDist);
		
		if (i<2) 
			Navigator.turnTo(-90);
		else
			Navigator.turnTo(90);
		
		}
		
	}
	
	private static void travelForward(int distance) {
		Lab3.leftMotor.setSpeed(FORWARD_SPEED);
		Lab3.rightMotor.setSpeed(FORWARD_SPEED);

		Lab3.leftMotor.rotate(Navigator.convertDistance(Lab3.WHEEL_RADIUS, distance), true);
		Lab3.rightMotor.rotate(Navigator.convertDistance(Lab3.WHEEL_RADIUS, distance), false);
		
	}

}
