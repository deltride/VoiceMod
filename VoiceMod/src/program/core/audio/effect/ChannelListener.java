package program.core.audio.effect;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import program.core.audio.AudioEffect;
import program.core.audio.Channel;
import program.core.audio.Frame;

public class ChannelListener implements AudioEffect{
	String src;
	String dest;
	EffectPanel gui;
	public ChannelListener(String src, String dest){
		this.src = src;
		this.dest = dest;
		genGui();
	}
	public ChannelListener(){
		this("synth","main");
	}
	private void genGui(){
		gui = AudioEffect.super.getGUI();
		JTextField cSrc;
		gui.add(cSrc = new JTextField(src));
		cSrc.setBounds(1, 75, 300, 25);
		JLabel identifierA = new JLabel("Source Channel: "+cSrc.getText());
		identifierA.setHorizontalAlignment(JLabel.CENTER);
		identifierA.setBounds(1,50,300,25);
		cSrc.getDocument().addDocumentListener(new DocumentListener(){
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
				src = cSrc.getText();
				identifierA.setText("Source Channel: "+src);
			}
		});
		cSrc.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				src = cSrc.getText();
				identifierA.setText("Source Channel: "+src);
			}
		});
		gui.add(identifierA);
		JTextField channel;
		gui.add(channel = new JTextField(dest));
		channel.setBounds(1, 155, 300, 25);
		JLabel identifier = new JLabel("Destination Channel: "+channel.getText());
		identifier.setHorizontalAlignment(JLabel.CENTER);
		identifier.setBounds(1,130,300,25);
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
				dest = channel.getText();
				identifier.setText("Destination Channel: "+dest);
			}
		});
		channel.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				dest = channel.getText();
				identifier.setText("Destination Channel: "+dest);
			}
		});
		gui.add(identifier);
	}
	@Override
	public void pass(Frame f) {
		Channel s = f.getSubChannel(src);
		Channel d = f.getSubChannel(dest);
		if(s == null){
			return;
		}
		s.recompose();
		float[][] a = s.getRaw();
		if(d == null){
			float[][] res = new float[2][s.getRaw()[0].length];
			System.arraycopy(a[0], 0, res[0], 0, Frame.getSamples()*2);
			System.arraycopy(a[1], 0, res[1], 0, Frame.getSamples()*2);
			d = new Channel(res);
			f.setSubChannel(dest, d);
			return;
		}
		d.recompose();
		float[][] b = d.getRaw();
		for(int i = 0 ; i < Frame.getSamples()*2 ; i++){
			b[0][i] += a[0][i];
			b[1][i] += a[1][i];
		}
	}

	@Override
	public String getName() {
		return "Channel Listener";
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
		return src.replaceAll(":", "")+":"+dest.replaceAll(":", "");
	}
	@Override
	public AudioEffect fromString(String s) {
		String[] val = s.split(":");
		return new ChannelListener(val[0],val[1]);
	}
}
