package Utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import gradbot.DCWaveSurfer;
import robocode.Condition;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

// DataPoint contains information about the battlefield at a segment in time
// The correct angle to fire at based on the bulletPower and the
public class DataPoint extends Condition {
	// target properties that we segment along
	public double distance;
	public final double parallelVelocity;
	public final double perpendicularVelocity;
	public final double relativeHeading;
	public final double acceleration;
	public final double distanceToWall;
	public final double distanceToCorner;
//	public final double timeSinceDirectionChange
//	public final double timeSinceAccelerationChange
//	public final double timeOccurred
//	public final double bulletPower
	public final double isVirtual;
	
	// supplementary fields
	public final Point2D.Double sourceOriginPosition;
	public final double initialBearing;
	public final double bulletSpeed;
	public double waveRadius;
	public final boolean forTargetting;
	public final DCWaveSurfer instance;

	public DataPoint(DCWaveSurfer source, ScannedRobotEvent target, boolean forMovement, double bulletPower, boolean isVirtualDataPoint, double prevVelocity, double battlefieldWidth, double battlefieldHeight) {
		distance = normalise(0, target.getDistance(), Math.max(battlefieldWidth, battlefieldHeight)); 
		parallelVelocity = normalise(-8.0, target.getVelocity() * -1 * Math.cos(target.getHeadingRadians() - (target.getBearingRadians() + source.getHeadingRadians())), 8.0);
		perpendicularVelocity = normalise(-8.0, target.getVelocity() * Math.sin(target.getHeadingRadians() - (target.getBearingRadians() + source.getHeadingRadians())), 8.0);
		relativeHeading = normalise(-180, Utils.normalRelativeAngleDegrees(target.getHeading() - (target.getBearing() + source.getHeading())), 180);
		acceleration = normalise(-2, getAccelerationStatus(target.getVelocity() - prevVelocity), 1);
		isVirtual = isVirtualDataPoint ? 1.0 : 0.0;
		
		sourceOriginPosition = forMovement ? source.getEnemyPosition() : new Point2D.Double(source.getX(), source.getY());
		distanceToWall = getDistanceToWall(sourceOriginPosition, battlefieldWidth, battlefieldHeight);
		distanceToCorner = getDistanceToCorner(sourceOriginPosition, battlefieldWidth, battlefieldHeight);
		
		initialBearing = target.getBearing();
		bulletSpeed = 20 - (bulletPower * 3);
		waveRadius = 2 * bulletSpeed;
		forTargetting = !forMovement;
		instance = source;
	}
	
	public double[] getDataArray() {
		double[] array = new double[8];
		array[0] = distance;
		array[1] = parallelVelocity;
		array[2] = perpendicularVelocity;
		array[3] = relativeHeading;
		array[4] = acceleration;
		array[5] = distanceToWall;
		array[6] = distanceToCorner;
		array[7] = isVirtual;
		return array;
	}
	
	private double getAccelerationStatus(double deltaVelocity) {
        if (deltaVelocity > 0) {
        	return 2.0; //accelerating
        } else if (deltaVelocity < 0) {
        	return 0.0; //deccelerating
        } else {
        	return 1.0; //constant acceleration
        }
	}
	
	private double getDistanceToWall(Point2D.Double enemyPosition, double battlefieldWidth, double battlefieldHeight) {        
        return Math.min(Math.min(enemyPosition.getX(), battlefieldWidth - enemyPosition.getX()), Math.min(enemyPosition.getY(), battlefieldHeight - enemyPosition.getY()));
	}
	
	private double getDistanceToCorner(Point2D.Double enemyPosition, double battleFieldWidth, double battlefieldHeight) {		
		double x = enemyPosition.getX();
		double y = enemyPosition.getY();
		
		if (x <= battleFieldWidth / 2 && y > battlefieldHeight / 2) { // top-left quadrant
			return enemyPosition.distance(new Point2D.Double(0, battlefieldHeight));
		} else if(x > battleFieldWidth /2 && y > battlefieldHeight / 2) { // top-right quadrant
			return enemyPosition.distance(new Point2D.Double(battleFieldWidth, battlefieldHeight));
		} else if(x <= battleFieldWidth / 2 && y <= battlefieldHeight / 2) { // bottom-left quadrant
			return enemyPosition.distance(new Point2D.Double(0, 0));
		} else { // bottom-right quadrant
			return enemyPosition.distance(new Point2D.Double(battleFieldWidth, 0));
		}
	}
	
	public String getValue() {
		return getDataArray().toString();
	}
	
	// returns a scaled value between 0 and 1
	private double normalise(double min, double value, double max) {
		return (value - min) / (max-min);
	}

	@Override
	public boolean test() {
		Point2D.Double targetLocation = forTargetting ? instance.getEnemyPosition() : instance.getPosition();
		waveRadius+=bulletSpeed;
		drawWave();
		
		if (distanceToPoint(targetLocation) <= waveRadius) {            
			if (!instance.dataPoints.remove(this)) {
            	double desiredDirection = Math.atan2(targetLocation.getX() - sourceOriginPosition.getX(), targetLocation.getY() - sourceOriginPosition.getY());
    			double absoluteDesiredDegrees = Math.toDegrees(desiredDirection);
    			DCWaveSurfer.kdTree.addPoint(getDataArray(), absoluteDesiredDegrees);
    			drawShootAngle(absoluteDesiredDegrees);
            }
            instance.removeCustomEvent(this);
            return true;
        }
        return false;
	}
	
	private double distanceToPoint(Point2D.Double p) {
        return sourceOriginPosition.distance(p);
    }
	
	private void drawShootAngle(double angle) {
		Graphics2D g = instance.getGraphics();

		int x = (int)(instance.getX() + Math.sin(Math.toRadians(angle)) * 800);
	    int y = (int)(instance.getY() + Math.cos(Math.toRadians(angle)) * 800);
	    
	    g.drawLine(x, y, (int)instance.getX(), (int)instance.getY());
	}
	
	private void drawWave() {
		Graphics2D g = instance.getGraphics();

		g.setColor(Color.orange);
		g.drawOval((int)(sourceOriginPosition.getX() - waveRadius), (int)(sourceOriginPosition.getY() - waveRadius), (int)waveRadius*2, (int)waveRadius*2);
	}
}
