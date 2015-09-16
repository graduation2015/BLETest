package jp.ac.it_college.std.bletest;


import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class BLEDeviceDetailFragment extends Fragment
        implements View.OnClickListener {

    private BluetoothDevice device;
    private BluetoothGatt bluetoothGatt;
    private List<BluetoothGattService> serviceList = new ArrayList<>();
    private View contentView;

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
                    bluetoothGatt.close();
                    bluetoothGatt = null;
                    break;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
        }
    };

    // Gattへの接続要求
    public void connect(Context context, BluetoothDevice device) {
        bluetoothGatt = device.connectGatt(context, false, mGattCallback);
        bluetoothGatt.connect();
        contentView.findViewById(R.id.btn_detail).setEnabled(true);
    }

    public void disconnect() {
        bluetoothGatt.disconnect();
        contentView.findViewById(R.id.btn_detail).setEnabled(false);
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
        contentView.findViewById(R.id.btn_detail).setOnClickListener(this);
        return contentView;
    }

    public void showFragments(BluetoothDevice device) {
        contentView.setVisibility(View.VISIBLE);
        this.device = device;
        showDetails(device);
    }

    public void showDetails(BluetoothDevice device) {
        ((TextView) contentView.findViewById(R.id.lbl_device_name))
                .setText(device.getName());
        ((TextView) contentView.findViewById(R.id.lbl_device_address))
                .setText(device.getAddress());
        ((TextView) contentView.findViewById(R.id.lbl_device_info))
                .setText(device.toString());
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
            case R.id.btn_detail:
                showDetails(bluetoothGatt.getDevice());
                break;
        }
    }
}