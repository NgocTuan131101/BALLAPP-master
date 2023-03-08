package com.example.ballapp.main.FragmentHome

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ballapp.R
import com.example.ballapp.Search.MainActivitySearch
import com.example.ballapp.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import java.io.File

class FragmentHome : Fragment() {

    private lateinit var homeBinding: FragmentHomeBinding
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid
    private val localFide = File.createTempFile("TempImage","jpg")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


}