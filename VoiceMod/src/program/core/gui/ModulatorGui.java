package program.core.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.sound.sampled.AudioFormat;
import javax.swing.JButton;
import javax.swing.JFrame;

import program.core.audio.AudioCoder;
import program.core.audio.AudioManager;
import program.core.audio.InputLineListener;
import program.core.audio.OutputLineWriter;
import program.core.audio.openal.AudioMaster;
import program.core.audio.openal.Source;

public class ModulatorGui {
	private static int SOUND;
	private JFrame frame;
	private Source mainSource;
	private DevicePanel devices;
	private ToggleButton deviceActive;
	private AudioCoder audio;
	public boolean closeRequested;
	
	private InputLineListener micIn;
	private OutputLineWriter speakerOut;

	public ModulatorGui(int width, int height) {
		closeRequested = false;
		initializeALContext();
		frame = new JFrame();
		initializeGui();
		frame.setSize(width, height);
		frame.setLayout(null);
		frame.setVisible(true);
		frame.setResizable(false);
		SOUND = AudioMaster.loadSoundfile("res/sound/SampleSound.wav");
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO destroy all sources
				micIn.close();
				speakerOut.close();
				
				mainSource.delete();
				AudioMaster.destroyAL();
				closeRequested = true;
			}
		});
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	public synchronized void update(){
		if(deviceActive.getToggleState()){
			byte[] dat = micIn.sampleStream(AudioManager.format.getFrameSize() * audio.getSampleSize());
			float[][] decoded = AudioManager.decodeStereo(dat);
			try {
				speakerOut.getStream().put(AudioManager.encodeStereo(audio.passAudio(decoded)));
			} catch (InterruptedException e) {}
		}
	}
	private void initializeGui() {
		AudioManager.updateAudioFormat(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false));
		devices = new DevicePanel();
		micIn = AudioManager.listen(devices.getSelectedInputMixer());
		speakerOut = AudioManager.write(devices.getSelectedOutputMixer());
		frame.add(devices);
		deviceActive = new ToggleButton("Off", "On",Color.RED, Color.GREEN){
			private static final long serialVersionUID = 1L;
			@Override
			public void toggleOn() {
				micIn.open();
				speakerOut.open();
			}
			@Override
			public void toggleOff() {
				micIn.close();
				speakerOut.close();
			}};
		deviceActive.setBounds(2, 150, 294, 35);
		frame.add(deviceActive);
		audio = new AudioCoder(2048);
		frame.add(audio.getAudioDisplay());
		frame.add(audio.getEffectMenu().getPipelineDisplay());
		frame.add(audio.getEffectMenu().getEffectDisplay());
		frame.add(audio.getEffectMenu().getPipelineHandlerMenu());
		JButton testB = new JButton("Play");
		testB.setBounds(900,0,100,50);
		testB.addActionListener(new PlayListener(mainSource));
		frame.add(testB);
	}
	private void initializeALContext(){
		AudioMaster.initAL();
		mainSource = new Source();
	}
	public void reloadDevices(){
		micIn.close();
		speakerOut.close();
		micIn = AudioManager.listen(devices.getSelectedInputMixer());
		speakerOut = AudioManager.write(devices.getSelectedOutputMixer());
		if(deviceActive.getToggleState()){
			micIn.open();
			speakerOut.open();
		}
	}

	public DevicePanel getDevicePanel() {
		return devices;
	}

	public ToggleButton getDeviceToggle() {
		return deviceActive;
	}

	public InputLineListener getMicIn() {
		return micIn;
	}

	public void setMicIn(InputLineListener micIn) {
		this.micIn = micIn;
	}

	public OutputLineWriter getSpeakerOut() {
		return speakerOut;
	}

	public void setSpeakerOut(OutputLineWriter speakerOut) {
		this.speakerOut = speakerOut;
	}

	public AudioCoder getAudioCoder() {
		return audio;
	}

	public JFrame getFrame() {
		return frame;
	}

	static class PlayListener implements ActionListener {
		Source s;
		public PlayListener(Source s){
			this.s = s;
		}
		@Override
		public void actionPerformed(ActionEvent event) {
			s.playSound(SOUND);
		}
	}
}
