package com.skichrome.oc.easyvgp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skichrome.oc.easyvgp.model.CustomerRepository
import com.skichrome.oc.easyvgp.model.Results.Error
import com.skichrome.oc.easyvgp.model.Results.Success
import com.skichrome.oc.easyvgp.model.local.database.Customers
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

    private val _customersSaved = MutableLiveData<Boolean>()
    val customersSaved: LiveData<Boolean> = _customersSaved

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
                    is Success -> true
                    is Error -> false
                    else -> false
                }
            _customersSaved.value = null
        }
    }

    // --- Events --- //

    fun onClickCustomer(customerId: Long)
    {
    }
}