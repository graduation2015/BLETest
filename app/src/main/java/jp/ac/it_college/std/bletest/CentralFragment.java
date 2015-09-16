package jp.ac.it_college.std.bletest;


import android.app.ListFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class CentralFragment extends ListFragment implements View.OnClickListener {

    private BLEScanner bleScanner;
    private List<BluetoothDevice> deviceList = new ArrayList<>();
    private View contentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contentView = inflater.inflate(R.layout.fragment_central, container, false);
        contentView.findViewById(R.id.btn_ble_scan_start).setOnClickListener(this);
        contentView.findViewById(R.id.btn_ble_scan_stop).setOnClickListener(this);

        setListAdapter(new BLEDeviceListAdapter(getActivity(), R.layout.row_devices, deviceList));
        bleScanner = new BLEScanner(getActivity(), this);
        return contentView;
    }

    // スキャンしたデバイスのリスト保存
    public void saveDevice(BluetoothDevice device) {
        if (deviceList == null) {
            deviceList = new ArrayList<>();
        }

        deviceList.add(device);
        ((BLEDeviceListAdapter) getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        deviceList.clear();
        ((BLEDeviceListAdapter) getListAdapter()).notifyDataSetChanged();
        stopScan();
    }

    private void startScan() {
        deviceList.clear();
        ((BLEDeviceListAdapter) getListAdapter()).notifyDataSetChanged();

        List<ScanFilter> filters = new ArrayList<>();

        ScanSettings.Builder settingsBuilder = new ScanSettings.Builder();
        settingsBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);

        ScanSettings settings = settingsBuilder.build();

        bleScanner.scan(filters, settings);

        contentView.findViewById(R.id.btn_ble_scan_start).setEnabled(false);
        contentView.findViewById(R.id.btn_ble_scan_stop).setEnabled(true);
    }

    private void stopScan() {
        bleScanner.stopScan();
        contentView.findViewById(R.id.btn_ble_scan_start).setEnabled(true);
        contentView.findViewById(R.id.btn_ble_scan_stop).setEnabled(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ble_scan_start:
                startScan();
                break;
            case R.id.btn_ble_scan_stop:
                stopScan();
                break;
        }
    }
}
