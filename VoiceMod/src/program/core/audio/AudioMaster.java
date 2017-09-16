package program.core.audio;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.newdawn.slick.openal.WaveData;

public class AudioMaster {
	private static List<Integer> activeBuffers;
	public static void initAL(){
		try{
			ALC.create();
			activeBuffers = new ArrayList<Integer>();
		}catch(Exception e){
			
		}
	}
	public static void destroyAL(){
		for(int i : activeBuffers){
			AL10.alDeleteBuffers(i);
		}
		ALC.destroy();
	}
	
	public static int loadSoundfile(String path){
		int buffer = AL10.alGenBuffers();
		activeBuffers.add(buffer);
		WaveData wave = WaveData.create(path);
		AL10.alBufferData(buffer, wave.format, wave.data, wave.samplerate);
		wave.dispose();
		return buffer;
	}
	public static void unloadSound(int buffer){
		AL10.alDeleteBuffers(buffer);
		activeBuffers.remove((Object)buffer);
	}
}
