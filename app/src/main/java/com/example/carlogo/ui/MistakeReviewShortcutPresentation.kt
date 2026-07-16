package com.example.carlogo.ui

internal data class MistakeReviewShortcutPresentation(
    val headline: String,
    val detail: String,
    val hasPendingMistakes: Boolean,
) {
    companion object {
        fun fromPendingMistakeCount(count: Int): MistakeReviewShortcutPresentation {
            require(count >= 0) { "Pending mistake count cannot be negative." }

            return if (count > 0) {
                MistakeReviewShortcutPresentation(
                    headline = "\u5f85\u590d\u4e60 $count \u9053",
                    detail = "\u67e5\u770b\u6613\u9519\u8f66\u578b\u548c\u54c1\u724c",
                    hasPendingMistakes = true,
                )
            } else {
                MistakeReviewShortcutPresentation(
                    headline = "\u6682\u65e0\u9519\u9898",
                    detail = "\u7ee7\u7eed\u4fdd\u6301",
                    hasPendingMistakes = false,
                )
            }
        }
    }
}
