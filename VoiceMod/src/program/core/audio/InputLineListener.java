package program.core.audio;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import program.core.Main;

public class InputLineListener {
	private volatile TargetDataLine line;
	private volatile boolean shouldContinue;
	private volatile BlockingDeque<byte[]> stream;

	public InputLineListener(TargetDataLine line) {
		this.line = line;
		shouldContinue = false;
		stream = new LinkedBlockingDeque<byte[]>();
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
	public BlockingDeque<byte[]> getStream(){
		return stream;
	}
	public byte[] sampleStream(int bytes){
		int i = 0;
		byte[] dat = new byte[bytes];
		byte[] sample;
		byte[] overflow = null;
		while(i < bytes){
			try {
				sample = stream.takeFirst();
				
				if(i+sample.length > bytes){
					overflow = new byte[i+sample.length-bytes];
				}
				for(int x = 0 ; x < sample.length ; x++){
					if(i+x < bytes){
						dat[i+x] = sample[x];
					}else{
						overflow[i+x-bytes] = sample[x];
					}
				}
				i+=sample.length;
				if(overflow != null){
					stream.addFirst(overflow);
					return dat;
				}else if(i+sample.length == bytes){
					return dat;
				}
			} catch (InterruptedException e) {}
		}
		return dat;
	}

	public void close() {
		shouldContinue = false;
	}

	class LineReader implements Runnable {
		@Override
		public void run() {
			line.flush();
			int bytesWritten;
			byte[] data = new byte[line.getBufferSize()/5];
			while(shouldContinue){
				bytesWritten = line.read(data, 0, data.length);
				byte[] truncated = new byte[bytesWritten];
				for(int i = 0 ; i < bytesWritten ; i++){
					truncated[i] = data[i];
				}
				stream.addLast(truncated);
			}
			line.flush();
			line.stop();
			line.close();
		}

	}
}