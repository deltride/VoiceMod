package program.core.audio;

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class AudioManager {
	public static AudioFormat format;
	public static void updateAudioFormat(AudioFormat format){
		AudioManager.format = format;
	}
	/**
	 * Returns a list of known output devices
	 */
	public static List<Mixer.Info> getAvailableOutputDevices(){
		List<Mixer.Info> available = new ArrayList<Mixer.Info>();
		DataLine.Info dat = new DataLine.Info(SourceDataLine.class, format);
		for(Mixer.Info m : AudioSystem.getMixerInfo()){
			if(AudioSystem.getMixer(m).isLineSupported(dat))
				available.add(m);
		}
		return available;
	}
	/**
	 * Returns a list of known input devices
	 */
	public static List<Mixer.Info> getAvailableInputDevices(){
		List<Mixer.Info> available = new ArrayList<Mixer.Info>();
		DataLine.Info dat = new DataLine.Info(TargetDataLine.class, format);
		for(Mixer.Info m : AudioSystem.getMixerInfo()){
			if(AudioSystem.getMixer(m).isLineSupported(dat))
				available.add(m);
		}
		return available;
	}
	/**
	 * Opens a line for listening, returning the generated line listener, or null if a line cannot be generated
	 */
	public static InputLineListener listen(Mixer.Info device){
		Mixer mixer = AudioSystem.getMixer(device);
		DataLine.Info dat = new DataLine.Info(TargetDataLine.class, format);
		if(mixer.isLineSupported(dat)){
			try {
				return new InputLineListener((TargetDataLine) mixer.getLine(dat));
			} catch (LineUnavailableException e) {}
		}
		return null;
	}
	/**
	 * Gets a line for listening, returning the generated line, or null if a line cannot be generated
	 */
	public static TargetDataLine getListeningLine(Mixer.Info device){
		Mixer mixer = AudioSystem.getMixer(device);
		DataLine.Info dat = new DataLine.Info(TargetDataLine.class, format);
		if(mixer.isLineSupported(dat)){
			try {
				return (TargetDataLine) mixer.getLine(dat);
			} catch (LineUnavailableException e) {}
		}
		return null;
	}
	/**
	 * Opens a line for writing, returning the generated line writer, or null if a line cannot be generated
	 */
	public static OutputLineWriter write(Mixer.Info device){
		Mixer mixer = AudioSystem.getMixer(device);
		DataLine.Info dat = new DataLine.Info(SourceDataLine.class, format);
		if(mixer.isLineSupported(dat)){
			try {
				return new OutputLineWriter((SourceDataLine) mixer.getLine(dat));
			} catch (LineUnavailableException e) {}
		}
		return null;
	}/**
	 * Gets a line for writing, returning the generated line, or null if a line cannot be generated
	 */
	public static SourceDataLine getWritingLine(Mixer.Info device){
		Mixer mixer = AudioSystem.getMixer(device);
		DataLine.Info dat = new DataLine.Info(SourceDataLine.class, format);
		if(mixer.isLineSupported(dat)){
			try {
				return (SourceDataLine) mixer.getLine(dat);
			} catch (LineUnavailableException e) {}
		}
		return null;
	}
	public static float[][] decodeStereo(byte[] data){
		float[][] decoded = new float[format.getChannels()][data.length/format.getFrameSize()];
		int sample = 0;
		for(int i = 0 ; i < data.length;){
			for(int c = 0 ; c < decoded.length ; c++){
				decoded[c][sample] = ((((short)data[i+1]) << 8) | (data[i] & 0x00FF)) / 32768.0f;
				i+=2;
			}
			sample++;
		}
		return decoded;
	}
	public static byte[] encodeStereo(float[][] decoded){
		byte[] data = new byte[decoded[0].length*format.getFrameSize()];
		int sample = 0;
		for(int i = 0 ; i < data.length;){
			for(int c = 0 ; c < decoded.length ; c++){
				short val = (short) (decoded[c][sample] * 32768);
				data[i+1] = (byte) (val >> 8);
				data[i] = (byte) (0x00FF & val);
				i+=2;
			}
			sample++;
		}
		return data;
	}
}
