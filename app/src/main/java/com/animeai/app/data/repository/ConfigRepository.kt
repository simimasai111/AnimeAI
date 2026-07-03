package com.animeai.app.data.repository

import android.content.Context
import com.animeai.app.data.model.ApiType
import com.animeai.app.data.model.ModelConfig
import com.animeai.app.data.model.PresetConfigs
import com.animeai.app.data.model.ThinkingStrength
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ConfigRepository(private val context: Context) {
    private val gson = Gson()
    private val prefs = context.getSharedPreferences("anime_ai_config", Context.MODE_PRIVATE)
    private val customConfigsKey = "custom_model_configs"
    private val activeConfigKey = "active_model_config_id"

    fun getActiveConfig(): ModelConfig {
        val activeId = prefs.getString(activeConfigKey, null) ?: return PresetConfigs.presets[0]
        return getConfigById(activeId) ?: PresetConfigs.presets[0]
    }

    fun setActiveConfig(id: String) {
        prefs.edit().putString(activeConfigKey, id).apply()
    }

    fun getConfigById(id: String): ModelConfig? {
        val presets = PresetConfigs.presets.find { it.id == id }
        if (presets != null) return presets
        return getCustomConfigs().find { it.id == id }
    }

    fun getAllConfigs(): List<ModelConfig> {
        return PresetConfigs.presets + getCustomConfigs()
    }

    fun getCustomConfigs(): List<ModelConfig> {
        val json = prefs.getString(customConfigsKey, null) ?: return emptyList()
        val type = object : TypeToken<List<ModelConfig>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveCustomConfig(config: ModelConfig) {
        val configs = getCustomConfigs().toMutableList()
        val idx = configs.indexOfFirst { it.id == config.id }
        if (idx >= 0) {
            configs[idx] = config
        } else {
            configs.add(config.copy(
                id = "custom_${System.currentTimeMillis()}",
                isCustom = true
            ))
        }
        prefs.edit().putString(customConfigsKey, gson.toJson(configs)).apply()
    }

    fun deleteCustomConfig(id: String) {
        val configs = getCustomConfigs().toMutableList()
        configs.removeAll { it.id == id }
        prefs.edit().putString(customConfigsKey, gson.toJson(configs)).apply()
    }

    companion object {
        @Volatile
        private var INSTANCE: ConfigRepository? = null

        fun getInstance(context: Context): ConfigRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ConfigRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
