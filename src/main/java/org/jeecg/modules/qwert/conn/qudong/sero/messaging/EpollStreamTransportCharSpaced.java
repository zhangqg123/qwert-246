/**
 * Copyright (C) 2014 Infinite Automation Software. All rights reserved.
 * @author Terry Packer
 */
package org.jeecg.modules.qwert.conn.qudong.sero.messaging;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jeecg.modules.qwert.conn.qudong.sero.epoll.InputStreamEPollWrapper;

/**
 * <p>EpollStreamTransportCharSpaced class.</p>
 *
 * @author Terry Packer
 * @version 5.0.0
 */
public class EpollStreamTransportCharSpaced extends EpollStreamTransport{

	private final long charSpacing; //Spacing for chars in nanoseconds
    private final OutputStream out; //Since the subclass has private members
	
	/**
	 * <p>Constructor for EpollStreamTransportCharSpaced.</p>
	 *
	 * @param in a {@link java.io.InputStream} object.
	 * @param out a {@link java.io.OutputStream} object.
	 * @param epoll a {@link org.jeecg.modules.qwert.conn.qudong.sero.epoll.InputStreamEPollWrapper} object.
	 * @param charSpacing a long.
	 */
	public EpollStreamTransportCharSpaced(InputStream in, OutputStream out,
			InputStreamEPollWrapper epoll, long charSpacing) {
		super(in, out, epoll);
		this.out = out;
		this.charSpacing = charSpacing;
	}
	
    /**
     * {@inheritDoc}
     *
     * Perform a write, ensure space between chars
     */
	@Override
    public void write(byte[] data) throws IOException {
		
		try{
		long waited = 0,writeStart,writeEnd, waitRemaining;
			for(byte b : data){
				writeStart = System.nanoTime();
				out.write(b);
				writeEnd = System.nanoTime();
				waited = writeEnd - writeStart;
				if(waited < this.charSpacing){
					waitRemaining = this.charSpacing - waited;
					Thread.sleep(waitRemaining / 1000000, (int)(waitRemaining % 1000000));
				}
					
			}
		}catch(Exception e){
			throw new IOException(e);
		}
        out.flush();
    }

    /** {@inheritDoc} */
    public void write(byte[] data, int len) throws IOException {
		try{
		long waited = 0,writeStart,writeEnd, waitRemaining;
			for(int i=0; i< len; i++){
				writeStart = System.nanoTime();
				out.write(data[i]);
				writeEnd = System.nanoTime();
				waited = writeEnd - writeStart;
				if(waited < this.charSpacing){
					waitRemaining = this.charSpacing - waited;
					Thread.sleep(waitRemaining / 1000000, (int)(waitRemaining % 1000000));
				}
					
			}
		}catch(Exception e){
			throw new IOException(e);
		}
        out.flush();
    }
}
