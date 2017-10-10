package program.core.audio;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import program.core.Main;

public class OutputLineWriter {
	private volatile SourceDataLine line;
	private volatile boolean shouldContinue;
	private volatile BlockingQueue<byte[]> buffer;

	public OutputLineWriter(SourceDataLine line) {
		this.line = line;
		shouldContinue = false;
		buffer = new LinkedBlockingQueue<byte[]>();
	}

	public void open() {
		try {
			line.open();
			line.start();
			shouldContinue = true;
			new Thread(new LineReader()).start();
		} catch (LineUnavailableException e) {
			Main.gui.getDevicePanel().updateInputDropdown();
			Main.gui.getDeviceToggle().setToggleState(false);
		}
	}

	public BlockingQueue<byte[]> getStream(){
		return buffer;
	}
	public void close() {
		shouldContinue = false;
	}

	class LineReader implements Runnable {
		@Override
		public void run() {
			line.drain();
			while(shouldContinue){
				byte[] data = buffer.poll();
				if(data != null){
					line.write(data, 0, data.length);
				}
			}
			line.drain();
			line.stop();
			line.close();
		}

	}
}
