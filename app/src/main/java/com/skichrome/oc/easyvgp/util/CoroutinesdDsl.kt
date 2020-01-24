package com.skichrome.oc.easyvgp.util

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Start a job that can be made of multiples tasks
 * Source : https://edit.theappbusiness.com/kotlin-coroutines-in-android-part-7-65f65f85824d
 */
private fun startJob(parentScope: CoroutineScope, coroutineContext: CoroutineContext, block: suspend CoroutineScope.() -> Unit) =
    parentScope.launch(coroutineContext) {
        supervisorScope {
            block()
        }
    }

/**
 * Start a sequential task that suspend the parent job / task until it's done and return the result
 * Source : https://edit.theappbusiness.com/kotlin-coroutines-in-android-part-7-65f65f85824d
 */
private suspend fun <T> startTask(coroutineContext: CoroutineContext, block: suspend CoroutineScope.() -> T): T =
    withContext(coroutineContext) {
        return@withContext block()
    }

/**
 * Start a parallel task. We can have multiple parallel tasks running at the same time ans wait for their result through their Deferred.
 * Source : https://edit.theappbusiness.com/kotlin-coroutines-in-android-part-7-65f65f85824d
 */
private fun <T> startTaskAsync(parentScope: CoroutineScope, coroutineContext: CoroutineContext, block: suspend CoroutineScope.() -> T): Deferred<T> =
    parentScope.async(coroutineContext) {
        return@async supervisorScope {
            return@supervisorScope block()
        }
    }

fun CoroutineScope.uiJob(block: suspend CoroutineScope.() -> Unit) =
    startJob(this, AppCoroutinesConfiguration.uiDispatchers, block)

fun CoroutineScope.backgroundJob(block: suspend CoroutineScope.() -> Unit) =
    startJob(this, AppCoroutinesConfiguration.backgroundDispatchers, block)

fun CoroutineScope.ioJob(block: suspend CoroutineScope.() -> Unit) =
    startJob(this, AppCoroutinesConfiguration.ioDispatchers, block)

suspend fun <T> uiTask(block: suspend CoroutineScope.() -> T): T =
    startTask(AppCoroutinesConfiguration.uiDispatchers, block)

suspend fun <T> backgroundTask(block: suspend CoroutineScope.() -> T): T =
    startTask(AppCoroutinesConfiguration.backgroundDispatchers, block)

suspend fun <T> ioTask(block: suspend CoroutineScope.() -> T): T =
    startTask(AppCoroutinesConfiguration.ioDispatchers, block)

fun <T> CoroutineScope.uiTaskAsync(block: suspend CoroutineScope.() -> T): Deferred<T> =
    startTaskAsync(this, AppCoroutinesConfiguration.uiDispatchers, block)

fun <T> CoroutineScope.backgroundTaskAsync(block: suspend CoroutineScope.() -> T): Deferred<T> =
    startTaskAsync(this, AppCoroutinesConfiguration.backgroundDispatchers, block)

fun <T> CoroutineScope.ioTaskAsync(block: suspend CoroutineScope.() -> T): Deferred<T> =
    startTaskAsync(this, AppCoroutinesConfiguration.ioDispatchers, block)