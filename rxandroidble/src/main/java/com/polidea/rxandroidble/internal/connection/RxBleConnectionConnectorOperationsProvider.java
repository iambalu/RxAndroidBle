package com.polidea.rxandroidble.internal.connection;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import com.polidea.rxandroidble.internal.operations.RxBleRadioOperationConnect;
import com.polidea.rxandroidble.internal.operations.RxBleRadioOperationDisconnect;
import com.polidea.rxandroidble.internal.util.BleConnectionCompat;

import java.util.concurrent.atomic.AtomicReference;

public class RxBleConnectionConnectorOperationsProvider {

    public static class RxBleOperations {

        public final RxBleRadioOperationConnect connect;
        public final RxBleRadioOperationDisconnect disconnect;

        public RxBleOperations(RxBleRadioOperationConnect connect, RxBleRadioOperationDisconnect disconnect) {
            this.connect = connect;
            this.disconnect = disconnect;
        }
    }

    public RxBleOperations provide(Context context,
                                   BluetoothDevice bluetoothDevice,
                                   boolean autoConnect,
                                   BleConnectionCompat connectionCompat,
                                   RxBleGattCallback gattCallback) {
        AtomicReference<BluetoothGatt> bluetoothGattAtomicReference = new AtomicReference<>();
        RxBleRadioOperationConnect operationConnect = new RxBleRadioOperationConnect(bluetoothDevice,
                gattCallback,
                connectionCompat,
                autoConnect);
        final RxBleRadioOperationDisconnect operationDisconnect = new RxBleRadioOperationDisconnect(
                gattCallback,
                bluetoothGattAtomicReference,
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE)
        );
        // getBluetoothGatt completed when the connection is unsubscribed
        operationConnect.getBluetoothGatt().subscribe(bluetoothGattAtomicReference::set, ignored -> {
        });
        return new RxBleOperations(operationConnect, operationDisconnect);
    }
}
