package com.example.bangkitcapstone.view.onboarding

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bangkitcapstone.view.onboarding.screen.FirstScreen
import com.example.bangkitcapstone.view.onboarding.screen.SecondScreen
import com.example.bangkitcapstone.view.onboarding.screen.ThirdScreen


class ViewPagerAdapter(
    activity: AppCompatActivity
): FragmentStateAdapter(activity) {

   private val fragmentList = listOf(
       FirstScreen(),
       SecondScreen(),
       ThirdScreen()
   )

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}
