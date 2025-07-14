package com.nesto.butcharytokens.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.nesto.butcharytokens.R
import com.nesto.butcharytokens.model.TokenStatusUpdateResponse
import com.nesto.butcharytokens.model.TokenlistResponseItem
import com.nesto.butcharytokens.retrofit.ApiClient
import com.nesto.butcharytokens.retrofit.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TokenAdapter(
    private val context: Context,
    private val tokenList: ArrayList<TokenlistResponseItem>?,
) : RecyclerView.Adapter<TokenAdapter.TokenViewHolder>() {

    class TokenViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tokenNumber: TextView = view.findViewById(R.id.token_number)
        val contactNumber: TextView = view.findViewById(R.id.contact_number)
        val status: TextView = view.findViewById(R.id.status)

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

            holder.itemView.setOnClickListener {
                updateTokenStatus(token.tokenNumber ?: "",position)
            }
        }
    }

    override fun getItemCount(): Int = tokenList?.size ?: 0

    private fun updateTokenStatus(tokenNumber: String, position: Int) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val apiService = ApiClient.getClient(context).create(ApiInterface::class.java)
        val call : Call<TokenStatusUpdateResponse>
        call = apiService.UpdateStatus(tokenNumber)
        call.enqueue(object : Callback<TokenStatusUpdateResponse?> {

            private var message: String? = null

            override fun onResponse(
                call: Call<TokenStatusUpdateResponse??>,
                response: Response<TokenStatusUpdateResponse??>
            ) {
                if (response.isSuccessful()) {
                    assert(response.body() != null)
                    if (response.body()?.status.equals("success")) {

                        if (tokenList != null) {
                            tokenList.removeAt(position)
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position, tokenList.size)
                        }

                    } else {
                        Toast.makeText(
                            context, response.message(), Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        context, response.message(), Toast.LENGTH_SHORT
                    ).show()
                }
                dialog.cancel()
            }

            override fun onFailure(call: Call<TokenStatusUpdateResponse?>, t: Throwable) {
                dialog.cancel()
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}
