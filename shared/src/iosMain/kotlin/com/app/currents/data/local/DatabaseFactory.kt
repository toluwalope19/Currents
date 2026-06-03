package com.app.currents.data.local


import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.app.currents.db.CurrentsDatabase

actual class DatabaseFactory {
    actual fun create(): CurrentsDatabase =
        CurrentsDatabase(
            NativeSqliteDriver(
                schema = CurrentsDatabase.Schema,
                name = "currents.db",
            )
        )
}