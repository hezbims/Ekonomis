package com.hezapp.ekonomis.add_or_update_transaction.presentation.search_and_choose_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.profile.AddNewProfileUseCase
import com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.profile.GetListProfileUseCase
import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileEntity
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType
import com.hezapp.ekonomis.core.domain.profile.model.CreateNewProfileError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchAndChooseProfileViewModel(
    transactionType: TransactionType,
    private val getListProfile: GetListProfileUseCase,
    private val addNewProfile: AddNewProfileUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(SearchAndChooseProfileUiState(
        transactionType = transactionType
    ))
    val state : StateFlow<SearchAndChooseProfileUiState>
        get() = _state.asStateFlow()

    fun onEvent(event: SearchAndChooseProfileEvent){
        when(event){
            SearchAndChooseProfileEvent.LoadAvailableProfiles ->
                loadAvailableProfiles()
            is SearchAndChooseProfileEvent.ChangeSearchQuery ->
                changeSearchQuery(event.newSearchQuery)
            is SearchAndChooseProfileEvent.CreateNewProfile ->
                createNewProfile(event.profileName)
            SearchAndChooseProfileEvent.DoneHandlingCreateNewProfileResponse ->
                doneHandlingCreateNewProfileResponse()
        }
    }

    private fun loadAvailableProfiles(){
        viewModelScope.launch(Dispatchers.IO) {
            val currentState = _state.value
            getListProfile(
                profileName = currentState.searchQuery,
                profileType = currentState.profileType,
            ).collect { response ->
                _state.update { it.copy(availableProfilesResponse = response) }
            }
        }
    }

    private fun changeSearchQuery(newSearchQuery: String){
        _state.update { it.copy(searchQuery = newSearchQuery) }
        loadAvailableProfiles()
    }

    private fun createNewProfile(profileName: String){
        viewModelScope.launch(Dispatchers.IO) {
            addNewProfile(newProfile = ProfileEntity(
                id = 0,
                name = profileName,
                type = _state.value.profileType,
            )).collect { response ->
                _state.update { it.copy(createNewProfileResponse = response) }
            }
        }
    }

    private fun doneHandlingCreateNewProfileResponse(){
        _state.update { it.copy(createNewProfileResponse = null) }
    }
}

data class SearchAndChooseProfileUiState(
    val availableProfilesResponse: ResponseWrapper<List<ProfileEntity>, MyBasicError> = ResponseWrapper.Loading(),
    val transactionType: TransactionType,
    val searchQuery: String = "",
    val createNewProfileResponse: ResponseWrapper<Any?, CreateNewProfileError>? = null,
){
    val profileType: ProfileType
        get() = transactionType.getProfileType()
}

sealed class SearchAndChooseProfileEvent {
    data object LoadAvailableProfiles : SearchAndChooseProfileEvent()
    class CreateNewProfile(val profileName: String) : SearchAndChooseProfileEvent()
    data object DoneHandlingCreateNewProfileResponse : SearchAndChooseProfileEvent()
    class ChangeSearchQuery(val newSearchQuery: String) : SearchAndChooseProfileEvent()
}