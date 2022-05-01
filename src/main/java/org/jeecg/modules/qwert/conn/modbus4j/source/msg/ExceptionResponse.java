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

import org.jeecg.modules.qwert.conn.modbus4j.source.exception.ModbusTransportException;
import org.jeecg.modules.qwert.conn.modbus4j.source.sero.util.queue.ByteQueue;

/**
 * <p>ExceptionResponse class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public class ExceptionResponse extends ModbusResponse {
    private final byte functionCode;

    /**
     * <p>Constructor for ExceptionResponse.</p>
     *
     * @param slaveId a int.
     * @param functionCode a byte.
     * @param exceptionCode a byte.
     * @throws ModbusTransportException if any.
     */
    public ExceptionResponse(int slaveId, byte functionCode, byte exceptionCode) throws ModbusTransportException {
        super(slaveId);
        this.functionCode = functionCode;
        setException(exceptionCode);
    }

    /** {@inheritDoc} */
    @Override
    public byte getFunctionCode() {
        return functionCode;
    }

    /** {@inheritDoc} */
    @Override
    protected void readResponse(ByteQueue queue) {
        // no op
    }

    /** {@inheritDoc} */
    @Override
    protected void writeResponse(ByteQueue queue) {
        // no op
    }
}
