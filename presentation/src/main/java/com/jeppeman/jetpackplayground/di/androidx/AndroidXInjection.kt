package com.jeppeman.jetpackplayground.di.androidx

import androidx.fragment.app.Fragment

object AndroidXInjection {
    fun inject(fragment: Fragment) {
        val hasFragmentXInjector = findHasFragmentInjector(fragment)
        val fragmentXInjector = hasFragmentXInjector?.fragmentXInjector
        fragmentXInjector?.inject(fragment)
    }

    private fun findHasFragmentInjector(fragment: Fragment): HasFragmentXInjector? {
        var parentFragment = fragment
        while (parentFragment.parentFragment != null) {
            if (parentFragment is HasFragmentXInjector) {
                return parentFragment
            }

            parentFragment = parentFragment.parentFragment!!
        }
        val activity = fragment.activity
        if (activity is HasFragmentXInjector) {
            return activity
        }
        if (activity?.application is HasFragmentXInjector) {
            return activity.application as HasFragmentXInjector
        }

        return null
    }
}