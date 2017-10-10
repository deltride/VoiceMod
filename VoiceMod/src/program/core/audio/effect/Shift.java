package program.core.audio.effect;

import program.core.audio.AudioEffect;
import program.core.audio.Frame;

public class Shift implements AudioEffect{
	private int binShift;
	public Shift(int shift){
		binShift = shift;
	}
	public Shift(){
		this(0);
	}
	@Override
	public void pass(Frame f) {
		double[][] mag = f.getMagnitudes();
		int period = mag[0].length;
		double[][] newMag = new double[2][period];
		for(int i = 0 ; i < period ; i++){
			int index = ((i+binShift) % period + period) % period;
			newMag[0][index] = mag[0][i];
			newMag[1][index] = mag[1][i];
		}
		for(int i = 0 ; i < period ; i++){
			f.setMagnitude(0, i, newMag[0][i]);
			f.setMagnitude(1, i, newMag[1][i]);
		}
	}

	@Override
	public String getName() {
		return "Pitch Shifter";
	}
	@Override
	public boolean getDecomposedCompatible() {
		return true;
	}
	@Override
	public boolean getComposedCompatible() {
		return false;
	}
}
