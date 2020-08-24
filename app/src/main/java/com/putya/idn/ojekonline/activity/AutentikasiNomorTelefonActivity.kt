package com.putya.idn.ojekonline.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.FirebaseDatabase
import com.putya.idn.ojekonline.MainActivity
import com.putya.idn.ojekonline.R
import com.putya.idn.ojekonline.utils.Constan
import kotlinx.android.synthetic.main.activity_autentikasi_nomor_telefon.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class AutentikasiNomorTelefonActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_autentikasi_nomor_telefon)

        val key = intent.getStringExtra(Constan.key)
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference(Constan.tb_user)

        //update realtime database
        autentikasiSubmit.onClick {
            if (autentikasiNomerHp.text.toString().isNotEmpty()) {
                myRef.child(key).child("hp")
                    .setValue(autentikasiNomerHp.text.toString())
                startActivity<MainActivity>()
            } else toast("Tidak boleh kosong")
        }
    }
}