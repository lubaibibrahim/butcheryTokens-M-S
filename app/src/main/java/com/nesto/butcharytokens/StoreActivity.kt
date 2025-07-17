package com.nesto.butcharytokens

import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nesto.butcharytokens.adapter.TokenAdapter
import com.nesto.butcharytokens.model.TokenlistResponseItem
import com.nesto.butcharytokens.retrofit.ApiClient
import com.nesto.butcharytokens.retrofit.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoreActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var storeId: String
    private lateinit var dept: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store)

        recyclerView = findViewById(R.id.recycler_tokens)
        var refresh_btn = findViewById<ImageView>(R.id.refresh_btn)
        recyclerView.layoutManager = GridLayoutManager(this, 3) // 3 tiles per row

        sharedPreferences = this.getSharedPreferences("sharedpreferences", MODE_PRIVATE)
        editor = sharedPreferences.edit()
        storeId = sharedPreferences.getString("storeId", "").toString()
        dept = sharedPreferences.getString("depttype", "").toString()

        Tokenlist(storeId.toString(),dept)

        refresh_btn.setOnClickListener {
            Tokenlist(storeId.toString(),dept)
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private val refreshRunnable = object : Runnable {
        override fun run() {
            callApi()
            handler.postDelayed(this, 60_000) // 60 seconds
        }
    }

    override fun onResume() {
        super.onResume()
        handler.post(refreshRunnable)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(refreshRunnable)
    }

    private fun callApi() {
        Tokenlist(storeId.toString(),dept)
    }

    private fun Tokenlist(storeId: String, depttype: String?) {
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView)
            .setCancelable(false).create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        val apiService = ApiClient.getClient(this).create(ApiInterface::class.java)
        val call: Call<ArrayList<TokenlistResponseItem>>

        call = apiService.Tokenlist(storeId,depttype)
        call.enqueue(object : Callback<ArrayList<TokenlistResponseItem>> {

            private var message: String? = null

            override fun onResponse(
                call: Call<ArrayList<TokenlistResponseItem>>, response: Response<ArrayList<TokenlistResponseItem>>
            ) {
                if (response.isSuccessful()) {
                    assert(response.body() != null)
                    if (response.body() != null) {

                        val adapter = TokenAdapter(
                            this@StoreActivity,
                            response.body()
                        )
                        recyclerView.adapter = adapter


                    } else {
                        Toast.makeText(
                            this@StoreActivity, response.message(), Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@StoreActivity, response.message(), Toast.LENGTH_SHORT
                    ).show()
                }
                dialog.cancel()
            }

            override fun onFailure(call: Call<ArrayList<TokenlistResponseItem>>, t: Throwable) {
                dialog.cancel()
                Toast.makeText(
                    applicationContext, t.message, Toast.LENGTH_SHORT
                ).show()
            }
        })

    }

}
