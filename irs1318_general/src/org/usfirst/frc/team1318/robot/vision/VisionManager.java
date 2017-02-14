package org.usfirst.frc.team1318.robot.vision;

import org.opencv.core.Point;
import org.usfirst.frc.team1318.robot.common.IController;
import org.usfirst.frc.team1318.robot.common.IDashboardLogger;
import org.usfirst.frc.team1318.robot.driver.Driver;
import org.usfirst.frc.team1318.robot.vision.pipelines.HSVGearCenterPipeline;
import org.usfirst.frc.team1318.robot.vision.pipelines.ICentroidVisionPipeline;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.vision.VisionRunner;
import edu.wpi.first.wpilibj.vision.VisionThread;

/**
 * Vision manager.
 * 
 * @author Will
 *
 */
@Singleton
public class VisionManager implements IController, VisionRunner.Listener<ICentroidVisionPipeline>
{
    private final static String LogName = "vision";

    private final IDashboardLogger logger;

    private final Object visionLock;
    private final VisionThread gearVisionThread;
    private final HSVGearCenterPipeline gearVisionPipeline;

    private Point center;

    private Double desiredAngleX;
    private Double measuredAngleX;
    private Double distanceFromRobot;

    private double lastMeasuredFps;

    /**
     * Initializes a new VisionManager
     */
    @Inject
    public VisionManager(IDashboardLogger logger)
    {
        this.logger = logger;

        this.visionLock = new Object();

        UsbCamera gearCamera = new UsbCamera("usb0", 0);
        gearCamera.setResolution(VisionConstants.LIFECAM_CAMERA_RESOLUTION_X, VisionConstants.LIFECAM_CAMERA_RESOLUTION_Y);
        gearCamera.setExposureManual(VisionConstants.LIFECAM_CAMERA_EXPOSURE);
        gearCamera.setBrightness(VisionConstants.LIFECAM_CAMERA_BRIGHTNESS);
        gearCamera.setFPS(VisionConstants.LIFECAM_CAMERA_FPS);

        //CameraServer.getInstance().addCamera(camera);

        //AxisCamera camera = CameraServer.getInstance().addAxisCamera(VisionConstants.AXIS_CAMERA_IP_ADDRESS);
        this.gearVisionPipeline = new HSVGearCenterPipeline(VisionConstants.SHOULD_UNDISTORT);
        this.gearVisionThread = new VisionThread(gearCamera, this.gearVisionPipeline, this);
        this.gearVisionThread.start();

        this.center = null;
        this.desiredAngleX = null;
        this.measuredAngleX = null;
        this.distanceFromRobot = null;

        this.lastMeasuredFps = 0.0;
    }

    public Point getCenter()
    {
        synchronized (this.visionLock)
        {
            return this.center;
        }
    }

    public Double getMeasuredAngle()
    {
        synchronized (this.visionLock)
        {
            return this.measuredAngleX;
        }
    }

    public Double getDesiredAngle()
    {
        synchronized (this.visionLock)
        {
            return this.desiredAngleX;
        }
    }

    public Double getMeasuredDistance()
    {
        synchronized (this.visionLock)
        {
            return this.distanceFromRobot;
        }
    }

    public double getLastMeasuredFps()
    {
        synchronized (this.visionLock)
        {
            return this.lastMeasuredFps;
        }
    }

    @Override
    public void update()
    {
        String centerString = "n/a";
        Point center = this.getCenter();
        if (center != null)
        {
            centerString = String.format("%f,%f", center.x, center.y);
        }

        this.logger.logString(VisionManager.LogName, "center", centerString);

        Double fps = this.getLastMeasuredFps();
        this.logger.logNumber(VisionManager.LogName, "fps", fps);

        Double dist = this.getMeasuredDistance();
        this.logger.logNumber(VisionManager.LogName, "dist", dist);

        Double dAngle = this.getDesiredAngle();
        this.logger.logNumber(VisionManager.LogName, "dAngle", dAngle);

        Double mAngle = this.getMeasuredAngle();
        this.logger.logNumber(VisionManager.LogName, "mAngle", mAngle);
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

    @Override
    public void copyPipelineOutputs(ICentroidVisionPipeline pipeline)
    {
        synchronized (this.visionLock)
        {
            this.center = pipeline.getCenter();

            this.desiredAngleX = pipeline.getDesiredAngleX();
            this.measuredAngleX = pipeline.getMeasuredAngleX();
            this.distanceFromRobot = pipeline.getRobotDistance();

            this.lastMeasuredFps = pipeline.getFps();
        }
    }
}
