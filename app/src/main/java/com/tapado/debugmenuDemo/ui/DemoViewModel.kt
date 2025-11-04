package com.tapado.debugmenuDemo.ui

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tapado.debugmenuDemo.analytics.MockAnalyticsManager
import com.tapado.debugmenuDemo.data.AppRepository
import com.tapadoo.debugmenu.logs.DebugLogs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
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
        Timber.d("ViewModel initialized")
        analytics.logScreen("DemoScreen")
    }

    fun onUserNameChanged(newValue: String) {
        Timber.d("User name input changed: $newValue")
        _userNameInput.value = newValue
    }

    fun saveUserName() {
        val name = _userNameInput.value
        if (name.isBlank()) {
            Timber.d("Attempted to save empty user name")
        }
        viewModelScope.launch {
            try {
                Timber.d("Saving user name: $name")
                repository.setUserName(name)
                val params = Bundle().apply { putString("user_name", name) }
                analytics.logEvent("set_user_name", params)
                Timber.d("User name saved successfully")
            } catch (e: Exception) {
                Timber.d("Failed to save user name", e)
            }
        }
    }

    fun setFeatureEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                Timber.d("Setting feature enabled: $enabled")
                repository.setFeatureEnabled(enabled)
                val params = Bundle().apply { putString("feature_enabled", enabled.toString()) }
                analytics.logEvent("set_feature_enabled", params)
                Timber.d("Feature flag updated successfully")
            } catch (e: Exception) {
                Timber.e(e,"Failed to update feature flag")
            }
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