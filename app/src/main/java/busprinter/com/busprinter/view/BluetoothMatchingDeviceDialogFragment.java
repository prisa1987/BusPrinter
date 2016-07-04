package busprinter.com.busprinter.view;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import busprinter.com.busprinter.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BluetoothMatchingDeviceDialogFragment extends DialogFragment {

    List<Pair<Integer, Object>> bluetoothDevices = new ArrayList<>();

    BluetoothMatchingDeviceAdapter adapter;
    BluetoothMatchingDeviceClickListener clickListener;

    private BluetoothAdapter bluetoothAdapter;

    private Integer SECTION_TYPE = 0;
    private Integer DEVICE_TYPE = 1;
    private boolean isRegistered = false;

    @BindView(R.id.rvBluetooth)
    RecyclerView rvBluetooth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("Discovery Bluetooth");
        View view = inflater.inflate(R.layout.dialog_pair_bluetooth, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setUp();
    }

    void setUp() {
        adapter = new BluetoothMatchingDeviceAdapter();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothDevices.add(Pair.<Integer, Object>create(SECTION_TYPE, getString(R.string.pair)));
        pairBluetoothDevice();

        bluetoothDevices.add(Pair.<Integer, Object>create(SECTION_TYPE, getString(R.string.search)));
        clickListener = (BluetoothMatchingDeviceClickListener) getActivity();
        rvBluetooth.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvBluetooth.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isRegistered) {
            getContext().unregisterReceiver(mReceiver);
        }
    }

    //=======================
    // Bluetooth Pair
    //=======================
    void pairBluetoothDevice() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice bluetoothDevice : pairedDevices) {
            bluetoothDevices.add(Pair.<Integer, Object>create(DEVICE_TYPE, bluetoothDevice));
            adapter.notifyDataSetChanged();
        }
    }

    //=======================
    // Bluetooth Discovery
    //=======================
    final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && device.getType() != BluetoothDevice.DEVICE_TYPE_CLASSIC)
                    return;
                bluetoothDevices.add(Pair.<Integer, Object>create(DEVICE_TYPE, device));
                adapter.notifyDataSetChanged();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                bluetoothAdapter.cancelDiscovery();
                getContext().unregisterReceiver(mReceiver);
            }
        }
    };


    //=======================
    //Adapter
    //=======================
    class BluetoothMatchingDeviceAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (viewType == R.string.section_type) {
                View view = layoutInflater.inflate(R.layout.item_section_bluetooth, parent, false);
                return new BluetoothViewHolder(view);
            } else {
                View view = layoutInflater.inflate(R.layout.item_pair_bluetooth, parent, false);
                return new BluetoothViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (getItemViewType(position) == R.string.device_type) {
                BluetoothDevice device = (BluetoothDevice) bluetoothDevices.get(position).second;
                ((BluetoothViewHolder) holder).setDeviceName(device.getName());
                ((BluetoothViewHolder) holder).setBlueToothDevice(device);
            } else {
                if (bluetoothDevices.get(position).second.equals(getString(R.string.search))) {
                    ((BluetoothViewHolder) holder).showSearchIcon();
                }
                ((BluetoothViewHolder) holder).setDeviceName((String) bluetoothDevices.get(position).second);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (bluetoothDevices.get(position).first.equals(SECTION_TYPE)) {
                return R.string.section_type;
            }
            return R.string.device_type;
        }

        @Override
        public int getItemCount() {
            return bluetoothDevices.size();
        }
    }

    //=======================
    //ViewHolder
    //=======================
    class BluetoothViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvDeviceName)
        TextView tvDeviceName;

        BluetoothDevice bluetoothDevice;

        BluetoothViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setDeviceName(String name) {
            tvDeviceName.setText(name);
        }

        void setBlueToothDevice(BluetoothDevice bluetoothDevice) {
            this.bluetoothDevice = bluetoothDevice;
        }

        void showSearchIcon() {
            ImageView ivSearch = (ImageView) itemView.findViewById(R.id.ivSearch);
            ivSearch.setVisibility(View.VISIBLE);
        }

        @OnClick(R.id.llDevice)
        void setClick() {
            if (tvDeviceName.getText().toString().equals(getString(R.string.search))) {
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                bluetoothAdapter.startDiscovery();
                getContext().registerReceiver(mReceiver, filter);
                isRegistered = true;
            } else if (getItemViewType() == R.string.device_type) {
                clickListener.onChoose(bluetoothDevice);
                dismiss();
            } else {
                ImageView ivSearch = (ImageView) getView().findViewById(R.id.ivSearch);
                ivSearch.setVisibility(View.GONE);
            }
        }

    }

}

interface BluetoothMatchingDeviceClickListener {
    void onChoose(BluetoothDevice device);
}
