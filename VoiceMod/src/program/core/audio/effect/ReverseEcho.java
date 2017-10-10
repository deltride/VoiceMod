package program.core.audio.effect;

import java.util.LinkedList;

import program.core.audio.AudioEffect;
import program.core.audio.Frame;

public class ReverseEcho implements AudioEffect{
	LinkedList<double[][]> dat;
	double scale;
	double minScale;
	int timeDom;
	float peak;
	double integrationFactor;
	public ReverseEcho(float time, float minScale, float peak){
		dat = new LinkedList<double[][]>();
		timeDom = (int) Math.floor(time * Frame.getBaseFrequency() * 2);
		scale = Math.pow(10, Math.log(minScale)/timeDom);
		this.minScale = Math.pow(scale, timeDom);
		//integrate [scale^x  from 0 to timeDom] to find area so we can average properly
		integrationFactor = Math.log(scale);
		integrationFactor = Math.pow(scale, timeDom)/integrationFactor - (1/integrationFactor);
		this.peak = peak;
		//add empty samples to simulate an absent echo until the audio stream is initiated
		for(int i = 0 ; i < timeDom ; i++){
			dat.add(new double[2][1]);
		}
	}
	public ReverseEcho(){
		this(2,.1f,5);
	}
	@Override
	public void pass(Frame f) {
			Object[] array = dat.toArray();
			double[][] thisFrame = new double[2][Frame.getSamples()];
			for(int t = 0 ; t < dat.size() ; t++){
				double[][] sample = (double[][]) array[t];
				if(sample[0].length != Frame.getSamples()){
					continue;
				}
				double scaleFactor = Math.pow(scale, t);//maybe implement this into standard echoes as pow(scale,timeDom-t)
				for(int i = 0 ; i < sample[0].length ; i++){
					//get the largest frequency sample overall to add to the stream
					
					//METHOD 1: MAXING
					thisFrame[0][i] = Math.max(thisFrame[0][i], sample[0][i]*scaleFactor);
					thisFrame[1][i] = Math.max(thisFrame[1][i], sample[1][i]*scaleFactor);
					
					//METHOD 2: AVERAGING
//					thisFrame[0][i] += sample[0][i]*scaleFactor/integrationFactor;
//					thisFrame[1][i] += sample[1][i]*scaleFactor/integrationFactor;
				}
			}
			//update the buffer, adding the newest and removing the oldest
			dat.removeFirst();
			double[][] mag = new double[2][thisFrame[0].length];
			for(int i = 0; i < mag[0].length ; i++){
				mag[0][i] = Math.min(f.getMagnitude(0, i), peak);
				mag[1][i] = Math.min(f.getMagnitude(1, i), peak);
				f.setMagnitude(0, i, mag[0][i]*minScale + thisFrame[0][i]);
				f.setMagnitude(1, i, mag[1][i]*minScale + thisFrame[1][i]);
			}
			dat.addLast(mag);
	}

	@Override
	public String getName() {
		return "Reverse Echo";
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
