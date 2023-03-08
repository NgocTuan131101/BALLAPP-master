package com.example.ballapp.Onboarding.OnboardingStart

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ballapp.OnboardingPhone.Login.MainActivityLogin
import com.example.ballapp.Transformer.DepthPageTransformer
import com.example.ballapp.Utils.Animation
import com.example.ballapp.databinding.ActivityMainOnBoardingStartBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivityOnBoardingStart : AppCompatActivity() {
    private lateinit var MainActivityOnBoardingStart: ActivityMainOnBoardingStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivityOnBoardingStart = ActivityMainOnBoardingStartBinding.inflate(layoutInflater)
        setContentView(MainActivityOnBoardingStart.root)

        MainActivityOnBoardingStart.ViewPager2.setPageTransformer(DepthPageTransformer())
            initViewPager()
            initNext()
    }
    private fun initNext() {
        next()
        skip()
    }

    // next gd trong OnBoardingStart
    private fun skip() {
        MainActivityOnBoardingStart.skip.setOnClickListener{
            startActivity(Intent(this,MainActivityLogin::class.java))
            finish()
        }
    }
    private fun next() {
        MainActivityOnBoardingStart.next.setOnClickListener{

            if(getItem() > MainActivityOnBoardingStart.ViewPager2.childCount){
                startActivity(Intent(this,MainActivityLogin::class.java))
                finish()
                Animation.animateSlideLeft(this)
            }else{
                MainActivityOnBoardingStart.ViewPager2.setCurrentItem(getItem() + 1,true)
            }
        }
    }
    private fun getItem(): Int {
        return MainActivityOnBoardingStart.ViewPager2.currentItem
    }
    private fun initViewPager() {
        MainActivityOnBoardingStart.ViewPager2.adapter = PagerAdapter(this, this)
        MainActivityOnBoardingStart.Tablayout.let { tabLayout ->
            TabLayoutMediator(tabLayout,
                MainActivityOnBoardingStart.ViewPager2) { _, _ -> }.attach()
        }
    }
}