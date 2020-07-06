package org.wangyichen.anynote.utils

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AppExecutors private constructor(
    val diskIO: Executor = DiskIOExecutor(),
    val mainThread: Executor = MainThreadExecutor()
) {

    private class DiskIOExecutor : Executor {
        private val diskIO = Executors.newSingleThreadExecutor()
        override fun execute(p0: Runnable) {
            diskIO.execute(p0)
        }
    }

    private class MainThreadExecutor : Executor {
        private val MainThreadExecutor = Handler(Looper.getMainLooper())
        override fun execute(p0: Runnable) {
            MainThreadExecutor.post(p0)
        }
    }

    companion object {
        private var INSTANCE: AppExecutors? = null
        private val lock = Any()
        fun getInstance():AppExecutors{
            synchronized(lock) {
                if (INSTANCE == null) {
                    INSTANCE = AppExecutors()
                }
                return INSTANCE!!
            }
        }
    }
}