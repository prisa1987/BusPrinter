package busprinter.com.busprinter.view;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import busprinter.com.busprinter.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BluetoothPairDialogFragment extends DialogFragment {

    List<String> deviceNames;
    List<String> deviceAddress;
    static String ARG_DEVICES_NAME = "arg_devices_name";
    static String ARG_DEVICES_ADDRESS = "arg_devices_address";

    BluetoothPairAdapter adapter;
    BluetoothPairClickListener clickListner;

    private BluetoothAdapter mBluetoothAdapter;
    private List<BluetoothDevice> pairedDeviceList, foundDeviceList;
    private BroadcastReceiver mBluetoothReceiver;

    @BindView(R.id.rvBluetooth)
    RecyclerView rvBluetooth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get Bundle
        deviceNames = getArguments().getStringArrayList(ARG_DEVICES_NAME);
        deviceAddress = getArguments().getStringArrayList(ARG_DEVICES_ADDRESS);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("Paired Bluetooth");
        View view = inflater.inflate(R.layout.dialog_pair_bluetooth, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setUp();
    }

    void setUp() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        clickListner = (BluetoothPairClickListener) getActivity();
        adapter = new BluetoothPairAdapter();
        rvBluetooth.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvBluetooth.setAdapter(adapter);
    }

//    //=======================
//    // Bluetooth
//    private class BluetoothDeviceReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            // When discovery finds a device
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                // Get the BluetoothDevice object from the Intent
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && device.getType() != BluetoothDevice.DEVICE_TYPE_CLASSIC)
//                    return;
//                if (!foundDeviceList.contains(device)) {
//                    foundDeviceList.add(device);
//                    adapter.notifyDataSetChanged();
//                }
//            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
//                mBluetoothAdapter.cancelDiscovery();
//                mContext.unregisterReceiver(mBluetoothReceiver);
//                mRegistered = false;
////                tvSearchDevice.setEnabled(true);
////                progressBar.setVisibility(View.GONE);
//                if (foundDeviceList.size() == 0) {
//                    tvFoundDeviceEmpty.setVisibility(View.VISIBLE);
//                }
//            }
//        }
//    }

    //=================================
    //Adapter
    //=================================
    class BluetoothPairAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.item_pair_bluetooth, parent, false);
            return new BluetoothViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((BluetoothViewHolder) holder).setDeviceName(deviceNames.get(position));
            ((BluetoothViewHolder) holder).setAddress(deviceAddress.get(position));
        }

        @Override
        public int getItemCount() {
            return deviceNames.size();
        }
    }

    //=================================
    //ViewHolder
    //=================================
    class BluetoothViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvDeviceName)
        TextView tvDeviceName;

        String address;

        public BluetoothViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setDeviceName(String name) {
            tvDeviceName.setText(name);
        }

        void setAddress(String address) {
            this.address = address;
        }

        @OnClick(R.id.llDevice)
        void setClick() {
            clickListner.onChoose(address);
            dismiss();
        }

    }

}
interface BluetoothPairClickListener {
     void onChoose(String deviceName);
}
