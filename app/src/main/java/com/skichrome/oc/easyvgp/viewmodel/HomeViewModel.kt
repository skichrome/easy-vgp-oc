package com.skichrome.oc.easyvgp.viewmodel

import androidx.lifecycle.*
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.model.HomeRepository
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.User
import com.skichrome.oc.easyvgp.util.Event
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: HomeRepository) : ViewModel()
{
    // =================================
    //              Fields
    // =================================
    // =================================

    // --- Events

    private val _currentUserId = MutableLiveData<Event<Long>>()
    val currentUserId: LiveData<Event<Long>> = _currentUserId

    private val _message = MutableLiveData<Event<Int>>()
    val message: LiveData<Event<Int>> = _message

    private val _userClicked = MutableLiveData<Event<Long>>()
    val userCLicked: LiveData<Event<Long>> = _userClicked

    // --- Data

    private val _users: LiveData<List<User>> = repository.observeUsers().map { userList ->
        if (userList is Success)
            return@map userList.data
        else
        {
            showMessage(R.string.view_model_home_user_list_error)
            return@map emptyList<User>()
        }
    }

    // =================================
    //              Methods
    // =================================

    // --- Events

    fun onUserClick(userId: Long)
    {
        _userClicked.value = Event(userId)
    }

    private fun showMessage(msgRef: Int)
    {
        _message.value = Event(msgRef)
    }

    // --- Data

    fun getCurrentFirebaseUser(uid: String)
    {
        viewModelScope.launch {
            val knownUser = repository.getUserByFirebaseUid(uid)
            if (knownUser is Success)
                _currentUserId.value = Event(knownUser.data.id)
            else
                _currentUserId.value = Event(-1L)
        }
    }

    fun saveNewUser(user: User)
    {
        viewModelScope.launch {
            val result = repository.insertNewUser(user)
            if (result is Success)
            {
                _currentUserId.value = Event(result.data)
                showMessage(R.string.view_model_home_user_insert)
            } else
                showMessage(R.string.view_model_home_user_insert_error)
        }
    }

    fun updateUser(user: User)
    {
        viewModelScope.launch {
            val result = repository.updateUser(user)
            if (result is Success)
                showMessage(R.string.view_model_home_user_update)
            else
                showMessage(R.string.view_model_home_user_update_error)
        }
    }
}