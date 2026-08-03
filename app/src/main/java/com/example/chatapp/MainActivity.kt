package com.example.chatapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import com.example.chatapp.a_authentication.SessionManager
import com.example.chatapp.a_application.appContainer

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        setupStartDestination()
        Log.d("APP_TEST", application.javaClass.name)
    }
    private fun setupStartDestination() {
        val navHost = supportFragmentManager
            .findFragmentById(R.id.fc_auth_nav_host_system_userId_activityMain)
        as NavHostFragment  // حول ال contenrFragment من xml الي كائن يفهمة android
        val navController = navHost.navController //جيب المسوال المتحكم في التنقل لل navhost
        val graph = navController.navInflater.inflate(R.navigation.main_nav_graph)  // حول الصفحة xml  الي object اندرويد

        if (appContainer.checkSessionUseCase()){
            Log.d("mainActivityStartDelivered"," CreateStart ")

            appContainer.startObserverDelivered()
            graph.setStartDestination(R.id.list_chats_nav_graph)
        }else{
            graph.setStartDestination(R.id.authentication_nav_host)
        }
        navController.graph = graph  //طبق التعديل الجديد علي الnavgraph
    }

}