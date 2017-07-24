package org.usfirst.frc.team1318.robot;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import org.usfirst.frc.team1318.robot.common.IDashboardLogger;
import org.usfirst.frc.team1318.robot.common.IMechanism;
import org.usfirst.frc.team1318.robot.common.SmartDashboardLogger;
import org.usfirst.frc.team1318.robot.common.wpilib.ITimer;
import org.usfirst.frc.team1318.robot.common.wpilib.IWpilibProvider;
import org.usfirst.frc.team1318.robot.common.wpilib.TimerWrapper;
import org.usfirst.frc.team1318.robot.common.wpilib.WpilibProvider;
import org.usfirst.frc.team1318.robot.compressor.CompressorMechanism;
import org.usfirst.frc.team1318.robot.driver.ButtonMap;
import org.usfirst.frc.team1318.robot.driver.IButtonMap;
import org.usfirst.frc.team1318.robot.drivetrain.DriveTrainMechanism;
import org.usfirst.frc.team1318.robot.general.PositionManager;
import org.usfirst.frc.team1318.robot.general.PowerManager;
import org.usfirst.frc.team1318.robot.onemotor.OneMotorMechanism;
import org.usfirst.frc.team1318.robot.vision.VisionManager;

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

    @Singleton
    @Provides
    public MechanismManager getMechanismManager(Injector injector)
    {
        List<IMechanism> mechanismList = new ArrayList<>();
        mechanismList.add(injector.getInstance(PowerManager.class));
        mechanismList.add(injector.getInstance(PositionManager.class));
        mechanismList.add(injector.getInstance(VisionManager.class));
        mechanismList.add(injector.getInstance(CompressorMechanism.class));
        mechanismList.add(injector.getInstance(DriveTrainMechanism.class));
        mechanismList.add(injector.getInstance(OneMotorMechanism.class));
        return new MechanismManager(mechanismList);
    }
    /*
    @Singleton
    @Provides
    @Named("ONEMOTOR_MOTOR")
    public ICANTalon getOneMotorMotor()
    {
        CANTalonWrapper master = new CANTalonWrapper(ElectronicsConstants.ONEMOTOR_MASTER_MOTOR_CHANNEL);
        master.enableBrakeMode(false);
        master.reverseSensor(false);
    
        CANTalonWrapper follower = new CANTalonWrapper(ElectronicsConstants.ONEMOTOR_FOLLOWER_MOTOR_CHANNEL);
        follower.enableBrakeMode(false);
        follower.reverseOutput(true);
        follower.changeControlMode(CANTalonControlMode.Follower);
        follower.set(ElectronicsConstants.ONEMOTOR_MASTER_MOTOR_CHANNEL);
    
        if (TuningConstants.ONEMOTOR_USE_PID)
        {
            master.changeControlMode(CANTalonControlMode.Speed);
            master.setPIDF(
                TuningConstants.ONEMOTOR_PID_KP,
                TuningConstants.ONEMOTOR_PID_KI,
                TuningConstants.ONEMOTOR_PID_KD,
                TuningConstants.ONEMOTOR_PID_KF);
        }
        else
        {
            master.changeControlMode(CANTalonControlMode.PercentVbus);
        }
    
        return master;
    }*/
}
