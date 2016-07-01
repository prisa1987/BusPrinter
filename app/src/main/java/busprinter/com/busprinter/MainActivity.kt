package busprinter.com.busprinter

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val REQUEST_ENABLE_BT = 101
    var printer = PrinterConnector()
    val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btConnect.setOnClickListener {
            setBluetooth()
//            val adapter = BluetoothAdapter.getDefaultAdapter()
//            printer.connectBluetooth()
        }
        btPrint.setOnClickListener {
            printer.beep()
        }
    }


    fun setBluetooth() {
        mBluetoothAdapter?.let {
            if (!mBluetoothAdapter.isEnabled()) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            } else {
                discover()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_ENABLE_BT -> discover()
        }

    }

    fun discover() {
        val pairedDevices = mBluetoothAdapter.bondedDevices
        // If there are paired devices
        if (pairedDevices.size > 0) {
            // Loop through paired devices
            pairedDevices.forEach {
                // Add the name and address to an array adapter to show in a ListView
//                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
               if(it.name == "820USEB")  {
                   printer.connectBluetooth(it)
               }
            }
        }
    }
}
