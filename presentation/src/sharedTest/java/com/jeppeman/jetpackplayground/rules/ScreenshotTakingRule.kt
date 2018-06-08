package com.jeppeman.jetpackplayground.rules

import android.os.Environment
import android.util.Log
import androidx.test.runner.screenshot.BasicScreenCaptureProcessor
import androidx.test.runner.screenshot.Screenshot
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.io.File
import java.io.IOException

fun takeScreenshot(parentFolderPath: String = "", screenShotName: String) {
    Log.d("Screenshots", "Taking screenshot of '$screenShotName'")

    try {
        val screenCapture = Screenshot.capture()
        val processors = setOf(MyScreenCaptureProcessor(parentFolderPath))
        screenCapture.apply {
            name = screenShotName
            process(processors)
        }
        Log.d("Screenshots", "Screenshot taken")
    } catch (ex: IOException) {
        Log.e("Screenshots", "Could not take the screenshot", ex)
    }
}

class ScreenshotTakingRule : TestWatcher() {

    override fun failed(e: Throwable?, description: Description) {
        val parentFolderPath = "failures/${description.className}"
        takeScreenshot(parentFolderPath = parentFolderPath, screenShotName = description.methodName)
    }
}

class MyScreenCaptureProcessor(parentFolderPath: String) : BasicScreenCaptureProcessor() {

    init {
        this.mDefaultScreenshotPath = File(
                File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        "jetpackplayground"
                ).absolutePath,
                "screenshots/$parentFolderPath"
        )
    }

    override fun getFilename(prefix: String): String = prefix
}