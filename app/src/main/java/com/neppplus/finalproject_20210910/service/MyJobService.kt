package com.neppplus.finalproject_20210910.service

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log

class MyJobService : JobService() {

    companion object {
        val JOB_A = 1000
    }

    override fun onStartJob(p0: JobParameters?): Boolean {

        Log.d("테스트", p0!!.jobId.toString())
        return false
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        return false
    }

}