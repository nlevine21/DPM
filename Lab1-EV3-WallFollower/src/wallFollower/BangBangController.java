/*
 * Noah Levine - 260684940
 * Steven Cangul - 260744412
 */

package wallFollower;
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
		
		if (distance >= 255 && filterControl < FILTER_OUT) {
			// bad value, do not set the distance var, however do increment the
			// filter value
			filterControl++;
		} else if (distance >= 255) {
			// We have repeated large values, so there must actually be nothing
			// there: leave the distance alone
			this.distance = distance;
		} else {
			// distance went below 255: reset filter and leave
			// distance alone.
			filterControl = 0;
			this.distance = distance;
		}

		
		
		
		
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
			
			if(count < COUNT_BOUND){
				count++;
				leftMotor.setSpeed(motorHigh);
				rightMotor.setSpeed(motorHigh);
				leftMotor.forward();
				rightMotor.forward();
				return;
			}
			
			else {
			
				/* If the error in distance is less than 20, the bot is dangerously close to the wall. To correct this, the left motor
				 * is set to "motorHigh" in the forward direction and the right motor is set to "motorLow" in the backwards
				 * direction.
				 */
				
				
			if (Math.abs (distError )< 20)
			{
				leftMotor.setSpeed (motorHigh);
				rightMotor.setSpeed(motorLow);
				
				
				/* If the error in distance is less than 10, the bot is too close to the wall. A less aggressive approach is used.
				 * The left motor is set to "motorHigh" in the forwards direction. However, the right motor is turned off to 
				 * give it a greater turning radius.
				 */
				
				
					if (Math.abs (distError) < 10)
				{
							leftMotor.setSpeed(motorHigh);
							rightMotor.setSpeed( 0 );
				}
				
					
					
				leftMotor.forward();
				rightMotor.backward();
				
				
				
			}
			
			
			/* This is the least aggressive correction for when the bot is close. It gives the largest 
			 * turning radius.
			 */
			
			else
				
			{
			leftMotor.setSpeed(motorHigh);
			rightMotor.setSpeed(motorLow);
			leftMotor.forward();
			rightMotor.forward();
			}
			
			}
				
		}
		
		
		/* If the error in the distance is less than zero, it signifies that the robot is too far from the wall.
		 * To correct this, we would need to accelerate the right wheel and decrease the speed of the left wheel.
		 */
		
		else if (distError < 0) {
	
			
			/* If the error is less than 20, use a less aggressive approach for the correction */
			
			if (Math.abs (distError) < 20)
			{
				leftMotor.setSpeed (motorLow);
				rightMotor.setSpeed(motorHigh);
				
				// If the error is less than 10, use a more aggressive correction */
				
						if (Math.abs (distError) < 10)
				{
							leftMotor.setSpeed(0);
							rightMotor.setSpeed( motorHigh );
				}
				
				leftMotor.forward();
				rightMotor.forward();
			}
			
			/* If the error is greater than 20, use the least aggressive correction by setting the left wheel to
			 * "motorLow" and the right wheel to "motorHigh". Both motors are moving in the forward direction.
			 */
			
			else
				
			{	
			leftMotor.setSpeed(motorLow);
			rightMotor.setSpeed(motorHigh);
			leftMotor.forward();
			rightMotor.forward();
			}
		}
		
		
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}
}