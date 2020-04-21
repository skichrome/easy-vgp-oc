package com.skichrome.oc.easyvgp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.base.HomeRepository
import com.skichrome.oc.easyvgp.model.local.database.UserAndCompany
import com.skichrome.oc.easyvgp.util.Event
import com.skichrome.oc.easyvgp.util.uiJob
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: HomeRepository) : BaseViewModel()
{
    // =================================
    //              Fields
    // =================================

    // --- Events

    private val _currentUser = MutableLiveData<UserAndCompany>()
    val currentUser: LiveData<UserAndCompany> = _currentUser

    private val _currentUserId = MutableLiveData<Event<Long>>()
    val currentUserId: LiveData<Event<Long>> = _currentUserId

    private val _onSaveEvent = MutableLiveData<Event<UserAndCompany>>()
    val onSaveEvent: LiveData<Event<UserAndCompany>> = _onSaveEvent

    private val _onSaveSuccessEvent = MutableLiveData<Event<Boolean>>()
    val onSaveSuccessEvent: LiveData<Event<Boolean>> = _onSaveSuccessEvent

    // --- Data

    private val _controlList = MutableLiveData<List<*>>()
    val controlList: LiveData<List<*>> = _controlList

    // =================================
    //              Methods
    // =================================

    // --- Events

    fun onSaveClick(userAndCompany: UserAndCompany)
    {
        _onSaveEvent.value = Event(userAndCompany)
    }

    // --- Data

    fun getCurrentFirebaseUser(uid: String)
    {
        viewModelScope.launch {
            val users = repository.getAllUserAndCompany()
            if (users is Success)
            {
                val result = users.data.filter { it.user.firebaseUid == uid }
                when (result.size)
                {
                    1 ->
                    {
                        _currentUser.value = result.first()
                        _currentUserId.value = Event(result.first().user.id)
                    }
                    0 -> _currentUserId.value = Event(-1L)
                    else -> showMessage(R.string.view_model_home_user_filter_error)
                }
            }
            else
            {
                handleError(users as? Error)
                showMessage(R.string.view_model_home_user_filter_error)
            }
        }
    }

    fun saveNewUserAndCompany(userAndCompany: UserAndCompany)
    {
        viewModelScope.launch {
            val result = repository.insertNewUserAndCompany(userAndCompany)
            if (result is Success)
            {
                _currentUserId.value = Event(result.data)
                _currentUser.value = userAndCompany
                showMessage(R.string.view_model_home_user_insert)
            }
            else
            {
                handleError(result as? Error)
                showMessage(R.string.view_model_home_user_insert_error)
            }
        }
    }

    fun updateUserAndCompany(userAndCompany: UserAndCompany)
    {
        viewModelScope.launch {
            val result = repository.updateNewUserAndCompany(userAndCompany)
            if (result is Success)
            {
                _onSaveSuccessEvent.value = Event(true)
                showMessage(R.string.view_model_home_user_update)
            }
            else
            {
                handleError(result as? Error)
                showMessage(R.string.view_model_home_user_update_error)
            }
        }
    }

    fun synchronizeLocalDatabaseWithRemote()
    {
        viewModelScope.uiJob {
            val results = repository.synchronizeDatabase()
            if (results is Error)
            {
                handleError(results as? Error)
                showMessage(R.string.view_model_home_update_error)
            }
        }
    }
}