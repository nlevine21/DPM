package wallFollower;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class PController implements UltrasonicController {
	
	private final int bandCenter, bandwidth;
	private final int motorStraight = 200, FILTER_OUT = 20;
	private final int countBound = 30; //for countTimesDistanceOutOfRange, 40 equals to 2s.
	private final int PConstant = 10;
	private int MaxCorrection = 100;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private int distance;
	private int filterControl;
	int count = 0;
	
	
	public PController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
					   int bandCenter, int bandwidth) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwidth = bandwidth;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		leftMotor.setSpeed(motorStraight);					// Initalize motor rolling forward
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
		filterControl = 0;
	}
	
	@Override
	public void processUSData(int distance) {
		/*
		// rudimentary filter - toss out invalid samples corresponding to null signal.
		// (n.b. this was not included in the Bang-bang controller, but easily could have).
		//
		if (distance > 255 && filterControl < FILTER_OUT) {
			// bad value, do not set the distance var, however do increment the filter value
			filterControl ++;
		} else if (distance > 255){
			// true 255, therefore set distance to 255
			this.distance = 255;
		} else {
			// distance went below 255, therefore reset everything.
			filterControl = 0;
			this.distance = distance;
		}
		*/
		
		int error = (distance - bandCenter);
		
		if(Math.abs(error) <= bandwidth){
			count = 0;
			leftMotor.setSpeed(motorStraight);
			rightMotor.setSpeed(motorStraight);
			leftMotor.forward();
			rightMotor.forward();
		}
		
		int dif = calcP(error);
		
		
		//too close to the wall
		if (error<0){
					count = 0;
					leftMotor.setSpeed(motorStraight+2*dif);
					rightMotor.setSpeed(motorStraight-dif);
					leftMotor.forward();
					rightMotor.backward();
		}
		
		//too far from the wall
		if (error>0) {
			// filter checks for gaps and avoids cutting corners
			if(count < countBound){
				count++;
				leftMotor.setSpeed(motorStraight);
				rightMotor.setSpeed(motorStraight);
				leftMotor.forward();
				rightMotor.forward();
				return;
			}
			else {
			leftMotor.setSpeed(motorStraight);
			rightMotor.setSpeed(motorStraight+2*dif);
			leftMotor.forward();
			rightMotor.forward();
			}	
			}
		}
		
		
	public int calcP(int error){	
		int correction;
	
		if (error<0){
			error = error* (-1);
		}
		correction = (int)(PConstant*(double)error);
	
		if (correction>= motorStraight){
			correction = MaxCorrection;
		}
		return correction;
	}
	
	@Override
	public int readUSDistance() {
		return this.distance;
	}

}