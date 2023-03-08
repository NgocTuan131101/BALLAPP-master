package com.example.ballapp.OnboardingPhone.Login

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.ballapp.R
import com.example.ballapp.Utils.Animation
import com.example.ballapp.Utils.AuthConnection.auth
import com.example.ballapp.databinding.ActivityMainLoginBinding
import com.example.ballapp.databinding.LoadingDialogBinding
import com.example.ballapp.main.MainActivity
import com.example.ballball.utils.ClearableEditText.makeClearableEditText
import com.example.ballball.utils.ClearableEditText.onRightDrawableClicked
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit
class MainActivityLogin : AppCompatActivity() {

    private lateinit var ActivityLoginBinding :ActivityMainLoginBinding
    private lateinit var LoadingDialogBinding:LoadingDialogBinding
    lateinit var resendToken :PhoneAuthProvider.ForceResendingToken
    //'ma thong bao co the duoc su dung de buoc gui lai mã xac minh SMS(ForceResendingToken)
    private lateinit var callbacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks
    //Cc cuoc goi lai da dang ky cho cac su kien xac thuc dien thoai khac nhau. (OnVerificationStateChangedCallbacks)
    private var userNumber: String? = null
    lateinit var storedVerificationId : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityLoginBinding = ActivityMainLoginBinding.inflate(layoutInflater)
        setContentView(ActivityLoginBinding.root)
        init()
    }

    private fun init() {
        makeclearbleEditText()
        login()
    }

    private fun login() {
        val dialog = Dialog(this,R.style.MyAlertDialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        LoadingDialogBinding = com.example.ballapp.databinding.LoadingDialogBinding.inflate(layoutInflater)
        dialog.setContentView(LoadingDialogBinding.root)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        ActivityLoginBinding.send.setOnClickListener{
            phonecheck()
            dialog.show()
        }

        callbacks = object:PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                startActivity(Intent(applicationContext,MainActivity::class.java))
                Animation.animateSlideLeft(this@MainActivityLogin)
                finish()
            }
            // Hiển thị thông báo lỗi
            override fun onVerificationFailed(p0: FirebaseException) {
                Toast.makeText(applicationContext,"Error has an error",Toast.LENGTH_SHORT).show()
                finish()
            }
            // intent dữ liêu qua Activity MainActivitySignlnVerify
            override fun onCodeSent(
                VerificationID: String,
                token: PhoneAuthProvider.ForceResendingToken) {
                Log.d("TAG","onCodeSent :$VerificationID")
                storedVerificationId = VerificationID
                resendToken = token
                val intent = Intent(applicationContext,MainActivitySignlnVerify::class.java)
                intent.putExtra("storedVerificationId",storedVerificationId)
                intent.putExtra("userPhoneNumber",userNumber)
                dialog.dismiss()
                startActivity(intent)
                Animation.animateSlideLeft(this@MainActivityLogin)
            }

        }
    }

    private fun phonecheck() {
        val areaCode = ActivityLoginBinding.phoneCode.text.toString().trim()
        var phoneNumber = ActivityLoginBinding.phoneNumber.text.toString().toInt().toString().trim()
        userNumber = "$areaCode $phoneNumber"
        // kiểu tra PhoneNumber
        if(phoneNumber.isEmpty()){
            Toast.makeText(this,"Please enter the phone number",Toast.LENGTH_SHORT).show()
        }else{
            phoneNumber = areaCode + phoneNumber
            sendVertification(phoneNumber)
        }
    }

    private fun sendVertification(phoneNumber: String) {
        val option =PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L,TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(option)
    }

    // thêm quyền hủy Drawab
    private fun makeclearbleEditText() {
        addRightCancelDrawable(ActivityLoginBinding.phoneNumber)
        ActivityLoginBinding.phoneNumber.onRightDrawableClicked {
            it.text.clear()
        }
        // tạo mới editText và có thể xóa được
        ActivityLoginBinding.phoneNumber.makeClearableEditText(null,null)
    }

    private fun addRightCancelDrawable(phoneNumber: TextInputEditText) {
        val cancel = ContextCompat.getDrawable(this,R.drawable.ic_baseline_clear_24)
        cancel?.setBounds(0,0,cancel.intrinsicWidth,cancel.intrinsicHeight)
        phoneNumber.setCompoundDrawables(null,null,cancel,null)
    }

}