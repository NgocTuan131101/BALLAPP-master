package com.example.ballapp.Account.Name

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.ballapp.Account.Avatar.MainActivityAvatar
import com.example.ballapp.R
import com.example.ballapp.Utils.Animation
import com.example.ballapp.databinding.ActivityMainName2Binding
import com.example.ballball.utils.ClearableEditText.makeClearableEditText
import com.example.ballball.utils.ClearableEditText.onRightDrawableClicked
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivityName : AppCompatActivity() {
    private val nameViewModel: NameViewModel by viewModels()
    private lateinit var activityMainNameBinding : ActivityMainName2Binding
    private var phoneNumber : String? = null
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       activityMainNameBinding = ActivityMainName2Binding.inflate(layoutInflater)
        setContentView(activityMainNameBinding.root)
        initEvents()
        initObserve()
    }



    private fun initEvents() {
        next()
        EditTextClearble()
    }

    private fun initObserve() {
        nameViewModel.saveUsers.observe(this){result ->
            when(result ){
                is NameViewModel.SaveUsers.ResultOk ->{
                    startActivity(Intent(this,MainActivityAvatar::class.java))
                    Animation.animateSlideLeft(this)
                }
                is NameViewModel.SaveUsers.ResultError ->{
                    Toast.makeText(this,result.errorMessage,Toast.LENGTH_SHORT)
                }
            }
        }
    }
    private fun EditTextClearble() {
        addRightCancelDrawable(activityMainNameBinding.name)
        activityMainNameBinding.name.onRightDrawableClicked {
            it.text.clear()
        }
        activityMainNameBinding.name.makeClearableEditText(null,null)
    }

    private fun addRightCancelDrawable(name: TextInputEditText) {
        val cancel = ContextCompat.getDrawable(this,R.drawable.ic_baseline_clear_24)
        cancel?.setBounds(0,0,cancel.intrinsicWidth,cancel.intrinsicHeight)
        name.setCompoundDrawables(null,null,cancel,null)
    }

    private fun next() {
        phoneNumber = intent.getStringExtra("phoneNumber")
        activityMainNameBinding.next.setOnClickListener{
            if(activityMainNameBinding.name.text.isNullOrEmpty()){
                Toast.makeText(this,"Please enter your name",Toast.LENGTH_SHORT).show()
            }else {
                phoneNumber?.let { phoneNumber ->
                    if (userUID != null){
                        nameViewModel.saveUsers(
                            userUid = userUID,
                            userName = activityMainNameBinding.name.text.toString(),
                            userPhone = phoneNumber
                        )
                    }
                }
            }
        }
    }
}