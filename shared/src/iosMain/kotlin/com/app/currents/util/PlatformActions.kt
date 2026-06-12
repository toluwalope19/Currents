package com.app.currents.util

import platform.Foundation.NSURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController

actual fun openUrl(url: String) {
    val nsUrl = NSURL.URLWithString(url) ?: return
    UIApplication.sharedApplication.openURL(
        url = nsUrl,
        options = emptyMap<Any?, Any?>(),
        completionHandler = null,
    )
}

actual fun shareText(title: String, url: String) {
    val items = listOf("$title\n\n$url")
    val activityViewController = UIActivityViewController(
        activityItems = items,
        applicationActivities = null,
    )
    UIApplication.sharedApplication
        .keyWindow
        ?.rootViewController
        ?.presentViewController(
            viewControllerToPresent = activityViewController,
            animated = true,
            completion = null,
        )
}