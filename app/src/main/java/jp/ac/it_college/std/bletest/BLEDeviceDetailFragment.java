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
    private static final String TAG = "BLEDeviceDetailFragmentClass";

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
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                handleCharacteristic(characteristic);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                handleCharacteristic(characteristic);
            }
        }
    };

    // Gattへの接続要求
    public void connect(Context context, BluetoothDevice device) {
        bluetoothGatt = device.connectGatt(context, false, mGattCallback);
        bluetoothGatt.connect();

        contentView.findViewById(R.id.btn_read).setEnabled(true);
        contentView.findViewById(R.id.btn_write).setEnabled(true);
    }

    public void disconnect() {
        if (bluetoothGatt != null) {
            bluetoothGatt.close();
            bluetoothGatt = null;
        }
        contentView.findViewById(R.id.btn_read).setEnabled(false);
        contentView.findViewById(R.id.btn_write).setEnabled(false);
    }

    private void readCharacteristic() {
        if (mStatus == BluetoothGatt.GATT_SUCCESS && bluetoothGatt != null) {
            bluetoothGatt.readCharacteristic(getCharacteristic());
        } else {
            Toast.makeText(getActivity(), "Read failure", Toast.LENGTH_SHORT).show();
        }

    }

    private void writeCharacteristic() {
        if (mStatus == BluetoothGatt.GATT_SUCCESS && bluetoothGatt != null) {
            getCharacteristic().setValue(Advertise.SERVER_MESSAGE_WRITE.getBytes());
            bluetoothGatt.writeCharacteristic(getCharacteristic());
        } else {
            Toast.makeText(getActivity(), "Write failure", Toast.LENGTH_SHORT).show();
        }
    }

    private BluetoothGattCharacteristic getCharacteristic() {
        return bluetoothGatt
                .getService(UUID.fromString(Advertise.SERVICE_UUID_YOU_CAN_CHANGE))
                .getCharacteristic(UUID.fromString(Advertise.CHAR_UUID_YOU_CAN_CHANGE));
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
        contentView.findViewById(R.id.btn_write).setOnClickListener(this);
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
            case R.id.btn_write:
                writeCharacteristic();
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

    private void handleCharacteristic(BluetoothGattCharacteristic characteristic) {
        byte[] bytes = characteristic.getValue();
        String msg = new String(bytes);
        ((TextView) contentView.findViewById(R.id.lbl_message)).setText(msg);
//        ((ImageView) contentView.findViewById(R.id.img_response)).setImageBitmap(decodeBytes(bytes));
    }
}
