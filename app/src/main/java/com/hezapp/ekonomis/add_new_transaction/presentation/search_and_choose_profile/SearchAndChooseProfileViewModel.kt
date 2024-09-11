package com.hezapp.ekonomis.add_new_transaction.presentation.search_and_choose_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hezapp.ekonomis.core.data.profile.FakeProfileRepo
import com.hezapp.ekonomis.core.domain.entity.ProfileEntity
import com.hezapp.ekonomis.core.domain.entity.support_enum.ProfileType
import com.hezapp.ekonomis.core.domain.entity.support_enum.TransactionType
import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.profile.CreateNewProfileError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchAndChooseProfileViewModel(
    transactionType: TransactionType
) : ViewModel() {

    class Factory(private val transactionType: TransactionType) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SearchAndChooseProfileViewModel(transactionType) as T
        }
    }

    private val profileRepo = FakeProfileRepo()

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
            profileRepo.getPersonFiltered(
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
            profileRepo.addNewProfile(profile = ProfileEntity(
                name = profileName,
                type = _state.value.profileType
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
    val createNewProfileResponse: ResponseWrapper<Any?,CreateNewProfileError>? = null,
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