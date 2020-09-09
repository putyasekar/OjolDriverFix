package com.putya.idn.ojekonline.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.putya.idn.ojekonline.model.Users

//proses pengambilan token dari firebase
object FirebaseHelper {

    private var auth: FirebaseAuth? = null

    fun insertUser(
        name: String,
        email: String,
        hp: String,
        uid: String

    ): Boolean {
        val token = FirebaseInstanceId.getInstance().token
        var user = Users()
        val uid = auth?.currentUser?.uid

        user.email = email
        user.name = name
        user.hp = hp
        user.uid = uid
        user.latitude = "0.0"
        user.longitude = "0.0"
        user.token = token

        val database = FirebaseDatabase.getInstance()
        var key = database.reference.push().key
        var myRef = database.getReference(Constan.tb_user)

        key?.let { myRef.child(it).setValue(user) }

        return true
    }
}