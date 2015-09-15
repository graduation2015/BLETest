package jp.ac.it_college.std.bletest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class BLEScanner extends ScanCallback {

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private ArrayList<BluetoothDevice> deviceList = new ArrayList<>();
    private boolean isScanning;
    private CentralFragment fragment;

    public BLEScanner(Context context, CentralFragment fragment) {
        //初期化
        bluetoothManager = (BluetoothManager) context
                .getSystemService(Context.BLUETOOTH_SERVICE);
        // mBluetoothAdapterの取得
        mBluetoothAdapter = bluetoothManager.getAdapter();
        // mBluetoothLeScannerの初期化
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        this.fragment = fragment;
    }

    // スキャン実施
    public void scan(List<ScanFilter> filters, ScanSettings settings) {
        // スキャンフィルタを設定するならこちら
        mBluetoothLeScanner.startScan(filters, settings, this);
        isScanning = true;
    }

    //スキャン停止
    public void stopScan() {
        if (mBluetoothLeScanner != null) {
            mBluetoothLeScanner.stopScan(this);
            isScanning = false;
        }
    }

    // スキャンしたデバイスがリストに追加済みかどうかの確認
    public boolean isAdded(BluetoothDevice device) {
        if (deviceList != null && deviceList.size() > 0) {
            return deviceList.contains(device);
        } else {
            return false;
        }
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        super.onScanResult(callbackType, result);

        if (result != null && result.getDevice() != null) {
            if (isAdded(result.getDevice())) {
                // No add
            } else {
                fragment.saveDevice(result.getDevice());
//                saveDevice(result.getDevice());
            }
        }
    }
}
