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

import org.jeecg.modules.qwert.conn.modbus4j.source.Modbus;
import org.jeecg.modules.qwert.conn.modbus4j.source.ProcessImage;
import org.jeecg.modules.qwert.conn.modbus4j.source.base.ModbusUtils;
import org.jeecg.modules.qwert.conn.modbus4j.source.code.FunctionCode;
import org.jeecg.modules.qwert.conn.modbus4j.source.exception.ModbusIdException;
import org.jeecg.modules.qwert.conn.modbus4j.source.exception.ModbusTransportException;
import org.jeecg.modules.qwert.conn.modbus4j.source.sero.util.queue.ByteQueue;

/**
 * <p>WriteMaskRegisterRequest class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public class WriteMaskRegisterRequest extends ModbusRequest {
    private int writeOffset;

    /**
     * The andMask determines which bits we want to change. If a bit in the andMask is 1, it indicates that the value
     * should not be changed. If it is zero, it should be changed according to the orMask value for that bit.
     */
    private int andMask;

    /**
     * The orMask determines what value a bit will have after writing if the andMask allows that bit to be changed. If a
     * changable bit in the orMask is 0, the bit in the result will be zero. Ditto for 1.
     */
    private int orMask;

    /**
     * Constructor that defaults the masks to have no effect on the register. Use the setBit function to modify mask
     * values.
     *
     * @param slaveId a int.
     * @param writeOffset a int.
     * @throws ModbusTransportException when necessary
     */
    public WriteMaskRegisterRequest(int slaveId, int writeOffset) throws ModbusTransportException {
        this(slaveId, writeOffset, 0xffff, 0);
    }

    /**
     * <p>Constructor for WriteMaskRegisterRequest.</p>
     *
     * @param slaveId a int.
     * @param writeOffset a int.
     * @param andMask a int.
     * @param orMask a int.
     * @throws ModbusTransportException if any.
     */
    public WriteMaskRegisterRequest(int slaveId, int writeOffset, int andMask, int orMask)
            throws ModbusTransportException {
        super(slaveId);
        this.writeOffset = writeOffset;
        this.andMask = andMask;
        this.orMask = orMask;
    }

    /** {@inheritDoc} */
    @Override
    public void validate(Modbus modbus) throws ModbusTransportException {
        ModbusUtils.validateOffset(writeOffset);
    }

    /**
     * <p>setBit.</p>
     *
     * @param bit a int.
     * @param value a boolean.
     */
    public void setBit(int bit, boolean value) {
        if (bit < 0 || bit > 15)
            throw new ModbusIdException("Bit must be between 0 and 15 inclusive");

        // Set the bit in the andMask to 0 to allow writing.
        andMask = andMask & ~(1 << bit);

        // Set the bit in the orMask to write the correct value.
        if (value)
            orMask = orMask | 1 << bit;
        else
            orMask = orMask & ~(1 << bit);
    }

    WriteMaskRegisterRequest(int slaveId) throws ModbusTransportException {
        super(slaveId);
    }

    /** {@inheritDoc} */
    @Override
    protected void writeRequest(ByteQueue queue) {
        ModbusUtils.pushShort(queue, writeOffset);
        ModbusUtils.pushShort(queue, andMask);
        ModbusUtils.pushShort(queue, orMask);
    }

    @Override
    ModbusResponse handleImpl(ProcessImage processImage) throws ModbusTransportException {
        short value = processImage.getHoldingRegister(writeOffset);
        value = (short) ((value & andMask) | (orMask & (~andMask)));
        processImage.writeHoldingRegister(writeOffset, value);
        return new WriteMaskRegisterResponse(slaveId, writeOffset, andMask, orMask);
    }

    /** {@inheritDoc} */
    @Override
    public byte getFunctionCode() {
        return FunctionCode.WRITE_MASK_REGISTER;
    }

    @Override
    ModbusResponse getResponseInstance(int slaveId) throws ModbusTransportException {
        return new WriteMaskRegisterResponse(slaveId);
    }

    /** {@inheritDoc} */
    @Override
    protected void readRequest(ByteQueue queue) {
        writeOffset = ModbusUtils.popUnsignedShort(queue);
        andMask = ModbusUtils.popUnsignedShort(queue);
        orMask = ModbusUtils.popUnsignedShort(queue);
    }
}
