package com.putya.idn.ojekonline.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.putya.idn.ojekonline.R
import com.putya.idn.ojekonline.model.Booking
import kotlinx.android.synthetic.main.history_item.view.*

class HistoryAdapter(
    private val mValues: List<Booking>
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryAdapter.ViewHolder, position: Int) {
        val item = mValues[position]

        holder.mAwal.text = item.lokasiAwal
        holder.mTanggal.text = item.tanggal
        holder.mTujuan.text = item.lokasiTujuan
    }

    override fun getItemCount(): Int = mValues.size

    //initialisation
    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        var mAwal: TextView = mView.item_awal
        var mTujuan: TextView = mView.item_tujuan
        var mTanggal: TextView = mView.item_tanggal
    }
}