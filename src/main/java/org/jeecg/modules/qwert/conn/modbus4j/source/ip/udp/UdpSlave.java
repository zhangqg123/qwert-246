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
package org.jeecg.modules.qwert.conn.modbus4j.source.ip.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jeecg.modules.qwert.conn.modbus4j.source.ModbusSlaveSet;
import org.jeecg.modules.qwert.conn.modbus4j.source.base.BaseMessageParser;
import org.jeecg.modules.qwert.conn.modbus4j.source.base.BaseRequestHandler;
import org.jeecg.modules.qwert.conn.modbus4j.source.base.ModbusUtils;
import org.jeecg.modules.qwert.conn.modbus4j.source.exception.ModbusInitException;
import org.jeecg.modules.qwert.conn.modbus4j.source.ip.encap.EncapMessageParser;
import org.jeecg.modules.qwert.conn.modbus4j.source.ip.encap.EncapRequestHandler;
import org.jeecg.modules.qwert.conn.modbus4j.source.ip.xa.XaRequestHandler;
import org.jeecg.modules.qwert.conn.modbus4j.source.ip.xa.XaMessageParser;
import org.jeecg.modules.qwert.conn.modbus4j.source.sero.messaging.IncomingMessage;
import org.jeecg.modules.qwert.conn.modbus4j.source.sero.messaging.IncomingRequestMessage;
import org.jeecg.modules.qwert.conn.modbus4j.source.sero.messaging.OutgoingResponseMessage;
import org.jeecg.modules.qwert.conn.modbus4j.source.sero.util.queue.ByteQueue;

/**
 * <p>UdpSlave class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public class UdpSlave extends ModbusSlaveSet {
    // Configuration fields
    private final int port;

    // Runtime fields.
    DatagramSocket datagramSocket;
    private final ExecutorService executorService;
    final BaseMessageParser messageParser;
    final BaseRequestHandler requestHandler;

    /**
     * <p>Constructor for UdpSlave.</p>
     *
     * @param encapsulated a boolean.
     */
    public UdpSlave(boolean encapsulated) {
        this(ModbusUtils.TCP_PORT, encapsulated);
    }

    /**
     * <p>Constructor for UdpSlave.</p>
     *
     * @param port a int.
     * @param encapsulated a boolean.
     */
    public UdpSlave(int port, boolean encapsulated) {
        this.port = port;

        if (encapsulated) {
            messageParser = new EncapMessageParser(false);
            requestHandler = new EncapRequestHandler(this);
        }
        else {
            messageParser = new XaMessageParser(false);
            requestHandler = new XaRequestHandler(this);
        }

        executorService = Executors.newCachedThreadPool();
    }

    /** {@inheritDoc} */
    @Override
    public void start() throws ModbusInitException {
        try {
            datagramSocket = new DatagramSocket(port);

            DatagramPacket datagramPacket;
            while (true) {
                datagramPacket = new DatagramPacket(new byte[1028], 1028);
                datagramSocket.receive(datagramPacket);

                UdpConnectionHandler handler = new UdpConnectionHandler(datagramPacket);
                executorService.execute(handler);
            }
        }
        catch (IOException e) {
            throw new ModbusInitException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void stop() {
        // Close the socket first to prevent new messages.
        datagramSocket.close();

        // Close the executor service.
        executorService.shutdown();
        try {
            executorService.awaitTermination(3, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            getExceptionHandler().receivedException(e);
        }
    }

    // int getSlaveId() {
    // return slaveId;
    // }
    //
    // ProcessImage getProcessImage() {
    // return processImage;
    // }

    class UdpConnectionHandler implements Runnable {
        private final DatagramPacket requestPacket;

        UdpConnectionHandler(DatagramPacket requestPacket) {
            this.requestPacket = requestPacket;
        }

        public void run() {
            try {
                ByteQueue requestQueue = new ByteQueue(requestPacket.getData(), 0, requestPacket.getLength());

                // Parse the request data and get the response.
                IncomingMessage request = messageParser.parseMessage(requestQueue);
                OutgoingResponseMessage response = requestHandler.handleRequest((IncomingRequestMessage) request);

                if (response == null)
                    return;

                // Create a response packet.
                byte[] responseData = response.getMessageData();
                DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length,
                        requestPacket.getAddress(), requestPacket.getPort());

                // Send the response back.
                datagramSocket.send(responsePacket);
            }
            catch (Exception e) {
                getExceptionHandler().receivedException(e);
            }
        }
    }
}
