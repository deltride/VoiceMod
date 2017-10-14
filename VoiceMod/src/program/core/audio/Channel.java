package program.core.audio;

import program.core.Main;

public class Channel {
	protected float[][] raw;
	protected double[][] magnitudes;
	protected boolean decomposed;
	public Channel(float[][] raw) {
		this.raw = raw;
		decomposed = false;
	}
	public void decompose(){
		if(!decomposed){
			Main.gui.getAudioCoder().getFft().realForward(raw[0]);
			Main.gui.getAudioCoder().getFft().realForward(raw[1]);
			magnitudes = new double[2][Frame.getSamples()];
			for(int i = 0 ; i < Frame.getSamples() * 2 ; i += 2){
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
	public void setRaw(float[][] raw) {
		this.raw = raw;
	}
	public double[][] getMagnitudes() {
		return magnitudes;
	}
	public void setMagnitudes(double[][] magnitudes) {
		this.magnitudes = magnitudes;
	}
	public boolean isDecomposed() {
		return decomposed;
	}
}
