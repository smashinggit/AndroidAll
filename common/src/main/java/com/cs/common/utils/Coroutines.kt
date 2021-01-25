package com.cs.common.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun ViewModel.launch(
    block: suspend CoroutineScope.() -> Unit,
    onComplete: () -> Unit,
    onError: (Throwable) -> Unit
) {
    viewModelScope.launch(context = CoroutineExceptionHandler { coroutineContext, throwable ->
        onError(throwable)
    }) {
        try {
            block()
        } finally {
            onComplete()
        }
    }
}