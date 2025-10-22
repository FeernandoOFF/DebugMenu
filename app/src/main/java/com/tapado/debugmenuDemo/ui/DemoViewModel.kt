package com.tapado.debugmenuDemo.ui

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tapado.debugmenuDemo.analytics.MockAnalyticsManager
import com.tapado.debugmenuDemo.data.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.jvm.java

data class DemoUiState(
    val userName: String = "",
    val featureEnabled: Boolean = false
)

class DemoViewModel(
    private val repository: AppRepository,
    private val analytics: MockAnalyticsManager
) : ViewModel() {

    private val _userNameInput = MutableStateFlow("")
    val userNameInput: StateFlow<String> = _userNameInput

    val state: StateFlow<DemoUiState> = combine(
        repository.userName,
        repository.featureEnabled
    ) { name, enabled ->
        DemoUiState(name, enabled)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DemoUiState())

    init {
        analytics.logScreen("DemoScreen")
        println("DemoViewModel init")
    }

    fun onUserNameChanged(newValue: String) {
        _userNameInput.value = newValue
    }

    fun saveUserName() {
        val name = _userNameInput.value
        viewModelScope.launch {
            repository.setUserName(name)
            val params = Bundle().apply { putString("user_name", name) }
            analytics.logEvent("set_user_name", params)
        }
    }

    fun setFeatureEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setFeatureEnabled(enabled)
            val params = Bundle().apply { putString("feature_enabled", enabled.toString()) }
            analytics.logEvent("set_feature_enabled", params)
        }
    }

    companion object {
        class Factory(private val context: Context) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return when {
                    modelClass.isAssignableFrom(DemoViewModel::class.java) ->
                        DemoViewModel(AppRepository(context), MockAnalyticsManager()) as T
                    else -> throw IllegalArgumentException("Unknown ViewModel class")
                }
            }

        }
    }
}