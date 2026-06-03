package com.app.currents

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform