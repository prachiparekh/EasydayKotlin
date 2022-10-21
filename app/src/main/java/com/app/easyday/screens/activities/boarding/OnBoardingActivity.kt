package com.app.easyday.screens.activities.boarding

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ActivityNavigator
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.app.easyday.R
import com.app.easyday.app.sources.local.model.OnboardingItem
import com.app.easyday.app.sources.local.prefrences.AppPreferencesDelegates
import com.app.easyday.screens.activities.auth.AuthActivity
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_on_boarding.*


@AndroidEntryPoint
class OnBoardingActivity : AppCompatActivity() {

    private lateinit var adapter: OnBoardingAdapter
    var items: List<OnboardingItem> = arrayListOf()

    init {
        items = getOnBoardingItems()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_on_boarding)

        adapter = OnBoardingAdapter(items)
        pager.adapter = adapter

        pager.currentItem = 0

        TabLayoutMediator(dots_layout, pager) { tab, position ->

        }.attach()
        pager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)

                title1.text = getString(items[position].titleResId)

                if (position == items.size - 1)
                    cta.text = getString(R.string.get_started)
                else
                    cta.text = getString(R.string.next)

            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })

        cta.setOnClickListener {
            if (pager.currentItem == items.size-1) {
                AppPreferencesDelegates.get().wasOnboardingSeen = true
                val activityNavigator = ActivityNavigator(baseContext)
                activityNavigator.navigate(
                    activityNavigator.createDestination().setIntent(
                        Intent(
                            baseContext,
                            AuthActivity::class.java
                        )
                    ), null, null, null
                )
                finish()
            }else{
                pager.currentItem=pager.currentItem+1
            }
        }
    }

    private fun getOnBoardingItems() = arrayListOf<OnboardingItem>().apply {

        add(OnboardingItem(R.string.boarding_title1, R.drawable.boarding_img1))
        add(OnboardingItem(R.string.boarding_title2, R.drawable.boarding_img2))
        add(OnboardingItem(R.string.boarding_title3, R.drawable.boarding_img3))
    }

}