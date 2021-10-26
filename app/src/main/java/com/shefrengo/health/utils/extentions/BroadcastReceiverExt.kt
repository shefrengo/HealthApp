package com.shefrengo.health.utils.extentions

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

class BroadcastReceiverExt<T>(context: T, constructor: Builder.() -> Unit) : LifecycleObserver where T : Context, T : LifecycleOwner {

    private val appContext = context.applicationContext
    private val filter: IntentFilter
    private val instructions: List<Instructions>

    init {
        val builder = Builder()
        constructor(builder)
        filter = builder.filter()
        instructions = builder.instructions()

        context.lifecycle.addObserver(this)
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            for (ins in instructions) {
                if (ins.matches(intent)) {
                    ins.execution().invoke(intent)
                    break
                }
            }
        }
    }

    @OnLifecycleEvent(ON_START)
    fun start() {
        appContext.registerReceiver(broadcastReceiver, filter)
    }

    @OnLifecycleEvent(ON_DESTROY)
    fun stop() = appContext.unregisterReceiver(broadcastReceiver)
}

class Builder internal constructor() {

    private val filter = IntentFilter()
    private val instructions = mutableListOf<Instructions>()

    fun onAction(action: String, execution: Execution) {
        filter.addAction(action)
        instructions.add(Instructions.OnAction(action, execution))
    }

    fun onDataScheme(scheme: String, execution: Execution) {
        filter.addDataScheme(scheme)
        instructions.add(Instructions.OnDataScheme(scheme, execution))
    }

    fun onCategory(category: String, execution: Execution) {
        filter.addCategory(category)
        instructions.add(Instructions.OnCategory(category, execution))
    }

    internal fun filter() = filter

    internal fun instructions() = instructions
}

typealias Execution = (Intent) -> Unit

sealed class Instructions {

    abstract fun matches(intent: Intent): Boolean

    abstract fun execution(): Execution

    data class OnAction(val action: String, val execution: Execution) : Instructions() {

        override fun matches(intent: Intent): Boolean {
            return intent.action == action
        }

        override fun execution() = execution
    }

    data class OnDataScheme(val scheme: String, val execution: Execution) : Instructions() {
        override fun matches(intent: Intent): Boolean {
            return intent.data?.scheme == scheme
        }

        override fun execution() = execution
    }

    data class OnCategory(val category: String, val execution: Execution) : Instructions() {
        override fun matches(intent: Intent): Boolean {
            return intent.hasCategory(category)
        }

        override fun execution() = execution
    }
}