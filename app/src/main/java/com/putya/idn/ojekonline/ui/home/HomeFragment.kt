package com.putya.idn.ojekonline.ui.home

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.putya.idn.ojekonline.R
import com.putya.idn.ojekonline.utils.GPSTrack
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*

class HomeFragment : Fragment(), OnMapReadyCallback {
    private var map: GoogleMap? = null
    var tanggal: String? = null
    var latAwal: Double? = null
    var longAwal: Double? = null
    var latAkhir: Double? = null
    var longAkhir: Double? = null
    var jarak: String? = null
    var dialog: Dialog? = null
    var key: String? = null
    private var auth: FirebaseAuth? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.fragment_home, container, false
        )
    }

    //showing maps to fragment
    override fun onMapReady(p0: GoogleMap?) {
        map = p0
        map?.uiSettings?.isMyLocationButtonEnabled = false
        map?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(-6.3088652, 106.682188), 12f
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initialisation from map view
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { this }
    }

    //showing user location base on the gps device
    private fun showGPS() {
        val gps = context?.let { GPSTrack(it) }

        if (gps?.canGetLocation()!!) {
            latAwal = gps.latitude
            longAwal = gps.longitude

            showMarker(latAwal ?: 0.0, longAwal ?: 0.0, "My Location")

            val name = showName(latAwal ?: 0.0, longAwal ?: 0.0)
            home_awal.text = name

        } else gps.showSettingGPS()
    }

    //GEO CODER
    //translate coordinate to location name
    private fun showName(lat: Double, long: Double): String? {
        var name = ""
        var geocoder = Geocoder(context, Locale.getDefault())

        try {
            val addresses = geocoder.getFromLocation(lat, long, 1)
            if (addresses.size > 0) {
                val fetchedAddress = addresses.get(0)
                val strAddress = StringBuilder()

                for (i in 0..fetchedAddress.maxAddressLineIndex) {
                    name = strAddress.append(fetchedAddress.getAddressLine(i)).append("").toString()
                }
            }
        } catch (e: Exception) {

        }
        return name
    }

    private fun showMainMarker(lat: Double, long: Double, msg: String) {
        val res = context?.resources
        val marker1 = BitmapFactory.decodeResource(res, R.drawable.placeholder)
        val smallmarker = Bitmap.createScaledBitmap(marker1, 80, 120, false)

        val coordinate = LatLng(lat, long)

        map?.addMarker(
            MarkerOptions().position(coordinate).title(msg)
                .icon(BitmapDescriptorFactory.fromBitmap(smallmarker))
        )

        //manage camera zoom
        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 16f))

        //marker position always in center
        map?.moveCamera(CameraUpdateFactory.newLatLng(coordinate))
    }

    private fun showMarker(lat: Double, long: Double, msg: String) {
        val coordinat = LatLng(lat, long)

        map?.addMarker(
            MarkerOptions()
                .position
                    (coordinat)
                .title(msg)
        )
        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinat, 16f))

        map?.moveCamera(CameraUpdateFactory.newLatLng(coordinat))
    }

    override fun onResume() {
        key?.let { bookingHistoryUse(it) }
        mapView?.onResume()
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    private fun bookingHistoryUse(it: String): Any {
        TODO("Not yet implemented")
    }
}