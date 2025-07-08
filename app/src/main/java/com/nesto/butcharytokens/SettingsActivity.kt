package com.nesto.butcharytokens

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var sharedPrefs: SharedPreferences
    lateinit var selectedUsertype: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPrefs = getSharedPreferences("AppPrefs", MODE_PRIVATE)

        val save_btn = findViewById<Button>(R.id.save_btn)
        val usertype_spinner = findViewById<Spinner>(R.id.usertype_spinner)

        val user_type_options_list = resources.getStringArray(R.array.user_type_options)
        val UsertypeAdapter = this@SettingsActivity.let {
            ArrayAdapter<String>(
                it, android.R.layout.simple_spinner_item, user_type_options_list as List<String>
            )
        }
        UsertypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        usertype_spinner?.setAdapter(UsertypeAdapter)

        usertype_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                selectedUsertype = parent.getItemAtPosition(position).toString()
                //Toast.makeText(applicationContext, "Selected: $selectedUsertype", Toast.LENGTH_SHORT).show()
                // You can also save it to a variable or SharedPreferences
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Optional: Handle case where nothing is selected
            }
        }

        save_btn.setOnClickListener {
            sharedPrefs.edit().putString("app_mode", selectedUsertype)
                .putBoolean("first_run_done", true).apply()
            if (selectedUsertype.equals("customer")) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                if (selectedUsertype.equals("customer")) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }

            }

        }

    }
}
