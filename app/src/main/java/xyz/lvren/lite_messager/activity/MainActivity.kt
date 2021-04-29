package xyz.lvren.lite_messager.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import xyz.lvren.lite_messager.MyApplication
import xyz.lvren.lite_messager.R
import xyz.lvren.lite_messager.databinding.ActivityMainBinding
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val TAG = "xyz.lvren.lite_messager.activity.MainActivity"

    private val httpServer = xyz.lvren.lite_messager.http.HttpServer(MyApplication.context, 8080)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        val navController = this.findNavController(R.id.myNavHostFragment)
        drawerLayout = binding.drawerLayout
        appBarConfiguration =
            AppBarConfiguration(setOf(R.id.mainFragment, R.id.settingFragment), drawerLayout)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(binding.navView, navController)
        Log.d(TAG, "服务启动中")
        try {
            httpServer.start()
            // TODO: 开启一个websocket服务器，接受消息
            Log.d(TAG, "服务启动完成")
        } catch (e: IOException) {
            e.printStackTrace()
            Log.d(TAG, "服务启动错误")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.myNavHostFragment)
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        httpServer.stop()
    }


}