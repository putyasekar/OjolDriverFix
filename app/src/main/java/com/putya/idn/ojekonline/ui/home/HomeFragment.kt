package com.putya.idn.ojekonline.ui.home

import android.annotation.SuppressLint
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.putya.idn.ojekonline.R
import com.putya.idn.ojekonline.activity.WaitingDriverActivity
import com.putya.idn.ojekonline.model.Booking
import com.putya.idn.ojekonline.model.ResultRoute
import com.putya.idn.ojekonline.model.RoutesItem
import com.putya.idn.ojekonline.network.NetworkModule
import com.putya.idn.ojekonline.network.RequestNotification
import com.putya.idn.ojekonline.utils.ChangeFormat
import com.putya.idn.ojekonline.utils.Constan
import com.putya.idn.ojekonline.utils.GPSTrack
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_home.*
import okhttp3.ResponseBody
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class HomeFragment : Fragment(), OnMapReadyCallback {
    var map: GoogleMap? = null
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
        val view = inflater.inflate(
            R.layout.fragment_home, container, false
        )

        auth = FirebaseAuth.getInstance()
        return view
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
        mapView.getMapAsync(this)

        showPermission()
        visibleView(false)

        key?.let { bookingHistoryUse(it) }

        home_awal?.onClick {
            takeLocation(1)
        }

        home_tujuan.onClick {
            takeLocation(2)
        }

        home_bottom_next.onClick {
            if (home_awal.text.isNotEmpty()
                && home_tujuan.text.isNotEmpty()
            ) {
                insertServer()
            } else {
                toast("Tidak boleh kosong").show()
                view.let { Snackbar.make(it, "Tidak Boleh Kosong", Snackbar.LENGTH_SHORT).show() }
            }
        }

        showPermission()
    }

    private fun showPermission() {
        showGPS()

        if (activity?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED) {
            if (activity?.let {
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        it, android.Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                }!!) {
                showGPS()
            } else {
                requestPermissions(
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ), 1
                )
            }
        }
    }

    //insert data booking to realtime database
    private fun insertServer() {
        val currentTime = Calendar.getInstance().time
        tanggal = currentTime.toString()
        insertRequest(
            currentTime.toString(),
            auth?.uid.toString(),
            home_awal.text.toString(),
            latAwal, longAwal, home_tujuan.text.toString(),
            latAkhir, longAkhir, home_price.text.toString(),
            jarak.toString()
        )
    }

    private fun insertRequest(
        tanggal: String,
        uid: String,
        lokasiAwal: String,
        latAwal: Double?,
        longAwal: Double?,
        lokasiTujuan: String,
        latTujuan: Double?,
        longTujuan: Double?,
        harga: String,
        jarak: String
    ): Boolean {
        val booking = Booking()
        booking.tanggal = tanggal
        booking.uid = uid
        booking.lokasiAwal = lokasiAwal
        booking.latAwal = latAwal
        booking.longAwal = longAwal
        booking.lokasiTujuan = lokasiTujuan
        booking.latTujuan = latTujuan
        booking.longTujuan = longTujuan
        booking.jarak = jarak
        booking.harga = harga
        booking.status = 1
        booking.driver = ""

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference(Constan.tb_booking)
        key = database.reference.push().key
        val k = key

        pushNotif(booking)
        k?.let { bookingHistoryUse(it) }

        myRef.child(key ?: "").setValue(booking) //insert to database

        return true
    }

    private fun pushNotif(booking: Booking) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Driver")
        myRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                for (issue in snapshot.children) {
                    val token = issue.child("Token").getValue(String::class.java)

                    println(token.toString())
                    val request = RequestNotification()
                    request.token = token
                    request.sendNotificationModel = booking

                    NetworkModule.getServiceFcm().sendChatNotification(request)
                        .enqueue(object : Callback<ResponseBody> {

                            override fun onResponse(
                                call: Call<ResponseBody>,
                                response: Response<ResponseBody>
                            ) {
                                response.body()
                                Log.d("Response Server", response.message())
                            }

                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                Log.d("Network Failed: ", t.message.toString())
                            }

                        })
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
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

    @SuppressLint("CheckResult")
    private fun route() {
        val origin = latAwal.toString() + "," + longAwal.toString()
        val dest = latAkhir.toString() + "," + longAkhir.toString()

        NetworkModule.getService().actionRoute(origin, dest, Constan.API_KEY)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ t: ResultRoute? ->
                showData(t?.routes)
            }, {})
    }

    //showing route price
    private fun showData(routes: List<RoutesItem?>?) {
        visibleView(true)

        if (routes != null) {
            val point = routes[0]?.overviewPolyline?.points
            jarak = routes[0]?.legs?.get(0)?.distance?.text
            val jarakValue = routes[0]?.legs?.get(0)?.distance?.value
            val waktu = routes[0]?.legs?.get(0)?.duration?.text

            home_waktu_distance.text = waktu + "(" + jarak + ")"

            val priceX = jarakValue?.toDouble()?.let { Math.round(it) }
            val price = priceX?.div(1000.0)?.times(2000.0)
            val price2 = ChangeFormat.toRupiahFormat2(price.toString())

            home_price.text = "Rp. " + price2
        } else {
            alert {
                message = "data route null"
            }.show()
        }
    }

    private fun visibleView(status: Boolean) {
        if (status) {
            home_bottom?.visibility = View.VISIBLE
            home_bottom_next?.visibility = View.VISIBLE
        } else {
            home_bottom?.visibility = View.GONE
            home_bottom_next?.visibility = View.GONE
        }
    }

    fun takeLocation(status: Int) {
        try {
            context?.applicationContext?.let {
                Places.initialize(
                    it, Constan.API_KEY
                )
            }
            val fields = arrayListOf(
                Place.Field.ID, Place.Field.NAME,
                Place.Field.LAT_LNG, Place.Field.ADDRESS
            )

            val intent = context?.applicationContext?.let {
                Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(it)
            }
            startActivityForResult(intent, status)
        } catch (e: GooglePlayServicesAvailabilityException) {

        } catch (e: GooglePlayServicesNotAvailableException) {

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                val place = data?.let { Autocomplete.getPlaceFromIntent(it) }

                latAwal = place?.latLng?.latitude
                longAwal = place?.latLng?.longitude

                home_awal.text = place?.address.toString()
                showMainMarker(
                    latAwal ?: 0.0, longAwal ?: 0.0,
                    place?.address.toString()
                )
                Log.i("Location", "Place: " + place?.name)
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = data?.let { Autocomplete.getStatusFromIntent(it) }

                Log.i("Location", status?.statusMessage)
            } else if (resultCode == RESULT_CANCELED) {

            }
        } else {
            if (resultCode == RESULT_OK) {
                val place = data?.let { Autocomplete.getPlaceFromIntent(it) }

                latAkhir = place?.latLng?.latitude
                longAkhir = place?.latLng?.longitude

                showMarker(
                    latAkhir ?: 0.0, longAwal ?: 0.0,
                    place?.address.toString()
                )

                route()

                Log.i("Location", "Place" + place?.name)
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = data?.let { Autocomplete.getStatusFromIntent(it) }
                Log.i("Location", status?.statusMessage)
            } else if (resultCode == RESULT_CANCELED) {

            }
        }
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

    private fun bookingHistoryUse(key: String) {
        showDialog(true)
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference(Constan.tb_booking)

        myRef.child(key).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val booking = snapshot.getValue(Booking::class.java)
                if (booking?.driver != "") {
                    startActivity<WaitingDriverActivity>(Constan.key to key)
                    showDialog(false)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun showDialog(status: Boolean) {
        dialog = activity?.let { Dialog(it) }
        dialog?.setContentView(R.layout.dialog_waiting_driver)

        if (status) {
            dialog?.show()
        } else dialog?.dismiss()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            showGPS()
        }
    }
}