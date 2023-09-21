package com.ramawidi.ghoresepmakanan.view.adapters

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

// Variabel jenis Bundle memungkinkan pengiriman data Parcelable ke Fragmen.
class ViewPagerAdapter(private val resultBundle: Bundle, private val fragments: ArrayList<Fragment>,
                       activity: AppCompatActivity): FragmentStateAdapter(activity) {

    override fun createFragment(position: Int): Fragment {
        fragments[position].arguments = resultBundle
        return fragments[position]
    }

    override fun getItemCount(): Int {
        return fragments.size
    }

}