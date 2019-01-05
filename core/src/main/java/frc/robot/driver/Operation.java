package frc.robot.driver;

public enum Operation
{
    // Vision operations:
    EnableVision,

    // DriveTrain operations:
    DriveTrainEnablePID,
    DriveTrainDisablePID,
    DriveTrainMoveForward,
    DriveTrainTurn,
    DriveTrainSimpleMode,
    DriveTrainUseBrakeMode,
    DriveTrainUsePositionalMode,
    DriveTrainUsePathMode,
    DriveTrainLeftPosition,
    DriveTrainRightPosition,
    DriveTrainLeftVelocity,
    DriveTrainRightVelocity,
    DriveTrainLeftAcceleration,
    DriveTrainRightAcceleration,
    DriveTrainSwapFrontOrientation,

    // OneMotor operations:
    OneMotorPower,
}
