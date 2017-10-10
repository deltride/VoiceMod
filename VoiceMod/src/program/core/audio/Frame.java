package program.core.audio;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import program.core.Main;

public class Frame {
	private float[][] raw;
	private double[][] magnitudes;
	private static int samples;
	private boolean decomposed;
	private static float baseFrequency;
	private Map<String,Channel> subChannels;
	/**
	 * Generates a frame object from the raw post-FFT data
	 */
	public Frame(float[][] raw) {
		this.raw = raw;
		samples = raw[0].length/4;
		decomposed = false;
		subChannels = new HashMap<String,Channel>();
	}
	public double getMagnitude(int channel, int frequencyBin){
		return magnitudes[channel][frequencyBin];
	}
	/**
	 * Sets the magnitude of a given frequency bin. (calls scaleMagnitude() with mag/currentMag)
	 */
	public void setMagnitude(int channel, int frequencyBin, double mag){
		double cMag = magnitudes[channel][frequencyBin];
		if(cMag > 0){
			scaleMagnitude(channel,frequencyBin,mag/cMag);
		}
	}
	/**
	 * Multiplies the current magnitude within a given frequency bin
	 */
	public void scaleMagnitude(int channel, int frequencyBin, double mult){
		raw[channel][2*frequencyBin] *= mult;
		raw[channel][2*frequencyBin+1] *= mult;
		magnitudes[channel][frequencyBin] *= mult;
	}
	public Channel getSubChannel(String id){
		return subChannels.get(id);
	}
	public void setSubChannel(String id, Channel sub){
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
	
	public float[][] getRaw() {
		return raw;
	}
	public double[][] getMagnitudes() {
		return magnitudes;
	}
	public static int getSamples() {
		return samples;
	}
	public void decompose(){
		if(!decomposed){
			Main.gui.getAudioCoder().getFft().realForward(raw[0]);
			Main.gui.getAudioCoder().getFft().realForward(raw[1]);
			magnitudes = new double[2][samples];
			for(int i = 0 ; i < samples * 2 ; i += 2){
				magnitudes[0][i/2] = Math.sqrt(raw[0][i]*raw[0][i] + raw[0][i+1]*raw[0][i+1]);
				magnitudes[1][i/2] = Math.sqrt(raw[1][i]*raw[1][i] + raw[1][i+1]*raw[1][i+1]);
			}
			decomposed = true;
		}
	}
	public void recompose(){
		if(decomposed){
			Main.gui.getAudioCoder().getFft().realInverse(raw[0], true);
			Main.gui.getAudioCoder().getFft().realInverse(raw[1], true);
			decomposed = false;
		}
	}
	public boolean isDecomposed() {
		return decomposed;
	}
	static class Channel {
		private float[][] raw;
		private double[][] magnitudes;
		private boolean decomposed;
		public Channel(float[][] raw) {
			this.raw = raw;
			decomposed = false;
		}
		public void decompose(){
			if(!decomposed){
				Main.gui.getAudioCoder().getFft().realForward(raw[0]);
				Main.gui.getAudioCoder().getFft().realForward(raw[1]);
				magnitudes = new double[2][samples];
				for(int i = 0 ; i < samples * 2 ; i += 2){
					magnitudes[0][i/2] = Math.sqrt(raw[0][i]*raw[0][i] + raw[0][i+1]*raw[0][i+1]);
					magnitudes[1][i/2] = Math.sqrt(raw[1][i]*raw[1][i] + raw[1][i+1]*raw[1][i+1]);
				}
				decomposed = true;
			}
		}
		public void recompose(){
			if(decomposed){
				Main.gui.getAudioCoder().getFft().realInverse(raw[0], true);
				Main.gui.getAudioCoder().getFft().realInverse(raw[1], true);
				decomposed = false;
			}
		}
		public double getMagnitude(int channel, int frequencyBin){
			return magnitudes[channel][frequencyBin];
		}
		public void setMagnitude(int channel, int frequencyBin, double mag){
			double cMag = magnitudes[channel][frequencyBin];
			if(cMag > 0){
				scaleMagnitude(channel,frequencyBin,mag/cMag);
			}
		}
		public void scaleMagnitude(int channel, int frequencyBin, double mult){
			raw[channel][2*frequencyBin] *= mult;
			raw[channel][2*frequencyBin+1] *= mult;
			magnitudes[channel][frequencyBin] *= mult;
		}
		public float[][] getRaw() {
			return raw;
		}
		public double[][] getMagnitudes() {
			return magnitudes;
		}
		public static int getSamples() {
			return samples;
		}
	}
}
