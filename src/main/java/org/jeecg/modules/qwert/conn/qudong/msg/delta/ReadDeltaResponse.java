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
package org.jeecg.modules.qwert.conn.qudong.msg.delta;

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
public class ReadDeltaResponse extends ReadResponse {
	private byte[] data;
	private String msg;

    ReadDeltaResponse(int slaveId, byte[] data) throws QudongTransportException {
        super(slaveId, data);
        this.data=data;
    }

    public ReadDeltaResponse(int slaveId) throws QudongTransportException {
        super(slaveId);
    }
    public ReadDeltaResponse(int slaveId, String msg) throws QudongTransportException {
        super(slaveId);
        this.msg=msg;
    }
    /** {@inheritDoc} */
    @Override
    public byte getFunctionCode() {
        return FunctionCode.READ_DELTA_REGISTERS;
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
        byte b = queue.pop();
        if (b != QwertAsciiUtils.START)
            throw new QudongTransportException("Invalid message start: " + b);
        byte[] bur = new byte[6];
        queue.pop(bur,0,6);
        ByteQueue burQueue = new ByteQueue(3);
        burQueue.push(bur[3]);
        burQueue.push(bur[4]);
        burQueue.push(bur[5]);
        String len = fromAsciiDelta(burQueue, burQueue.size());
        int aa = Integer.parseInt(len);
        byte[] msgQueue = new byte[aa];
        queue.pop(msgQueue,0,aa);
        queue.pop(queue.size());
        queue.push(msgQueue);
        // Convert to unascii
        msg= fromAsciiDelta(queue, queue.size());

    }

    private static String readAsciiDelta(ByteQueue from) {
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

    public static String fromAsciiDelta(ByteQueue queue, int asciiLen) {
        int len = asciiLen ;
        StringBuilder sb= new StringBuilder();
        for (int i = 0; i < len; i++)
            sb.append(readAsciiDelta(queue));
        return sb.toString();
    }

    public short[] getShortData() {
      return convertToShorts(data);
  }

}
