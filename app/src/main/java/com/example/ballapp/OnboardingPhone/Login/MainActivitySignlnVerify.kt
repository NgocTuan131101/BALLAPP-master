package com.example.ballapp.OnboardingPhone.Login

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import java.util.concurrent.TimeUnit
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import com.example.ballapp.Account.Avatar.MainActivityAvatar
import com.example.ballapp.Account.Name.MainActivityName
import com.example.ballapp.R
import com.example.ballapp.Utils.Animation
import com.example.ballapp.Utils.AuthConnection.auth
import com.example.ballapp.Utils.DatabaseConnection
import com.example.ballapp.databinding.ActivityMainSignlnVerifyBinding
import com.example.ballapp.databinding.LoadingDialogBinding
import com.example.ballapp.main.FragmentHome.FragmentHome
import com.example.ballapp.main.MainActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*

class MainActivitySignlnVerify : AppCompatActivity() {
    private lateinit var MainActivitySignln: ActivityMainSignlnVerifyBinding
    private var phomeNumber: String? = null
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var login: LoadingDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivitySignln = ActivityMainSignlnVerifyBinding.inflate(layoutInflater)
        setContentView(MainActivitySignln.root)
        initSignlnVerify()
    }

    private fun initSignlnVerify() {
        Callbacks()
        bindingPhome()
        resendcode()
        back()
        tryOtherPhoneNumber()
        SignlnVerify()
    }

    // kiểm tra mã OTP
    private fun SignlnVerify() {
        val dialog = Dialog(this, R.style.MyAlertDialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        login = LoadingDialogBinding.inflate(layoutInflater)
        dialog.setContentView(login.root)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val storedVerifycationId = intent.getStringExtra("storedVerificationId")

        MainActivitySignln.verify.setOnClickListener {
            val OTP = MainActivitySignln.pin.text.toString().trim()
            if (OTP.isNotEmpty()) {
                dialog.show()
                val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    storedVerifycationId.toString(), OTP
                )
                auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Logged in successfully", Toast.LENGTH_SHORT).show()
                        val UserUID = FirebaseAuth.getInstance().currentUser?.uid
                        DatabaseConnection.databaseReference.getReference("Teams").child(UserUID!!)
                            .get()
                            .addOnSuccessListener {
                                if (it.exists()) {
                                    dialog.dismiss()
                                    startActivity(Intent(this, MainActivity::class.java))
                                    Animation.animateSlideLeft(this)
                                } else {
                                    dialog.dismiss()
                                    val intent = Intent(applicationContext, MainActivityName::class.java)
                                    intent.putExtra("phoneNumber", phomeNumber)
                                    startActivity(intent)
                                    finishAffinity()
                                    Animation.animateSlideLeft(this)
                                }
                            }
                    } else {
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            dialog.dismiss()
                            Toast.makeText(this, "Invalid OTP code", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                dialog.dismiss()
                Toast.makeText(this, "Please enter the OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun tryOtherPhoneNumber() {
        MainActivitySignln.tryOtherNumber.setOnClickListener {
            finish()
            Animation.animateSlideRight(this)
        }
    }

    private fun resendcode() {
        MainActivitySignln.resend.setOnClickListener {
            phomeNumber?.let {
                resend(it)
            }
        }
    }

    private fun resend(phomeNumber: String) {
        val option = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phomeNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(option)
    }

    //bingding MainActivityLogin -> MainActivitySignlnVerify
    private fun bindingPhome() {
        val userPhoneNumber = intent.getStringExtra("userPhoneNumber")
        MainActivitySignln.userPhoneNumber.text = userPhoneNumber
        phomeNumber = userPhoneNumber?.trim()

    }

    private fun back() {
        MainActivitySignln.back.setOnClickListener {
            finish()
            Animation.animateSlideRight(this)
        }
    }

    private fun Callbacks() {
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {

            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Toast.makeText(applicationContext, "Error has an error", Toast.LENGTH_SHORT).show()

            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                Toast.makeText(applicationContext, "OTP code sent again,", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Animation.animateSlideRight(this)
    }
}