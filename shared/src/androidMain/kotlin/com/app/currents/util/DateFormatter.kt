package com.app.currents.util

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

actual fun formatRelativeTime(dateString: String): String {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val date = LocalDateTime.parse(dateString, formatter)
        val now = LocalDateTime.now(ZoneId.of("UTC"))
        val minutes = ChronoUnit.MINUTES.between(date, now)

        when {
            minutes < 1    -> "Just now"
            minutes < 60   -> "${minutes}m ago"
            minutes < 1440 -> "${minutes / 60}h ago"
            minutes < 10080 -> "${minutes / 1440}d ago"
            else           -> "${minutes / 10080}w ago"
        }
    } catch (e: Exception) {
        dateString
    }
}