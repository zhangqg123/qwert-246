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
package org.jeecg.modules.qwert.conn.modbus4j.source.serial.rtu;

import org.jeecg.modules.qwert.conn.modbus4j.source.base.ModbusUtils;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.ModbusMessage;
import org.jeecg.modules.qwert.conn.modbus4j.source.serial.SerialMessage;
import org.jeecg.modules.qwert.conn.modbus4j.source.sero.util.queue.ByteQueue;

/**
 * Convenience superclass primarily for calculating CRC values.
 *
 * @author mlohbihler
 * @version 5.0.0
 */
public class RtuMessage extends SerialMessage {
    /**
     * <p>Constructor for RtuMessage.</p>
     *
     * @param modbusMessage a {@link ModbusMessage} object.
     */
    public RtuMessage(ModbusMessage modbusMessage) {
        super(modbusMessage);
    }

    /**
     * <p>getMessageData.</p>
     *
     * @return an array of {@link byte} objects.
     */
    public byte[] getMessageData() {
        ByteQueue queue = new ByteQueue();

        // Write the particular message.
        modbusMessage.write(queue);

        // Write the CRC
        ModbusUtils.pushShort(queue, ModbusUtils.calculateCRC(modbusMessage));

        // Return the data.
        return queue.popAll();
    }
}