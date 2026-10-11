package com.exmachina.xvrtracker.common.util

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet

fun <T : Any> List<T>.toImmutableList(): ImmutableList<T> = ImmutableList.copyOf(this)

fun <T : Any> emptyImmutableList(): ImmutableList<T> = ImmutableList.of<T>()

fun <K : Any, V : Any> Map<K, V>.toImmutableMap(): ImmutableMap<K, V> = ImmutableMap.copyOf(this)

fun <K : Any, V : Any> emptyImmutableMap(): ImmutableMap<K, V> = ImmutableMap.of<K, V>()

fun <T : Any> Set<T>.toImmutableSet(): ImmutableSet<T> = ImmutableSet.copyOf(this)

fun <T : Any> emptyImmutableSet(): ImmutableSet<T> = ImmutableSet.of<T>()
