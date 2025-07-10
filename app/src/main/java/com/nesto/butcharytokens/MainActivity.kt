package com.nesto.butcharytokens

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bxl.config.editor.BXLConfigLoader
import com.nesto.butcharytokens.model.NewTokenRequest
import com.nesto.butcharytokens.model.NewTokenResponse
import com.nesto.butcharytokens.retrofit.ApiClient
import com.nesto.butcharytokens.retrofit.ApiInterface
import jpos.POSPrinter
import jpos.POSPrinterConst
import jpos.JposException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var usbManager: UsbManager
    private lateinit var etMobile: EditText
    private lateinit var btnPrint: Button

    private val ACTION_USB_PERMISSION = "com.nesto.butcharytokens.USB_PERMISSION"
    private val logicalName = "SRP-350plusIII"
    private lateinit var posPrinter: POSPrinter
    private var isPrinterConnected = false

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_USB_PERMISSION) {
//                val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
//                val device: UsbDevice? = intent?.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)

                val granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)

                if (device != null && granted) {
                    Toast.makeText(this@MainActivity, "USB permission granted", Toast.LENGTH_SHORT)
                        .show()
                    connectToPrinter()
                } else {
                    Toast.makeText(this@MainActivity, "USB permission denied", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etMobile = findViewById(R.id.et_mobile)
        btnPrint = findViewById(R.id.btn_generate_token)

        usbManager = getSystemService(Context.USB_SERVICE) as UsbManager
        posPrinter = POSPrinter(this)

        // Register receiver once during activity lifecycle
        val filter = IntentFilter(ACTION_USB_PERMISSION)
        registerReceiver(usbReceiver, filter)

        btnPrint.setOnClickListener {
//            requestUsbPermission()
//            btnPrint.isEnabled = false
//            requestUsbPermission()
//            btnPrint.postDelayed({ btnPrint.isEnabled = true }, 2000)

            val token=etMobile.text.toString()
            GenerateToken(token,"0502241751")
        }
    }

    private fun requestUsbPermission() {
        val deviceList = usbManager.deviceList
        for (device in deviceList.values) {
            if (device.vendorId == 5380) { // Bixolon vendor ID (decimal of 0x1504)
                if (usbManager.hasPermission(device)) {
                    Toast.makeText(this, "Already has permission", Toast.LENGTH_SHORT).show()
                    connectToPrinter()
                } else {
                    val permissionIntent = PendingIntent.getBroadcast(
                        this,
                        0,
                        Intent(ACTION_USB_PERMISSION),
                        PendingIntent.FLAG_IMMUTABLE
                    )
                    usbManager.requestPermission(device, permissionIntent)
                }
                return
            }
        }
        Toast.makeText(this, "No supported USB printer found", Toast.LENGTH_SHORT).show()
    }


    private fun connectToPrinter() {

        try {
            try {
                posPrinter.release()
            } catch (_: Exception) {
            }
            try {
                posPrinter.close()
            } catch (_: Exception) {
            }

            val configLoader = BXLConfigLoader(this)
            configLoader.removeEntry(logicalName) // remove old config
            configLoader.addEntry(
                logicalName,
                BXLConfigLoader.DEVICE_CATEGORY_POS_PRINTER,
                "BIXOLON",
                BXLConfigLoader.DEVICE_BUS_USB,
                ""
            )
            configLoader.saveFile()

            posPrinter.open(logicalName)
            Thread.sleep(300)
            posPrinter.claim(1000) // This line fails if device is locked or permission denied
            posPrinter.deviceEnabled = true
            isPrinterConnected = true

            val tokenNumber = "25"
            val mobileNumber = etMobile.text.toString().trim()

            val printText = buildString {
                append("\u001b|cA\n")
                append("Nesto Fish & Butchery\n")
                append("------------------------------\n")
                append("Token #: $tokenNumber\n")
                append("Mobile: $mobileNumber\n")
                append("------------------------------\n")
                append("Please wait for your turn\n\n")
                append("\u001b|1lF")
            }

            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, printText)

        } catch (e: JposException) {
            Toast.makeText(this, "Printing failed: ${e.message}", Toast.LENGTH_LONG).show()
            Log.e("PrinterError", "JPOS Exception", e)
            isPrinterConnected = false
        } catch (e: Exception) {
            Toast.makeText(this, "Unexpected error: ${e.message}", Toast.LENGTH_LONG).show()
            Log.e("PrinterError", "Unexpected Exception", e)
            isPrinterConnected = false
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            if (::posPrinter.isInitialized && isPrinterConnected) {
                posPrinter.release()
                posPrinter.close()
                isPrinterConnected = false
            }
        } catch (e: Exception) {
            Log.e("CleanupError", "Error closing printer", e)
        }
        unregisterReceiver(usbReceiver)
    }

    /////////////////////////

    private fun GenerateToken(tokenNumber: String,contactNumber: String) {
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog, null)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        val apiService = ApiClient.getClient(this).create(ApiInterface::class.java)
        val call: Call<NewTokenResponse>

        var request = NewTokenRequest()
        request.token_number = tokenNumber
        request.contact_number = contactNumber

        call = apiService.GenerateToken(request)
        call.enqueue(object : Callback<NewTokenResponse?> {

            private var message: String? = null

            override fun onResponse(
                call: Call<NewTokenResponse??>,
                response: Response<NewTokenResponse??>
            ) {
                if (response.isSuccessful()) {
                    assert(response.body() != null)
                    if (response.body()?.id!=null) {
                        Toast.makeText(
                            this@MainActivity,
                            response.body()?.id.toString(),
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        response.message(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                dialog.cancel()
            }

            override fun onFailure(call: Call<NewTokenResponse??>, t: Throwable) {
                dialog.cancel()
                Toast.makeText(
                    applicationContext,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

    }

}
