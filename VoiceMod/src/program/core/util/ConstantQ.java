package program.core.util;

import org.jtransforms.fft.FloatFFT_1D;

public class ConstantQ {
	private static final double LOG2 = Math.log(2);
	private static final double THRESHOLD = .001;
	
	private float minFreq, maxFreq, binsPerOctave;
	private int sampleRate;
	
	private float q;
	private int binCount;
	private float octaves;
	private int fftLength;
	
	private float[][] qKernel;
	private int[][] qKernelIndices;
	private float[] binFrequencies;
	
	private FloatFFT_1D fft;
	
	public ConstantQ(float minFreq, float maxFreq, float binsPerOctave, int sampleRate) {
		//set & calc vars
		this.minFreq = minFreq;
		this.maxFreq = maxFreq;
		this.binsPerOctave = binsPerOctave;
		this.sampleRate = sampleRate;
		q = (float) (1/(Math.pow(2, 1/binsPerOctave)-1));
		octaves = (float) (Math.log(maxFreq/minFreq)/LOG2);
		binCount = (int) Math.ceil(octaves * binsPerOctave);
		fftLength = (int) Math.pow(2, Math.ceil(Math.log(
				Math.ceil(q * sampleRate/minFreq)
				)/LOG2));
		fft = new FloatFFT_1D(fftLength);//SAMPLERATE???
		qKernel = new float[binCount][];
		qKernelIndices = new int[binCount][];
		binFrequencies = new float[binCount];
		//iterate through each bin
		
		for(int i = 0 ; i < binCount ; i++){
			float[] kernelF = new float[fftLength * 2];
			//freq = baseFreq * 2^(octave #)
			binFrequencies[i] = (float) (minFreq * Math.pow(2, i/binsPerOctave));
			//hann function into kernel[], set everything outside domain to 0
			int l = (int) Math.min(Math.ceil(q * sampleRate / binFrequencies[i]),fftLength);
			for(int j = 0 ; j < l*2 ; j+=2){
				double r = (.5 - (Math.cos(Math.PI * j / l))/.5)/l;//FUNC???
				double theta = q * (Math.PI * j) / l;
				kernelF[j] = (float) (r*Math.cos(theta));
				kernelF[j+1] = (float) (r*Math.sin(theta));
			}
			for(int j = l*2 ; j < fftLength*2 ; j++){
				kernelF[j] = 0;
			}
			fft.complexForward(kernelF);
			
			float[] kernelT = new float[fftLength*2];
			int[] ind = new int[fftLength];
			int k = 0;
			
			//do proper indexing
			for(int j = 0, jInv = kernelF.length-2 ; j < kernelF.length/2 ; j+=2, jInv-=2){
				double mag = Math.sqrt(kernelF[j] * kernelF[j] + kernelF[j+1] * kernelF[j+1]) + 
						Math.sqrt(kernelF[jInv] * kernelF[jInv] + kernelF[jInv+1] * kernelF[jInv+1]);
				if(mag > THRESHOLD){
					ind[k] = 0;
					kernelT[2*k] = kernelF[j] + kernelF[jInv];
					kernelT[2*k+1] = kernelF[j+1] + kernelF[jInv+1];
					k++;
				}
			}
			
			kernelF = new float[k*2];
			int[] indices = new int[k];
			//transfer, normalize, and conjugate
			for(int j = 0 ; j < kernelF.length ; j++){
				kernelF[j] = -kernelT[j]/fftLength;
				if(j % 2 == 1){
					kernelF[j] *= -1;
				}
			}
			for(int j = 0 ; j < indices.length ; j++){
				indices[j] = ind[j];
			}
			qKernel[i] = kernelF;
			qKernelIndices[i] = indices;
		}
	}
	public float[] calculate(float[] data){
		fft.realForward(data);
		float[] coeff = new float[binCount * 2];
		for(int i = 0 ; i < qKernel.length ; i++){
			float[] kernel = qKernel[i];
			int[] indices = qKernelIndices[i];
			float re = 0;
			float im = 0;
			for(int j = 0, l = 0; j < kernel.length; j+=2,l++){
				int index = indices[l];
				float dRe = data[index];
				float dIm = data[index+1];
				float kRe = kernel[j];
				float kIm = kernel[j+1];
				re += dRe * kRe - dIm * kIm;
				im += dRe * kIm + dIm * kRe;
			}
			coeff[2*i] = re;
			coeff[2*i + 1] = im;
		}
		return coeff;
	}
	public float getMinFreq() {
		return minFreq;
	}
	public float getMaxFreq() {
		return maxFreq;
	}
	public float getBinsPerOctave() {
		return binsPerOctave;
	}
	public int getSampleRate() {
		return sampleRate;
	}
	public float getQ() {
		return q;
	}
	public int getBinCount() {
		return binCount;
	}
	public float getOctaves() {
		return octaves;
	}
	public int getFftLength() {
		return fftLength;
	}
	public float[][] getqKernel() {
		return qKernel;
	}
	public int[][] getqKernelIndices() {
		return qKernelIndices;
	}
	public float[] getBinFrequencies() {
		return binFrequencies;
	}
}
