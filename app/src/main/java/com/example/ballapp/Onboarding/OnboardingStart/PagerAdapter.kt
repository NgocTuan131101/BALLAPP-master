package com.example.ballapp.Onboarding.OnboardingStart

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.example.ballapp.R

class PagerAdapter(fragmentActivity: FragmentActivity,
    private val context : Context
    ) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            //goi ham newInstance o class BlankFragment1
            //cach 1
            0 -> OnBoardingStarOnee.newInstance(
                //set du lieu len
                "Bắt kèo nhanh chóng",
                "Lorem ipsum dolor sit amet consectetur adipiscing elit utaliquam, purus sit",
                R.drawable.onboarbinding1
            )

            // cach 2
            1 -> OnBoardingStarOnee.newInstance(
//                context.resources.getString(R.string.title_onboarding_2),
//                context.resources.getString(R.string.description_onboarding_2),
                "Tạo trận dễ dàng",
                "Lorem ipsum dolor sit amet consectetur adipiscing elit utaliquam, purus sit",
                R.drawable.onboarbinding2
            )
            else -> OnBoardingStarOnee.newInstance(
                "Giao lưu kết bạn",
                "Lorem ipsum dolor sit amet consectetur adipiscing elit utaliquam, purus sit",
                R.drawable.onboarbinding3
            )
        }
    }
}