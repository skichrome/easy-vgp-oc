package com.skichrome.oc.easyvgp.model

sealed class Results<out R>
{

    data class Success<out T>(val data: T) : Results<T>()
    data class Error(val exception: Exception) : Results<Nothing>()
    object Loading : Results<Nothing>()

    override fun toString(): String
    {
        return when (this)
        {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            Loading -> "Loading"
        }
    }
}