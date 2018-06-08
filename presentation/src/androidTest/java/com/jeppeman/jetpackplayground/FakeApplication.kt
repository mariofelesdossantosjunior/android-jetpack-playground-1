package com.jeppeman.jetpackplayground

import com.jeppeman.jetpackplayground.di.DaggerFakeAppComponent

class FakeApplication : MainApplication() {
    override fun inject() {
        DaggerFakeAppComponent.builder()
                .create(this)
                .inject(this)
    }
}