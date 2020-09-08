package com.putya.idn.ojekonline.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.putya.idn.ojekonline.MainActivity
import com.putya.idn.ojekonline.R
import com.putya.idn.ojekonline.model.Users
import com.putya.idn.ojekonline.utils.Constan
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import kotlin.math.sign

class LoginActivity : AppCompatActivity() {
    var googleSignInClient: GoogleSignInClient? = null
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        signUpButtonGmail.onClick {
            signIn()
        }
        signUpLink.onClick {
            startActivity<SignUpActivity>()
        }
        loginButton.onClick {
            if (loginEmail.text.isNotEmpty() &&
                loginPassword.text.isNotEmpty()
            ) {
                authUserSignIn(
                    loginEmail.text.toString(),
                    loginPassword.text.toString()
                )
            }
        }
    }

    private fun authUserSignIn(email: String, pass: String) {
        var status: Boolean? = null

        auth?.signInWithEmailAndPassword(email, pass)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    startActivity<MainActivity>()
                    finish()
                } else {
                    toast("Login Failed")
                    Log.e("Error", "Message")
                }
            }
    }

    private fun signIn() {
        val gson = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gson)

        val signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, 4)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 4) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {

            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        var uid = String()
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)

        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth?.currentUser
                    checkDatabase(task.result?.user?.uid, account)
                    uid = user?.uid.toString()
                } else {

                }
            }
    }

    private fun checkDatabase(uid: String?, account: GoogleSignInAccount?) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference(Constan.tb_user)
        val query = myRef.orderByChild("uid").equalTo(auth?.uid)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    startActivity<MainActivity>()
                } else {
                    account?.displayName?.let {
                        account.email?.let { it1 ->
                            insertUser(it, it1, "", uid)
                        }
                    }
                }
            }
        })
    }

    private fun insertUser(name: String, email: String, hp: String, uid: String?): Boolean {

        val token =FirebaseInstanceId.getInstance().token

        val user = Users()
        user.email = email
        user.name = name
        user.hp = hp
        user.uid = uid
        user.latitude = "0.0"
        user.longitude = "0.0"
        user.token = token

        val database = FirebaseDatabase.getInstance()
        val key = database.reference.push().key
        val myRef = database.getReference(Constan.tb_user)

        myRef.child(key ?: "").setValue(user)

        startActivity<AutentikasiNomorTelefonActivity>(Constan.key to key)
        return true
    }
}