package program.core.audio.effect;

import program.core.audio.AudioEffect;
import program.core.audio.Frame;

public class LowPass implements AudioEffect{
	private static double[] mask;
	private int bin;
	private int smoothingWindow;
	public LowPass(float cuttoffFreq, int smoothingWindow){
		bin = (int) (cuttoffFreq - Frame.getBaseFrequency()) - smoothingWindow;
		this.smoothingWindow = smoothingWindow;
		mask = new double[1];
		for(int i = 0 ; i < mask.length ; i++){
			if(i < bin){
				mask[i] = 1;
			}else if(i > bin + 2 * smoothingWindow){
				mask[i] = 0;
			}else{
				mask[i] = .5 * (Math.cos((i-bin) * Math.PI/smoothingWindow)+1);
			}
		}
	}
	public LowPass(){
		this(4000,10);
	}
	@Override
	public void pass(Frame f) {
		if(Frame.getSamples() != mask.length){
			mask = new double[Frame.getSamples()];
			for(int i = 0 ; i < mask.length ; i++){
				if(i < bin){
					mask[i] = 1;
				}else if(i > bin + 2 * smoothingWindow){
					mask[i] = 0;
				}else{
					mask[i] = .5 * (Math.cos((i-bin) * Math.PI/smoothingWindow)+1);
				}
			}
		}
		for(int i = 0 ; i < Frame.getSamples() ; i++){
			f.scaleMagnitude(0, i, mask[i]);
			f.scaleMagnitude(1, i, mask[i]);
		}
		
	}

	@Override
	public String getName() {
		return "Low-Pass Filter";
	}

	@Override
	public boolean getDecomposedCompatible() {
		return true;
	}
	@Override
	public boolean getComposedCompatible() {
		return false;
	}
	@Override
	public String saveToString() {
		return null;
	}
	@Override
	public AudioEffect fromString(String s) {
		return null;
	}
}
