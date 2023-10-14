package com.example.goldparfumadmin.alarm


import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

//
//class AlarmSchedulerImpl(
//    private val context: Context
//): AlarmScheduler {
//
//    private val alarmManager = context.getSystemService(AlarmManager::class.java)
//
//    override fun schedule() {
////        val intent = Intent(context, AlarmReceiver::class.java).apply {
////            putExtra("EXTRA_MESSAGE", "intent done")
////        }
////
////        alarmManager.setRepeating(
////            AlarmManager.RTC_WAKEUP,
////            5000,
////            60000,
////            //LocalDateTime.now().plusSeconds(10).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000,
////            PendingIntent.getBroadcast(
////                context,
////                0,
////                intent,
////                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
////            )
////        )
//
//        val pendingIntent = PendingIntent.getBroadcast(
//            context,
//            0,
//            Intent(context, AlarmReceiver::class.java),
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        val timeOff: Calendar = Calendar.getInstance()
//        val days: Int = Calendar.TUESDAY + (7 - timeOff.get(Calendar.DAY_OF_WEEK))
//        timeOff.add(Calendar.DATE, days)
//        timeOff.set(Calendar.HOUR, 12)
//        timeOff.set(Calendar.MINUTE, 0)
//        timeOff.set(Calendar.SECOND, 0)
//
//        alarmManager[AlarmManager.RTC_WAKEUP, timeOff.timeInMillis] = pendingIntent
//
//    }
//
//    override fun cancel() {
//        alarmManager.cancel(
//            PendingIntent.getBroadcast(
//                context,
//                0,
//                Intent(context, AlarmReceiver::class.java),
//                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//            )
//        )
//    }
//}