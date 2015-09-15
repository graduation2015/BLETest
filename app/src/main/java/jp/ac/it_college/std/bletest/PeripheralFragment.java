package jp.ac.it_college.std.bletest;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class PeripheralFragment extends Fragment implements View.OnClickListener{

    private Advertise advertise;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_peripheral, container, false);

        advertise = new Advertise();
        view.findViewById(R.id.btn_start_advertise).setOnClickListener(this);
        view.findViewById(R.id.btn_stop_advertise).setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start_advertise:
                advertise.startAdvertise(getActivity());
                Toast.makeText(getActivity(), "Start advertise", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_stop_advertise:
                advertise.stopAdvertise();
                Toast.makeText(getActivity(), "Stop advertise", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
