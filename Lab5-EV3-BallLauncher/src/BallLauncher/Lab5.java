package BallLauncher;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;


public class Lab5 {
	
	// Static Resources:
	// Left motor connected to output A	
	// Right motor connected to output D
	// Top Motors connected to outputs B and C
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private static final EV3LargeRegulatedMotor topMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	private static final EV3LargeRegulatedMotor topMotor2 = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
	
	
	// Constants
	private static final int ROTATE_SPEED = 100;
	private static final int ACCELERATION = 3000;
	private static final int LAUNCH_ANGLE = 125;
	private static final int INITIAL_ANGLE = 40;
	private static final double WHEEL_RADIUS = 2.05;
	private static final double TRACK = 16.7;
	private static final double TURN_ANGLE = 18.435;
	private static final TextLCD t = LocalEV3.get().getTextLCD();
	
	
	
	

	public static void main(String[] args) {
		
		//Rotate the motors forward to prepare for launch
		topMotor.rotate(-INITIAL_ANGLE, true);
		topMotor2.rotate(-INITIAL_ANGLE, false);
		
		while (true) {
			
			int buttonChoice = 0;
			
			//Get the button choice from the user
			do {
				// clear the display
				t.clear();

				// ask the user where the target is 
				t.drawString("< Left | Right >", 0, 0);
				t.drawString("       |        ", 0, 1);
				t.drawString(" ______|_______ ", 0, 2);
				t.drawString("                ", 0, 3);
				t.drawString("       Up       ", 0, 4);

				buttonChoice = Button.waitForAnyPress();
				
				//If the escape button is pressed, exit the program
				if (buttonChoice == Button.ID_ESCAPE) {
					System.exit(0);
				}
				
			} while (buttonChoice == 0);
			
			//Lock both of the top motors to prepare for the launch
			topMotor.stop();
			topMotor2.stop();
			
		
			//If the left button is pressed, the target is on the left
			if (buttonChoice == Button.ID_LEFT) {
				
				//Rotate to the left and launch
				turnTo(-TURN_ANGLE);
				launch();
				
				buttonChoice = 0;
				turnTo(TURN_ANGLE);
				
			}
			
			//If the top button is pressed, the target is straight ahead
			if (buttonChoice == Button.ID_UP) {
				
				//Launch the ball
				launch();
				buttonChoice = 0;

				
			}
			
			//If the right button is pressed, the target is on the right
			if (buttonChoice == Button.ID_RIGHT){
				
				//Rotate to the right and launch
				turnTo(TURN_ANGLE);
				launch();
				
				buttonChoice = 0;
				turnTo(-TURN_ANGLE);
				
			}
			
			
		}
	}

	
	//Method which launches the ball
	private static void launch() {
		
			//Set the accelerations of the top motors
			topMotor.setAcceleration(ACCELERATION);
			topMotor2.setAcceleration(ACCELERATION);
			
			//Set the speeds of the top motors
			topMotor.setSpeed(topMotor.getMaxSpeed());
			topMotor2.setSpeed(topMotor2.getMaxSpeed());
			
			//Rotate the top motors forward to launch ball
			topMotor.rotate(-LAUNCH_ANGLE, true);
			topMotor2.rotate(-LAUNCH_ANGLE, false);
			
			//Rotate the top motors back to the initial orientation
			topMotor.rotate(LAUNCH_ANGLE,true);
			topMotor2.rotate(LAUNCH_ANGLE,false);
			
			//Lock the motors
			topMotor.stop();
			topMotor2.stop();
	}
	
	
	//turnTo method from Navigation
	private static void turnTo(double theta)
	{
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);

		leftMotor.rotate(convertAngle (WHEEL_RADIUS, TRACK, theta), true);
		rightMotor.rotate(-convertAngle(WHEEL_RADIUS, TRACK, theta), false);
	}
	
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
	
}