package Lab4;



import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

public class LightLocalizer {
	private Odometer odo;
	private SampleProvider colorSensor;
	private float[] colorData;	
	public Navigation nav;
	
	private final static double SENSOR_DIST = 3.65;
		
	public LightLocalizer(Odometer odo, SampleProvider colorSensor, float[] colorData) {
		this.odo = odo;
		this.colorSensor = colorSensor;
		this.colorData = colorData;
		nav = new Navigation(this.odo);  // Initialize navigation object
	}
	
	public void doLocalization() {
		double XDistance;
	
		nav.setSpeeds(200, 200);
		while (true) {
		colorSensor.fetchSample(colorData,0);
		if ( (colorData[0]*1000) < 200) {
			Sound.beep();
			nav.setSpeeds(0, 0);
			XDistance = odo.getX();
			nav.goForward(-XDistance);
			break;
		}
	}
		turnLeft();
		nav.setSpeeds(200, 200);
		
		while (true) {
			colorSensor.fetchSample(colorData,0);
			if ( (colorData[0]*1000) < 200) {
				Sound.beep();
				nav.goForward(SENSOR_DIST);
				turnRight();
				nav.setSpeeds(0, 0);
				XDistance += SENSOR_DIST;
				nav.goForward(XDistance);
				break;
			}
		}
		


		
		odo.setPosition(new double[]{0, 0,0}, new boolean[] {true,true,true});
		
		
			}
	
	private void turnRight() {
		// turn 90 degrees clockwise
		Lab4.leftMotor.setSpeed(USLocalizer.ROTATION_SPEED);
		Lab4.rightMotor.setSpeed(USLocalizer.ROTATION_SPEED);

		Lab4.leftMotor.rotate(convertAngle(Lab4.WHEEL_RADIUS, Lab4.TRACK, 90.0), true);
		Lab4.rightMotor.rotate(-convertAngle(Lab4.WHEEL_RADIUS, Lab4.TRACK, 90.0), false);
	}
	
	private void turnLeft() {
		// turn 90 degrees clockwise
		Lab4.leftMotor.setSpeed(USLocalizer.ROTATION_SPEED);
		Lab4.rightMotor.setSpeed(USLocalizer.ROTATION_SPEED);

		Lab4.leftMotor.rotate(-convertAngle(Lab4.WHEEL_RADIUS, Lab4.TRACK, 90.0), true);
		Lab4.rightMotor.rotate(convertAngle(Lab4.WHEEL_RADIUS, Lab4.TRACK, 90.0), false);
	}
	
	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
	
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}
	}




