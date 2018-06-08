package com.jeppeman.jetpackplayground.data.executor

import com.jeppeman.jetpackplayground.domain.executor.ThreadExecutor
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobExecutor @Inject constructor() : ThreadExecutor {

    private val threadExecutor =
            ThreadPoolExecutor(
                    3,
                    5,
                    10,
                    TimeUnit.SECONDS,
                    LinkedBlockingQueue(),
                    JobThreadFactory())

    override fun execute(command: Runnable?) {
        threadExecutor.execute(command)
    }
}

class JobThreadFactory : ThreadFactory {

    private var counter: Int = 0

    override fun newThread(r: Runnable?): Thread {
        return Thread(r, "android_${counter++}")
    }
}