package com.example.projeto_softinsa_mobile_app

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class Adaptador_ViewPagerVaga (fragmentManager: FragmentManager, private val isColaborador: Boolean) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return vagaFragment.newInstance(position)
    }

    override fun getCount(): Int {

        return if (!isColaborador) {
            1
        } else {
            2
        }
    }
    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Abertas"
            1 -> "Internas"
            else -> null
        }
    }
    }
