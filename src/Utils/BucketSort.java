package Utils;

public class BucketSort {
	private int[] bucketArray;
	private double bucketWidth;	
	
	public BucketSort(double[] data, int numBuckets, double maxSpan) {
		this.bucketWidth = maxSpan / numBuckets;
		this.bucketArray = new int[numBuckets];
		
		for (int i=0; i<data.length; i++) {
			add(data[i]);
		}
	}
	
	public void add(double value) {
		bucketArray[(int)(value/bucketWidth)]++;
	}
	
	public double getCenterOfMaxBucket() {
		int maxIndex = 0;
		int maxCount = 0;
		for (int i=0; i<bucketArray.length; i++) {
			if (bucketArray[i] > maxCount) {
				maxCount = bucketArray[i];
				maxIndex = i;
			}
		}
		
		return (maxIndex * bucketWidth) - (bucketWidth / 2);
	}
}
