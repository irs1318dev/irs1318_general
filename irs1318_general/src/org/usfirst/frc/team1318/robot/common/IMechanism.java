package org.usfirst.frc.team1318.robot.common;

import org.usfirst.frc.team1318.robot.driver.Driver;

/**
 * The mechanism defines the logic that controls a mechanism given inputs and operator-requested actions, and 
 * translates those into the abstract functions that should be applied to the outputs.
 * 
 * @author Will
 *
 */
public interface IMechanism
{
    /**
     * calculate the various outputs to use based on the inputs and apply them to the outputs for the relevant mechanism
     */
    public void update();

    /**
     * stop the relevant mechanism
     */
    public void stop();

    /**
     * set the driver that the mechanism should use
     * @param driver to use
     */
    public void setDriver(Driver driver);
}