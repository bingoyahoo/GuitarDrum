package sg.edu.nus.guitardrum;

import jAudioFeatureExtractor.Aggregators.Mean;
import jAudioFeatureExtractor.AudioFeatures.Compactness;
import jAudioFeatureExtractor.AudioFeatures.LPC;
import jAudioFeatureExtractor.AudioFeatures.MagnitudeSpectrum;
import jAudioFeatureExtractor.AudioFeatures.PowerSpectrum;
import jAudioFeatureExtractor.AudioFeatures.RMS;
import jAudioFeatureExtractor.AudioFeatures.SpectralCentroid;
import jAudioFeatureExtractor.AudioFeatures.ZeroCrossings;
import jAudioFeatureExtractor.jAudioTools.AudioSamples;

/**
 * 
 * This library provides around 50 feature extractor implementation.  Just a few of them are
 * chosen as the example feature extractors in this demo class.  Details implementation can
 * be found in the jAudioFeatureExtractor.AudioFeatures package.
 *
 */


public class FeaturesExtractor {
	AudioSamples audio;
	double sampling_rate;
	double[] samples;
	double[][] windowed_samples;
	double[][] other_features;
	double[] mag_spectrum;
	double[] power_spectrum;
	int window_size = 1024;
	
//	// API to manipulate samples, sample rate and spectrum
//	public FeaturesExtractor() throws Exception {
//		samples = new double[]{1.0, 2.0, 3.0, 1.0, 2.0, 3.0, 1.0, 2.0, 3.0, 1.0, 2.0, 3.0, };
//		sampling_rate = 100;
//
//		MagnitudeSpectrum ms = new MagnitudeSpectrum();
//		mag_spectrum = ms.extractFeature(samples, sampling_rate, null);
//
//		PowerSpectrum ps = new PowerSpectrum();
//		power_spectrum = ps.extractFeature(samples, sampling_rate, null);
//	}

	// API to manipulate samples, sample rate and spectrum
	public FeaturesExtractor(double[] buffer_samples, double buffer_sampling_rate) throws Exception {
		samples = buffer_samples;
		sampling_rate = buffer_sampling_rate;

		MagnitudeSpectrum ms = new MagnitudeSpectrum();
		mag_spectrum = ms.extractFeature(samples, sampling_rate, null);

		PowerSpectrum ps = new PowerSpectrum();
		power_spectrum = ps.extractFeature(samples, sampling_rate, null);
	}
	
	/**calculate features of single window samples.  A feature extractor takes in 3 arguments
	 * - samples, sampling rate and other features which is the dependence feature (need to check
	 * 	the feature extractor implementation for details).
	 * @throws Exception
	 */
	public void calculateFeatuers() throws Exception {
		RMS rms = new RMS();
		double[] result = rms.extractFeature(samples, sampling_rate, null);
		System.out.println(result[0]);
		
		ZeroCrossings zc = new ZeroCrossings();
		result = zc.extractFeature(samples, sampling_rate, null);
		System.out.println(result[0]);
		
		LPC lpc = new LPC();
		result = lpc.extractFeature(samples, sampling_rate, null);
		System.out.println(result[0]);
		
		Compactness ct = new Compactness();
		other_features = new double[1][];
		other_features[0] = mag_spectrum;
		result = ct.extractFeature(samples, sampling_rate, other_features);
		System.out.println(result[0]);

		SpectralCentroid sc = new SpectralCentroid();
		other_features = new double[1][];
		other_features[0] = power_spectrum;
		result = sc.extractFeature(samples, sampling_rate, other_features);
		System.out.println(result[0]);
//
//		MFCC mfcc = new MFCC();
//		other_features = new double[1][];
//		other_features[0] = mag_spectrum;
//		result = mfcc.extractFeature(samples, sampling_rate, other_features);
////		System.out.println(result[0]);
	}
	
	// calculate the aggregate value of multiple windowed samples
	public double[] calculateFeatuersMean() throws Exception {
		Mean mean = new Mean();
		RMS rms = new RMS();
		
		int[] featureIndex = new int[]{0};
		mean.setSource(rms);
		mean.init(featureIndex);
		
		double[][][] values = new double[][][]{windowed_samples};
		mean.aggregate(values);
		
		double[] feature_means = mean.getResults();
		return feature_means;
	}

}
