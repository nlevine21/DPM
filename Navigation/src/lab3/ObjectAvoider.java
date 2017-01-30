package lab3;
import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;



public class ObjectAvoider extends Thread{
	
	//Constants
	private static final int FORWARD_SPEED = 200;
	private static final int OBJECT_CLOSE_DISTANCE = 20;
	private static final int OBJECT_LENGTH = 25;
	private static final int OBJECT_WIDTH = 15;
	
	//Sensor data
	private SampleProvider us;
	private float[] usData;
	
	//The object avoider has a navigator thread which is interrupted if needed
	private static Thread nav;
	

	/*
	 * Constructor
	 */
	public ObjectAvoider(SampleProvider us, float[] usData, Thread nav) {
		this.us = us;
		this.usData = usData;
		this.nav = nav;
	}

		
	public void run() {
		
		int distance;
		while (true) {
			//Acquire data and cast to int
			us.fetchSample(usData,0);							
			distance=(int)(usData[0]*100.0);				
			
			//Loop until the object is too close 
			if (distance <= OBJECT_CLOSE_DISTANCE) {
				
				//Stop the motors and interrupt the navigator
				Lab3.leftMotor.stop(); Lab3.rightMotor.stop();
				nav.interrupt();
				
				//Go around the wall and stop obtaining values from the sensor (we have already avoided the object)
				goAroundObject(distance);
				break;
			}
			try { Thread.sleep(50); } catch(Exception e){}		// Poor man's timed sampling
		}
		
		/*
		 * Travel to the destination (60,0)
		 * 
		 * Case 1: The object appears on the first path. After the robot goes around the object he mustve travelled at least 55cm 
		 * in the x direction. He is thus approximately at (0,60). We can travel to (60,0)
		 * 
		 * Case 2: The object appears on the second path. After the robot goes around the object we must travel to (60,0)
		 */
		Navigator.travelTo(60,0);
		
		
	}
	

	/*
	 * Method which allows robot to go around object. 
	 * The distance that object is from the robot is given
	 */
	private void goAroundObject(int distance) {
		//Turn 15 degrees to undo -15 degree offset caused by interrupting the navigator thread
		Navigator.turnTo(15);
		
		//Turn the robot clockwise
		Navigator.turnTo(90);
		
		for (int i=0; i<3; i++) {
		
		//The travel distance should be the length of the object in order for successful avoidance	
		int travelDist = OBJECT_LENGTH;
		
		//However in iteration 2, the travel distance should be the width plus twice the initial distance for successful avoidance
		if (i==1) {
			travelDist =  OBJECT_WIDTH + 2*distance;
		}
			
		//Travel forward
		travelForward(travelDist);
		
		//Turn the robot counter clock-wise. Unless it is around the object, then turn it clockwise
		if (i<2) 
			Navigator.turnTo(-90);
		else
			Navigator.turnTo(90);
		
		}
		
	}
	
	/*
	 * This method tells the robot to travel a certain distance forward linearly 
	 */
	private static void travelForward(int distance) {
		Lab3.leftMotor.setSpeed(FORWARD_SPEED);
		Lab3.rightMotor.setSpeed(FORWARD_SPEED);

		Lab3.leftMotor.rotate(Navigator.convertDistance(Lab3.WHEEL_RADIUS, distance), true);
		Lab3.rightMotor.rotate(Navigator.convertDistance(Lab3.WHEEL_RADIUS, distance), false);
		
	}

}
