package com.happy.easter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.TimeUnit

/**
 * Class used to gain access to the HappyEaster Performance API.
 */
class HappyEasterPerformance internal constructor(
    private val application: HappyEasterApplication
) {
    /**
     * Creates a new [HappyEasterHttpMetric] instance.
     *
     * @param url The URL of the request.
     * @param httpMethod The HTTP method of the request.
     */
    fun newHttpMetric(
        url: String,
        httpMethod: String
    ) = HappyEasterHttpMetric(application, url, httpMethod)

    /**
     * Adds a new feature to the list of features.
     * In metrics, the latest added feature that matches the URL RegEx (if any) will be used.
     * Otherwise, the feature will be determined by the stack trace.
     *
     * @param urlRegex The URL RegEx to match.
     * @param feature The feature name.
     */
    fun addFeature(urlRegex: Regex, feature: String) {
        application.addFeature(urlRegex, feature)
    }

    /**
     * Removes a feature from the list of features.
     *
     * @param urlRegex The URL RegEx to match.
     */
    fun removeFeature(urlRegex: Regex) {
        application.removeFeature(urlRegex)
    }

    /**
     * Adds a new feature to the list of features and removes it when the lifecycle gets destroyed.
     *
     * @param urlRegex The URL RegEx to match.
     * @param feature The feature name.
     * @param lifecycle The lifecycle to observe.
     *
     * @see addFeature
     */
    fun addFeature(urlRegex: Regex, feature: String, lifecycle: Lifecycle) {
        addFeature(urlRegex, feature)
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                removeFeature(urlRegex)
            }
        })
    }

    companion object {
        /**
         * Configuration used by the HappyEaster Performance Metrics queue.
         *
         * Can be modified in realtime, affecting the queue behavior.
         */
        var queueConfiguration = QueueConfiguration()

        /**
         * @return a new [HappyEasterPerformance] instance.
         */
        fun getInstance() = HappyEasterPerformance(HappyEasterApplication.getInstance())
    }

    /**
     * Configuration for the HappyEaster Performance Metrics queue.
     *
     * In order for metrics to be sent,
     * the number of metrics in the queue must not be less than [queueRequestMinSize]
     * __and__ the time between two requests must not be less than [queueRequestMinInterval].
     */
    data class QueueConfiguration(
        /**
         * The minimal number of metrics to be sent in a single request.
         *
         * If the number of metrics in the queue is less than this threshold,
         * the metrics will not be sent until the threshold is reached.
         */
        val queueRequestMinSize: Int = 10,

        /**
         * The minimal number of milliseconds between
         * two metrics requests, _whether successful or not_.
         *
         * If the time between two requests is less than this threshold,
         * the metrics will not be sent until the threshold is reached.
         */
        val queueRequestMinInterval: Long = TimeUnit.SECONDS.toMillis(10)
    )
}
