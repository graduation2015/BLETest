package jp.ac.it_college.std.bletest;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class MainActivity extends Activity {

    private final int REQUEST_ENABLE_BT = 0x01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            //BLE非対応
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            dialog.setMessage("BLE not supported");
            dialog.show();
            return;
        }

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        actionBar.addTab(actionBar.newTab()
                .setText("Peripheral")
                .setTabListener(new TabListener<PeripheralFragment>(
                        this, "tag1", PeripheralFragment.class)));

        actionBar.addTab(actionBar.newTab()
                .setText("Central")
                .setTabListener(new TabListener<CentralFragment>(
                        this, "tag1", CentralFragment.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        bluetoothSetUp();
    }

    private void bluetoothSetUp() {
        BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();

        if (bt == null) {
            return;
        }

        if (!bt.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }
}
