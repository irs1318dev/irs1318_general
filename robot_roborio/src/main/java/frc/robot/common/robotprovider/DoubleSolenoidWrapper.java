package frc.robot.common.robotprovider;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class DoubleSolenoidWrapper implements IDoubleSolenoid
{
    private final DoubleSolenoid wrappedObject;

    public DoubleSolenoidWrapper(int forwardChannel, int reverseChannel)
    {
        this.wrappedObject = new DoubleSolenoid(forwardChannel, reverseChannel);
    }

    public DoubleSolenoidWrapper(int moduleNumber, int forwardChannel, int reverseChannel)
    {
        this.wrappedObject = new DoubleSolenoid(moduleNumber, forwardChannel, reverseChannel);
    }

    public void set(DoubleSolenoidValue value)
    {
        Value wpilibValue = Value.kOff;
        if (value == DoubleSolenoidValue.Forward)
        {
            wpilibValue = Value.kForward;
        }
        else if (value == DoubleSolenoidValue.Reverse)
        {
            wpilibValue = Value.kReverse;
        }

        this.wrappedObject.set(wpilibValue);
    }
}
