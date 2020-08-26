package com.putya.idn.ojekonline.ui.notifications

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.putya.idn.ojekonline.R
import com.putya.idn.ojekonline.activity.LoginActivity
import com.putya.idn.ojekonline.model.Users
import com.putya.idn.ojekonline.utils.Constan
import kotlinx.android.synthetic.main.fragment_notifications.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.startActivity

class NotificationsFragment : Fragment() {
    var auth: FirebaseAuth? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference(Constan.tb_user)
        var query = myRef.orderByChild("uid").equalTo(auth?.uid)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (issue in snapshot?.children) {
                    val data = issue?.getValue(Users::class.java)
                    showProfile(data)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun showProfile(data: Users?) {
        tv_profile_email.text = data?.email
        tv_profile_name.text = data?.name
        tv_profile_hp.text = data?.hp

        profile_sign_out.onClick {
            auth?.signOut()
            startActivity<LoginActivity>()
        }
    }
}