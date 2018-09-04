package Utils;

import java.io.Serializable;

public class KdNode implements Comparable<KdNode>, Serializable {
	private static final long serialVersionUID = 3007596214916968733L;
	
	final Double firingAngle;
	final double distance;

	public KdNode(double firingAngle, double distance) {
		this.firingAngle = firingAngle;
		this.distance = distance;
	}

	public double getValue() {
		return firingAngle;
	}

	public double getDistance() {
		return distance;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(distance);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((firingAngle == null) ? 0 : firingAngle.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof KdNode))
			return false;
		KdNode other = (KdNode) obj;
		if (Double.doubleToLongBits(distance) != Double
				.doubleToLongBits(other.distance))
			return false;
		if (firingAngle == null) {
			if (other.firingAngle != null)
				return false;
		} else if (!firingAngle.equals(other.firingAngle))
			return false;
		return true;
	}

	public int compareTo(KdNode a) {
		return (int) Math.signum(distance - a.distance);
	}
}
