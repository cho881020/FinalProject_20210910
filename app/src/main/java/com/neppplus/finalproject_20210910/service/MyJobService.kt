package com.neppplus.finalproject_20210910.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.SystemClock
import android.util.Log

class MyJobService : JobService() {

    companion object {
        val JOB_A = 1000
    }

    override fun onStartJob(p0: JobParameters?): Boolean {

        Log.d("테스트", p0!!.jobId.toString())
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)  // 1
        val pendingIntent = PendingIntent.getBroadcast(     // 2
            this, AlarmReceiver.NOTIFICATION_ID, intent,
            PendingIntent.FLAG_UPDATE_CURRENT)


        val triggerTime = (SystemClock.elapsedRealtime()  // 4
                + 60 * 1000)
        alarmManager.set(   // 5
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            triggerTime,
            pendingIntent
        )
        return false
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        return false
    }

}