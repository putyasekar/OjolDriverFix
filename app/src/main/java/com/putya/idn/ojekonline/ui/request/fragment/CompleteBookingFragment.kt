package com.putya.idn.ojekonline.ui.request.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.putya.idn.ojekonline.R
import com.putya.idn.ojekonline.model.Booking
import com.putya.idn.ojekonline.ui.request.adapter.BookingAdapter
import com.putya.idn.ojekonline.ui.request.detail.DetailRequest
import com.putya.idn.ojekonline.utils.Constan
import kotlinx.android.synthetic.main.fragment_item_list.*
import org.jetbrains.anko.support.v4.startActivity
import java.lang.IllegalStateException

class CompleteBookingFragment : Fragment() {
    private var columnnClout = 1
    private var listener: RequestBookingFragment.OnListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)

        exPlore()
        return view
    }

    private fun exPlore() {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference(Constan.tb_booking)
        val data = ArrayList<Booking>()
        val query = myRef.orderByChild("Driver").equalTo("")
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (issue in snapshot.children) {
                    val dataFirebase = issue.getValue(Booking::class.java)

                    if (dataFirebase?.status == 4) {
                        val booking = Booking()

                        booking.tanggal = dataFirebase?.tanggal
                        booking.uid = dataFirebase?.uid
                        booking.lokasiAwal = dataFirebase?.lokasiAwal
                        booking.latAwal = dataFirebase?.latAwal
                        booking.longAwal = dataFirebase?.longAwal
                        booking.latTujuan = dataFirebase?.latTujuan
                        booking.lokasiTujuan = dataFirebase?.lokasiTujuan
                        booking.jarak = dataFirebase?.jarak
                        booking.harga = dataFirebase?.harga
                        booking.status = dataFirebase?.status
                        booking.longTujuan = dataFirebase?.longTujuan

                        data.add(booking)
                        showData(data)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun showData(data: ArrayList<Booking>) {
        try {
            list.adapter = BookingAdapter(
                data,
                object : RequestBookingFragment.OnListFragmentInteractionListener {

                    override fun onListFragmentInteraction(item: Booking?) {
                        startActivity<DetailRequest>(
                            Constan.booking to item!!,
                            Constan.status to 1
                        )
                    }
                })
            list.layoutManager = LinearLayoutManager(context)
        } catch (e: IllegalStateException) {

        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(item: Booking?)
    }

}