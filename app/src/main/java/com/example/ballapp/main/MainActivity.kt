package com.example.ballapp.main


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.media.audiofx.Equalizer.Settings
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.ballapp.Account.Main
import com.example.ballapp.CreatMatch.ActivityCreatMatchNew
import com.example.ballapp.R
import com.example.ballapp.Utils.Animation
import com.example.ballapp.Utils.Model.currentAddress
import com.example.ballapp.Utils.Model.currentLat
import com.example.ballapp.Utils.Model.currentLong
import com.example.ballapp.Utils.StorageConnection
import com.example.ballapp.databinding.ActivityMainBinding
import com.example.ballapp.databinding.DailogAccessLocationBinding
import com.example.ballapp.databinding.DialogSuccessBinding
import com.example.ballapp.databinding.NewLocationBinding
import com.example.ballapp.main.FragmentHome.FragmentHome
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.collection.LLRBNode.Color
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var mNavController: NavController
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private val mainActivityViewModel: MainActivityViewModel by viewModels()
    private val permissionId = 0
    private lateinit var mdailogAccessLocationBinding: DailogAccessLocationBinding
    private val userID = FirebaseAuth.getInstance().currentUser?.uid
    private var teamAvatarUrl: String? = null
    private var teamName: String? = null


    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        initEvents()
        updateUserObserve()
    }

    private fun initEvents() {
        createnewMatch()
        handleVariables()
        navBinding()
        locationRequest()
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun locationRequest() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationProviderClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val list: List<Address> = geocoder.getFromLocation(
                            location.latitude,
                            location.longitude,
                            1
                        ) as List<Address>
                        currentLat = list[0].latitude
                        currentLong = list[0].longitude
                        currentAddress = list[0].getAddressLine(0)
                        Log.e("Address", currentAddress.toString())
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_SHORT).show()
                val intent = Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        val dialog = Dialog(this, R.style.MyTimePickerTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mdailogAccessLocationBinding = DailogAccessLocationBinding.inflate(layoutInflater)
        dialog.setContentView(mdailogAccessLocationBinding.root)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        mdailogAccessLocationBinding.Yes.setOnClickListener {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ),
                permissionId
            )
            dialog.dismiss()
        }
        mdailogAccessLocationBinding.No.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun navBinding() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.activity_main_container) as NavHostFragment
        mNavController = navHostFragment.navController
        setupWithNavController(mainBinding.bottomNavigation, mNavController)
        mainBinding.bottomNavigation.menu.getItem(2).isEnabled = false
        val badge = mainBinding.bottomNavigation.getOrCreateBadge(R.id.chatFragment)
        badge.isVisible = false
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                locationRequest()
            }
        }
    }

    private fun handleVariables() {
        if (userID == null) {
            return
        }
        StorageConnection.storageReference.getReference("Users").child(userID!!).downloadUrl
            .addOnSuccessListener {
                val userAvatarUrl = it.toString()
                mainActivityViewModel.updateUser(userID, userAvatarUrl)
            }
            .addOnFailureListener {
                Log.e("Error", it.toString())
            }
        StorageConnection.storageReference.getReference("Teams").child(userID).downloadUrl
            .addOnSuccessListener {
                teamAvatarUrl = it.toString()
                mainActivityViewModel.updateTeams(userID, teamAvatarUrl!!)
            }
            .addOnFailureListener {
                Log.e("Error", it.toString())
            }
        FirebaseDatabase.getInstance().getReference("Teams").child(userID!!).get()
            .addOnSuccessListener {
                teamName = it.child("teamName").value.toString()
            }
    }

    private fun updateUserObserve() {
        mainActivityViewModel.updateUsers.observe(this) { result ->
            when (result) {
                is MainActivityViewModel.UpdateUsers.ResultOk -> {}
                is MainActivityViewModel.UpdateUsers.ResultError -> {}
                else -> {}
            }
        }
    }


    private fun createnewMatch() {
        mainBinding.fab.setOnClickListener {
            startActivity(Intent(this, ActivityCreatMatchNew::class.java))
            Animation.animateSlideLeft(this)
        }
    }

}