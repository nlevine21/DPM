/* 
 * OdometryCorrection.java
 */
package ev3Odometer;

import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	
	//public static Port sensorPort = LocalEV3.get().getPort("S1");   
	public static Port portColor = LocalEV3.get().getPort("S1");
	public static SensorModes myColor = new EV3ColorSensor(portColor);
	public static SampleProvider myColorSample = myColor.getMode("Red");
	public static float[] sampleColor = new float[myColor.sampleSize()];
	public static int numSamples = 0;
	private Odometer odometer;
	public static int count = 0;
	boolean testLine = false;
	// constructor
	public OdometryCorrection(Odometer odometer) {
		this.odometer = odometer;
	}

	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd = 0;
		double tempDist = 0; //temporary distance for y axis
	
		
	
		
		while (true) {
			correctionStart = System.currentTimeMillis();
	
			myColorSample.fetchSample(sampleColor,0);
			
			// If a black Line is detected make boolean testLine true
			
			if ( (sampleColor[0]*1000) < 200) {
				testLine = true;
				Sound.beep();
				
			}
			
			// If the line is there increment the amount of lines crossed and make the boolean
			// testLine variable false
			if (testLine == true){
				
				testLine = false;
				count++;
						
				
				/*
				 * ROBOT MOVING IN POSITIVE Y DIRECTION
				 */
				
				//Keep the reading that the odometer reads at the first black line as a reference point
				//Store the reference point to the tempDist variable
				if (count == 1){ 
					
					tempDist =  odometer.getY();
				}
				
				switch (count){
				
				//When the robot hits the 2nd or 3rd line, we know that the odometer should read
				// 30.48+tempDist or 2*30.48+tempDist respectively. Update accordingly
				case (2) : case (3) :{
					
					odometer.setY(tempDist + ((count - 1)* (30.48)));
					break;
					
				}
				
				/*
				 * ROBOT MOVING IN POSITIVE X DIRECTION
				 */
				
				//Similar to Y, keep the first reading as a reference point
				case (4):{
					tempDist = odometer.getX();
					break;
				
				}
				
				//Apply same logic as when robot was moving forward in Y direction
				case (5): case(6):{
					odometer.setX(tempDist + ((count - 4)* (30.48)));
					break;
					
				}
				
				/*
				 * ROBOT MOVING IN NEGATIVE Y DIRECTION
				 */
				
				//Apply same logic as for when robot was moving in positive Y/X
				case (7):{
					tempDist = odometer.getY();
					break;
				

				}
				
				case (8): case (9): {
					odometer.setY(tempDist - ((count - 7)* (30.48)));
					break;
		
				}
				
				/*
				 * ROBOT MOVING IN NEGATIVE X DIRECTION
				 */
				
				
				//Same logic is applied as before
				case (10):{
					tempDist = odometer.getX();
					break;
				
				}
					
				case (11) : case (12): {
					odometer.setX(tempDist - ((count - 10)* (30.48)));
					break;

				}
					
				}
				
	

				
				//Sound.beep();
			
			}
			
			numSamples++;
				
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// this ensure the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
			
			
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
	}
}