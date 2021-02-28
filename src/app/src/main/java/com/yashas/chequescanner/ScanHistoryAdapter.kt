package com.yashas.chequescanner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.yashas.chequescanner.database.ScanHistoryEntity
import com.yashas.chequescanner.utils.ScanHistoryDBUtils

class ScanHistoryAdapter(val context: Context, val list: ArrayList<ScanHistoryEntity>): RecyclerView.Adapter<ScanHistoryAdapter.ScanHistoryAdapterViewHolder>() {
    class ScanHistoryAdapterViewHolder(view: View): RecyclerView.ViewHolder(view){
        val payee: AppCompatTextView = view.findViewById(R.id.payee)
        val name: AppCompatTextView = view.findViewById(R.id.name)
        val accountNumber: AppCompatTextView = view.findViewById(R.id.accountNumber)
        val date: AppCompatTextView = view.findViewById(R.id.date)
        val amount: AppCompatTextView = view.findViewById(R.id.amount)
        val delete: AppCompatImageButton = view.findViewById(R.id.delete)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ScanHistoryAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_item, parent, false)
        return ScanHistoryAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScanHistoryAdapterViewHolder, position: Int) {
        val item = list[position]
        holder.payee.text = item.payeeName
        holder.name.text = item.name
        holder.accountNumber.text = item.accountNumber
        holder.date.text = item.date
        holder.amount.text = item.amount
        holder.delete.setOnClickListener {
            list.removeAt(position)
            notifyItemRemoved(position)
            ScanHistoryDBUtils(context, 2, item).execute()
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}