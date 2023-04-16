package ru.ok.android.itmohack2023

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.happy.easter.HappyEasterPerformance
import org.json.JSONObject

class JNIActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jniactivity)
        Threads.ioPool.execute {
            val metric = HappyEasterPerformance.getInstance()
                .newHttpMetric("http://www.boredapi.com/api/activity/", "GET")
            metric.start()
            var result = nativeFunction() ?: return@execute
            if (result == "error") {
                metric.setResponseCode(400)
            } else {
                metric.setResponseCode(200)
            }
            metric.setContentType("application/json")
            metric.setResponseBytes(result.length.toLong())
            metric.stop()
            result = result.dropWhile { it != '{' }

            val textJson = JSONObject(result)
            val act =
                textJson.getString("activity")
            runOnUiThread {
                findViewById<TextView>(R.id.result).text = act
            }
        }
    }

    external fun nativeFunction(): String?

    companion object {
        init {
            System.loadLibrary("jnisocket");
        }
    }
}