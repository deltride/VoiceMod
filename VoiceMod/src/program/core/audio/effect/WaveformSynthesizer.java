package program.core.audio.effect;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import program.core.audio.AudioEffect;
import program.core.audio.Channel;
import program.core.audio.Frame;

public class WaveformSynthesizer implements AudioEffect{
	private static final double HALF_STEP = Math.pow(2, 1/12d)-1;
	private float frequency;
	private double currOffset;
	private String channelWrite;
	private EffectPanel gui;
	public WaveformSynthesizer(float frequency, String channelWrite){
		this.frequency = frequency;
		this.channelWrite = channelWrite;
		genGui();
	}
	public WaveformSynthesizer(){
		this(440,"synth");
	}
	private void genGui(){
		gui = AudioEffect.super.getGUI();
		JTextField channel;
		gui.add(channel = new JTextField(channelWrite));
		channel.setBounds(1, 75, 300, 25);
		JLabel identifier = new JLabel("Channel: "+channel.getText());
		identifier.setHorizontalAlignment(JLabel.CENTER);
		identifier.setBounds(1,50,300,25);
		channel.getDocument().addDocumentListener(new DocumentListener(){
			public void changedUpdate(DocumentEvent e) {
				change();
			}
			public void removeUpdate(DocumentEvent e) {
				change();
			}
			public void insertUpdate(DocumentEvent e) {
				change();
			}
			public void change() {
				channelWrite = channel.getText();
				identifier.setText("Channel: "+channelWrite);
			}
		});
		channel.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				channelWrite = channel.getText();
				identifier.setText("Channel: "+channelWrite);
			}
		});
		gui.add(identifier);
		JSpinner sliderTime;
		SpinnerNumberModel model = new SpinnerNumberModel(frequency,10,10000,frequency * HALF_STEP);
		gui.add(sliderTime = new JSpinner(model));
		sliderTime.setBounds(1, 155, 300, 25);
		JLabel identifierA = new JLabel("Frequency: "+Math.floor(((double)sliderTime.getValue())*100)/100+"Hz");
		JLabel identifierB = new JLabel("Pitch: "+pitchFromFrequency(frequency));
		identifierA.setHorizontalAlignment(JLabel.CENTER);
		identifierA.setBounds(1,130,300,25);
		sliderTime.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				frequency = (float) ((double)sliderTime.getValue());
				model.setStepSize(((double)sliderTime.getValue()) * HALF_STEP);
				identifierA.setText("Frequency: "+Math.floor(((double)sliderTime.getValue())*100)/100+"Hz");
				identifierB.setText("Pitch: "+pitchFromFrequency(frequency));
			}
		});
		gui.add(identifierA);
		
		JButton snap;
		gui.add(snap = new JButton("Snap to Pitch"));
		snap.setBounds(1, 235, 300, 50);
		identifierB.setHorizontalAlignment(JLabel.CENTER);
		identifierB.setBounds(1,210,300,25);
		snap.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				frequency = (float) (440*Math.pow(2,
						((double)Math.round(Math.log(frequency/440)/Math.log(2)*12)
						)/12));
				sliderTime.setValue((double)frequency);
				model.setStepSize(((double)frequency) * HALF_STEP);
				identifierA.setText("Frequency: "+Math.floor(frequency*100)/100+"Hz");
				identifierB.setText("Pitch: "+pitchFromFrequency(frequency));
			}
		});
		gui.add(identifierB);
	}
	private static String pitchFromFrequency(float freq){
		String res;
		double p = Math.log(freq/440)/Math.log(2);
		int oct = (int) (Math.floor(p));
		p = (p-oct)*12;
		oct = (int) (Math.floor(p+57+(oct*12))/12);
		int note = (int) (Math.round(p)+12) % 12;
		switch(note){
			case 0:
				res = "A";
				break;
			case 1:
				res = "Bb";
				break;
			case 2:
				res = "B";
				break;
			case 3:
				res = "C";
				break;
			case 4:
				res = "C#";
				break;
			case 5:
				res = "D";
				break;
			case 6:
				res = "Eb";
				break;
			case 7:
				res = "E";
				break;
			case 8:
				res = "F";
				break;
			case 9:
				res = "F#";
				break;
			case 10:
				res = "G";
				break;
			case 11:
				res = "Ab";
				break;
			default:
				res = "%error%";
				break;
		}
		int cents = (int) Math.round(((p-note+.5)%1-.5)*100);
		if(cents >= 0){
			res+=oct+" +"+cents;
		}else{
			res+=oct+" "+cents;
		}
		return res;
	}
	@Override
	public void pass(Frame f) {
		Channel c = f.getSubChannel(channelWrite);
		float coeff = (float) (frequency*(Math.PI*2)/Frame.getBaseFrequency()/(Frame.getSamples()*2));
		if(c == null){
			float[][] dat = new float[2][Frame.getSamples()*2];
			for(int i = 0 ; i < dat[0].length; i++){
				float val = (float) Math.sin(i*coeff+currOffset)/10;
				dat[0][i] = val;
				dat[1][i] = val;
			}
			currOffset = (Frame.getSamples()*coeff+currOffset) % (Math.PI*2);
			f.setSubChannel(channelWrite, new Channel(dat));
		}else{
			float[][] dat = c.getRaw();
			for(int i = 0 ; i < dat[0].length; i++){
				float val = (float) Math.sin(i*coeff+currOffset)/10;
				dat[0][i] += val;
				dat[1][i] += val;
			}
			currOffset = (Frame.getSamples()*coeff+currOffset) % (Math.PI*2);
			f.setSubChannel(channelWrite, c);
		}
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
	@Override
	public EffectPanel getGUI() {
		return gui;
	}
	@Override
	public String saveToString() {
		return channelWrite.replaceAll(":", "")+":"+frequency;
	}
	@Override
	public AudioEffect fromString(String s) {
		String[] val = s.split(":");
		return new WaveformSynthesizer(Float.parseFloat(val[1]),val[0]);
	}

}
