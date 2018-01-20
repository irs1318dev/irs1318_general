package org.usfirst.frc.team1318.robot;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import org.usfirst.frc.team1318.robot.common.IDashboardLogger;
import org.usfirst.frc.team1318.robot.common.IMechanism;
import org.usfirst.frc.team1318.robot.common.MechanismManager;
import org.usfirst.frc.team1318.robot.common.SmartDashboardLogger;
import org.usfirst.frc.team1318.robot.common.wpilib.ITimer;
import org.usfirst.frc.team1318.robot.common.wpilib.IWpilibProvider;
import org.usfirst.frc.team1318.robot.common.wpilib.TimerWrapper;
import org.usfirst.frc.team1318.robot.common.wpilib.WpilibProvider;
import org.usfirst.frc.team1318.robot.driver.ButtonMap;
import org.usfirst.frc.team1318.robot.driver.common.IButtonMap;
<<<<<<< HEAD
import org.usfirst.frc.team1318.robot.onemotor.OneMotorMechanism;
=======
import org.usfirst.frc.team1318.robot.drivetrain.DriveTrainMechanism;
>>>>>>> refs/heads/CANDriveTrain

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;

public class RobotModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        this.bind(IWpilibProvider.class).to(WpilibProvider.class);
        this.bind(ITimer.class).to(TimerWrapper.class);
        this.bind(IButtonMap.class).to(ButtonMap.class);
    }

    @Singleton
    @Provides
    public MechanismManager getMechanismManager(Injector injector)
    {
        List<IMechanism> mechanismList = new ArrayList<>();
        mechanismList.add(injector.getInstance(DriveTrainMechanism.class));
        //mechanismList.add(injector.getInstance(PositionManager.class));
        //mechanismList.add(injector.getInstance(PowerManager.class));
        //mechanismList.add(injector.getInstance(PositionManager.class));
        //mechanismList.add(injector.getInstance(VisionManager.class));
        //mechanismList.add(injector.getInstance(CompressorMechanism.class));
        mechanismList.add(injector.getInstance(OneMotorMechanism.class));
        return new MechanismManager(mechanismList);
    }

    @Singleton
    @Provides
    public IDashboardLogger getLogger()
    {
        IDashboardLogger logger = new SmartDashboardLogger();
        //        try
        //        {
        //            String fileName = String.format("/home/lvuser/%1$d.csv", Calendar.getInstance().getTime().getTime());
        //            IDashboardLogger csvLogger = new CSVLogger(fileName, new String[] { "r.time", "vision.mAngle", "vision.dist" });
        //            logger = new MultiLogger(logger, csvLogger);
        //        }
        //        catch (IOException e)
        //        {
        //            e.printStackTrace();
        //        }

        return logger;
    }
}
