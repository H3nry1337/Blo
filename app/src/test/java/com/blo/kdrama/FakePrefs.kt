package com.blo.kdrama

import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences

class FakePrefs : SharedPreferences {
    private val map = HashMap<String, Any?>()
    override fun getAll(): MutableMap<String, *> = map
    override fun getString(k: String, d: String?) = map[k] as String? ?: d
    @Suppress("UNCHECKED_CAST")
    override fun getStringSet(k: String, d: MutableSet<String>?) = map[k] as MutableSet<String>? ?: d
    override fun getInt(k: String, d: Int) = map[k] as Int? ?: d
    override fun getLong(k: String, d: Long) = map[k] as Long? ?: d
    override fun getFloat(k: String, d: Float) = map[k] as Float? ?: d
    override fun getBoolean(k: String, d: Boolean) = map[k] as Boolean? ?: d
    override fun contains(k: String) = k in map
    override fun registerOnSharedPreferenceChangeListener(l: SharedPreferences.OnSharedPreferenceChangeListener?) {}
    override fun unregisterOnSharedPreferenceChangeListener(l: SharedPreferences.OnSharedPreferenceChangeListener?) {}
    override fun edit(): SharedPreferences.Editor = object : SharedPreferences.Editor {
        override fun putString(k: String, v: String?) = apply { map[k] = v }
        override fun putStringSet(k: String, v: MutableSet<String>?) = apply { map[k] = v?.toMutableSet() }
        override fun putInt(k: String, v: Int) = apply { map[k] = v }
        override fun putLong(k: String, v: Long) = apply { map[k] = v }
        override fun putFloat(k: String, v: Float) = apply { map[k] = v }
        override fun putBoolean(k: String, v: Boolean) = apply { map[k] = v }
        override fun remove(k: String) = apply { map.remove(k) }
        override fun clear() = apply { map.clear() }
        override fun commit() = true
        override fun apply() {}
    }
}

class PrefsContext(base: Context) : ContextWrapper(base) {
    private val prefs = FakePrefs()
    override fun getSharedPreferences(name: String?, mode: Int): SharedPreferences = prefs
    override fun getApplicationContext(): Context = this
}
