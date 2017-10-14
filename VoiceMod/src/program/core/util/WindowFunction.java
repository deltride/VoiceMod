package program.core.util;

public class WindowFunction {
	private static final float TWO_PI = (float) (Math.PI * 2);
	
	//https://en.wikipedia.org/wiki/Window_function
	public static float[] rootNormalize(float[] func){
		for(int i = 0 ; i < func.length ; i++){
			func[i] = (float) Math.sqrt(func[i]);
		}
		return func;
	}
	public static float[] RECTANGLE(int size){
		float[] w = new float[size];
		for(int n = 0 ; n < size ; n++){
			w[n] = 1;
		}
		return w;
	}
	
	public static float[] SINE(int size){
		float[] w = new float[size];
		for(int n = 0 ; n < size ; n++){
			w[n] = (float) Math.sin((TWO_PI/2) * n / (size-1));
		}
		return w;
	}
	
	public static float[] HANN(int size){
		float[] w = new float[size];
		for(int n = 0 ; n < size ; n++){
			w[n] = .5f * ((float)(1-Math.cos(TWO_PI*n/(size-1))));
		}
		return w;
	}
	
	public static float[] HAMMING(int size){
		float[] w = new float[size];
		for(int n = 0 ; n < size ; n++){
			w[n] = (float)(.54f-.46*Math.cos(TWO_PI*n/(size-1)));
		}
		return w;
	}
	
	public static float[] TUKEY(int size){
		float[] w = new float[size];
		for(int n = 0 ; n < size ; n++){
			if(n < .25f*(size-1)){
				w[n] = .5f * ((float)(1 + Math.cos(TWO_PI * ((2*n/(size-1))-.5))));
			}else if(n < (size-1) * (.75f)){
				w[n] = 1;
			}else{
				w[n] = .5f * ((float)(1 + Math.cos(TWO_PI * ((2*n/(size-1))-3))));
			}
		}
		return w;
	}
}
