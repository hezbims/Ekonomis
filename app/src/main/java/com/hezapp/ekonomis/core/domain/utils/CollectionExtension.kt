package com.hezapp.ekonomis.core.domain.utils

fun <T> Collection<T>.contains(condition : (T) -> Boolean) : Boolean{
    return firstOrNull { condition(it) } != null
}