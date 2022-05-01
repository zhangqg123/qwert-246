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
package org.jeecg.modules.qwert.conn.qudong.serial.rtu;

import org.jeecg.modules.qwert.conn.qudong.base.BaseMessageParser;
import org.jeecg.modules.qwert.conn.qudong.msg.QwertMessage;
import org.jeecg.modules.qwert.conn.qudong.sero.messaging.IncomingMessage;
import org.jeecg.modules.qwert.conn.qudong.sero.util.queue.ByteQueue;

/**
 * <p>RtuMessageParser class.</p>
 *
 * @author Matthew Lohbihler
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
    protected IncomingMessage parseMessageImpl(ByteQueue queue, QwertMessage qr) throws Exception {
        if (master)
            return RtuMessageResponse.createDianzongMessageResponse(queue,qr);
        return RtuMessageRequest.createDianzongMessageRequest(queue);
    }
}
