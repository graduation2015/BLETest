package jp.ac.it_college.std.bletest;


import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

public class BLEDeviceDetailFragment extends Fragment
        implements View.OnClickListener {

    private BluetoothDevice device;
    private BluetoothGatt bluetoothGatt;
    private View contentView;
    private int mStatus;

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    bluetoothGatt = gatt;
                    discoverService();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    disconnect();
                    break;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            bluetoothGatt = gatt;
            mStatus = status;
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
/*
                String msg = characteristic.getStringValue(0);
                ((TextView) contentView.findViewById(R.id.lbl_message)).setText(msg);
*/
                byte[] bytes = characteristic.getValue();

                ((ImageView) contentView.findViewById(R.id.img_response)).setImageBitmap(decodeBytes(bytes));
            }
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }
    };

    // Gattへの接続要求
    public void connect(Context context, BluetoothDevice device) {
        bluetoothGatt = device.connectGatt(context, false, mGattCallback);
        bluetoothGatt.connect();

        contentView.findViewById(R.id.btn_read).setEnabled(true);
    }

    public void disconnect() {
        if (bluetoothGatt != null) {
            bluetoothGatt.close();
            bluetoothGatt = null;
        }
        contentView.findViewById(R.id.btn_read).setEnabled(false);
    }

    private void readCharacteristic() {
        if (mStatus == BluetoothGatt.GATT_SUCCESS && bluetoothGatt != null) {
            BluetoothGattCharacteristic characteristic = bluetoothGatt
                    .getService(UUID.fromString(Advertise.SERVICE_UUID_YOU_CAN_CHANGE))
                    .getCharacteristic(UUID.fromString(Advertise.CHAR_UUID_YOU_CAN_CHANGE));
            bluetoothGatt.readCharacteristic(characteristic);
        } else {
            Toast.makeText(getActivity(), "Read failure", Toast.LENGTH_SHORT).show();
        }

    }

    // サービス取得要求
    public void discoverService() {
        if (bluetoothGatt != null) {
            bluetoothGatt.discoverServices();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contentView = inflater.inflate(R.layout.fragment_ble_device_detail, container, false);
        contentView.findViewById(R.id.btn_connect).setOnClickListener(this);
        contentView.findViewById(R.id.btn_disconnect).setOnClickListener(this);
        contentView.findViewById(R.id.btn_read).setOnClickListener(this);
        return contentView;
    }

    public void showDetails(BluetoothDevice device) {
        contentView.setVisibility(View.VISIBLE);
        this.device = device;
        ((TextView) contentView.findViewById(R.id.lbl_device_name))
                .setText("Device name: " + device.getName());
        ((TextView) contentView.findViewById(R.id.lbl_device_address))
                .setText("Device address: " + device.getAddress());
        ((TextView) contentView.findViewById(R.id.lbl_device_type))
                .setText("Device type: " + getDeviceType(device.getType()));
        ((TextView) contentView.findViewById(R.id.lbl_message))
                .setText("Message: ");
        ((ImageView) contentView.findViewById(R.id.img_response)).setImageBitmap(null);
    }

    private String getDeviceType(int type) {
        switch (type) {
            case BluetoothDevice.DEVICE_TYPE_CLASSIC:
                return "DEVICE_TYPE_CLASSIC";
            case BluetoothDevice.DEVICE_TYPE_LE:
                return "DEVICE_TYPE_LE";
            case BluetoothDevice.DEVICE_TYPE_DUAL:
                return "DEVICE_TYPE_DUAL";
            case BluetoothDevice.DEVICE_TYPE_UNKNOWN:
                return "DEVICE_TYPE_UNKNOWN";
            default:
                return null;
        }

    }

    private void resetViews() {
        contentView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
        resetViews();
        disconnect();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_connect:
                connect(getActivity(), device);
                break;
            case R.id.btn_disconnect:
                disconnect();
                break;
            case R.id.btn_read:
                readCharacteristic();
                break;
        }
    }

    private Bitmap decodeBytes(byte[] bytes) {
        Bitmap bitmap = null;

        if (bytes != null) {
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        return bitmap;
    }
}
