package program.core.audio;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Frame extends Channel{
	private static int samples;
	private static float baseFrequency;
	private Map<String,Channel> subChannels;
	/**
	 * Generates a frame object from the raw post-FFT data.
	 * <p>Each frame has a main and sub channels for handling more complex features</p>
	 */
	public Frame(float[][] raw) {
		super(raw);
		samples = raw[0].length/4;
		decomposed = false;
		subChannels = new HashMap<String,Channel>();
	}
	public Channel getSubChannel(String id){
		if(id.equals("main"))
			return this;
		return subChannels.get(id);
	}
	public void setSubChannel(String id, Channel sub){
		if(id.equals("main")){
			raw = sub.raw;
			magnitudes = sub.magnitudes;
			decomposed = sub.decomposed;
		}
		subChannels.put(id, sub);
	}
	public Set<String> getSubChannelNames(){
		return subChannels.keySet();
	}
	public int getFrequencyBin(float hz){
		return (int) Math.floor(hz/baseFrequency);
	}
	public float getMinFrequency(int bin){
		return bin*baseFrequency;
	}
	public static void updateBaseFrequency(int sampleSize){
		baseFrequency = AudioManager.format.getFrameRate()/(sampleSize*2);
	}
	public static float getBaseFrequency(){
		return baseFrequency;
	}
	public double[][] getMagnitudes() {
		return magnitudes;
	}
	public static int getSamples() {
		return samples;
	}
}
