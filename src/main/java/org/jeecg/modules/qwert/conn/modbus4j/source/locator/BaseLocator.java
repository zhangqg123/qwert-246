package org.jeecg.modules.qwert.conn.modbus4j.source.locator;

import java.nio.charset.Charset;

import org.jeecg.modules.qwert.conn.modbus4j.source.code.DataType;
import org.jeecg.modules.qwert.conn.modbus4j.source.code.RegisterRange;
import org.jeecg.modules.qwert.conn.modbus4j.source.exception.ModbusIdException;
import org.jeecg.modules.qwert.conn.modbus4j.source.exception.ModbusTransportException;
import org.jeecg.modules.qwert.conn.modbus4j.source.base.ModbusUtils;
import org.jeecg.modules.qwert.conn.modbus4j.source.base.RangeAndOffset;

/**
 * <p>Abstract BaseLocator class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
abstract public class BaseLocator<T> {
    //
    //
    // Factory methods
    //
    /**
     * <p>coilStatus.</p>
     *
     * @param slaveId a int.
     * @param offset a int.
     * @return a {@link BaseLocator} object.
     */
    public static BaseLocator<Boolean> coilStatus(int slaveId, int offset) {
        return new BinaryLocator(slaveId, RegisterRange.COIL_STATUS, offset);
    }

    /**
     * <p>inputStatus.</p>
     *
     * @param slaveId a int.
     * @param offset a int.
     * @return a {@link BaseLocator} object.
     */
    public static BaseLocator<Boolean> inputStatus(int slaveId, int offset) {
        return new BinaryLocator(slaveId, RegisterRange.INPUT_STATUS, offset);
    }

    /**
     * <p>inputRegister.</p>
     *
     * @param slaveId a int.
     * @param offset a int.
     * @param dataType a int.
     * @return a {@link BaseLocator} object.
     */
    public static BaseLocator<Number> inputRegister(int slaveId, int offset, int dataType) {
        return new NumericLocator(slaveId, RegisterRange.INPUT_REGISTER, offset, dataType);
    }

    /**
     * <p>inputRegisterBit.</p>
     *
     * @param slaveId a int.
     * @param offset a int.
     * @param bit a int.
     * @return a {@link BaseLocator} object.
     */
    public static BaseLocator<Boolean> inputRegisterBit(int slaveId, int offset, int bit) {
        return new BinaryLocator(slaveId, RegisterRange.INPUT_REGISTER, offset, bit);
    }

    /**
     * <p>holdingRegister.</p>
     *
     * @param slaveId a int.
     * @param offset a int.
     * @param dataType a int.
     * @return a {@link BaseLocator} object.
     */
    public static BaseLocator<Number> holdingRegister(int slaveId, int offset, int dataType) {
        return new NumericLocator(slaveId, RegisterRange.HOLDING_REGISTER, offset, dataType);
    }

    /**
     * <p>holdingRegisterBit.</p>
     *
     * @param slaveId a int.
     * @param offset a int.
     * @param bit a int.
     * @return a {@link BaseLocator} object.
     */
    public static BaseLocator<Boolean> holdingRegisterBit(int slaveId, int offset, int bit) {
        return new BinaryLocator(slaveId, RegisterRange.HOLDING_REGISTER, offset, bit);
    }

    /**
     * <p>createLocator.</p>
     *
     * @param slaveId a int.
     * @param registerId a int.
     * @param dataType a int.
     * @param bit a int.
     * @param registerCount a int.
     * @return a {@link BaseLocator} object.
     */
    public static BaseLocator<?> createLocator(int slaveId, int registerId, int dataType, int bit, int registerCount) {
        RangeAndOffset rao = new RangeAndOffset(registerId);
        return createLocator(slaveId, rao.getRange(), rao.getOffset(), dataType, bit, registerCount,
                StringLocator.ASCII);
    }

    /**
     * <p>createLocator.</p>
     *
     * @param slaveId a int.
     * @param registerId a int.
     * @param dataType a int.
     * @param bit a int.
     * @param registerCount a int.
     * @param charset a {@link java.nio.charset.Charset} object.
     * @return a {@link BaseLocator} object.
     */
    public static BaseLocator<?> createLocator(int slaveId, int registerId, int dataType, int bit, int registerCount,
            Charset charset) {
        RangeAndOffset rao = new RangeAndOffset(registerId);
        return createLocator(slaveId, rao.getRange(), rao.getOffset(), dataType, bit, registerCount, charset);
    }

    /**
     * <p>createLocator.</p>
     *
     * @param slaveId a int.
     * @param range a int.
     * @param offset a int.
     * @param dataType a int.
     * @param bit a int.
     * @param registerCount a int.
     * @return a {@link BaseLocator} object.
     */
    public static BaseLocator<?> createLocator(int slaveId, int range, int offset, int dataType, int bit,
            int registerCount) {
        return createLocator(slaveId, range, offset, dataType, bit, registerCount, StringLocator.ASCII);
    }

    /**
     * <p>createLocator.</p>
     *
     * @param slaveId a int.
     * @param range a int.
     * @param offset a int.
     * @param dataType a int.
     * @param bit a int.
     * @param registerCount a int.
     * @param charset a {@link java.nio.charset.Charset} object.
     * @return a {@link BaseLocator} object.
     */
    public static BaseLocator<?> createLocator(int slaveId, int range, int offset, int dataType, int bit,
            int registerCount, Charset charset) {
        if (dataType == DataType.BINARY) {
            if (BinaryLocator.isBinaryRange(range))
                return new BinaryLocator(slaveId, range, offset);
            return new BinaryLocator(slaveId, range, offset, bit);
        }
        if (dataType == DataType.CHAR || dataType == DataType.VARCHAR)
            return new StringLocator(slaveId, range, offset, dataType, registerCount, charset);
        return new NumericLocator(slaveId, range, offset, dataType);
    }

    private final int slaveId;
    protected final int range;
    protected final int offset;

    /**
     * <p>Constructor for BaseLocator.</p>
     *
     * @param slaveId a int.
     * @param range a int.
     * @param offset a int.
     */
    public BaseLocator(int slaveId, int range, int offset) {
        this.slaveId = slaveId;
        this.range = range;
        this.offset = offset;
    }

    /**
     * <p>validate.</p>
     *
     * @param registerCount a int.
     */
    protected void validate(int registerCount) {
        try {
            ModbusUtils.validateOffset(offset);
            ModbusUtils.validateEndOffset(offset + registerCount - 1);
        }
        catch (ModbusTransportException e) {
            throw new ModbusIdException(e);
        }
    }

    /**
     * <p>getDataType.</p>
     *
     * @return a int.
     */
    abstract public int getDataType();

    /**
     * <p>getRegisterCount.</p>
     *
     * @return a int.
     */
    abstract public int getRegisterCount();

    /**
     * <p>Getter for the field <code>slaveId</code>.</p>
     *
     * @return a int.
     */
    public int getSlaveId() {
        return slaveId;
    }

    /**
     * <p>Getter for the field <code>range</code>.</p>
     *
     * @return a int.
     */
    public int getRange() {
        return range;
    }

    /**
     * <p>Getter for the field <code>offset</code>.</p>
     *
     * @return a int.
     */
    public int getOffset() {
        return offset;
    }

    //    public SlaveAndRange getSlaveAndRange() {
    //        return slaveAndRange;
    //    }

    /**
     * <p>getEndOffset.</p>
     *
     * @return a int.
     */
    public int getEndOffset() {
        return offset + getRegisterCount() - 1;
    }

    /**
     * <p>bytesToValue.</p>
     *
     * @param data an array of {@link byte} objects.
     * @param requestOffset a int.
     * @return a T object.
     */
    public T bytesToValue(byte[] data, int requestOffset) {
        // Determined the offset normalized to the response data.
        return bytesToValueRealOffset(data, offset - requestOffset);
    }

    /**
     * <p>bytesToValueRealOffset.</p>
     *
     * @param data an array of {@link byte} objects.
     * @param offset a int.
     * @return a T object.
     */
    abstract public T bytesToValueRealOffset(byte[] data, int offset);

    /**
     * <p>valueToShorts.</p>
     *
     * @param value a T object.
     * @return an array of {@link short} objects.
     */
    abstract public short[] valueToShorts(T value);
}
