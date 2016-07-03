package busprinter.com.busprinter;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import driver.BarcodeType;
import driver.Contants;
import driver.HsBluetoothPrintDriver;

public class PrinterManager {

    PrinterViewAction viewAction;

    public PrinterManager(PrinterViewAction viewAction) {
        this.viewAction = viewAction;
    }

    public void connectBluetooth(BluetoothDevice bluetoothDevice) {
        ConnStateHandler connStateHandler = new ConnStateHandler();
        HsBluetoothPrintDriver hsBluetoothPrintDriver = HsBluetoothPrintDriver.getInstance();
        hsBluetoothPrintDriver.setHandler(connStateHandler);
        hsBluetoothPrintDriver.start();
        hsBluetoothPrintDriver.connect(bluetoothDevice);
    }

    public void stopConnection() {
        HsBluetoothPrintDriver.getInstance().stop();
    }

    public void beep() {
        HsBluetoothPrintDriver.getInstance().Beep((byte) 1, (byte) 1);
    }

    public void selfPrinter() {
        HsBluetoothPrintDriver.getInstance().SelftestPrint();
    }

    public void halfCut() {
        HsBluetoothPrintDriver.getInstance().PartialCutPaper();
    }

    public void fullCut() {
        HsBluetoothPrintDriver.getInstance().CutPaper();
    }

    public void printCodeBar(String code) {
        HsBluetoothPrintDriver.getInstance().CODEBAR(code);
    }

    public void printQRCode(String code) {
        HsBluetoothPrintDriver.getInstance().AddCodePrint(BarcodeType.QR_CODE, code);
        print();
    }

    private void print() {
        HsBluetoothPrintDriver hsBluetoothPrintDriver = HsBluetoothPrintDriver.getInstance();
        hsBluetoothPrintDriver.Begin();
        hsBluetoothPrintDriver.SetDefaultSetting();
        hsBluetoothPrintDriver.SetPrintRotate((byte) 0);
        hsBluetoothPrintDriver.SetAlignMode((byte) 0x01);
        hsBluetoothPrintDriver.SetHRIPosition((byte) 0x02);
        hsBluetoothPrintDriver.LF();
        hsBluetoothPrintDriver.CR();
        hsBluetoothPrintDriver.LF();
        hsBluetoothPrintDriver.CR();
        hsBluetoothPrintDriver.LF();
        hsBluetoothPrintDriver.CR();
        hsBluetoothPrintDriver.SetPrintRotate((byte) 0);
    }


    private class ConnStateHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            switch (data.getInt("flag")) {
                case Contants.FLAG_STATE_CHANGE:
                    int state = data.getInt("state");
                    break;
                case Contants.FLAG_FAIL_CONNECT:
                    Log.d("-->","failed");
                    viewAction.showFailed();
                    break;
                case Contants.FLAG_SUCCESS_CONNECT:
                    Log.d("-->","Connected");
                    viewAction.showConnected();
                    break;
            }
        }

    }

}
