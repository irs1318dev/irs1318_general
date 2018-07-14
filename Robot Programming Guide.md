# Robot Programming Guide

**Table of Contents**
1. [Overview](#overview)
2. [General Robot Design](#general-robot-design)
   1. [RoboRIO](#roborio)
   2. [Actuators](#actuators)
      1. [Motors (Talons, Jaguars, Victors)](#motors-talons-jaguars-victors)
      2. [Pistons/Pneumatic Cylinders (DoubleSolenoids)](#pistonspneumatic-cylinders-doublesolenoids)
   3. [Sensors](#sensors)
      1. [Limit Switches](#limit-switches)
      2. [Encoders](#encoders)
      3. [Through-Beam Sensors](#through-beam-sensors)
      4. [Distance Sensors](#distance-sensors)
   4. [Other](#other)
      1. [Logger](#logger)
      2. [LED Lights](#led-lights)
   5. [User Input Devices](#user-input-devices)
      1. [Joysticks](#joysticks)
      2. [Button Pads](#button-pads)
      3. [Dip Switches](#dip-switches)
   6. [Modes](#modes)
3. [Robot Code Design](#robot-code-design)
   1. [Robot.java](#robotjava)
   2. [Mechanisms](#mechanisms)
   3. [ElectronicsConstants](#electronicsconstants)
   4. [Driver](#driver)
      1. [Operation](#operation)
         1. [Analog Operations](#analog-operations)
         2. [Digital Operations](#digital-operations)
      2. [Tasks](#tasks)
      3. [UserDriver](#userdriver)
         1. [Macros](#macros)
         2. [Shifts](#shifts)
      4. [AutonomousDriver](#autonomousdriver)
         1. [Autonomous Routines](#autonomous-routines)
   5. [TuningConstants](#tuningconstants)
   6. [HardwareConstants](#hardwareconstants)
   7. [External Libraries](#external-libraries)
      1. [Guice](#guice)
      2. [OpenCV](#opencv)
      3. [CTRE Phoenix](#ctre-phoenix)
      4. [NavX MXP](#navx-mxp)
      5. [JUnit](#junit)
      6. [Mockito](#mockito)
4. [Instructions](#instructions)
   1. [Setting up your Environment](#setting-up-your-environment)
   2. [Making Simple Operation changes](#making-simple-operation-changes)
   3. [Writing a new Mechanism](#writing-a-new-mechanism)
      1. [Define mechanism class and member variables](#define-mechanism-class-and-member-variables)
      2. [Write mechanism constructor](#write-mechanism-constructor)
      3. [Write mechanism readSensors function](#write-mechanism-readsensors-function)
      4. [Write mechanism update function](#write-mechanism-update-function)
      5. [Write mechanism stop function](#write-mechanism-stop-function)
      6. [Write mechanism setDriver function](#write-mechanism-setdriver-function)
   4. [Writing Macros and Autonomous Routines](#writing-macros-and-autonomous-routines)
      1. [Writing Tasks](#writing-tasks)
         1. [Define task class, member variables, and constructor](#define-task-class-member-variables-and-constructor)
         2. [Define task begin function](#define-task-begin-function)
         3. [Define task update function](#define-task-update-function)
         4. [Define task end function](#define-task-end-function)
         5. [Define task hasCompleted function](#define-task-hascompleted-function)
      2. [Adding Macros](#adding-macros)
      3. [Composing Tasks into Routines](#composing-tasks-into-routines)
         1. [SequentialTask.Sequence()](#sequentialtasksequence)
         2. [ConcurrentTask.AnyTasks()](#concurrenttaskanytasks)
         3. [ConcurrentTask.AllTasks()](#concurrenttaskalltasks)
5. [Advanced Topics](#advanced-topics)
   1. [PID Controllers](#pid-controllers)
   2. [Motion Planning](#motion-planning)
   3. [Vision](#vision)

## Overview
> "Everything should be made as simple as possible, but not simpler." - Albert Einstein

The Issaquah Robotics Society’s Robot code is designed to be a good example of a moderately large software project that students of varying levels of experience with programming can contribute to.  It is aimed at making the programming of the robot easy, so that it can respond well to changes to the physical robot as well as changes in the way that we wish to control the robot.  It achieves this by encapsulating each part in a different area of the code, with the idea that there should be very little duplication of code to ease maintainability.

## General Robot Design
Robots in FRC tend to have a small set of different pieces that we want to utilize, that can be arranged in a large number of ways to make complex mechanisms.  These mechanisms are designed before the code for them is written, so you should know what they are composed of by the time you are writing any code.  I’ll list out a few of these pieces here and explain what they do and how we typically use them.

### RoboRIO
RoboRIO is the name of the computing device that is used to control the robot.  It is produced by National Instruments and runs a customized version of Linux on an ARM processor.  We write code that uses a library called WPILib to handle interactions between the Robot, Driver Station, etc.

### Actuators
#### Motors (Talons, Jaguars, Victors)
Electric motors are typically used to provide movement for the robot.  They provide a rotational force that is dependent on the current setting on them and the amount of voltage that is available.  Motors are useful when a certain amount of motion is needed or when there are motions that need to happen at different speeds (as opposed to all-or-nothing).  Motors are used in places such as drive trains, elevators, and intakes.  In WPILib, they are controlled using a double value (rational number) between -1.0 and 1.0.  Since 2018, we have typically used the Talon SRX which can incorporate the abilities of a motor, an encoder, a top/bottom limit switch, and a PID controller.

#### Pistons/Pneumatic Cylinders (DoubleSolenoids)
Pneumatic cylinders are also used to provide movement for components on the robot.  They provide a lateral force and are controlled very simply by a set of 2 valves (solenoids) that change where air pressure is directed in order to move the rod within the cylinder.  There are 3 settings: "off" which means that no air pressure is applied through either valve, "forward" which means that air pressure is applied through one valve, and "reverse" which means that air pressure is applied through the other valve.  Forward and Reverse typically correlates to whether the piston is actively pushing out or actively pulling in, but it depends on how the pneumatics are set up.  Due to the way that they work, pneumatics controlled by solenoids trigger all-or-nothing movements.  Solenoids are often used in "shooters", such as the kicker on the 2016 robot.  In WPILib, they are controlled using settings of Value.kOff, Value.kForward, and Value.kReverse.

### Sensors
#### Limit Switches
Limit switches are simple switches that are used to sense when two things are physically touching.  They are simple electronic devices that complete a circuit (or break a circuit) when the switch is pressed, and break a circuit (or complete it) when released.  In WPILib, you would use a DigitalInput, which returns a true or false based on whether the limit switch is pressed or not.

#### Encoders
Encoders are used to measure the amount that an axle has rotated.  There are different types of encoders, but we typically use a quadrature encoder.  These encoders can detect the amount of rotation and the direction in which the axle has rotated.  Each encoder has a rating for how many "pulses" or ticks it receives in a complete rotation of the axle.  Using some simple math based on the sizes of the wheels (and gears), you can calculate how far something has travelled.  In WPILib, you would use an Encoder object, which returns the number of ticks/pulses, the distance (based on the distance per pulse), or the velocity (if you trust the timer on the robot).

#### Through-Beam Sensors
Through-Beam Sensors are simple infrared sensors and lights that are used to sense whether there is anything between the light and sensor.  They are often used in the real world at the bottom of a garage door to detect if anything is under the garage door so it doesn’t get crushed.  This can be used on a robot to sense whether something is in a given spot.  We used one on the 2015 robot to detect whether any tote was contained within the intake in a proper place to be picked up by the robot’s elevator.  In WPILib, you would use an AnalogInput, which returns a double value (rational number) which indicates how many volts were detected by the infrared sensor.  This value will differ based on the through-beam sensor, so you can tell through experimentation whether it is tripped or not.

#### Distance Sensors
There are various types of distance sensors, which can use either sound or light to sense how far away the robot is from something else.  In WPILib, you would use an AnalogInput, which would return a double value (rational number) which indicates how many volts were detected by the sensor.  This value will differ based on the sensor and its placement, so you can tell through experimentation what the values mean.

### Other
#### Logger
A Logger is something that takes information that is used and outputs it to some other form - as output either on the driver station console, in the smart-dashboard, or into a file on the RoboRIO.  We typically want to log all sensor information and most output information so that we can be sure that the robot is doing what we tell it to do.  If the robot is not doing what we are telling it to, this makes it easier to determine whether the problem is a hardware problem (electronics, mechanical) or a software problem.

#### LED Lights
We also often use LED lights as an indicator of some kind, or in association with a Vision system that takes advantage of retro-reflective tape.  These are usually controlled with a DigitalOutput in WPILib.

### User Input Devices
#### Joysticks
The joystick is a normal computer joystick, much like you’d find for playing a flight simulator game.  The ones that we have used for the past few years is the Logitech Xtreme 3D Pro, which has 12 buttons, the primary X and Y axis, a Throttle axis, and a directional hat.  Our team typically uses a joystick for the driver’s input method.

#### Button Pads
The button pad is a normal computer button pad.  The ones that we use are a gaming button pad which has 12 buttons.  Our team often uses these for the co-driver’s input method.

#### Dip Switches
Dip Switches are simple toggle switches which are used to switch between different modes.  Our team typically uses them to allow us to select which of several different pre-programmed autonomous routines to use without having to change anything within the code or rely on the smart dashboard.

### Modes
During each match, the first portion is a part called autonomous mode, where the robot drives itself.  The next portion is called teleop mode, where a user drives the robot.

## Robot Code Design
### Robot.java
The "main loop" of the robot is in Robot.java, which has a few entry points from the WPILib infrastructure which our code depends on.  Robot.java typically stays exactly the same from year to year.

The entry points for code execution comes from WPILib at the following times:
* When the robot is first turned on (robotInit)
  * In robotInit, we initialize the mechanisms that the robot will use.
When the robot is enabled in teleop mode (teleopInit)
  * In teleopInit, we initialize the driver as a UserDriver and then run generalInit.  In generalInit we apply the driver to each of the mechanisms.
* When the robot is enabled in autonomous mode (autonomousInit).
  * In autonomousInit, we initialize the driver as an AutonomousDriver, and then call generalInit.
* When the robot is disabled (disableInit).
  * In disabledInit, we call the stop function for each of our mechanisms and the driver.
* Every ~20ms while the robot is enabled in teleop mode (teleopPeriodic).
  * In teleopPeriodic, we simply call our "generalPeriodic" function because we have structured our code to be the same for our teleop and autonomous modes.  In generalPeriodic, we first call the readSensor function for each of our mechanisms, then call update on the driver that is being used in the current mode, and finally call the update function for each of our mechanisms.
* Every ~20ms while the robot is enabled in autonomous mode (autonomousPeriodic).
  * In autonomousPeriodic, we again call "generalPeriodic".

### Mechanisms
Mechanism classes handle the reading of all of the sensors and control of all of the actuators on each mechanism of the robot.  There is one Mechanism class for each individual part of the robot, named using the pattern "ThingMechanism" (where "Thing" is the name of the mechanism, like "DriveTrain").  Mechanisms read from all of the Sensors and translate the Operations from the Driver into the functions that need to be called on the individual Actuators.  This typically involves some math and logic to convert the data from the operations into the particular actions that need to happen.  For example, when using a typical Tank drivetrain, the DriveTrain Mechanism calculates the speed settings to apply to the left and right motors based on the DriveForward operation and the Turn operation.  Also, there may be other concerns to take care of, such as how to respond based on the presence or absence of a setting from another operation or a sensor.

The Mechanisms implement the IMechanism interface which has the definitions of functions that every Mechanism must implement.  In the mechanism, the readSensor and update functions are the most important, and are called every ~20 milliseconds.  The readSensors function reads the current values from all of the sensors and stores them locally in member variables on the object.  The update function calculates what should be applied to the output devices based on the current Operations and the data read from the sensors.  It is important that these functions execute quickly, so anything that depends on a certain length of time elapsing should be calculated between separate runs of update and not involve any sleep operation.  Therefore, most actions that take multiple iterations of the update function belong in a macro instead of being hard-coded into the Mechanism.

### ElectronicsConstants
ElectroincsConstants is a class that holds constant values for all of the physical connections (PWM channels, Digital IO channels, Analog IO channels, CAN ids, etc.) that are needed to be known in order to control the correct output device and read the correct sensors.  We keep this information in a separate class (and all in a single file) so that there is only one place to update if the Electronics sub-team needs to re-run the wiring, or in case there are wiring differences between the practice robot and the competition robot.

### Driver
The Driver is the actor that controls the robots.  The Driver triggers different Operations to occur based on the intent of the current actor that is controlling the robot.  There are two types of Driver - the UserDriver and the AutonomousDriver.

#### Operation
An Operation is a single basic action that the robot can perform.  There are typically many operations for each mechanism.  These operations should be thought of as the most basic ways of controlling each mechanism.  Operations are also the building blocks on top of which we will build out Macros and Autonomous Routines.  Operations can be either Analog or Digital.

##### Analog Operations
Analog operations are typically operations that happen to a certain extent and are controlled by an axis on the joystick during teleop mode (e.g. the drivetrain is controlled by pushing forward along the Y axis of the joystick).  Analog operations are expected to return a double value (rational number, usually between -1.0 and 1.0).

##### Digital Operations
Digital operations are operations that are either happening or not happening and are controlled by a button on the joystick or button pad during teleop mode (e.g. a trigger on the joystick to cause a "shoot" action).  Digital operations are expected to return a boolean value (true or false).

There are three main types of digital operations 
* Simple: which is "true" whenever the button is actively being pressed, and "false" (off) otherwise.  A simple button would typically be used for spinning an intake roller while trying to pick up a ball.
* Toggle: which is "true" from the time that it is first pressed until the next time it is pressed, and then false until it is pressed again.  A toggle button could be used for enabling the vision system, though we often use a click button instead.
* Click: which is true the first time we run an update after each time the button is pressed, and false until the button has been released and pressed again).  A click button would typically be used for shooting a ball or lifting an arm.

#### Tasks
Tasks are used to control operations or groups of operations that run until a certain condition is met.  They are used within Macros and Autonomous Routines and can be composed together to perform complex actions.  Tasks themselves should aim to be relatively simple and only accomplish on thing if possible - this helps prevent code duplication later.  An example of a single task is "DriveDistanceTask", which drives forward for a specified distance, or "DriveTimedTask" which drives forward for a specified length of time.

Tasks implement the IControlTask interface, and typically extend from the ControlTaskBase class.  Tasks apply settings to one or more operations.  Every ~20ms, the update function is called to update the settings based on the criteria defined in the task.  Before running update, the hasCompleted function is called to check whether the task should end.

#### UserDriver
The UserDriver represents the driver and co-driver operating the robot using joysticks and button pads.  The user driver is in charge of reading from the joysticks and button pads, and then using the ButtonMap schema to translate the individual actions taken on the joystick into Operations and Macro Operations.  The UserDriver is used during Teleop mode.

##### Macros
Macros are groupings of different Operations that need to happen in a certain order and with certain conditions between the various operations.  This is typically done by defining a bunch of individual "tasks" that perform one operation until it has completed, and then composing them together using different types of logic.  One example of a macro would be the extending of the "murder arm" from the 2015 robot.  The murder arm macro could involve 3 operations: extending the arm, opening the rake, and then lowering the arm, each separated by a time delay to ensure that the arm was able to extend before opening the rake.  Another example of a macro from 2018 would be the climb macro, which moved the robot forwards a few inches, raised the elevator to the desired height for the hook to be level with the climbing bar, and then moved the robot backwards so that the hook was placed over the bar.

##### Shifts
Sometimes there aren't enough buttons on the joystick in order to accomodate the number of operations and macros that we want to have available to the Driver.  We have the ability to define "shifts" that allow the same button to mean different things depending on when another button is pressed.  These shifts are described in the ButtonMap along with the Operations.

#### AutonomousDriver
The AutonomousDriver runs the autonomous routine and translates that routine into the set of Operations so that the correct Operations happen at the correct times.  The AutonomousDriver is used during Autonomous mode.

##### Autonomous Routines
Autonomous routines are designed very similarly to macros, except that they are triggered automatically by the autonomous driver instead of by buttons on the joystick.

### TuningConstants
In order to simplify tuning the settings of the robot, we often store settings that we will likely want to change as constants in the TuningConstants class.  Settings that may need to be tuned include things like the speed at which to run an intake, or the speed at which to turn when the joystick is in a certain position.  These settings are usually things that are hard to know in advance, and the appropriate settings are discovered by testing the robot.  There are many things that aren’t known in advance by the software team, so putting all of these things in TuningConstants in an orderly fashion can help speed up the tuning process of the robot.

### HardwareConstants
Similar to the ElectronicsConstants and TuningConstants, we also store some facts about the dimensions of the robot and the different parts of the robot as constants in the HardwareConstants class.  This tends to include things like the diameter of the wheels and the width of the robot, which may be useful for calculations that need to be made during autonomous mode or for differential odometry (which calculates the robot’s position and orientation on the field as compared to the starting position/orientation by using the encoders).

### External Libraries
The robot code makes use of a number of external libraries in order to make programming the robot more straightforward.

#### Guice
[Guice](https://github.com/google/guice) (pronounced "juice") is a dependency injection library, which is responsible for the various "@Inject" and "@Singleton" markup that is seen throughout the code.  The purpose of Guice is to make it easier to plug together the entire system in such a way that it is still unit-testable.

#### OpenCV
[OpenCV](https://opencv.org/) is a computer vision library that is used for fast and efficient processing of images.  We use OpenCV functions to capture images, manipulate them (undistort, HSV filtering), write them, and discover important parts of them (find contours).

#### CTRE Phoenix
[CTRE Phoenix](https://github.com/CrossTheRoadElec/Phoenix-Documentation) is a library that provides the ability to communicate with and control the Talon SRX and Victor SR over CAN.  We use CTRE Phoenix to control the majority of our Talon SRXs so that we can run PID on the Talon SRX itself for a faster update rate.

#### NavX MXP
The [NavX MXP](http://www.pdocs.kauailabs.com/navx-mxp/software/) has a library that is used to interact with the NavX MXP.  The NavX uses its Gyroscope and Accelerometers in order to provide orientation measurements for field positioning purposes.

#### JUnit
[JUnit](https://junit.org/junit4/) is a unit testing library for Java.  JUnit is fairly simple and provides some comparison functions and a framework for running unit tests.

#### Mockito
[Mockito](http://site.mockito.org/) is a library for mocking objects for unit testing.  Mockito provides a way to create fake versions of objects that have behaviors that you can describe in a very succinct way.

## Instructions
### Setting up your Environment
After following the steps for [Installing Eclipse](https://wpilib.screenstepslive.com/s/currentCS/m/java/l/599681-installing-eclipse-c-java), you will need to do the following:
1. Create a new Robot project in Eclipse to set up environment variables (File --> New --> Project, select "Robot Java Project", select "Iterative Robot", and enter your team number).  Once you have created the project, right-click it and delete it.
2. Copy all of the files from the [Robot Libraries](https://github.com/irs1318dev/RobotLibraries) repository into the corresponding library directory (on Windows, C:\Users\<username>\wpilib\user\java\lib).
3. Get the code onto your local machine.
   a. Copy the repository's URL.  In GitHub, find the repository you are interested in, click the "Clone or download" button, and then copy the text (e.g. "https://github.com/irs1318dev/irs1318_general.git").
   b. In Eclipse, go to the "Git" perspective (Window --> Perspective --> Open Perspective --> other..., Git).
   c. Click the "Clone a git repository and add the clone to this view" button.  It should auto-fill the URL you copied above.
   d. Follow through the wizard, being sure to enable the "Import all existing Eclipse projects after clone finishes".
   e. Switch back to the Java perspective.  Your project should now appear.

### Making Simple Operation changes
To add a new action that the robot can take with a mechanism, first open the Operation enum (Operation.java) and add a new value to the list in that file.  We try to keep the various operations organized, so we keep them listed in a different section for each Mechanism.  The operation should be named starting with the mechanism (e.g. "DriveTrain", "Intake", etc.), and then a description of the action (e.g. "Turn", "RaiseArm", etc.).  Remember that Operations are a single, simple thing that is done by the robot.  Any more complex action that we want the robot to take will be a Macro which composes these Operations together (which we will talk about later).

Next, you will open the ButtonMap.java file and add another mapping into the OperationSchema that describes the Operation that you just added.  There are two types of operations - Analog or Digital.  Analog operations represent things that are double (decimal) values, typically between -1.0 and 1.0.  Digital operations represent things that are Boolean values (true or false).  Each type, Analog or Digital, has their own type of Description.

```java
put(
    Operation.DriveTrainTurn,
    new AnalogOperationDescription(
        UserInputDevice.Driver,
        AnalogAxis.X,
        ElectronicsConstants.INVERT_Y_AXIS,
        TuningConstants.DRIVETRAIN_Y_DEAD_ZONE));
```

The Analog description takes parameters describing the User Input Device (Driver or CoDriver joystick) and the axis of the joystick (X, Y, Throttle, etc.).
```java
put(
    Operation.IntakeRaiseArm,
    new DigitalOperationDescription(
        UserInputDevice.Driver,
        UserInputDeviceButton.JOYSTICK_STICK_BOTTOM_RIGHT_BUTTON,
        ButtonType.Simple));
```

The Digital description takes arguments describing the User Input Device, the button on the joystick, and the type of button (Simple, Toggle, or Click).

### Writing a new Mechanism
Mechanisms handle the interactions with the actuators (e.g. motors, pneumatic solenoids) and sensors (e.g. Encoders, Limit Switches) of each part of the robot, controlling them based on the operations from the Driver.  A mechanism is a class that implements the IMechanism interface with a name based on the name of that portion of the robot (e.g. DriveTrain, Intake) combined with "Mechanism", such as ThingMechanism.  It should be placed within the mechanisms folder with the other mechanisms and managers.

#### Define mechanism class and member variables
```java
@Singleton
public class ThingMechanism implements IMechanism
{
  // logging constant
  private static final String logName = "thing";

  // sensors and actuators
  private final ISomeSensor nameOfSensor;
  private final ISomeActuator nameOfAcutator;

  // logger
  private IDashboardLogger logger;

  // driver
  private Driver driver;

  // sensor values
  private boolean someSetting;

  // mechanism state
  private boolean someState;
```

At the top of the class, you should have a list of the definitions of your different actuators and sensors (see the "```private final ISomeActuator nameOfActuator;```" and "```private final ISomeSensor nameOfSensor;```").  These will be initialized in the constructor (a special function, as will be described below.  After the set of actuators and sensors are defined, you will also need to define the logger ("```private IDashboardLogger logger;```"), the driver ("```private Driver driver;```"), anything that will be read from the sensors ("```private boolean someSetting;```"), and any state that needs to be kept for the operation of the mechanism ("```private boolean someState;```").

#### Write mechanism constructor
```java
  @Inject
  public ThingMechanism(IWpilibProvider provider, IDashboardLogger logger)
  {
    this.nameOfSensor = provider.GetSomeSensor(ElectronicsConstants.SOME_SENSOR_CHANNEL);
    this.nameOfActuator = provider.GetSomeActuator(ElectronicsConstants.SOME_ACTUATOR_CHANNEL);

    this.logger = logger;

    this.someSetting = false;
    this.someState = false;
  }
  ...
```

After defining all of the class's variables, you will define a constructor named like "```public ThingMechanism(IWpilibProvider provider, IDashboardLogger logger)```".  Since 2017 we’ve made use of Google’s Guice to control dependency injection, which is the reason why the special @Inject markup is required.  You will then set the value for each actuator and sensor you defined at the top in the constructor by calling the corresponding function on the IWpilibProvider that is passed into the constructor.  These functions will take some number of arguments based on how the actuators/sensors are physically plugged together in the robot.  These arguments should be placed as constants in the ElectronicsConstants file.  We don’t necessarily know in advance how the robot plugs together, so they can be initialized with a value of -1 until we do.  After initializing the sensors and actuators, you should set the logger as provided and the settings and states to their default values.

#### Write mechanism readSensors function
```java
  @Override
  public void readSensors()
  {
    this.someSetting = this.nameOfSensor.get();

    this.logger.logBoolean(ThingMechanism.LogName, "someSetting", this.someSetting);
  }
```
The readSensors function reads from the relevant sensors for that mechanism, stores the results in class member variables, and then logs the results to the logger.

#### Write mechanism update function
```java
  @Override
  public void update()
  {
    boolean shouldThingAction = this.driver.getDigital(Operation.ThingAction);

    this.nameOfActuator.set(shouldThingAction);
  }
```

The update function examines the inputs from the Driver, and then calculates the various outputs to use applies them to the outputs for the relevant actuators.  For some mechanisms, the logic will be very simple - reading an operation and applying it to an actuator.  Other mechanisms will involve some internal state and information from the most recent readings from the sensors in order to determine what the actuator should do.

#### Write mechanism stop function
```java
  @Override
  public void stop()
  {
    this.nameOfActuator.set(0.0);
  }
```

The stop function tells each of the actuators to stop moving.  This typically means setting any Motor to 0.0 and any DoubleSolenoid to kOff.  It is called when the robot is being disabled, and it is very important to stop everything to ensure that the robot is safe.

#### Write mechanism setDriver function
```java
  @Override
  public void setDriver(Driver driver)
  {
    this.driver = driver;
  }
```

Sets the driver to use in this class (the implementation of this function should basically just contain "this.driver = driver;").  It is called when the robot is entering either the autonomous or teleop state, so it can also be used to reset the state of the mechanism if that is needed.

### Writing Macros and Autonomous Routines
Macros and Autonomous routines both involve control tasks.  These tasks control the robot through setting Operations.  For more advanced tasks, they can read the current state of the robot by running the functions that expose sensors on the Mechanism.

#### Writing Tasks
Tasks are used to control operations or groups of operations that run until a certain condition is met.  A task is a class that implements the IControlTask interface, and typically extends from the ControlTaskBase class. Tasks are named based on the sort of action they perform (e.g. RaiseElevator) combined with "Task", such as RaiseElevatorTask.  It should be placed within the controltasks folder with the other mechanisms and managers.

##### Define task class, member variables, and constructor
```java
public class RaiseElevatorTask extends ControlTaskBase implements IMechanism
{
  private ElevatorMechanism elevator;

  public RaiseElevatorTask()
  {
  }
```

At the top of the class, you should declare any member variables that you need.  Some of these may be initialized in the constructor, whereas others will be initialized in the begin function.

##### Define task begin function
```java
  public void begin()
  {
    this.elevator = this.getInjector().getInstance(ElevatorMechanism.class);
  }
```

The begin function is called at the very beginning of the task, and can be used to set some initial state and retrieve any mechanism that we need to reference.

##### Define task update function
```java
  public void update()
  {
    this.setDigitalOperationState(Operation.ElevatorRaise, true);
  }
```

The update function is called every ~20ms and should update the relevant operations.

##### Define task end function
```java
  public void end()
  {
    this.setDigitalOperationState(Operation.ElevatorRaise, false);
  }
```

The end function is called when the task has ended.  The end function resets the operations that were used to their default value and should clear any state that needs to be cleared.

##### Define task hasCompleted function
```java
  public boolean hasCompleted()
  {
    return this.elevatorMechanism.isRaised();
  }
```

The hasCompleted function is called by the driver to check whether the particular task should complete.  Often this is based on either the amount of time has elapsed since the task began, or it could be based on some sensor condition being met.

#### Adding Macros
To add a new Macro, you should add a new MacroOperation to the MacroOperation enumeration, and a new MacroOperationDecription to the MacroSchema within ButtonMap.

```java
put(
    MacroOperation.SomeMacro,
    new MacroOperationDescription(
        UserInputDevice.Driver,
        UserInputDeviceButton.JOYSTICK_STICK_THUMB_BUTTON,
        ButtonType.Toggle,
        () -> new SomeTask(),
        new Operation[]
        {
            Operation.ThingAction,
        }));
```
The MacroOperationDescription requires arguments describing the user input device to use, the button that triggers the macro, a constructor for the task that should be used within the macro, and a list of the different operations that this macro uses.

#### Composing Tasks into Routines
Tasks can be grouped together in interesting ways to describe more complex tasks.  By having tasks happen in a certain order and sometimes simultaneously, you can end up with a routine that performs interesting things.  Some particular tasks you can utilize include:

##### SequentialTask.Sequence()
Sequential task starts and completes each task in the order they are listed.

```java
SequentialTask.Sequence(
  new WaitTask(3.0),
  new DriveForwardTask(3.5));
```

The example above is a sequence of two tasks, where it will first wait 3 seconds and then will drive 3.5 inches forward.

##### ConcurrentTask.AnyTasks()
Concurrent AnyTasks starts all of the tasks at the same time and completes them when one of them has considered itself to be completed.

```java
ConcurrentTask.AnyTasks(
  new WaitTask(3.0),
  new DriveForwardTask(3.5));
```

The example above is a pair of two tasks that will execute at the same time, completing when either 3 seconds has elapsed or once the robot has driven 3.5 inches forward.

##### ConcurrentTask.AllTasks()
Concurrent AllTasks starts all of the tasks at the same time and completes when all of them have considered themselves to be completed.

```java
ConcurrentTask.AllTasks(
  new WaitTask(3.0),
  new DriveForwardTask(3.5));
```

The example above is a pair of two tasks that will execute at the same time, completing when the task has taken 3 seconds AND has driven 3.5 inches forward.

## Advanced Topics
### PID Controllers
"PID" stands for **P**roportional **I**ntegral and **D**erivative.  PID is a way of controlling a part of a robot that incorporates feedback from sensors in order to control the operation of the robot.  PID is often used as a way for correcting for _error_ caused by things like friction or other forces pushing back on the robot.  PID takes in values according to put in the current _measured value_ (the value discovered from an encoder or other sensor) and _setpoint_ (the desired value).  We typically use PID for elevators and for Positional control in the drivetrain.  For Velocity control we also use Feed-Forward to provide additional control.

With PID, there are different constant values that need to be discovered experimentally for the P, I, D, and F values.  Typically, F is only used for Velocity control.  P is used for basically all PID controllers.  I is used to correct error from slight overshoots or undershoots over time.  D is used to reduce oscillation around the setpoint.  For more information, Wikipedia has an ok article.

### Motion Planning
[Video](http://www.youtube.com/watch?v=8319J1BEHwM)

### Vision
To-Do.