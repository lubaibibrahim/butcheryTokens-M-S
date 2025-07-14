package com.nesto.butcharytokens.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nesto.butcharytokens.R
import com.nesto.butcharytokens.model.TokenlistResponseItem


class TokenAdapter(private val tokenList: ArrayList<TokenlistResponseItem>?) :
    RecyclerView.Adapter<TokenAdapter.TokenViewHolder>() {

    class TokenViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tokenNumber: TextView = view.findViewById(R.id.token_number)
        val contactNumber: TextView = view.findViewById(R.id.contact_number)
        val status: Button = view.findViewById(R.id.status)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TokenViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_token_tile, parent, false)
        return TokenViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TokenViewHolder, position: Int) {
        val token = tokenList?.get(position)

        if (token != null) {
            holder.status.text = "Status: ${token.status}"
            holder.tokenNumber.text = "Token #: ${token.tokenNumber?.takeLast(3)}"
            holder.contactNumber.text = "Contact: ${token.contactNumber}"
        }

        holder.status.setOnClickListener {



        }
    }

    override fun getItemCount(): Int = tokenList!!.size
}
