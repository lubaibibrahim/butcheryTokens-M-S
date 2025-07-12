package com.nesto.butcharytokens

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
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
import android.media.AudioManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var usbManager: UsbManager
    private lateinit var etMobile: EditText
    private lateinit var btnPrint: Button
    private lateinit var mobileNumber: String
    private lateinit var storeId: String
    lateinit var malayalamSpeaker: MalayalamSpeaker

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private val ACTION_USB_PERMISSION = "com.nesto.butcharytokens.USB_PERMISSION"
    private val logicalName = "SRP-350plusIII"
    private lateinit var posPrinter: POSPrinter
    private var isPrinterConnected = false

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_USB_PERMISSION) {

                val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                val granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)

                if (device != null && granted) {
                    //  Toast.makeText(this@MainActivity, "USB permission granted", Toast.LENGTH_SHORT) .show()
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
        malayalamSpeaker = MalayalamSpeaker(this)


        sharedPreferences = this.getSharedPreferences("sharedpreferences", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        storeId = sharedPreferences.getString("storeId", "").toString()

        // Register receiver once during activity lifecycle
        val filter = IntentFilter(ACTION_USB_PERMISSION)
        registerReceiver(usbReceiver, filter)

        btnPrint.setOnClickListener {
            mobileNumber = etMobile.text.toString().trim()

            //printing
            requestUsbPermission()
            btnPrint.isEnabled = false
            btnPrint.postDelayed({ btnPrint.isEnabled = true }, 2000)

//            text to speech
//            setTTSVolumeMax()
//            val malayalamText = "അബിൻ, നന്ദിയുണ്ടേ.....!"
//            malayalamSpeaker.speak(malayalamText)

            //sending token to backend
//            val token=etMobile.text.toString()
//            GenerateToken(token,"0502241751",storeId)
        }
    }

    private fun requestUsbPermission() {
        val deviceList = usbManager.deviceList
        for (device in deviceList.values) {
            if (device.vendorId == 5380) { // Bixolon vendor ID (decimal of 0x1504)
                if (usbManager.hasPermission(device)) {
                    //  Toast.makeText(this, "Already has permission", Toast.LENGTH_SHORT).show()
                    connectToPrinter()
                } else {
                    val permissionIntent = PendingIntent.getBroadcast(
                        this, 0, Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE
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
                "SRP-350plusIII",
                BXLConfigLoader.DEVICE_BUS_USB,
                ""
            )
            configLoader.saveFile()

            posPrinter.open(logicalName)
            Thread.sleep(300)
            posPrinter.claim(1000) // This line fails if device is locked or permission denied
            posPrinter.deviceEnabled = true
            isPrinterConnected = true
            val currentTime =SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale("en", "IN")).format(Date())
            val tokenNo = generateTokenNumber(this, storeId)
            printTokenWithBixolon(tokenNo, mobileNumber,"Lubaib Ibrahim" ,posPrinter)

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
        malayalamSpeaker.shutdown()
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

    private fun GenerateToken(tokenNumber: String, contactNumber: String,storeId:String) {
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog, null)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this).setView(dialogView)
            .setCancelable(false).create()

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
                call: Call<NewTokenResponse??>, response: Response<NewTokenResponse??>
            ) {
                if (response.isSuccessful()) {
                    assert(response.body() != null)
                    if (response.body()?.id != null) {
                        Toast.makeText(
                            this@MainActivity, response.body()?.id.toString(), Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        Toast.makeText(
                            this@MainActivity, response.message(), Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@MainActivity, response.message(), Toast.LENGTH_SHORT
                    ).show()
                }
                dialog.cancel()
            }

            override fun onFailure(call: Call<NewTokenResponse??>, t: Throwable) {
                dialog.cancel()
                Toast.makeText(
                    applicationContext, t.message, Toast.LENGTH_SHORT
                ).show()
            }
        })

    }

    private fun setTTSVolumeMax() {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0
        )
    }

    fun generateTokenNumber(context: Context, storeId: String): String {
        if (storeId.length != 4) throw IllegalArgumentException("Store ID must be 4 digits")
        val date = SimpleDateFormat("ddMMyy", Locale.getDefault()).format(Date())

        var lastSequence = sharedPreferences.getInt("last_sequence", 0)
        val newSequence = (lastSequence + 1).coerceAtMost(999)
        val sequenceStr = newSequence.toString().padStart(3, '0')

        editor.putInt("last_sequence", newSequence)
        editor.putString("last_date", date)
        editor.apply()

        return "$storeId$date$sequenceStr"
    }


    fun printTokenWithBixolon(
        tokenNumber: String,
        mobileNumber: String,
        name: String,
        posPrinter: jpos.POSPrinter
    ) {
        try {
            val currentTime =
                SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale("en", "IN")).format(Date())

            val header = """
            NESTO FRESH
            JVC Branch - Dubai
            Cashier: System User
            $currentTime
            ------------------------------
        """.trimIndent()


            // Token info (center aligned, bold token details)
            val tokenInfo = buildString {
                append("\u001b|cA")              // Center alignment
                append("\u001b|bC\u001b|4C") //Big font
                append("Token: $tokenNumber\n")
                append("\u001b|1C") //Back to normal font
                append("Mobile : $mobileNumber\n")
                append("Customer: $name\n")
                append("\u001b|N")
                append("\u001b|cA")
                append("------------------------------\n")
                append("Get notified once the item is ready!\n")
                append("Scan the QR code to know the status\n")
            }

            // Print header
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, header)
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, "\n")

            // Print barcode
            posPrinter.printBarCode(
                POSPrinterConst.PTR_S_RECEIPT,
                tokenNumber, // Use token as QR content
                POSPrinterConst.PTR_BCS_QRCODE,
                8,
                8,
                POSPrinterConst.PTR_BC_CENTER,
                POSPrinterConst.PTR_BC_TEXT_BELOW
            )

            // Print token info
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, tokenInfo)

            // Feed & cut
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, "\n\n\n\n\n")
            posPrinter.cutPaper(90)

        } catch (e: JposException) {
            Log.e("BIXOLON", "Print error: ${e.message}", e)
        }
    }

}
