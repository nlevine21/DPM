/*
 * Noah Levine - 260684940
 * Steven Cangul - 260744412
 */

package lab3;
import lejos.hardware.motor.*;

public class BangBangController implements UltrasonicController{
	private final int bandCenter, bandwidth;
	private final int motorLow, motorHigh;
	private int distance;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private final int FILTER_OUT = 20;
	private int filterControl;
	
	private int count;
	private final int COUNT_BOUND = 40;
	
	public BangBangController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
							  int bandCenter, int bandwidth, int motorLow, int motorHigh) {
		//Default Constructor
		this.bandCenter = bandCenter; 				//Offset from the wall
		this.bandwidth = bandwidth;					//Dead band width
		this.motorLow = motorLow;					//Lower speed for motor
		this.motorHigh = motorHigh;					//Upper speed for motor
		this.leftMotor = leftMotor;					//Speed of left motor
		this.rightMotor = rightMotor;				// Speed of right motor
		filterControl = 0;
		
		leftMotor.setSpeed(motorHigh);				// Start robot moving forward
		rightMotor.setSpeed(motorHigh);				
		leftMotor.forward();
		rightMotor.forward();
	}
	
	@Override
	public void processUSData(int distance) {
		

		
		// TODO: process a movement based on the us distance passed in (BANG-BANG style)
		
		int distError = bandCenter - distance; 		//Measured error from desired distance
		
		
		/* Evaluate whether the difference in distance is within the accepted error range. If it is in
		 * the accepted range, we tell the robot to set both of its motors to the "motorHigh" speed
		 * and forward direction.
		 */
		
		if (Math.abs(distError) <= bandwidth) {
			leftMotor.setSpeed(motorHigh);				
			rightMotor.setSpeed(motorHigh);				
			leftMotor.forward();
			rightMotor.forward();
		}
		
		
		/* If the error in the distance is greater than zero, it signifies that the robot is too close to the wall.
		 * To correct this, we would need to decrease the speed of the right wheel and increase the speed of the left wheel.
		 */
		
		
		else if (distError > 0) {
			leftMotor.setSpeed(motorLow);
			rightMotor.setSpeed(motorHigh+motorLow);
			leftMotor.forward();
			rightMotor.forward();
		}

		
		else if (distError < 0) { 
			leftMotor.setSpeed(motorLow+motorLow);
			rightMotor.setSpeed(motorLow);
			leftMotor.forward();
			rightMotor.forward();
		}
	
		
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}
}
