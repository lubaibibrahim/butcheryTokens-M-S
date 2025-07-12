package com.nesto.butcharytokens

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nesto.butcharytokens.SplashActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var selected: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPreferences = this.getSharedPreferences("sharedpreferences", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        val store_id = findViewById<EditText>(R.id.store_id)
        val save_btn = findViewById<Button>(R.id.save_btn)
        val usertype_spinner = findViewById<Spinner>(R.id.usertype_spinner)

        val adapter = ArrayAdapter.createFromResource(
            this,                      // context
            R.array.user_type_options,        // array resource
            android.R.layout.simple_spinner_item // layout for each item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        usertype_spinner.adapter = adapter

        usertype_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                selected = parent.getItemAtPosition(position).toString()
//                Toast.makeText(this@SettingsActivity, "Selected: $selected", Toast.LENGTH_SHORT)
//                    .show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Optional
            }
        }


        save_btn.setOnClickListener {
            val storeId = store_id.text.toString()
            if (store_id != null) {
                editor.putString("usertype", selected)
                editor.putString("storeId",storeId)
                editor.apply()
                editor.commit()

                if(selected.equals("customer")){
                val intent = Intent(this@SettingsActivity, MainActivity::class.java)
                finish()
                this@SettingsActivity.startActivity(intent)
                overridePendingTransition(
                    R.anim.fade_in,
                    R.anim.fade_out
                )}else{
                    val intent = Intent(this@SettingsActivity, StoreActivity::class.java)
                    finish()
                    this@SettingsActivity.startActivity(intent)
                    overridePendingTransition(
                        R.anim.fade_in,
                        R.anim.fade_out
                    )
                }
            }

        }
    }
}
