package program.core.audio.openal;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALContext;

import program.core.util.Vector3f;

public class AudioMaster {
	public static volatile boolean hasContext = false;
	private static List<Integer> activeBuffers;
	private static ALContext context;

	public static void initAL() {
		if (!hasContext) {
			try {
				context = ALContext.create();
				context.makeCurrent();
				activeBuffers = new ArrayList<Integer>();
				setListenerLocation(new Vector3f(), new Vector3f());
				hasContext = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void destroyAL() {
		if (hasContext) {
			for (int i : activeBuffers) {
				AL10.alDeleteBuffers(i);
			}
			AL.destroy(context);
			hasContext = false;
		}
	}

	public static void setListenerLocation(Vector3f position, Vector3f velocity) {
		AL10.alListener3f(AL10.AL_POSITION, position.x, position.y, position.z);
		AL10.alListener3f(AL10.AL_VELOCITY, velocity.x, velocity.y, velocity.z);
	}

	public static int loadSoundfile(String path) {
		int buffer = AL10.alGenBuffers();
		activeBuffers.add(buffer);
		WaveData wave;
		try {
			wave = WaveData.create(new File(path).toURI().toURL());
			AL10.alBufferData(buffer, wave.format, wave.data, wave.samplerate);
			wave.dispose();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return buffer;
	}

	public static void unloadSound(int buffer) {
		AL10.alDeleteBuffers(buffer);
		activeBuffers.remove((Object) buffer);
	}
}
