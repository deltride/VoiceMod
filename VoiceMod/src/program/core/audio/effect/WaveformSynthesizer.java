package program.core.audio.effect;

import java.util.function.DoubleUnaryOperator;

import program.core.audio.AudioEffect;
import program.core.audio.Frame;

public class WaveformSynthesizer implements AudioEffect{
	DoubleUnaryOperator func;
	float frequency;
	double currOffset;
	String channelWrite;
	public WaveformSynthesizer(DoubleUnaryOperator func, float frequency, String channelWrite){
		this.frequency = frequency;
		this.func = func;
		this.channelWrite = channelWrite;
	}
	public WaveformSynthesizer(){
		this((t)->Math.sin(t*(Math.PI*2)),440,"synth");
	}
	@Override
	public void pass(Frame f) {
		
	}

	@Override
	public String getName() {
		return "Waveform Synthesizer/Oscillator";
	}

	@Override
	public boolean getDecomposedCompatible() {
		return false;
	}

	@Override
	public boolean getComposedCompatible() {
		return true;
	}

}
