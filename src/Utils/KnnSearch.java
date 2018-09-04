package Utils;

import java.util.List;

import Utils.KdTree.Entry;

/**
 * @author Alex Schultz
 */
public class KnnSearch {
	protected final int dimension;
    KdTree<String> tree;
	
	public KnnSearch(int dimension) {
		this.dimension = dimension;
		this.tree = new KdTree.SqrEuclid<String>(dimension,null); 
	}

	public void addPoint(double[] location, double firingAngleOffset) {
		tree.addPoint(location, firingAngleOffset);
	}

	public double[] getNearestNeighbors(double[] location, int size) {
		List<Entry<Double>> list = tree.nearestNeighbor(location, size, false);
		
		double[] points = new double[list.size()];
		int i = 0;
		for (KdTree.Entry<Double> entry : list) {
			points[i] = entry.value;
			i++;
		}
		
		return points;
	}

	public final Neighbors getNeighbors(double[] location, int size) {
		double[] result = getNearestNeighbors(location, size);
		return new Neighbors(result);
	}
	
	protected final double getDistance(double[] p1, double[] p2) {
		double result = 0;
		for (int i = 0; i < p1.length; i++) {
			result += (p1[i] - p2[i]) * (p1[i] - p2[i]);
		}
		return result;
	}

	public class Neighbors {
		public final KdNode[] result;

		public Neighbors(KdNode[] result) {
			this.result = result;
		}

		public Neighbors(double[] values) {
			result = new KdNode[values.length];
			int count = 0;
			for (int i=0; i<values.length; i++) {
				result[count++] = new KdNode(values[i], 100);
			}
		}
		
		public KdNode[] getResult() {
			return result;
		}
	}

}
