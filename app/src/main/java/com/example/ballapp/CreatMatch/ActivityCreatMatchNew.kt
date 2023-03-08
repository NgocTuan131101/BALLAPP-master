package com.example.ballapp.CreatMatch

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.TimePicker
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.example.ballapp.R
import com.example.ballapp.Utils.Animation
import com.example.ballapp.Utils.DatabaseConnection
import com.example.ballapp.Utils.MessageConnection
import com.example.ballapp.Utils.StorageConnection
import com.example.ballapp.databinding.ActivityMainCreatMatchBinding
import com.example.ballapp.databinding.CreateMatchNewDialogBinding
import com.example.ballapp.databinding.DialogSuccessBinding
import com.example.ballapp.databinding.NewLocationBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class ActivityCreatMatchNew : AppCompatActivity() {
    private lateinit var successBinding: DialogSuccessBinding
    private lateinit var newcreatMatch: ActivityMainCreatMatchBinding
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var newLocationBinding: NewLocationBinding
    private val creatMatchViewModel: CreatMatchViewModel by viewModels()
    private lateinit var createMatchNewDialogBinding: CreateMatchNewDialogBinding
    private var matchDate: String? = null
    private var teamName: String? = null
    private var deviceToken: String? = null
    private var teamImageUrl: String? = null
    private var phone: String? = null
    private var locationAddress: String? = null
    private var lat: Double? = null
    private var long: Double? = null
    private var geoHash: String? = null


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newcreatMatch = ActivityMainCreatMatchBinding.inflate(layoutInflater)
        setContentView(newcreatMatch.root)
        initEvents()
        initObserves()
        if(userUID != null){
            creatMatchViewModel.loadTeamInfo(userUID)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initObserves() {
        handleVariables()
        notificationObserve()
        saveUpComingObserve()
        teamInfoObserve()
        saveRequestObserve()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initEvents() {
        back()
        locationSelect()
        timeSelect()
//        PickImageGallery()
        ButtonNewLocation()
        disablePreDay()

    }
//    private fun PickImageGallery() {
//        newcreatMatch.teamIagme.setOnClickListener {
//            imageSelect()
//        }
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun ButtonNewLocation() {
        newcreatMatch.newRequest.setOnClickListener {
            if (matchDate == null) {
                val currentDate = LocalDate.now()
                val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH)
                matchDate = currentDate.format(dateFormatter)
            }
            val dialog = Dialog(this, R.style.MyTimePickerTheme)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            createMatchNewDialogBinding = CreateMatchNewDialogBinding.inflate(layoutInflater)
            dialog.setCancelable(false)
            dialog.setContentView(createMatchNewDialogBinding.root)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            createMatchNewDialogBinding.date.text = matchDate
            createMatchNewDialogBinding.time.text = newcreatMatch.time.text
            createMatchNewDialogBinding.yes.setOnClickListener {
                save()
                dialog.dismiss()
            }
            createMatchNewDialogBinding.no.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    private fun save() {
        with(newcreatMatch) {
            val location = textLocation.text.toString()
            val time = time.text.toString()
            val note = note.text.toString()
            val teamPeopleNumber = peopleNumber.text.toString()
            val matchID =
                DatabaseConnection.databaseReference.getReference("Request_Match").push().key
            if (userUID != null) {
                // Lưu yêu cầu phù hợp
                creatMatchViewModel.saveRequest(
                    userUID, matchID!!,
                    deviceToken!!, teamName!!, phone!!, matchDate!!, time, location, note,
                    teamPeopleNumber, teamImageUrl!!,
                    locationAddress!!, lat!!, long!!, geoHash!!
                )
                // lưu thông báo khớp vs yêu cầu
                creatMatchViewModel.notification(matchID, teamName!!, userUID)
                // lưu mới trận đấu đã tạo
                creatMatchViewModel.saveNewCreate(
                    userUID, matchID,
                    deviceToken!!, teamName!!, phone!!, matchDate!!, time, location, note,
                    teamPeopleNumber, teamImageUrl!!,
                    locationAddress!!, lat!!, long!!, geoHash!!
                )
                Log.e("Info", userUID)
                Log.e("Info", matchID)
                Log.e("Info", deviceToken!!)
                Log.e("Info", teamName!!)
                Log.e("Info", phone!!)
                Log.e("Info", matchDate!!)
                Log.e("Info", time)
                Log.e("Info", location)
                Log.e("Info", note)
                Log.e("Info", teamPeopleNumber)
                Log.e("Info", teamImageUrl!!)
                Log.e("Info", locationAddress!!)
                Log.e("Info", lat.toString())
                Log.e("Info", long.toString())
            }
        }
    }

    private fun saveRequestObserve() {
        creatMatchViewModel.saveRequest.observe(this) { result ->
            when (result) {
                is CreatMatchViewModel.SaveRequest.Loading -> {}
                is CreatMatchViewModel.SaveRequest.ResultOk -> {
                    val dialog = Dialog(this, R.style.MyTimePickerTheme)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    successBinding = DialogSuccessBinding.inflate(layoutInflater)
                    dialog.setContentView(successBinding.root)
                    dialog.setCancelable(false)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    successBinding.next.setOnClickListener {
                        dialog.dismiss()
                        finish()
                        Animation.animateSlideRight(this)
                    }
                    dialog.show()
                    val handler = Handler()
                    handler.postDelayed({
                        dialog.dismiss()
                        finish()
                        Animation.animateSlideRight(this)
                    }, 1000)
                }
                is CreatMatchViewModel.SaveRequest.ResultError -> {}
            }
        }
    }

    /*
            Hiện thị dialog chọn sân and onclick sân muốn đá -.-
     */
    private fun locationSelect() {
        newcreatMatch.layoutInformationLocatiom.setOnClickListener {
            val DialogLocation = BottomSheetDialog(this)
            newLocationBinding = NewLocationBinding.inflate(layoutInflater)
            DialogLocation.setContentView(newLocationBinding.root)
            newLocationBinding.khoaHoc.setOnClickListener {
                newcreatMatch.textLocation.text = newLocationBinding.khoaHoc.text
                DialogLocation.dismiss()
                Animation.animateFade(this)
            }
            newLocationBinding.monaco.setOnClickListener {
                newcreatMatch.textLocation.text = newLocationBinding.monaco.text
                DialogLocation.dismiss()
                Animation.animateFade(this)
            }
            newLocationBinding.lamHoang.setOnClickListener {
                newcreatMatch.textLocation.text = newLocationBinding.lamHoang.text
                DialogLocation.dismiss()
                Animation.animateFade(this)
            }
            newLocationBinding.anCuu.setOnClickListener {
                newcreatMatch.textLocation.text = newLocationBinding.anCuu.text
                DialogLocation.dismiss()
                Animation.animateFade(this)
            }
            newLocationBinding.luat.setOnClickListener {
                newcreatMatch.textLocation.text = newLocationBinding.luat.text
                DialogLocation.dismiss()
                Animation.animateFade(this)
            }
            newLocationBinding.uyenPhuong.setOnClickListener {
                newcreatMatch.textLocation.text = newLocationBinding.uyenPhuong.text
                DialogLocation.dismiss()
                Animation.animateFade(this)
            }
            newLocationBinding.yDuoc.setOnClickListener {
                newcreatMatch.textLocation.text = newLocationBinding.yDuoc.text
                DialogLocation.dismiss()
                Animation.animateFade(this)
            }
            newLocationBinding.xuanPhu.setOnClickListener {
                newcreatMatch.textLocation.text = newLocationBinding.xuanPhu.text
                DialogLocation.dismiss()
                Animation.animateFade(this)
            }
            DialogLocation.show()
        }
    }

//    private fun imageSelect() {
//        val intent = Intent(Intent.ACTION_PICK)
//        intent.type = "image/*"
//        startActivityForResult(intent, 0)
//
//    }

    private fun teamInfoObserve() {
        if (userUID != null) {
            creatMatchViewModel.loadTeamInfo(userUID)
        }

        creatMatchViewModel.loadTeamInfo.observe(this) { result ->
            with(newcreatMatch) {
//                progressBar.visibility = View.GONE
                timeLayout.visibility = View.VISIBLE
                scrollView.visibility = View.VISIBLE
            }
            when (result) {
                is CreatMatchViewModel.LoadTeamInfo.Loading -> {
//                    newcreatMatch.progressBar.visibility = View.VISIBLE
                }
                is CreatMatchViewModel.LoadTeamInfo.LoadImageOk -> {
                    Glide.with(newcreatMatch.teamIagme).load(result.teamImageUrl).centerCrop()
                        .into(newcreatMatch.teamIagme)
                }
                is CreatMatchViewModel.LoadTeamInfo.LoadInfoOk -> {
                    newcreatMatch.textLocation.text = result.teamLocation
                    newcreatMatch.peopleNumber.text = result.teamPeopleNumber
                }
                is CreatMatchViewModel.LoadTeamInfo.LoadImageError -> {}
                is CreatMatchViewModel.LoadTeamInfo.LoadInfoError -> {}
            }
        }

    }
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 0 && resultCode == RESULT_OK) {
//            newcreatMatch.teamIagme.setImageURI(data?.data)
//        }
//    }

    private fun timeSelect() {
        newcreatMatch.addTime.setOnClickListener {
            val mCurrentTime = Calendar.getInstance()
            val hour = mCurrentTime.get(Calendar.HOUR_OF_DAY)
            val minute = mCurrentTime.get(Calendar.MINUTE)

            TimePickerDialog(
                this,
                R.style.MyTimePickerTheme,
                object : TimePickerDialog.OnTimeSetListener {
                    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                        newcreatMatch.time.text =
                            String.format("%02d:%02d", hourOfDay, minute)
                    }
                },
                hour,
                minute,
                true
            ).show()
        }
    }

    private fun disablePreDay() {
        newcreatMatch.calendar.minDate = System.currentTimeMillis() - 1000
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleVariables() {
        newcreatMatch.calendar.setOnDateChangeListener { view, year, month, dayOfMonth ->
            var currentMonth = month.plus(1).toString()
            if (currentMonth.length == 1) {
                currentMonth = "0$currentMonth"
            }
            var dayOfMonthString = dayOfMonth.toString()
            if (dayOfMonthString.length == 1) {
                dayOfMonthString = "0$dayOfMonthString"
            }
            matchDate = "$dayOfMonthString/$currentMonth/$year"
            Log.e("DATE", matchDate.toString())
        }

        MessageConnection.firebaseMessaging.token.addOnCompleteListener(
            OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e(
                        ContentValues.TAG,
                        "Fetching FCM registration token failed",
                        task.exception
                    )
                    return@OnCompleteListener
                } else {
                    deviceToken = task.result
                }
            })
        FirebaseDatabase.getInstance().getReference("Users").child(userUID!!).get()
            .addOnSuccessListener {
                phone = it.child("userPhone").value.toString()
            }
        FirebaseDatabase.getInstance().getReference("Teams").child(userUID).get()
            .addOnSuccessListener {
                teamName = it.child("teamName").value.toString()
            }
        StorageConnection.storageReference.getReference("Teams")
            .child(userUID).downloadUrl.addOnSuccessListener {
                teamImageUrl = it.toString()
            }
            .addOnFailureListener {
                Log.e("Error", it.toString())
            }
    }

    private fun back() {
        newcreatMatch.back.setOnClickListener {
            finish()
            Animation.animateSlideRight(this)
        }
    }

    private fun saveUpComingObserve() {
        creatMatchViewModel.saveNewCreate.observe(this) { result ->
            when (result) {
                is CreatMatchViewModel.NewCreateResult.ResultOk -> {}
                is CreatMatchViewModel.NewCreateResult.ResultError -> {}
            }
        }
    }

    private fun notificationObserve() {
        creatMatchViewModel.notification.observe(this) { result ->
            when (result) {
                is CreatMatchViewModel.NotificationResult.ResultOk -> {}
                is CreatMatchViewModel.NotificationResult.ResultError -> {}
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Animation.animateSlideRight(this)
    }
}