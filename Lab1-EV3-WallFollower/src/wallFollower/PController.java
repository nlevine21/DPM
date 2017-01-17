/*
 * Noah Levine - 260684940
 * Steven Cangul - 260744412
 */


package wallFollower;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class PController implements UltrasonicController {
	
	private final int bandCenter, bandwidth;
	private final int motorStraight = 200;
	private final int PConstant = 10;
	private int maxDelta = 100;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private int distance;

	
	//Variables for Filter
	int filterControl = 0;
	private final int FILTER_OUT = 40; 
	
	
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
			filterControl = 0;
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
			filterControl = 0;
			leftMotor.setSpeed(motorStraight+2*delta);
			rightMotor.setSpeed(motorStraight-delta);
			leftMotor.forward();
			rightMotor.backward();
		}
		
		/*
		 * Robot is too far from the wall
		 */
		if (error>0) {
			/*
			 * Filter for incorrect large values. Continue moving robot forward
			 * if variances in distance do not occur repeatedly (40 consecutive time).
			 * In which case, the robot is actually too far from the wall
			 */
			
			if(filterControl < FILTER_OUT)
			{
				filterControl++;
				leftMotor.setSpeed(motorStraight);
				rightMotor.setSpeed(motorStraight);
				leftMotor.forward();
				rightMotor.forward();
				return;
			}
			else 
				/*
				 * Robot is actually too far from the wall. Increase the speed of the
				 * right motor by 2*delta. We turn less aggressively than when robot is close
				 * to reduce unnecessary jerky motion
				 */
			{
				leftMotor.setSpeed(motorStraight);
				rightMotor.setSpeed(motorStraight+2*delta);
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