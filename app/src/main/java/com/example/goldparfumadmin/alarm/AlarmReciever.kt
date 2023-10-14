package com.example.goldparfumadmin.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.goldparfumadmin.repository.FireRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

//@AndroidEntryPoint
//class AlarmReceiver: BroadcastReceiver() {
//
//    @Inject
//    lateinit var repository: FireRepository
//
//    override fun onReceive(context: Context?, intent: Intent?) {
//        CoroutineScope(Job() + Dispatchers.Default).launch {
//            //repository.collectOrders()
//            Log.d("SCHEDULER_TEST", "onReceive: ${intent?.getStringExtra("EXTRA_MESSAGE")}")
//        }
//    }
//}