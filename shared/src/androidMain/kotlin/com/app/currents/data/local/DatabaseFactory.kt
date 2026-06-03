package com.app.currents.data.local

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.app.currents.db.CurrentsDatabase

actual class DatabaseFactory(private val context: Context) {
    actual fun create(): CurrentsDatabase =
        CurrentsDatabase(
            AndroidSqliteDriver(
                schema = CurrentsDatabase.Schema,
                context = context,
                name = "currents.db",
            )
        )
}