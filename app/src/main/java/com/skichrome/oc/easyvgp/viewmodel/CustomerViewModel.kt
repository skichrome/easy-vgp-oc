package com.skichrome.oc.easyvgp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.model.CustomerRepository
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.Customers
import com.skichrome.oc.easyvgp.util.Event
import com.skichrome.oc.easyvgp.util.uiJob

class CustomerViewModel(private val repository: CustomerRepository) : ViewModel()
{
    // =================================
    //              Fields
    // =================================

    private val _customers: LiveData<List<Customers>> = repository.getAllCustomers()
    val customers: LiveData<List<Customers>> = _customers

    private val _customer = MutableLiveData<Customers>()
    val customer: LiveData<Customers> = _customer

    // --- Events --- //

    private val _customersSaved = MutableLiveData<Event<Boolean>>()
    val customersSaved: LiveData<Event<Boolean>> = _customersSaved

    private val _customerClick = MutableLiveData<Event<Long>>()
    val customerClick: LiveData<Event<Long>> = _customerClick

    private val _customerLongClick = MutableLiveData<Event<Long>>()
    val customerLongClick: LiveData<Event<Long>> = _customerLongClick

    private val _errorMessage = MutableLiveData<Event<Int>>()
    val errorMessage: LiveData<Event<Int>> = _errorMessage

    // =================================
    //              Methods
    // =================================

    private fun showMessage(msg: Int)
    {
        _errorMessage.value = Event(msg)
    }

    fun loadCustomerById(customerId: Long)
    {
        viewModelScope.uiJob {
            repository.getCustomerById(customerId).let { results ->
                if (results is Success)
                    _customer.value = results.data
                else
                    showMessage(R.string.view_model_customer_get_by_id)
            }
        }
    }

    fun saveCustomer(customer: Customers)
    {
        viewModelScope.uiJob {
            val result = repository.saveCustomers(customer)
            if (result is Success)
                _customersSaved.value = Event(true)
            else
                showMessage(R.string.view_model_customer_insert_error)
        }
    }

    fun updateCustomer(customer: Customers)
    {
        viewModelScope.uiJob {
            val result = repository.updateCustomers(customer)
            if (result is Success)
                _customersSaved.value = Event(true)
            else
                showMessage(R.string.view_model_customer_update_error)
        }
    }

    // --- Events --- //

    fun onClickCustomer(customerId: Long)
    {
        _customerClick.value = Event(customerId)
    }

    fun onLongClickCustomer(customerId: Long)
    {
        _customerLongClick.value = Event(customerId)
    }
}