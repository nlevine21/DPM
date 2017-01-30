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

	
	//Initialize Odometer, Screen and Odometer Display
	public static final Odometer odometer = new Odometer(leftMotor,rightMotor);
	public static final TextLCD t = LocalEV3.get().getTextLCD();
	public static final  OdometerDisplay odometryDisplay = new OdometerDisplay(odometer,t);
	
	// Constants
	public static final double WHEEL_RADIUS = 2.05;
	public static final double TRACK = 15.8;


	public static void main(String[] args) {
		int buttonChoice;

		
		do {
			// clear the display
			t.clear();

			// ask the user whether the robot should follow path 1 or 2. (without/with avoidance)
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
			
			
			//Start the odometer and odemeter display
			odometer.start();
			odometryDisplay.start();
			
			// spawn a new Thread starting the second navigator
			(new Thread() {
				public void run() {
					Navigator2.drive(leftMotor, rightMotor, WHEEL_RADIUS, WHEEL_RADIUS, TRACK);
				}
			}).start();
			
		} else {
			
			// start the odometer and the odometry display
			odometer.start();
			odometryDisplay.start();

			// spawn a new Thread starting the first navigator
			(new Thread() {
				public void run() {
					Navigator.drive(leftMotor, rightMotor, WHEEL_RADIUS, WHEEL_RADIUS, TRACK);
				}
			}).start();
		}
		
		
		//If a button is pressed, terminate the program
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
	
}
