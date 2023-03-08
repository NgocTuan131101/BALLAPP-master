package com.example.ballapp.Onboarding.OnboardingStart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.ballapp.R
import com.example.ballapp.databinding.FragmentOnBoardingStarOneeBinding

class OnBoardingStarOnee : Fragment() {
    private lateinit var Onboarbinding: FragmentOnBoardingStarOneeBinding
    private var title : String? = null
    private var description : String? = null
    private var imageResource : Int = 0
    private lateinit var tvTitle: TextView
    private lateinit var tvDescription : TextView
    private lateinit var image: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            title = requireArguments().getString(ARG_PARAM1)
            description = requireArguments().getString(ARG_PARAM2)
            imageResource = requireArguments().getInt(ARG_PARAM3)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // cach khoi tao binding trong fragment
        Onboarbinding = FragmentOnBoardingStarOneeBinding.inflate(inflater, container, false)

        //bind du lieu len fragment
        tvTitle = Onboarbinding.textOboarTitle
        tvDescription = Onboarbinding.textOboarDescription
        image = Onboarbinding.image
        tvTitle.text = title
        tvDescription.text = description
//        image.setImageResource(imageResource)
//        return Onboarbinding.root
        //image.setImageResource(imageResource)
        Glide.with(this)
            .load("https://s3-alpha-sig.figma.com/img/0382/7ec0/5f1701e1e21ec96168e9b6f55cac71b5?Expires=1674432000&Signature=HBZpLHv9GSfoAV5dwT3ig42pf4c0jznVPiqual8vwL6ZqSf2Du6fnb7XMjHfpr-FF7q9ElWz1XTQPxb~0MW1sxLncc0I0E4AhxhUsHkG5eEB5m6KDpX9MpdNDH95mnSgonNG6WyT5kz7tse5Rc3J6kT-73XVbpEsMqFp-4Urk0uQui9SWfYwhsZmRsra5nppS~CKS49OkvMRJVeBjNebb~VeAKEnlAtcvPXNRiDDODSgFh~EeWpceuuokcLKl5-72o6haVreMww~2YJsRtTvgcxYjfg0utRkPORUtHqY34N1zpA4NA6GrqNTAmpqWNqVMwXt4EyetOGZUFu1q4a~bw__&Key-Pair-Id=APKAQ4GOSFWCVNEHN3O4")
            .fitCenter()
            .skipMemoryCache(true)
//            .diskCacheStrategy(DiskCacheStrategy.NONE)
//            .placeholder(R.drawable.onboarbinding2)
            .into(Onboarbinding.image)
        return Onboarbinding.root

    }

    companion object {

        // viet fuction de bind du lieu cho 3 fragment

        private const val ARG_PARAM1 = "fragment1"
        private const val ARG_PARAM2 = "fragment2"
        private const val ARG_PARAM3 = "fragment3"

        fun newInstance(
            title: String?,
            description: String?,
            imageResource: Int
        ) : OnBoardingStarOnee {
            val fragment = OnBoardingStarOnee()
            val args = Bundle()
            args.putString(ARG_PARAM1, title)
            args.putString(ARG_PARAM2, description)
            args.putInt(ARG_PARAM3, imageResource)
            fragment.arguments = args
            return fragment
        }
    }

}