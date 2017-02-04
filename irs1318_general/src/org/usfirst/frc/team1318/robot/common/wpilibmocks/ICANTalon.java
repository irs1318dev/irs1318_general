package org.usfirst.frc.team1318.robot.common.wpilibmocks;

public interface ICANTalon extends IMotor
{
    void changeControlMode(CANTalonControlMode mode);
    void setPIDF(double p, double i, double d, double f);
    void setPIDF(double p, double i, double d, double f, int izone, double closeLoopRampRate, int profile);
    void reverseOutput(boolean flip);
    void reverseSensor(boolean flip);
    void enableBrakeMode(boolean brake);
    double getSpeed();
    double getError();
}
