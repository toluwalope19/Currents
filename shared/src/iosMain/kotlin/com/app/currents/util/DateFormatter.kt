package com.app.currents.util

import platform.Foundation.NSCalendar
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSTimeInterval
import platform.Foundation.timeIntervalSinceDate

actual fun formatRelativeTime(dateString: String): String {
    return try {
        val formatter = NSDateFormatter().apply {
            dateFormat = "yyyy-MM-dd HH:mm:ss"
        }
        val date = formatter.dateFromString(dateString) ?: return dateString
        val now = NSDate()
        val seconds = now.timeIntervalSinceDate(date).toLong()
        val minutes = seconds / 60

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