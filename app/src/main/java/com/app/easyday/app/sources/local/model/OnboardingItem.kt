package com.app.easyday.app.sources.local.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class OnboardingItem(
    @StringRes val titleResId: Int,
    @DrawableRes val imageResId: Int
)