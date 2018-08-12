package com.switchamajig.youtubetimeoutrestorer

import android.accessibilityservice.AccessibilityService
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class YouTubeTimeoutAccessibilityService : AccessibilityService() {
    val TIMEOUT = 8000L
    val handler = Handler()
    var scheduled = false

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val node = findCloseButton()
        if ((node != null) && !scheduled) {
            handler.postDelayed({clickNode(node)}, TIMEOUT)
            scheduled = true
        }
    }
    override fun onInterrupt() {}

    fun findCloseButton() : AccessibilityNodeInfo? {
        val windowList = windows
        for (window in windowList) {
            val root = window.root
            if ((root != null) && TextUtils.equals(root.packageName, "com.google.android.youtube")) {
                val closeButton = findCloseButtonDfs(root)
                if (closeButton != null) return closeButton
            }
        }
        return null
    }

    fun findCloseButtonDfs(root: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        if (isCloseButton(root)) return root

        for (i in 0..root.childCount - 1) {
            val child = root.getChild(i)
            if (child != null) {
                val button = findCloseButtonDfs(child)
                if (button != null) return button
            }
        }
        return null
    }

    fun isCloseButton(node: AccessibilityNodeInfo) : Boolean {
        return (TextUtils.equals(node.contentDescription, "Hide controls"))
    }

    fun clickNode(node: AccessibilityNodeInfo) {
        node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        scheduled = false
    }
}