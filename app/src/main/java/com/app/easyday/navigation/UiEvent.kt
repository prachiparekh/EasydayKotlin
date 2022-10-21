package com.app.easyday.navigation

import android.content.Intent
import androidx.annotation.StringRes

sealed class UiEvent {

    class ShowToast(@StringRes val messageResId: Int) : UiEvent()

    class ShowToastMsg(val message: String) : UiEvent()

    object HideKeyboard : UiEvent()

}