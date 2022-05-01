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
package org.jeecg.modules.qwert.conn.modbus4j.source.ip;

import org.jeecg.modules.qwert.conn.modbus4j.source.msg.ModbusResponse;
import org.jeecg.modules.qwert.conn.modbus4j.source.sero.messaging.IncomingResponseMessage;
import org.jeecg.modules.qwert.conn.modbus4j.source.sero.messaging.OutgoingResponseMessage;

/**
 * <p>IpMessageResponse interface.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public interface IpMessageResponse extends OutgoingResponseMessage, IncomingResponseMessage {
    /**
     * <p>getModbusResponse.</p>
     *
     * @return a {@link ModbusResponse} object.
     */
    ModbusResponse getModbusResponse();
}
