package org.usfirst.frc.team1318.robot.general;

import org.usfirst.frc.team1318.robot.common.ComplementaryFilter;
import org.usfirst.frc.team1318.robot.common.IController;
import org.usfirst.frc.team1318.robot.common.wpilibmocks.IPowerDistributionPanel;
import org.usfirst.frc.team1318.robot.driver.Driver;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * Power manager.
 * 
 * @author Will
 *
 */
@Singleton
public class PowerManager implements IController
{
    private final IPowerDistributionPanel pdp;
    private ComplementaryFilter filter;

    /**
     * Initializes a new PowerComponent
     */
    @Inject
    public PowerManager(@Named("POWERMANAGER_PDP") IPowerDistributionPanel pdp)
    {
        this.pdp = pdp;
        this.filter = new ComplementaryFilter(0.4, 0.6, this.pdp.getVoltage());
    }

    public double getVoltage()
    {
        return this.filter.getValue();
    }

    @Override
    public void update()
    {
        this.filter.update(this.pdp.getVoltage());
    }

    @Override
    public void stop()
    {
    }

    @Override
    public void setDriver(Driver driver)
    {
        // no-op
    }
}
