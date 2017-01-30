package lab3;



import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class Lab3 {
	
	// Static Resources:
	// Left motor connected to output A
	// Right motor connected to output D
	public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));

	// Constants
	public static final double WHEEL_RADIUS = 2.05;
	public static final double TRACK = 15.8;
	public static final double SENSOR_DIST = 5;

	public static void main(String[] args) {
		int buttonChoice;

		// some objects that need to be instantiated
		
		final TextLCD t = LocalEV3.get().getTextLCD();
		final Odometer odometer = new Odometer(leftMotor, rightMotor);
		final OdometerDisplay odometryDisplay = new OdometerDisplay(odometer,t);
		
		do {
			// clear the display
			t.clear();

			// ask the user whether the motors should drive in a square or float
			t.drawString("< Left | Right >", 0, 0);
			t.drawString("       |        ", 0, 1);
			t.drawString(" Drive | Drive  ", 0, 2);
			t.drawString(" along | along  ", 0, 3);
			t.drawString(" path  | path   ", 0, 4);
			t.drawString("       | with   ", 0, 5);
			t.drawString("       | brick  ", 0, 6);

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);

		if (buttonChoice == Button.ID_RIGHT) {
			
			
			odometer.start();
			odometryDisplay.start();
			
			// spawn a new Thread to avoid SquareDriver.drive() from blocking
			(new Thread() {
				public void run() {
					Navigator2.drive(leftMotor, rightMotor, WHEEL_RADIUS, WHEEL_RADIUS, TRACK, odometer);
				}
			}).start();
			
		} else {
			
			// start the odometer, the odometry display
			
			odometer.start();
			odometryDisplay.start();

			// spawn a new Thread to avoid SquareDriver.drive() from blocking
			(new Thread() {
				public void run() {
					Navigator.drive(leftMotor, rightMotor, WHEEL_RADIUS, WHEEL_RADIUS, TRACK, odometer);
				}
			}).start();
		}
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
	
}
