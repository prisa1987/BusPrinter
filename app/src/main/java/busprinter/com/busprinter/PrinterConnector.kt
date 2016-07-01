package busprinter.com.busprinter

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import driver.HsBluetoothPrintDriver

class PrinterConnector {

     fun connectBluetooth(bluetoothDevice: BluetoothDevice) {
        val hsBluetoothPrintDriver = HsBluetoothPrintDriver.getInstance()
        hsBluetoothPrintDriver.start()
        hsBluetoothPrintDriver.connect(bluetoothDevice)
    }

    fun selftestPrint() {
        val hsBluetoothPrintDriver = HsBluetoothPrintDriver.getInstance()
        hsBluetoothPrintDriver.SelftestPrint()
    }

    fun beep() {
        HsBluetoothPrintDriver.getInstance().Beep(1, 1)
    }
}
