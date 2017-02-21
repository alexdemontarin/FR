package org.usfirst.frc.team5293.robot;

import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This is a demo program showing the use of the RobotDrive class, specifically
 * it contains the code necessary to operate a robot with tank drive.
 *
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SampleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 *
 * WARNING: While it may look like a good choice to use for your code if you're
 * inexperienced, don't. Unless you know what you are doing, complex code will
 * be much more difficult under this system. Use IterativeRobot or Command-Based
 * instead if you're new.
 */
public class Robot extends SampleRobot {
	SpeedController frontLeft = new Talon(0);//Front Left
	SpeedController backLeft = new Talon(1);//Back Left
	SpeedController frontRight = new Talon(2);//Front Right
	SpeedController backRight = new Talon(3);//Back Right
	
	SpeedController shooter = new Spark(4); //For Shooter
	
	SpeedController winch = new Spark(5); //For Winch
	
	RobotDrive myRobot = new RobotDrive(frontLeft, backLeft, frontRight, backRight); // class that handles basic drive
												// operations
	
	Joystick leftStick = new Joystick(0); // set to ID 0 in DriverStation
	Joystick rightStick = new Joystick(1); // set to ID 1 in DriverStation
	JoystickButton trigger = new JoystickButton(leftStick, 1);
	
	//Autonomous Stuff
	final String defaultAuto = "Default Do Nothing";
	final String shootAuto = "Shoot for 10 Seconds";
	final String turnAutoLeftBumper = "Turn Off the Wall When in Contact With Left";
	final String turnAutoRightBumper = "Turn Off the Wall When in Contact With Right";
	final String turnAndDriveLeftBumperAuto = "Turn Off the Wall and Drive When in Contact With Left";
	final String turnAndDriveRightBumperAuto = "Turn Off the Wall and Drive When in Contact With Right";
	final String shootTurnDriveLeftBumperAuto = "Shoot Balls, Turn, Drive Forward When in Contact With Left";
	final String shootTurnDriveRightBumperAuto = "Shoot Balls, Turn, Drive Forward When in Contact With Right";
	SendableChooser<String> chooser = new SendableChooser<>();

	public Robot() {
		myRobot.setExpiration(0.1);
	}
	
	@Override
	public void robotInit() {
		//Add all options for autonomous in SmartDashboard
		chooser.addDefault("Drive at Half Speed for 5 Seconds", defaultAuto);
		chooser.addObject("Shoot Balls for 10 Seconds", shootAuto);
		chooser.addObject("Turns Off the Wall When in Contact With Right", turnAutoRightBumper);
		chooser.addObject("Turns Off the Wall When in Contact With Left", turnAutoLeftBumper);
		chooser.addObject("Turns Off the Wall and Drives Forward - Contact Left", turnAndDriveLeftBumperAuto);
		chooser.addObject("Turns Off the Wall and Drives Forward - Contact Right", turnAndDriveRightBumperAuto);
		chooser.addObject("Shoot Balls, Turn, Drive Forward - Contact Left", shootTurnDriveLeftBumperAuto);
		chooser.addObject("Shoot Balls, Turn, Drive Forward - Contact Right", shootTurnDriveRightBumperAuto);
		SmartDashboard.putData("Auto modes", chooser);
		
		//Camera Stuff
		CameraServer.getInstance().startAutomaticCapture();
	}

	

	@Override
	public void operatorControl() {
		myRobot.setSafetyEnabled(true);
		while (isOperatorControl() && isEnabled()) {
			//Adds dead zone and inversion to turning on second stick
			if(rightStick.getZ() > -.1 || rightStick.getZ() < .1){ //HOW DOES THIS NOT BREAK //??Does this break it if I set the first one ??
				 myRobot.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, true);
				 myRobot.setInvertedMotor(RobotDrive.MotorType.kFrontRight, true);
				 myRobot.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
				 myRobot.setInvertedMotor(RobotDrive.MotorType.kRearRight, true);
			}else{
				 myRobot.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, false);
				 myRobot.setInvertedMotor(RobotDrive.MotorType.kFrontRight, false);
				 myRobot.setInvertedMotor(RobotDrive.MotorType.kRearLeft, false);
				 myRobot.setInvertedMotor(RobotDrive.MotorType.kRearRight, false);
			}
			myRobot.arcadeDrive(leftStick, 1, rightStick, 2);
			
			//TODO: Add toggle button to make driving non-linear
			
			if(trigger.get())
				shooter.set(.82);
			else
				shooter.set(0);
			
			//TODO: Add deadzone to winch 
			//TODO: Add inversion if necessary
			//TODO: Add a non-linear curve via equation of (1/X , X = Y-Output ) or x^3 
				//-Change exponent to change severity of curve
			
			//Makes winch move based on 
			//if(rightStick.getY() != 0){
				//winch.set(rightStick.getY());
			//}
			
			/**
			 *CURVE BASED ON: https://www.wolframalpha.com/input/?i=Plot%5BPiecewise%5B%7B%7Bx%5E5,+x+%3C+0%7D,+%7Bx%5E3,+x+%3E+0%7D%7D%5D,+%7Bx,+-1,+1%7D%5D
			 *	or: Plot[Piecewise[{{x^5, x < 0}, {x^3, x > 0}}], {x, -1, 1}]
			 *	or: x^3 , [0, 1] ; x^5 [-1, 0)
			 * 
			 * if(rightStick.getY() >= 0){
			 * 		winch.set(Math.pow(rightStick.getY(), 3));
			 * }else{
			 * 		winch.set(Math.pow(rightStick.getY(), 5));
			 * }
			 */
			
			Timer.delay(0.005); // wait for a motor update time
		}
	}
	
	//??Need to add two different turn functions for different alliances??
	

	@Override
	public void autonomous() {
		String autoSelected = chooser.getSelected();
		// String autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + autoSelected);

		switch (autoSelected) {
		case shootAuto:
			myRobot.setSafetyEnabled(false);
			//myRobot.drive(-0.5, 1.0); // spin at half speed
			//Timer.delay(2.0); // for 2 seconds
			myRobot.drive(0.0, 0.0); // stop robot
			shooter.set(.9);
			Timer.delay(10.5);
			shooter.set(0);
			break;
		case turnAutoLeftBumper:
			myRobot.setSafetyEnabled(false);
			frontLeft.set(.7);
			backLeft.set(.7);
			frontRight.set(0);
			backRight.set(0);
			
			Timer.delay(1.0);
			
			frontLeft.set(0);
			backLeft.set(0);
			frontRight.set(0);
			backRight.set(0);
			break;
		case turnAutoRightBumper:
			myRobot.setSafetyEnabled(false);
			frontLeft.set(0);
			backLeft.set(0);
			frontRight.set(.7);
			backRight.set(.7);
			
			Timer.delay(1.0);
			
			frontLeft.set(0);
			backLeft.set(0);
			frontRight.set(0);
			backRight.set(0);
			
			break;
		case turnAndDriveRightBumperAuto:
			myRobot.setSafetyEnabled(false);
			frontLeft.set(0);
			backLeft.set(0);
			frontRight.set(.7);
			backRight.set(.7);
			
			Timer.delay(1);
			
			frontLeft.set(0);
			backLeft.set(0);
			frontRight.set(0);
			backRight.set(0);
			
			frontLeft.set(-.5);
			backLeft.set(-.5);
			frontRight.set(.5);
			backRight.set(.5);
			
			Timer.delay(2);
			
			frontLeft.set(0);
			backLeft.set(0);
			frontRight.set(0);
			backRight.set(0);
			break;
			
		case turnAndDriveLeftBumperAuto:
			myRobot.setSafetyEnabled(false);
			frontLeft.set(.7);
			backLeft.set(.7);
			frontRight.set(0);
			backRight.set(0);
			
			Timer.delay(1);
			
			frontLeft.set(0);
			backLeft.set(0);
			frontRight.set(0);
			backRight.set(0);
			
			frontLeft.set(.5);
			backLeft.set(.5);
			frontRight.set(-.5);
			backRight.set(-.5);
			
			Timer.delay(2);
			
			frontLeft.set(0);
			backLeft.set(0);
			frontRight.set(0);
			backRight.set(0);
			
			break;
		
			
		//INVERT THESE FOR RED SIDE
		case shootTurnDriveLeftBumperAuto:
			myRobot.setSafetyEnabled(false);
			myRobot.drive(0.0, 0.0);
			shooter.set(.9);
			Timer.delay(10);
			shooter.set(0);
			
			frontLeft.set(.7);
			backLeft.set(.7);
			frontRight.set(0);
			backRight.set(0);
			
			Timer.delay(1);
			
			frontLeft.set(0);
			backLeft.set(0);
			frontRight.set(0);
			backRight.set(0);
			
			frontLeft.set(.5);
			backLeft.set(.5);
			frontRight.set(-.5);
			backRight.set(-.5);
			
			Timer.delay(2);
			
			frontLeft.set(0);
			backLeft.set(0);
			frontRight.set(0);
			backRight.set(0);
			
			break;
			
		case shootTurnDriveRightBumperAuto:
			myRobot.setSafetyEnabled(false);
			myRobot.drive(0.0, 0.0);
			shooter.set(.9);
			Timer.delay(10);
			shooter.set(0);
			
			frontLeft.set(.7);
			backLeft.set(.7);
			frontRight.set(0);
			backRight.set(0);
			
			Timer.delay(1);
			
			frontLeft.set(0);
			backLeft.set(0);
			frontRight.set(0);
			backRight.set(0);
			
			frontLeft.set(.5);
			backLeft.set(.5);
			frontRight.set(-.5);
			backRight.set(-.5);
			
			Timer.delay(2);
			
			frontLeft.set(0);
			backLeft.set(0);
			frontRight.set(0);
			backRight.set(0);
			
			break;
			
		default:
			myRobot.setSafetyEnabled(false);
			myRobot.drive(0.5, 0.0); // drive forwards half speed
			Timer.delay(5.0); // for 5 seconds
			myRobot.drive(0.0, 0.0); // stop robot
			break;
		}
	}
}
