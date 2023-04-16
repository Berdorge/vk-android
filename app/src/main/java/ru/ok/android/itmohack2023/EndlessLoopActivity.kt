package ru.ok.android.itmohack2023

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.happy.easter.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class EndlessLoopActivity : AppCompatActivity() {
    private var requests = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ok_http)
        val list = findViewById<LinearLayout>(R.id.list)
        val textView = TextView(this)
        textView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        textView.text = "Requests: $requests"
        list.addView(textView)
        lifecycleScope.launch {
            while (true) {
                run(BuildConfig.API_URL + "timeout")
                textView.text = "Requests: ${++requests}"
            }
        }
    }

    @Throws(IOException::class)
    suspend fun run(url: String): String? = withContext(Dispatchers.IO) {
        val request: Request = Request.Builder()
            .url(url)
            .build()
        suspendCancellableCoroutine {
            val call = OkHttpClient().newCall(request)
            call.enqueue(object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: IOException) {
                    it.resumeWithException(e)
                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    it.resume(response.body?.string())
                }
            })
            it.invokeOnCancellation {
                call.cancel()
            }
        }
    }
}