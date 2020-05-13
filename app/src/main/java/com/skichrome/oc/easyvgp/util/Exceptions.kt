package com.skichrome.oc.easyvgp.util

class NetworkException(msg: String, e: Exception? = null) : Exception(msg, e)
class LocalRepositoryException(msg: String, e: Exception? = null) : Exception(msg, e)
class RemoteRepositoryException(msg: String, e: Exception? = null) : Exception(msg, e)
class ItemNotFoundException(msg: String, e: Exception? = null) : Exception(msg, e)
class NotImplementedException(msg: String, e: Exception? = null) : Exception(msg, e)