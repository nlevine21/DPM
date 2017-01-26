/*
 * Noah Levine - 260684940
 * Steven Cangul - 260744412
 */


package lab3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class PController implements UltrasonicController {
	
	private final int bandCenter, bandwidth;
	private final int motorStraight = 200;
	private final int PConstant = 10;
	private int maxDelta = 100;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private int distance;
	private boolean seenWall = false;
	
	
	
	
	public PController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
					   int bandCenter, int bandwidth) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwidth = bandwidth;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		
		
		leftMotor.setSpeed(motorStraight);					// Start moving forward
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();

	}
	
	@Override
	public void processUSData(int distance) {
	
		
		int error = (distance - bandCenter);		//Calculate error
		
		
		/*
		 * If the error is within the bandwidth, move each motor forward at
		 * motorStraight speed
		 */
		if(Math.abs(error) <= bandwidth){
			leftMotor.setSpeed(motorStraight);
			rightMotor.setSpeed(motorStraight);
			leftMotor.forward();
			rightMotor.forward();
		}
		
		
		//Calculate the severity of the delta based on the measured error
		int delta = calcP(error);
		
		
		/*
		 * Robot is too close to the wall. Increase the speed of the left motor 
		 * by 2*delta and decrease the speed of the right motor by delta. Move the
		 * left motor forward and the right motor backward. This will tighten the
		 * turning radius
		 */
		if (error<0)
		{
			seenWall = true;
			leftMotor.setSpeed(motorStraight+2*delta);
			rightMotor.setSpeed(motorStraight-delta);
			leftMotor.forward();
			rightMotor.backward();
		}
		
		/*
		 * Robot is too far from the wall
		 */
		if (error>0) {
		
			if (seenWall) {
				rightMotor.setSpeed(motorStraight);
				leftMotor.setSpeed(motorStraight+2*delta);
				leftMotor.forward();
				rightMotor.forward();
			}	
			}
		}
		
		
	public int calcP(int error){	
		//delta to be returned
		int delta;
		
		//Get absolute value of error
		error = Math.abs(error);
		
		//Calculate delta based on error and PConstant
		delta = (int)(PConstant*(double)error);
	
		//If  delta is greater than motorStraight, set it to the maximum delta
		if (delta>= motorStraight){
			delta = maxDelta;
		}
		return delta;
	}
	
	@Override
	public int readUSDistance() {
		return this.distance;
	}

}