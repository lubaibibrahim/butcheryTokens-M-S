package com.nesto.butcharytokens

import android.content.Context
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bixolon.printer.BixolonPrinter

class MainActivity : AppCompatActivity() {

    private lateinit var etMobile: EditText
    private lateinit var btnScan: Button
    private lateinit var btnGenerate: Button
    private lateinit var tvScannedCode: TextView
    private var scannedCode: String? = null
    private lateinit var bixolonPrinter: BixolonPrinter
    private lateinit var usbManager: UsbManager
    private val ACTION_USB_PERMISSION = "com.yourapp.USB_PERMISSION"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etMobile = findViewById(R.id.et_mobile)
        btnGenerate = findViewById(R.id.btn_generate_token)

        usbManager = getSystemService(Context.USB_SERVICE) as UsbManager
        bixolonPrinter = BixolonPrinter(this, mHandler, null)

        connectToUsbPrinter()
//        btnScan.setOnClickListener {
//            // TODO: Integrate barcode scanner (ZXing / ML Kit / Intent)
//            scannedCode = "INAAM12345678" // mock value
//            tvScannedCode.text = "Scanned: $scannedCode"
//        }

        btnGenerate.setOnClickListener {
        }
    }
        private fun connectToUsbPrinter() {
            val deviceList: HashMap<String, UsbDevice> = usbManager.deviceList
            for (device in deviceList.values) {
                if (BixolonPrinter.checkUsbDevice(device)) {
                    val permissionIntent = PendingIntent.getBroadcast(
                        this, 0, Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE
                    )
                    usbManager.requestPermission(device, permissionIntent)

                    bixolonPrinter.connect(device)
                    break
                }
            }
        }

        private val mHandler = android.os.Handler(android.os.Looper.getMainLooper()) {
            when (it.what) {
                BixolonPrinter.MESSAGE_STATE_CHANGE -> {
                    when (it.arg1) {
                        BixolonPrinter.STATE_CONNECTED -> {
                            bixolonPrinter.printText("Token #123\nThank you\n", BixolonPrinter.TEXT_ALIGNMENT_CENTER, BixolonPrinter.TEXT_ATTRIBUTE_FONT_A, BixolonPrinter.TEXT_SIZE_HORIZONTAL1 or BixolonPrinter.TEXT_SIZE_VERTICAL1, false)
                            bixolonPrinter.lineFeed(3, false)
                            bixolonPrinter.cutPaper(true)
                        }
                    }
                }
            }
            true
        }
    }

