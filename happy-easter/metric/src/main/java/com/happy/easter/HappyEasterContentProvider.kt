package com.happy.easter

import android.content.ContentProvider

internal class HappyEasterContentProvider : ContentProvider() {
    override fun onCreate(): Boolean {
        HappyEasterApplication.init(checkNotNull(context))
        return true
    }

    override fun getType(uri: android.net.Uri): String? = null

    override fun insert(
        uri: android.net.Uri,
        values: android.content.ContentValues?
    ): android.net.Uri? = null

    override fun delete(
        uri: android.net.Uri,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int = 0

    override fun update(
        uri: android.net.Uri,
        values: android.content.ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int = 0

    override fun query(
        uri: android.net.Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): android.database.Cursor? = null
}
