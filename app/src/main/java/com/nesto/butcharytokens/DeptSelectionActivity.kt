package com.nesto.butcharytokens

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class DeptSelectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_department_selection)

        var fishery_btn = findViewById<ImageView>(R.id.fishery_btn)
        var butchery_btn = findViewById<ImageView>(R.id.butchery_btn)

        fishery_btn.setOnClickListener {
            val intent = Intent(this@DeptSelectionActivity, CustomerActivity::class.java)
            intent.putExtra("dept","fish")
            this@DeptSelectionActivity.startActivity(intent)
            overridePendingTransition(
                R.anim.fade_in,
                R.anim.fade_out
            )

        }
        butchery_btn.setOnClickListener {
            val intent = Intent(this@DeptSelectionActivity, CustomerActivity::class.java)
            intent.putExtra("dept","butchery")
            this@DeptSelectionActivity.startActivity(intent)
            overridePendingTransition(
                R.anim.fade_in,
                R.anim.fade_out
            )

        }
    }
}