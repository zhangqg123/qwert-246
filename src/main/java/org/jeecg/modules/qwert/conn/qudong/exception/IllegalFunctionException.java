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
package org.jeecg.modules.qwert.conn.qudong.exception;

/**
 * <p>IllegalFunctionException class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public class IllegalFunctionException extends QudongTransportException {
    private static final long serialVersionUID = -1;

    private final byte functionCode;

    /**
     * <p>Constructor for IllegalFunctionException.</p>
     *
     * @param functionCode a byte.
     * @param slaveId a int.
     */
    public IllegalFunctionException(byte functionCode, int slaveId) {
        super("Function code: 0x" + Integer.toHexString(functionCode & 0xff), slaveId);
        this.functionCode = functionCode;
    }

    /**
     * <p>Getter for the field <code>functionCode</code>.</p>
     *
     * @return a byte.
     */
    public byte getFunctionCode() {
        return functionCode;
    }
}
