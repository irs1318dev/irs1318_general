package org.usfirst.frc.team1318.robot.common.wpilibmocks;

import edu.wpi.first.wpilibj.PowerDistributionPanel;

public class PowerDistributionPanelWrapper implements IPowerDistributionPanel
{
    private final PowerDistributionPanel wrappedObject;

    public PowerDistributionPanelWrapper()
    {
        this.wrappedObject = new PowerDistributionPanel();
    }

    public PowerDistributionPanelWrapper(int module)
    {
        this.wrappedObject = new PowerDistributionPanel(module);
    }

    public double getVoltage()
    {
        return this.wrappedObject.getVoltage();
    }
}
