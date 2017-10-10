package program.core.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import program.core.Main;
import program.core.audio.AudioManager;

public class DevicePanel extends JPanel{
	private static final long serialVersionUID = 1L;
	
	JComboBox<Mixer.Info> in;
	JComboBox<Mixer.Info> out;
	public DevicePanel(){
		super(null);
		JLabel identifier = new JLabel("Audio Input Device");
		identifier.setHorizontalAlignment(JLabel.CENTER);
		identifier.setBounds(5,15,290,25);
		add(identifier);
		
		in = new JComboBox<Mixer.Info>();
		in.setBounds(5, 35, 290, 25);
		in.setRenderer(new InfoRenderer());
		updateInputDropdown();
		add(in);
		
		identifier = new JLabel("Audio Output Device");
		identifier.setHorizontalAlignment(JLabel.CENTER);
		identifier.setBounds(5,65,290,25);
		add(identifier);
		
		out = new JComboBox<Mixer.Info>();
		out.setBounds(5, 85, 290, 25);
		out.setRenderer(new InfoRenderer());
		updateOutputDropdown();
		add(out);
		
		
		JButton update = new JButton("Search Devices");
		update.setBounds(5,120,125,25);
		update.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				updateInputDropdown();
				updateOutputDropdown();
			}
			
		});
		add(update);
		
		update = new JButton("Reload Devices");
		update.setBounds(170,120,125,25);
		update.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Main.gui.reloadDevices();
			}
			
		});
		add(update);
		
		setBounds(0,0,300,150);
		setBorder(BorderFactory.createTitledBorder(null, "Devices", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
	}
	
	public void updateInputDropdown(){
		Object s = in.getSelectedItem();
		in.removeAllItems();
		for(Mixer.Info a : AudioManager.getAvailableInputDevices()){
			in.addItem(a);
		}
		if(s != null)
			in.setSelectedItem(s);
	}
	public void updateOutputDropdown(){
		Object s = out.getSelectedItem();
		out.removeAllItems();
		for(Mixer.Info a : AudioManager.getAvailableOutputDevices()){
			out.addItem(a);
		}
		if(s != null)
			out.setSelectedItem(s);
	}
	public Mixer.Info getSelectedInputMixer(){
		return (Info) in.getSelectedItem();
	}
	public Mixer.Info getSelectedOutputMixer(){
		return (Info) out.getSelectedItem();
	}
	static class InfoRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(@SuppressWarnings("rawtypes") JList arg0, Object arg1, int arg2, boolean arg3, boolean arg4) {
			super.getListCellRendererComponent(arg0, arg1, arg2, arg3, arg4);
			setText(((Info)arg1).getName());
			return this;
		}
	}
}
