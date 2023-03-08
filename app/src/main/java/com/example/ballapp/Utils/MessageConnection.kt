package com.example.ballapp.Utils

import android.annotation.SuppressLint
import com.google.firebase.messaging.FirebaseMessaging

object MessageConnection {
    @SuppressLint("StaticFieldLeak")
    val firebaseMessaging = FirebaseMessaging.getInstance()
}