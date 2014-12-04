package edu.thu.keg.mdap.dm.uri;

public class MathUtil {
	public static final double log2 = Math.log(2);
	public static double EPSILON = 0.00000001;
	/**
	 * Returns the KL divergence, K(p1 || p2).
	 *
	 * The log is w.r.t. base 2. <p>
	 *
	 * *Note*: If any value in <tt>p2</tt> is <tt>0.0</tt> then the KL-divergence
	 * is <tt>infinite</tt>. 
	 * 
	 */
	public static double klDivergence(double[] p1, double[] p2) {
		assert (p1.length == p2.length);
		double klDiv = 0.0;
		for (int i = 0; i < p1.length; ++i) {
			if (p1[i] == 0) {
				continue;
			}
			if (p2[i] == 0) {
				return Double.POSITIVE_INFINITY;
			}
			klDiv += p1[i] * Math.log(p1[i] / p2[i]);
		}
		return klDiv / log2; // moved this division out of the loop -DM
	}

	// gsc
	/**
	 * Returns the Jensen-Shannon divergence.
	 */
	public static double jensenShannonDivergence(double[] p1, double[] p2) {
		assert(p1.length == p2.length);
		double[] average = new double[p1.length];
		for (int i = 0; i < p1.length; ++i) {
			average[i] += (p1[i] + p2[i])/2;
		}
		return (klDivergence(p1, average) + klDivergence(p2, average))/2;
	}
	
	public static double perplexity(double [] p1, double [] p2) {
		assert(p1.length == p2.length);
		double sum = 0;
		for (int i = 0; i < p1.length; i++) {
			if (p1[i] == 0)
				continue;
			sum += p1[i] * Math.log(p2[i]);
		}
		sum = - sum / log2;
		return Math.pow(2, sum);
	}

	public static int sampleFromMultiNominal(double[] p) {
		int ret;
		int size = p.length;
		// cumulate multinomial parameters
		for (int k = 1; k < size; k++){
			p[k] += p[k - 1];
		}
		// scaled sample because of unnormalized p[]
		double u = Math.random() * p[size - 1];

		for (ret = 0; ret < size; ret++){
			if (p[ret] > u) //sample topic w.r.t distribution p
				break;
		}
		return ret;
	}
}
