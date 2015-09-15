package jp.ac.it_college.std.bletest;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class BLEDeviceListAdapter extends ArrayAdapter<BluetoothDevice>{

    private List<BluetoothDevice> devices = null;
    private Context context;
    private int resource;

    public BLEDeviceListAdapter(Context context, int resource, List<BluetoothDevice> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.devices = objects;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resource, null);
        }

        BluetoothDevice device = devices.get(position);
        if (device != null) {
            TextView name = (TextView) view.findViewById(R.id.lbl_device_name);
            TextView address = (TextView) view.findViewById(R.id.lbl_device_address);

            if (name != null) {
                name.setText(device.getName());
            }

            if (address != null) {
                address.setText(device.getAddress());
            }

        }

        return view;
    }
}
