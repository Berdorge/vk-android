// HappyEasterServiceInterface.aidl
package com.happy.easter;

import com.happy.easter.data.HappyEasterMetricData;

interface HappyEasterServiceInterface {
    oneway void sendHttpMetric(in HappyEasterMetricData metricData);
    oneway void onDownloadEnqueued(in long downloadId, in String url, in String feature);
}