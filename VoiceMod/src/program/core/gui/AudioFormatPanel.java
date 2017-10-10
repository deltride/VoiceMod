package program.core.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.sound.sampled.AudioFormat;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

@Deprecated
public class AudioFormatPanel extends JPanel{
	private static final long serialVersionUID = 1L;

	private static final AudioFormat.Encoding[] encodingTypes = {AudioFormat.Encoding.PCM_SIGNED,AudioFormat.Encoding.PCM_UNSIGNED,
			AudioFormat.Encoding.PCM_FLOAT,AudioFormat.Encoding.ULAW,AudioFormat.Encoding.ALAW};
	
	JComboBox<AudioFormat.Encoding> encoding;
	JTextField samplerate;
	JTextField samplesize;
	
	public AudioFormatPanel(){
		super(null);
		JLabel identifier = new JLabel("Encoding");
		identifier.setHorizontalAlignment(JLabel.LEFT);
		identifier.setBounds(5,15,95,25);
		add(identifier);
		
		encoding = new JComboBox<AudioFormat.Encoding>(encodingTypes);
		encoding.setBounds(5, 35, 100, 25);
		add(encoding);
		
		identifier = new JLabel("Sample Rate");
		identifier.setHorizontalAlignment(JLabel.LEFT);
		identifier.setBounds(150,15,95,25);
		add(identifier);
		
		samplerate = new JTextField("44100");
		samplerate.setBounds(150,35,95,25);
		samplerate.addKeyListener(new NumberOnly());
		add(samplerate);
		
		
		identifier = new JLabel("Audio Output Device");
		identifier.setHorizontalAlignment(JLabel.CENTER);
		identifier.setBounds(5,65,290,25);
		add(identifier);
		
		JButton update = new JButton("Search Devices");
		update.setBounds(5,120,125,25);
		update.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
			}
			
		});
		add(update);
		
		setBounds(0,150,300,150);
		setBorder(BorderFactory.createTitledBorder(null, "Audio Formatting", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
	}
	class NumberOnly extends KeyAdapter {
		@Override
		public void keyTyped(KeyEvent e){
			if(!Character.isDigit(e.getKeyChar())){
				e.consume();
			}
		}
	}
}
