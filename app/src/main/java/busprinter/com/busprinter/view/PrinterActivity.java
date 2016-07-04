package busprinter.com.busprinter.view;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import busprinter.com.busprinter.PrinterManager;
import busprinter.com.busprinter.PrinterViewAction;
import busprinter.com.busprinter.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PrinterActivity extends AppCompatActivity implements BluetoothMatchingDeviceClickListener, PrinterViewAction {

    final int REQUEST_ENABLE_BT = 101;

    @BindView(R.id.tvDeviceName)
    TextView tvDeviceName;
    @BindView(R.id.btConnect)
    Button btConnect;
    @BindView(R.id.btDisConnect)
    Button btDisConnect;
    @BindView(R.id.btSelfPrint)
    Button btPrint;
    @BindView(R.id.glPrint)
    GridLayout glPrint;
    @BindView(R.id.ivStatus)
    ImageView ivStatus;

    private BluetoothAdapter adapter;
    private BluetoothDevice bluetoothDevice;
    private PrinterManager printerManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        printerManager = new PrinterManager(this);
    }

    @OnClick(R.id.tvDeviceName)
    void setUpBluetooth() {
        adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            // Device does not support Bluetooth
        } else {
            if (!adapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                openDialog();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        printerManager.stopConnection();
    }

    //===============================================
    //Click for each feature
    //===============================================
    @OnClick(R.id.btConnect)
    void connectPrinter() {
        printerManager.connectBluetooth(bluetoothDevice);
    }

    @OnClick(R.id.btDisConnect)
    void disconnectPrinter() {
        printerManager.stopConnection();
        showFailed();
    }

    @OnClick(R.id.btSelfPrint)
    void clickSelfPrint() {
        printerManager.selfPrinter();
    }

    @OnClick(R.id.btBeep)
    void clickBeep() {
        printerManager.beep();
    }

    @OnClick(R.id.btHalfCut)
    void clickHalfCut() {
        printerManager.halfCut();
    }

    @OnClick(R.id.btFullCut)
    void clickFullCut() {
        printerManager.fullCut();
    }

    @OnClick(R.id.btCodeBar)
    void clickPrintCodeBar() {
        String mockCode = "1902323423";
        printerManager.printCodeBar(mockCode);
    }

    @OnClick(R.id.btQRCode)
    void clickPrintQRCode() {
        String mockCode = "2323333";
        printerManager.printQRCode(mockCode);
    }

    //===============================================
    //View Action
    //===============================================
    @Override
    public void showConnected() {
        btConnect.setVisibility(View.GONE);
        btDisConnect.setVisibility(View.VISIBLE);
        ivStatus.setBackgroundColor(ContextCompat.getColor(this, R.color.green600));
        glPrint.setVisibility(View.VISIBLE);
    }

    @Override
    public void showFailed() {
        btConnect.setVisibility(View.VISIBLE);
        btDisConnect.setVisibility(View.GONE);
        ivStatus.setBackgroundColor(ContextCompat.getColor(this, R.color.red600));
        glPrint.setVisibility(View.GONE);
        Toast.makeText(this, "Can't connect, please try again", Toast.LENGTH_SHORT);
    }


    private void openDialog() {
        BluetoothMatchingDeviceDialogFragment dialog = new BluetoothMatchingDeviceDialogFragment();
        dialog.show(getSupportFragmentManager(), BluetoothMatchingDeviceDialogFragment.class.getName());
    }

    //===============================================
    //Enable bluetooth result
    //===============================================
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ENABLE_BT: {
                    openDialog();
                    break;
                }
            }
        }
    }

    @Override
    public void onChoose(BluetoothDevice pairedDevice) {
        tvDeviceName.setText(pairedDevice.getName());
        btConnect.setVisibility(View.VISIBLE);
        bluetoothDevice = pairedDevice;
    }

}
