package jp.ac.it_college.std.bletest;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BLEServer extends BluetoothGattServerCallback {

    //BLE
    private BluetoothGattServer bluetoothGattServer;
    public BLEServer(BluetoothGattServer gattServer) {
        this.bluetoothGattServer = gattServer;
    }

    //セントラル（クライアント）からReadRequestが来ると呼ばれる
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void onCharacteristicReadRequest(android.bluetooth.BluetoothDevice device, int requestId,
                                            int offset, BluetoothGattCharacteristic characteristic) {

        //セントラルに任意の文字を返信する
        characteristic.setValue("something you want to send");
        bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset,
                characteristic.getValue());

    }

    //セントラル（クライアント）からWriteRequestが来ると呼ばれる
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void onCharacteristicWriteRequest(android.bluetooth.BluetoothDevice device, int requestId,
                                             BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded,
                                             int offset, byte[] value) {

        //セントラルにnullを返信する
        bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, null);
    }
}