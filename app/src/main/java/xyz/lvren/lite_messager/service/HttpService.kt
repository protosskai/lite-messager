package xyz.lvren.lite_messager.service

import android.app.*
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.IBinder
import android.util.Log
import xyz.lvren.lite_messager.MyApplication
import xyz.lvren.lite_messager.R
import xyz.lvren.lite_messager.activity.MainActivity
import xyz.lvren.lite_messager.http.HttpServer
import java.lang.Exception

class HttpService : Service() {

    private lateinit var httpServer: HttpServer
    private val TAG = "xyz.lvren.lite_messager.service.HttpService"
    private val ID = "xyz.lvren.lite_messager.service"
    private val NAME = "Channel One"

    override fun onCreate() {
        super.onCreate()
        httpServer = HttpServer(MyApplication.context, 8888)
//        val intent = Intent(this, MainActivity::class.java)
//        val pi = PendingIntent.getActivity(this, 0, intent, 0)
//        val notification = Notification.Builder(this, CHANNEL_DEFAULT_IMPORTANCE)
//            .setContentTitle("Web服务")
//            .setContentText("Web服务正在运行")
//            .setWhen(System.currentTimeMillis())
//            .setSmallIcon(R.mipmap.ic_launcher)
//            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
//            .setContentIntent(pi)
//            .build()
//        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            httpServer.start()
            Log.d(TAG, "http服务器启动成功")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "http服务器启动失败")
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        httpServer.stop()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}