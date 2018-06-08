package com.jeppeman.jetpackplayground

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

class FakeApplicationTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        return super.newApplication(cl, FakeApplication::class.java.name, context)
    }
}