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

import org.jeecg.modules.qwert.conn.modbus4j.source.base.ModbusUtils;

/**
 * <p>IpParameters class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public class IpParameters {
    private String host;
    private int port = ModbusUtils.TCP_PORT;
    private boolean encapsulated;

    /**
     * <p>Getter for the field <code>host</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getHost() {
        return host;
    }

    /**
     * <p>Setter for the field <code>host</code>.</p>
     *
     * @param host a {@link java.lang.String} object.
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * <p>Getter for the field <code>port</code>.</p>
     *
     * @return a int.
     */
    public int getPort() {
        return port;
    }

    /**
     * <p>Setter for the field <code>port</code>.</p>
     *
     * @param port a int.
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * <p>isEncapsulated.</p>
     *
     * @return a boolean.
     */
    public boolean isEncapsulated() {
        return encapsulated;
    }

    /**
     * <p>Setter for the field <code>encapsulated</code>.</p>
     *
     * @param encapsulated a boolean.
     */
    public void setEncapsulated(boolean encapsulated) {
        this.encapsulated = encapsulated;
    }
}
