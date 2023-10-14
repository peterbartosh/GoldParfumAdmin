package com.example.goldparfumadmin

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.goldparfumadmin.admin.AdminNavHost
import com.example.goldparfumadmin.ui.theme.GoldParfumAdminTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        //val scheduler = AlarmSchedulerImpl(this)

        setContent {
            GoldParfumAdminTheme {


                //scheduler.schedule()

                val navController = rememberNavController()
                AdminNavHost(navController = navController)



           }

        }
    }
}