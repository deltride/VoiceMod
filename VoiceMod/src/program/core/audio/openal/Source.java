package program.core.audio.openal;

import org.lwjgl.openal.AL10;

public class Source {
	private int id;
	public Source(){
		id = AL10.alGenSources();
		AL10.alSourcef(id, AL10.AL_GAIN, 1);
		AL10.alSourcef(id, AL10.AL_PITCH, 1);
		AL10.alSource3f(id, AL10.AL_POSITION, 0, 0, 0);
	}
	public void delete(){
		AL10.alDeleteSources(id);
	}
	public void playSound(int buffer){
		AL10.alSourcei(id, AL10.AL_BUFFER, buffer);
		AL10.alSourcePlay(id);
	}
}
