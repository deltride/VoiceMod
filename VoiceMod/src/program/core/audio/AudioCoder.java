package program.core.audio;

import org.jtransforms.fft.FloatFFT_1D;

import program.core.gui.EffectMenu;
import program.core.gui.StereoAudioVisualizer;
import program.core.util.WindowFunction;

public class AudioCoder {
	private StereoAudioVisualizer audioDisplay;
	private EffectMenu effectMenu;
	private FloatFFT_1D fft;
	private int sampleSize;
	private float[] windowFunc;
	private float[][] overlap;
	private boolean firstPass;
	
	public AudioCoder(int sampleSize){
		audioDisplay = new StereoAudioVisualizer(2,188,294,200);
		this.sampleSize = sampleSize;
		fft = new FloatFFT_1D(sampleSize*2);
		Frame.updateBaseFrequency(sampleSize);
		windowFunc = WindowFunction.rootNormalize(WindowFunction.HAMMING(sampleSize*2));
		overlap = new float[2][sampleSize];
		effectMenu = new EffectMenu();
		firstPass = true;
	}

	public StereoAudioVisualizer getAudioDisplay() {
		return audioDisplay;
	}
	
	public EffectMenu getEffectMenu() {
		return effectMenu;
	}

	public float[][] passAudio(float[][] decoded){
		if(firstPass){
			//initializing in the most disgusting way ever
			firstPass = false;
			System.arraycopy(decoded[0], 0, overlap[0], 0, sampleSize);
			System.arraycopy(decoded[1], 0, overlap[1], 0, sampleSize);
			return new float[2][sampleSize];
		}
		float[][] pass = new float[2][sampleSize * 4];
		for(int i = 0 ; i < sampleSize ; i++){
			pass[0][i] = windowFunc[i] * overlap[0][i];
			pass[1][i] = windowFunc[i] * overlap[1][i];
		}
		System.arraycopy(decoded[0], 0, overlap[0], 0, sampleSize);
		System.arraycopy(decoded[1], 0, overlap[1], 0, sampleSize);
		for(int i = sampleSize ; i < sampleSize*2 ; i++){
			pass[0][i] = windowFunc[i] * decoded[0][i-sampleSize];
			pass[1][i] = windowFunc[i] * decoded[1][i-sampleSize];
		}
		Frame f = new Frame(pass);
		//preFFT
		for(AudioEffect effect : effectMenu.getEffects(0)){
			effect.pass(f);
		}
		f.decompose();
		//FFT
		for(AudioEffect effect : effectMenu.getEffects(1)){
			effect.pass(f);
		}
		f.recompose();
		//postFFT
		for(AudioEffect effect : effectMenu.getEffects(2)){
			effect.pass(f);
		}
		int halfSample = sampleSize/2;
		float[][] dat = f.getRaw();
		float[][] res = new float[2][sampleSize];
		System.arraycopy(dat[0], halfSample, res[0], 0, sampleSize);
		System.arraycopy(dat[1], halfSample, res[1], 0, sampleSize);
		audioDisplay.setFrame(f);
		return res;
	}
	

	public int getSampleSize() {
		return sampleSize;
	}

	public FloatFFT_1D getFft() {
		return fft;
	}

	public void setFft(FloatFFT_1D fft) {
		this.fft = fft;
	}
}
