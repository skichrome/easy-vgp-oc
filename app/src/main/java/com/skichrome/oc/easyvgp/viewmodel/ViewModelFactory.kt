package com.skichrome.oc.easyvgp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory : ViewModelProvider.Factory
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T
    {
        return when
        {
            modelClass.isAssignableFrom(CustomerViewModel::class.java) -> CustomerViewModel() as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}