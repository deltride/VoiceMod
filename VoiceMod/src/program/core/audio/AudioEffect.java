package program.core.audio;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;

public interface AudioEffect {
	public void pass(Frame f);
	public default EffectPanel getGUI(){
		return new EffectPanel();
	}
	public String getName();
	public boolean getDecomposedCompatible();
	public boolean getComposedCompatible();
	public class EffectPanel extends JPanel{
		private static final long serialVersionUID = 1L;
		public EffectPanel(){
			super(null);
			setBounds(482,0,538,300);
			setBorder(BorderFactory.createTitledBorder(null, "Effect", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
		}
		public static JSlider genSlider(int x, int y, int width, int height, String text, int min, int max, int init, int minorTick, int majorTick, boolean labelTicks){
			JSlider slider = new JSlider(JSlider.HORIZONTAL,min,max,init);
			slider.setBounds(x,y,width,height);
			slider.setMajorTickSpacing(majorTick);
			slider.setMinorTickSpacing(minorTick);
			slider.setPaintTicks(true);
			slider.setPaintLabels(labelTicks);
			return slider;
		}
	}
}
