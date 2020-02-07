package com.skichrome.oc.easyvgp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skichrome.oc.easyvgp.model.CustomerRepository
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.Customers
import com.skichrome.oc.easyvgp.util.Event
import com.skichrome.oc.easyvgp.util.uiJob

class CustomerViewModel(private val customerRepository: CustomerRepository) : ViewModel()
{
    // =================================
    //              Fields
    // =================================

    private val _customers: LiveData<List<Customers>> = customerRepository.getAllCustomers()
    val customers: LiveData<List<Customers>> = _customers

    private val _customer = MutableLiveData<Customers>()
    val customer: LiveData<Customers> = _customer

    // --- Events --- //

    private val _customersSaved = MutableLiveData<Event<Boolean>>()
    val customersSaved: LiveData<Event<Boolean>> = _customersSaved

    private val _customerClick = MutableLiveData<Event<Long>>()
    val customerClick: LiveData<Event<Long>> = _customerClick

    // =================================
    //              Methods
    // =================================

    fun loadCustomerById(customerId: Long)
    {
        viewModelScope.uiJob {
            customerRepository.getCustomerById(customerId).let { results ->
                if (results is Success)
                    _customer.value = results.data
            }
        }
    }

    fun saveCustomer(customer: Customers)
    {
        viewModelScope.uiJob {
            val savedCustomerId = customerRepository.saveCustomers(customer)
            _customersSaved.value =
                when (savedCustomerId)
                {
                    is Success -> Event(true)
                    is Error -> Event(false)
                    else -> Event(false)
                }
        }
    }

    fun updateCustomer(customer: Customers)
    {
        viewModelScope.uiJob {
            val updatedCustomerId = customerRepository.updateCustomers(customer)
            _customersSaved.value = when (updatedCustomerId)
            {
                is Success -> Event(true)
                is Error -> Event(false)
                else -> Event(false)
            }
        }
    }

    // --- Events --- //

    fun onClickCustomer(customerId: Long)
    {
        _customerClick.value = Event(customerId)
    }
}