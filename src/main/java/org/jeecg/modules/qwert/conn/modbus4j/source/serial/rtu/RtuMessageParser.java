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

import org.jeecg.modules.qwert.conn.modbus4j.source.base.BaseMessageParser;
import org.jeecg.modules.qwert.conn.modbus4j.source.sero.messaging.IncomingMessage;
import org.jeecg.modules.qwert.conn.modbus4j.source.sero.util.queue.ByteQueue;

/**
 * Message parser implementation for RTU encoding. Primary reference for the ordering of CRC bytes. Also provides
 * handling of incomplete messages.
 *
 * @author mlohbihler
 * @version 5.0.0
 */
public class RtuMessageParser extends BaseMessageParser {
    /**
     * <p>Constructor for RtuMessageParser.</p>
     *
     * @param master a boolean.
     */
    public RtuMessageParser(boolean master) {
        super(master);
    }

    /** {@inheritDoc} */
    @Override
    protected IncomingMessage parseMessageImpl(ByteQueue queue) throws Exception {
        if (master)
            return RtuMessageResponse.createRtuMessageResponse(queue);
        return RtuMessageRequest.createRtuMessageRequest(queue);
    }
    //
    // public static void main(String[] args) throws Exception {
    // ByteQueue queue = new ByteQueue(new byte[] { 5, 3, 2, 0, (byte) 0xdc, (byte) 0x48, (byte) 0x1d, 0 });
    // RtuMessageParser p = new RtuMessageParser(false);
    // System.out.println(p.parseResponse(queue));
    // }
}
