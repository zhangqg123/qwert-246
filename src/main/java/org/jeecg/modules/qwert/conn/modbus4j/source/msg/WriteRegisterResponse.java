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
package org.jeecg.modules.qwert.conn.modbus4j.source.msg;

import org.jeecg.modules.qwert.conn.modbus4j.source.base.ModbusUtils;
import org.jeecg.modules.qwert.conn.modbus4j.source.code.FunctionCode;
import org.jeecg.modules.qwert.conn.modbus4j.source.exception.ModbusTransportException;
import org.jeecg.modules.qwert.conn.modbus4j.source.sero.util.queue.ByteQueue;

/**
 * <p>WriteRegisterResponse class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public class WriteRegisterResponse extends ModbusResponse {
    private int writeOffset;
    private int writeValue;

    /** {@inheritDoc} */
    @Override
    public byte getFunctionCode() {
        return FunctionCode.WRITE_REGISTER;
    }

    WriteRegisterResponse(int slaveId) throws ModbusTransportException {
        super(slaveId);
    }

    WriteRegisterResponse(int slaveId, int writeOffset, int writeValue) throws ModbusTransportException {
        super(slaveId);
        this.writeOffset = writeOffset;
        this.writeValue = writeValue;
    }

    /** {@inheritDoc} */
    @Override
    protected void writeResponse(ByteQueue queue) {
        ModbusUtils.pushShort(queue, writeOffset);
        ModbusUtils.pushShort(queue, writeValue);
    }

    /** {@inheritDoc} */
    @Override
    protected void readResponse(ByteQueue queue) {
        writeOffset = ModbusUtils.popUnsignedShort(queue);
        writeValue = ModbusUtils.popUnsignedShort(queue);
    }

    /**
     * <p>Getter for the field <code>writeOffset</code>.</p>
     *
     * @return a int.
     */
    public int getWriteOffset() {
        return writeOffset;
    }

    /**
     * <p>Getter for the field <code>writeValue</code>.</p>
     *
     * @return a int.
     */
    public int getWriteValue() {
        return writeValue;
    }
}
