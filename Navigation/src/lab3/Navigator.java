package lab3;


import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Navigator extends Thread{
	
	//Constants
	private static final int FORWARD_SPEED = 250;
	private static final int ROTATE_SPEED = 150;
	private static final int THRESHOLD = 2;

	public static void drive(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
			double leftRadius, double rightRadius, double width) {
		
		
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
			    //Travel to the requested coordinates
				 travelTo(60,30);
				 travelTo(30,30);
				 travelTo(30,60);
				 travelTo (60,0);
				 
		
		}
		
		
	}

	/*
	 * The travelTo method makes the robot travel to the requested (x,y) location on the grid
	 */
	public static void travelTo (double x, double y){
		
		//Get the inital x and y coordinates of the robot from the odometer
		double initX = Lab3.odometer.getX();
		double initY = Lab3.odometer.getY();
		
		//Get the initial angle from the odometer (convert to degrees)
		double previousAngle = Lab3.odometer.getTheta() * 180/Math.PI;
		
		//Limit the previous angle to be in the range (0,180)
		if (Math.abs(previousAngle) >=180){
			previousAngle = 360 - Math.abs(previousAngle);
		}
		
		//Calculate the additional angle needed to turn (convert to degrees)
		double angle = Math.atan2(x - initX, y - initY);
		angle = angle*180/Math.PI;

		//Turn the minimum angle needed
		turnTo (-previousAngle+angle);
		
		//Set the motors to FORWARD_SPEED
		Lab3.leftMotor.setSpeed(FORWARD_SPEED);
		Lab3.rightMotor.setSpeed(FORWARD_SPEED);
		
		//Calculate the linear distance needed to travel
		double distance;
		distance = Math.pow((x - initX),2) + Math.pow((y - initY),2);
		distance = Math.pow(distance, 0.5);

		//Rotate both wheels for the calculated distance
		Lab3.leftMotor.rotate(convertDistance(Lab3.WHEEL_RADIUS, distance), true);
		Lab3.rightMotor.rotate(convertDistance(Lab3.WHEEL_RADIUS, distance), true);
		
		/*
		 * We have set the immediate return parameter on the second rotate to be true. This is done because this thread may
		 * be interrupted by another thread. We thus have to stay in this block until the robot has reached its destination.
		 * In order to do so, we use the odometer and check whether the robot is within a threshold of its desired location.
		 */
		while (true) {
			double currentX = Lab3.odometer.getX();
			double currentY = Lab3.odometer.getY();
			
			if (Math.abs(currentX - x) <= THRESHOLD && Math.abs(currentY - y) <= THRESHOLD) {
				break;
			}
		}
		

	}
	
	/*
	 * This method turns the robot to a desired angle represented in degrees
	 */
	public static void turnTo (double theta){
	
		Lab3.leftMotor.setSpeed(ROTATE_SPEED);
		Lab3.rightMotor.setSpeed(ROTATE_SPEED);
		
		
		Lab3.leftMotor.rotate(convertAngle(Lab3.WHEEL_RADIUS, Lab3.TRACK, theta), true);
		Lab3.rightMotor.rotate(-convertAngle(Lab3.WHEEL_RADIUS, Lab3.TRACK, theta), false);
		
	}
	
	
	/*
	 * Notice that no boolean isNavigating() is included. This is because this method is not essential. As opposed to implementing
	 * this method, we have elected to interrupt this thread when an object is seen. Then, any further navigating will be done by
	 * the interrupter thread.
	 * 
	 */
	
	
	public static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
}