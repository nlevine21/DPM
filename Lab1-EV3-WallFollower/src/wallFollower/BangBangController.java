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
		
		if (Math.abs(distError) <= bandwidth) {
			leftMotor.setSpeed(motorHigh);				
			rightMotor.setSpeed(motorHigh);				
			leftMotor.forward();
			rightMotor.forward();
		}
		
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
			
			if (Math.abs (distError )< 20)
			{
				leftMotor.setSpeed (motorHigh);
				rightMotor.setSpeed(motorLow);
				
				if (Math.abs (distError) < 10)
				{
					leftMotor.setSpeed(motorHigh);
					rightMotor.setSpeed( 0 );
				}
				
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
				
		}
		
		else if (distError < 0) {
	
			
			if (Math.abs (distError) < 20)
			{
				leftMotor.setSpeed (motorLow);
				rightMotor.setSpeed(motorHigh);
				
				if (Math.abs (distError) < 10)
				{
					leftMotor.setSpeed(0);
					rightMotor.setSpeed( motorHigh );
				}
				
				leftMotor.forward();
				rightMotor.forward();
			}
			
			else
				
			{	
			leftMotor.setSpeed(motorLow);
			rightMotor.setSpeed(motorHigh);
			leftMotor.forward();
			rightMotor.forward();}
		}
		
		
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}
}