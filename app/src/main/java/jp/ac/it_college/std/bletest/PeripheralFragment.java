package jp.ac.it_college.std.bletest;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class PeripheralFragment extends Fragment implements View.OnClickListener{

    private Advertise advertise;
    private View contentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contentView = inflater.inflate(R.layout.fragment_peripheral, container, false);
        EditText inputText = (EditText) contentView.findViewById(R.id.edit_server_message);

        advertise = new Advertise(getActivity(), inputText);
        contentView.findViewById(R.id.btn_start_advertise).setOnClickListener(this);
        contentView.findViewById(R.id.btn_stop_advertise).setOnClickListener(this);
        return contentView;
    }

    @Override
    public void onPause() {
        super.onPause();
        stopAdvertise();
    }

    private void startAdvertise() {
        advertise.startAdvertise(getActivity());
        contentView.findViewById(R.id.btn_start_advertise).setEnabled(false);
        contentView.findViewById(R.id.btn_stop_advertise).setEnabled(true);
    }

    private void stopAdvertise() {
        advertise.stopAdvertise();
        contentView.findViewById(R.id.btn_start_advertise).setEnabled(true);
        contentView.findViewById(R.id.btn_stop_advertise).setEnabled(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start_advertise:
                startAdvertise();
                break;
            case R.id.btn_stop_advertise:
                stopAdvertise();
                break;
        }
    }
}
