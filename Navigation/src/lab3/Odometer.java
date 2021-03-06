package lab3;

/*
 * Odometer.java
 */


import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Odometer extends Thread {
	// robot position
	private double x, y, theta;
	private int lastTachoL, lastTachoR;
	private int nowTachoL, nowTachoR;
	
	public static final int SINTERVAL=50;	// Period of sampling f (mSec)
	public static final int SLEEPINT=500;	// Period of display update (mSec)

	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	// odometer update period, in ms
	private static final long ODOMETER_PERIOD = 25;

	// lock object for mutual exclusion
	private Object lock;

	// default constructor
	public Odometer(EV3LargeRegulatedMotor leftMotor,EV3LargeRegulatedMotor rightMotor) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.x = 0.0;
		this.y = 0.0;
		this.theta = 0.0;
		this.lastTachoL = 0;
		this.lastTachoR = 0;
		lock = new Object();
	}

	// run method (required for Thread)
	public void run() {
		long updateStart, updateEnd;

		while (true) {
			updateStart = System.currentTimeMillis();
			//TODO put (some of) your odometer code here

			
				double distL, distR, deltaD, deltaT, dX, dY;
				
				//Obtain old tachometer readers
				nowTachoL = leftMotor.getTachoCount();      		
				nowTachoR = rightMotor.getTachoCount();
				
				//compute displacements for left and right wheels
				distL = 3.14159*Lab3.WHEEL_RADIUS*(nowTachoL-lastTachoL)/180;		
				distR = 3.14159*Lab3.WHEEL_RADIUS*(nowTachoR-lastTachoR)/180;
				
				//save current tachometer counts for next iteration calculations
				lastTachoL=nowTachoL;								
				lastTachoR=nowTachoR;
				
				//compute robot center displacement
				deltaD = 0.5*(distL+distR);		
				
				//compute angle robot is facing
				deltaT = (distL-distR)/Lab3.TRACK;							
														
				//compute X and Y components of displacement
			    dX = deltaD * Math.sin(theta);						
				dY = deltaD * Math.cos(theta);						
				
			
			
			synchronized (lock) {
				/**
				 * Don't use the variables x, y, or theta anywhere but here!
				 * Only update the values of x, y, and theta in this block. 
				 * Do not perform complex math
				 * 
				 */
				
				//Update x, y and theta readings
				x = x + dX;											
				y = y + dY;	
				theta += deltaT;
			}

			// this ensures that the odometer only runs once every period
			updateEnd = System.currentTimeMillis();
			if (updateEnd - updateStart < ODOMETER_PERIOD) {
				try {
					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometer will be interrupted by
					// another thread
				}
			}
		}
	}

	// accessors
	public void getPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				position[0] = x;
			if (update[1])
				position[1] = y;
			if (update[2])
				position[2] = theta*(180/Math.PI);
		}
	}

	public double getX() {
		double result;

		synchronized (lock) {
			result = x;
		}

		return result;
	}

	public double getY() {
		double result;

		synchronized (lock) {
			result = y;
		}

		return result;
	}

	public double getTheta() {
		double result;

		synchronized (lock) {
			result = theta;
		}

		return result;
	}

	// mutators
	public void setPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2];
		}
	}

	public void setX(double x) {
		synchronized (lock) {
			this.x = x;
		}
	}

	public void setY(double y) {
		synchronized (lock) {
			this.y = y;
		}
	}

	public void setTheta(double theta) {
		synchronized (lock) {
			this.theta = theta;
		}
	}

	/**
	 * @return the lastTachoL
	 */
	public int getlastTachoL() {
		return lastTachoL;
	}

	/**
	 * @param lastTachoL the lastTachoL to set
	 */
	public void setlastTachoL(int lastTachoL) {
		synchronized (lock) {
			this.lastTachoL = lastTachoL;	
		}
	}

	/**
	 * @return the lastTachoR
	 */
	public int getlastTachoR() {
		return lastTachoR;
	}

	/**
	 * @param lastTachoR the lastTachoR to set
	 */
	public void setlastTachoR(int lastTachoR) {
		synchronized (lock) {
			this.lastTachoR = lastTachoR;	
		}
	}
}