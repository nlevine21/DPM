package Lab4;

import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class USLocalizer {
	public static float ROTATION_SPEED = 120;
	
	private static final int WALL_DIST	= 33; // WALL_DIST in the reading of the US 

	private Odometer odo;
	private SampleProvider usSensor;
	private float[] usData;
	public Navigation nav;
	
	public USLocalizer(Odometer odo,  SampleProvider usSensor, float[] usData) {
		this.odo = odo;
		this.usSensor = usSensor;
		this.usData = usData;
		nav = new Navigation(this.odo);  // Initialize navigation object
	}
	
	public void doLocalization() {
		double angleA, angleB; 
		double actualAng = 0; 
		

			while(!seesWall()){
				rightTurn();
			}
			
			delay();
			
			while (seesWall()){	
				rightTurn();
			}
			
			angleA = odo.getAng(); 
			nav.setSpeeds(0, 0);
			delay();
		
			while(!seesWall()){
				leftTurn();
			}
			
			delay(); 
			
			
			while (seesWall()){
				leftTurn();
			}
			
			
			nav.setSpeeds(0, 0); 
			angleB = odo.getAng(); 
			delay();
			
			actualAng = calcHeading(angleA,angleB) + odo.getAng(); 
			odo.setPosition(new double [] {0.0, 0.0, actualAng}, new boolean [] {true, true, true});
			nav.turnTo(0, true); 
			
		
	}
	
	private float getFilteredData() { // filter the data 
		usSensor.fetchSample(usData, 0);
		float distance = usData[0] * 100;
		
		if (distance >= 50){
			distance = 50;
		}
		return distance;
	}
	
	private  void leftTurn(){ // rotate left
		nav.setSpeeds(-ROTATION_SPEED, ROTATION_SPEED);
	}

	private  void rightTurn(){ // rotate right
		nav.setSpeeds(ROTATION_SPEED, -ROTATION_SPEED);
	}
	
	private boolean seesWall() {
		return getFilteredData() >= WALL_DIST;
	}
	
	private void delay() {
		Delay.msDelay(1000);
	}
	private static double calcHeading(double angleA, double angleB){ // calculation of heading as shown in the tutorial
		
		double heading;
		
		if (angleA < angleB){
			heading = 45 - (angleA + angleB) /2 ;
		}else {
			heading = 225 - (angleA + angleB) /2 ;	
		}
		return heading;
	}
}