package com.jeppeman.jetpackplayground.di.androidx

import androidx.fragment.app.Fragment
import dagger.android.AndroidInjector

interface HasFragmentXInjector {
    val fragmentXInjector: AndroidInjector<Fragment>
}