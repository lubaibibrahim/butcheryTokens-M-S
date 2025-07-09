package com.nesto.butcharytokens

import android.R.attr.mode
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class StoreActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
//    private lateinit var adapter: TokenAdapter
//    private val tokenList = mutableListOf<Token>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store)

//        recyclerView = findViewById(R.id.recycler_tokens)
//        recyclerView.layoutManager = GridLayoutManager(this, 2) // 2 tiles per row
//
//        // Sample Data
//        tokenList.add(Token("001", "Mohammed"))
//        tokenList.add(Token("002", "Rashid"))
//        tokenList.add(Token("003", "Hameed"))
//
//        adapter = TokenAdapter(tokenList) { token ->
//            tokenList.remove(token)
//            adapter.notifyDataSetChanged()
//            Toast.makeText(this, "Marked Token #${token.tokenNumber} as Completed", Toast.LENGTH_SHORT).show()
//        }
//
//        recyclerView.adapter = adapter
    }
}
