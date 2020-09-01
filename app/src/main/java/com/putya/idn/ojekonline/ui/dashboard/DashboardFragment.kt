package com.putya.idn.ojekonline.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.putya.idn.ojekonline.R
import com.putya.idn.ojekonline.adapter.HistoryAdapter
import com.putya.idn.ojekonline.model.Booking
import com.putya.idn.ojekonline.utils.Constan
import kotlinx.android.synthetic.main.fragment_dashboard.*
import java.lang.IllegalStateException

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var auth: FirebaseAuth? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.fragment_dashboard, container, false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        auth?.uid?.let { bookingHistoryUser(it) }
    }

    private fun bookingHistoryUser(uid: String) {

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference(Constan.tb_booking)

        val data = ArrayList<Booking>()
        val query = myRef.orderByChild("uid").equalTo(uid)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (issue in snapshot.children) {
                    val dataFirebase = issue.getValue(Booking::class.java)
                    val booking = Booking()

                    booking.tanggal = dataFirebase?.tanggal
                    booking.uid = dataFirebase?.uid
                    booking.lokasiAwal = dataFirebase?.uid
                    booking.latAwal = dataFirebase?.latAwal
                    booking.longAwal = dataFirebase?.longAwal
                    booking.latTujuan = dataFirebase?.latTujuan
                    booking.longTujuan = dataFirebase?.longTujuan
                    booking.lokasiTujuan = dataFirebase?.lokasiTujuan
                    booking.jarak = dataFirebase?.jarak

                    data.add(booking)
                    showData(data)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    private fun showData(data: java.util.ArrayList<Booking>) {
        if (data != null) {
            try {
                rv1.adapter = HistoryAdapter(data)
                rv1.layoutManager = LinearLayoutManager(context)
            } catch (e: IllegalStateException) {

            }
        }
    }
}