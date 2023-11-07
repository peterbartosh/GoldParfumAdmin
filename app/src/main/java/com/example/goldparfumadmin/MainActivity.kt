package com.example.goldparfumadmin

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.navigation.compose.rememberNavController
import com.example.goldparfumadmin.data.utils.PersonalData
import com.example.goldparfumadmin.presentation.AdminNavHost
import com.example.goldparfumadmin.presentation.theme.GoldParfumAdminTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)



        setContent {
            GoldParfumAdminTheme {

                val firebaseAuth = FirebaseAuth.getInstance()
                if (firebaseAuth.uid != PersonalData.ADMIN_UID)
                    firebaseAuth.signInWithEmailAndPassword(PersonalData.ADMIN_EMAIL, PersonalData.ADMIN_PASSWORD)
                        .addOnCompleteListener {task ->
                            Log.d("SIGN_IN_TEST", "onCreate: isSucc = ${task.isSuccessful}, uid = ${firebaseAuth.uid}")
                        }
                else
                    Log.d("SIGN_IN_TEST", "onCreate: ${firebaseAuth.uid}")

                val navController = rememberNavController()
                AdminNavHost(navController = navController)
           }

        }
    }
}