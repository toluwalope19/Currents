package com.app.currents.data.local

import com.app.currents.db.CurrentsDatabase

expect class DatabaseFactory {
    fun create(): CurrentsDatabase
}