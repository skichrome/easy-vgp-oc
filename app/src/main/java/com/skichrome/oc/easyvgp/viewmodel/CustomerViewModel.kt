package com.skichrome.oc.easyvgp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skichrome.oc.easyvgp.model.CustomerRepository
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.Customers
import com.skichrome.oc.easyvgp.util.uiJob

class CustomerViewModel(private val customerRepository: CustomerRepository) : ViewModel()
{
    // =================================
    //              Fields
    // =================================

    private val _customers = MutableLiveData<List<Customers>>()
    val customers: LiveData<List<Customers>> = _customers

    // =================================
    //              Methods
    // =================================

    fun loadAllCustomers()
    {
        viewModelScope.uiJob {
            customerRepository.getAllCustomers().let { results ->
                if (results is Success)
                {
                    _customers.value = results.data
                }
            }
        }
    }
}