package com.rajapps.watsappstatussaver.utils

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.rajapps.watsappstatussaver.R

fun Activity.replaceFragment(fragment: Fragment, args: Bundle? = null) {
    val fragmentActivity = this as FragmentActivity
    fragmentActivity.supportFragmentManager.beginTransaction().apply {
        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        args?.let {
            fragment.arguments = it
        }
        replace(R.id.fragment_container, fragment)
        addToBackStack(null)
    }.commit()
}