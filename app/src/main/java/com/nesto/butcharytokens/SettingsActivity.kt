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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nesto.butcharytokens.SplashActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var selectedUser: String
    private var selectedDept: String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPreferences = this.getSharedPreferences("sharedpreferences", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        val store_id = findViewById<EditText>(R.id.store_id)
        val save_btn = findViewById<Button>(R.id.save_btn)
        val usertype_spinner = findViewById<Spinner>(R.id.usertype_spinner)
        val depttype_spinner = findViewById<Spinner>(R.id.depttype_spinner)
        val dept_title = findViewById<TextView>(R.id.dept_title)

        val Deptadapter = ArrayAdapter.createFromResource(
            this,                      // context
            R.array.dept_type_options,        // array resource
            android.R.layout.simple_spinner_item // layout for each item
        )
        Deptadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        depttype_spinner.adapter = Deptadapter

        val user_adapter = ArrayAdapter.createFromResource(
            this,                      // context
            R.array.user_type_options,        // array resource
            android.R.layout.simple_spinner_item // layout for each item
        )
        user_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        usertype_spinner.adapter = user_adapter

        usertype_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                selectedUser = parent.getItemAtPosition(position).toString()
                if(selectedUser.equals("store")){
                    depttype_spinner.visibility= View.VISIBLE
                    dept_title.visibility= View.VISIBLE
                }else{
                    depttype_spinner.visibility= View.GONE
                    dept_title.visibility= View.GONE
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Optional
            }
        }

        depttype_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                selectedDept = parent.getItemAtPosition(position).toString()

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Optional
            }
        }



        save_btn.setOnClickListener {
            val storeId = store_id.text.toString()
            if (store_id != null) {
                editor.putString("usertype", selectedUser)
                editor.putString("depttype", selectedDept)
                editor.putString("storeId",storeId)
                editor.apply()
                editor.commit()

                if(!storeId.isEmpty()&&storeId.length==4){
                if(selectedUser.equals("customer")){
                val intent = Intent(this@SettingsActivity, DeptSelectionActivity::class.java)
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
                }}
                else{
                    Toast.makeText(this, "Please enter a valid storeID", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}
