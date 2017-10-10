package program.core.audio.effect;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import program.core.audio.AudioEffect;
import program.core.audio.Frame;

public class VolumeControl implements AudioEffect {
	private float scale;
	private EffectPanel gui;
	public VolumeControl(float scale){
		this.scale = scale;
		genGui();
	}
	private void genGui(){
		gui = AudioEffect.super.getGUI();
		JSlider slider;
		gui.add(slider = EffectPanel.genSlider(1, 75, 300, 50, "Volume", 0, 200, (int)(scale*100), 10, 100, true));
		JLabel identifier = new JLabel("Volume Multiplier: "+slider.getValue()+"%");
		identifier.setHorizontalAlignment(JLabel.CENTER);
		identifier.setBounds(1,50,300,25);
		slider.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				scale = slider.getValue()/100f;
				identifier.setText("Volume Multiplier: "+slider.getValue()+"%");
			}
		});
		gui.add(identifier);
	}
	public VolumeControl(){
		this(1);
	}
	@Override
	public void pass(Frame f) {
		if(f.isDecomposed()){
			for(int i = 0 ; i < Frame.getSamples() ; i++){
				f.scaleMagnitude(0, i, scale);
				f.scaleMagnitude(1, i, scale);
			}
		}else{
			float[][] raw = f.getRaw();
			for(int i = 0 ; i < Frame.getSamples()*2 ; i++){
				raw[0][i] *= scale;
				raw[1][i] *= scale;
			}
		}
	}

	public double getScale() {
		return scale;
	}
	public void setScale(float scale) {
		this.scale = scale;
	}
	
	@Override
	public EffectPanel getGUI() {
		return gui;
	}

	@Override
	public String getName() {
		return "Volume Controller/Amplifier";
	}
	@Override
	public boolean getDecomposedCompatible() {
		return true;
	}
	@Override
	public boolean getComposedCompatible() {
		return true;
	}
}
