package wallFollower;
import lejos.hardware.motor.*;

public class BangBangController implements UltrasonicController{
	private final int bandCenter, bandwidth;
	private final int motorLow, motorHigh;
	private int distance;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	
	private int filterControl; 
	private final int FILTER_OUT = 20;
	
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
	}
	
	@Override
	public void processUSData(int distance) {
	
			
		// rudimentary filter - toss out invalid samples corresponding to null
				// signal.
				// (n.b. this was not included in the Bang-bang controller, but easily
				// could have).
				//
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
		
		int distError = bandCenter - this.distance; 		//Measured error from desired distance
		
		if (Math.abs(distError) <= bandwidth) {
			leftMotor.setSpeed(motorHigh);				
			rightMotor.setSpeed(motorHigh);				
			leftMotor.forward();
			rightMotor.forward();
		}
		
		else if (distError > 0) { 
			
			if (Math.abs (distError )< 20)
			{
				leftMotor.setSpeed (motorHigh);
				rightMotor.setSpeed(motorLow);
				leftMotor.forward();
				rightMotor.backward();
				
			}
			
			else
				
			{ 
			
			leftMotor.setSpeed(motorHigh);
			rightMotor.setSpeed(motorLow);
			leftMotor.forward();
			rightMotor.forward();
			}
		}
			
	
				
		
		else if (distError < 0) {
	
			
			if (Math.abs (distError) < 20)
			{
				leftMotor.setSpeed (motorLow);
				rightMotor.setSpeed(motorHigh);
				leftMotor.backward();
				rightMotor.forward();
			}
			
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
