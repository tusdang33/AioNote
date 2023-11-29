package com.notes.aionote

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notes.aionote.common.AioConst
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
	private val dataStore: DataStore<Preferences>
): ViewModel() {
	private val _mainUiState = MutableStateFlow(MainUiState())
	val mainUiState = _mainUiState.asStateFlow()
	
	init {
		viewModelScope.launch {
			dataStore.data.collectLatest { pref ->
				_mainUiState.update {
					it.copy(
						fontWeight = pref[intPreferencesKey(AioConst.PREFERENCE_KEY_FONT_WEIGHT)]
							?: 0
					)
				}
			}
		}
	}
}

data class MainUiState(
	val fontWeight: Int = 0
)