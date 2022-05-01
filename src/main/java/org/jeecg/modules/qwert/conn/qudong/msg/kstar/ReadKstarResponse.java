/*
 * ============================================================================
 * GNU General Public License
 * ============================================================================
 *
 * Copyright (C) 2006-2011 Serotonin Software Technologies Inc. http://serotoninsoftware.com
 * @author Matthew Lohbihler
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jeecg.modules.qwert.conn.qudong.msg.kstar;

import lombok.SneakyThrows;
import org.jeecg.modules.qwert.conn.qudong.base.QwertAsciiUtils;
import org.jeecg.modules.qwert.conn.qudong.base.QwertUtils;
import org.jeecg.modules.qwert.conn.qudong.code.FunctionCode;
import org.jeecg.modules.qwert.conn.qudong.exception.QudongTransportException;
import org.jeecg.modules.qwert.conn.qudong.msg.ReadResponse;
import org.jeecg.modules.qwert.conn.qudong.sero.util.queue.ByteQueue;

/**
 * <p>ReadDianzongResponse class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public class ReadKstarResponse extends ReadResponse {
	private byte[] data;
	private String msg;

    ReadKstarResponse(int slaveId, byte[] data) throws QudongTransportException {
        super(slaveId, data);
        this.data=data;
    }

    public ReadKstarResponse(int slaveId) throws QudongTransportException {
        super(slaveId);
    }
    public ReadKstarResponse(int slaveId, String msg) throws QudongTransportException {
        super(slaveId);
        this.msg=msg;
    }
    /** {@inheritDoc} */
    @Override
    public byte getFunctionCode() {
        return FunctionCode.READ_KSTAR_REGISTERS;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "ReadDianzongResponse [exceptionCode=" + exceptionCode + ", slaveId=" + slaveId
                + ", getFunctionCode()=" + getFunctionCode() + ", isException()=" + isException()
                + ", getExceptionMessage()=" + getExceptionMessage() + ", getExceptionCode()=" + getExceptionCode()
                + ", toString()=" + super.toString(true) + "]";
    }

	
	public String getMessage() {
		// TODO Auto-generated method stub
		return msg;
	}

	
    @Override
    final protected void writeImpl(ByteQueue queue) {
    	if(simulator==0) {
		}else {
            writeResponse(queue);
		}
    }
	
	public static String chkLength(int value){
		byte a1 = (byte) (value & 0xf);
		byte a2 = (byte) ((value>>4) & 0xf);
		byte a3 = (byte) ((value>>8) & 0xf);
		int sum = a1+a2+a3;
		sum=((~sum%0x10000+1)& 0xf)<<12 | (value&0xffff);
		return Integer.toHexString(sum);
	}

    /** {@inheritDoc} */
    @SneakyThrows
    @Override
    protected void readResponse(ByteQueue queue) {
        // Validate that the message starts with the required indicator
        byte b = queue.pop();
        if (b != QwertAsciiUtils.KSTAR_RETURN_START)
            throw new QudongTransportException("Invalid message start: " + b);

        // Find the end indicator
        int end = queue.indexOf(QwertAsciiUtils.END);
        if (end == -1)
            throw new ArrayIndexOutOfBoundsException();

        // Remove the message from the queue, leaving the LRC there
        byte[] asciiBytes = new byte[end];
        queue.pop(asciiBytes);
        ByteQueue msgQueue = new ByteQueue(asciiBytes);

        // Pop the end indicator off of the queue
        queue.pop(QwertAsciiUtils.END.length);
        // Convert to unascii
        msg=fromAsciiKstar(msgQueue, msgQueue.size());
    }

    private static String readAsciiKstar(ByteQueue from) {
//        int a1 = (lookupUnascii[from.pop()] << 4);
        byte a1 = from.pop();
        if(a1==46){
            return ".";
        }
        if(a1==32){
            return " ";
        }
        if(a1==59){
            return ";";
        }
        byte a2 = QwertAsciiUtils.lookupUnascii[a1];
        String a3 = "" + a2;
        return a3;
    }

    public static String fromAsciiKstar(ByteQueue queue, int asciiLen) {
        int len = asciiLen ;
        StringBuilder sb= new StringBuilder();
        for (int i = 0; i < len; i++)
            sb.append(readAsciiKstar(queue));
        return sb.toString();
    }

    public short[] getShortData() {
      return convertToShorts(data);
  }

}
