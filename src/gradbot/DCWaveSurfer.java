package gradbot;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import Utils.BucketSort;
import Utils.DataPoint;
import Utils.KdNode;
import Utils.KnnSearch;
import robocode.*;
import robocode.AdvancedRobot;
import robocode.util.Utils;

public class DCWaveSurfer extends AdvancedRobot {
	private double battlefieldWidth;
	private double battlefieldHeight;
	private Double prevTargetVelocity = null;
	private Double currentTargetVelocity = null;
	
	public static KnnSearch kdTree = new KnnSearch(8); //9 dimensional kdTree for use with nearest-neighbour search
	public ArrayList<DataPoint> dataPoints = new ArrayList<DataPoint>();
	public static Point2D.Double myPosition;
	public static Point2D.Double enemyPosition;
	private boolean readyToShoot = false;
	double targettingAngle = 0.0;
	
	public void run()
	{
		battlefieldWidth = getBattleFieldWidth();
		battlefieldHeight = getBattleFieldHeight();
		
		while(true){
			myPosition = new Point2D.Double(getX(), getY());
			turnRadarRightRadians(0.5);
			
			execute();
		}
	}	
	
	public void onScannedRobot(ScannedRobotEvent e)
    {
          double enemyAbsoluteBearing = getHeadingRadians() + e.getBearingRadians();
           setTurnRadarRightRadians(Utils.normalRelativeAngle((enemyAbsoluteBearing) - getRadarHeadingRadians()) * 2);
         
          DataPoint d = logNewWave(e);
         
          if (d!=null) {
                 double turnRemainining = Math.abs(this.getGunTurnRemaining());
                
                 if (Double.isNaN(turnRemainining) || Math.abs(this.getGunTurnRemaining())<0.1) {
                        Double targettingAngle = getTargettingAngle(d);
                        
                        if (targettingAngle!=null) {
                        	setTurnGunRightRadians(Utils.normalRelativeAngle(Math.toRadians(targettingAngle - getGunHeading())));
                            setFire(2.0);
                           
                            drawShootAngle(targettingAngle);
                        }                        
                        execute();
                 }           
          }
    }
	
	private double getBotHalfWidth(double distance) {
		return Math.abs(36.0/distance);
	}

	private double getMaxEscapeAngle(double velocity) {
		return Math.asin(8.0 / velocity);
	}
	
	private void drawShootAngle(double angle) {
		Graphics2D g = getGraphics();
		g.setColor(Color.RED);

		int x = (int)(getX() + Math.sin(Math.toRadians(angle)) * 800);
	    int y = (int)(getY() + Math.cos(Math.toRadians(angle)) * 800);
	    
	    g.drawLine(x, y, (int)getX(), (int)getY());
	}
	
	private DataPoint logNewWave(ScannedRobotEvent e) {
		prevTargetVelocity = currentTargetVelocity;
		currentTargetVelocity = e.getVelocity();
		enemyPosition = getEnemyCoordinates(getX(), getY(), getHeading(), e.getBearing(), e.getDistance());
		if (prevTargetVelocity != null) {
			DataPoint wave = new DataPoint(this, e, false, 3.0, true, prevTargetVelocity, battlefieldWidth, battlefieldHeight);
			addCustomEvent(wave);
			return wave;
		}
		return null;
	}
	
	private Double getTargettingAngle(DataPoint d) {
		double[] aimingAngles = kdTree.getNearestNeighbors(d.getDataArray(), 300);
		if (aimingAngles.length > 0) {
			double maxEscapeAngle = getMaxEscapeAngle(currentTargetVelocity);
			int numberOfBuckets = (int)(maxEscapeAngle / getBotHalfWidth(d.distance));
			return new BucketSort(aimingAngles, numberOfBuckets, maxEscapeAngle).getCenterOfMaxBucket();
		}
		return null;
	}
	
	public void onBulletHit(BulletHitEvent e)
	{

	}
	
	public void onBulletHitBullet(BulletHitBulletEvent e)
	{

	}	

	public void createSampleData(ScannedRobotEvent e) {

	}
	
	private Point2D.Double getEnemyCoordinates(double x, double y, double heading, double bearing, double distance) {
		// Calculate the angle to the scanned robot
        double angle = Math.toRadians((heading + bearing) % 360);

        // Calculate the coordinates of the robot
        double enemyX = (x + Math.sin(angle) * distance);
        double enemyY = (y + Math.cos(angle) * distance);
        
        return new Point2D.Double(enemyX, enemyY);
	}
	
	public Point2D.Double getPosition() {
		return myPosition;
	}
	
	public Point2D.Double getEnemyPosition() {
		return enemyPosition;
	}
	
	public void onSkippedTurn(SkippedTurnEvent e)
	{
		out.println("skipped turn");
	}
}

//public void onBulletMissed(BulletMissedEvent e)
//{
//
//}
//
//public void onDeath(DeathEvent e)
//{
//
//}
//
//public void onHitByBullet(HitByBulletEvent e)
//{
//
//}
//
//public void onHitRobot(HitRobotEvent e)
//{
//
//}
//
//public void onHitWall(HitWallEvent e)
//{
//
//}
//
//public void onRobotDeath(RobotDeathEvent e)
//{
//
//}
//
//public void onRoundEnded(RoundEndedEvent e)
//{
//
//}
//
//public void onStatus(StatusEvent e)
//{
//
//}
//
//public void onWin(WinEvent e)
//{
//
//}
//

//
//public void onBattleEnded(BattleEndedEvent e)
//{
//
//}