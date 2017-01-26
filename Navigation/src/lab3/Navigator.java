package lab3;


import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Navigator extends Thread{
	private static final int FORWARD_SPEED = 250;
	private static final int ROTATE_SPEED = 150;
	private static boolean isNavigating = false;
	private static int i =1 ;

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
				 Travelto(60,30);
				 Travelto(30,30);
				 Travelto(30,60);
				 Travelto (60,0);
		
		}
		
		
	}

	public static void Travelto (double x, double y){
		
		double initX = OdometerDisplay.odometer.getX();
		double initY = OdometerDisplay.odometer.getY();
		
		
		if (i==1)
			 Turnto (-63.435);
		if (i==2)
			 Turnto (153.435);
		if (i==3)
			Turnto(-90);
		if (i==4)
			 Turnto(-153.435);
			
		i++;
		
		Lab3.leftMotor.setSpeed(FORWARD_SPEED);
		Lab3.rightMotor.setSpeed(FORWARD_SPEED);
		
		double distance;
		
		distance = Math.pow((x - initX),2) + Math.pow((y - initY),2);
		distance = Math.pow(distance, 0.5);

		Lab3.leftMotor.rotate(convertDistance(Lab3.WHEEL_RADIUS, distance), true);
		Lab3.rightMotor.rotate(convertDistance(Lab3.WHEEL_RADIUS, distance), false);
		
	}
	
	
	public static void Turnto (double theta){
		
		double abs = Math.abs(theta);
		
		Lab3.leftMotor.setSpeed(ROTATE_SPEED);
		Lab3.rightMotor.setSpeed(ROTATE_SPEED);
		
		if (theta >=0){
			Lab3.leftMotor.rotate(-convertAngle(Lab3.WHEEL_RADIUS, Lab3.TRACK, abs), true);
			Lab3.rightMotor.rotate(convertAngle(Lab3.WHEEL_RADIUS, Lab3.TRACK, abs), false);
		}
		else {
			Lab3.leftMotor.rotate(convertAngle(Lab3.WHEEL_RADIUS, Lab3.TRACK, abs), true);
			Lab3.rightMotor.rotate(-convertAngle(Lab3.WHEEL_RADIUS, Lab3.TRACK, abs), false);
			
		}
		
		
	}
	
	
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
}