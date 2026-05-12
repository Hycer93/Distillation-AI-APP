package com.distillation.app.data.persona

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 人格配置管理
 * M3: 版本号管理、自定义规则存储（加密）
 */
@Singleton
class PersonaConfig @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs by lazy {
        EncryptedSharedPreferences.create(
            "persona_config",
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /** 获取当前人格版本号 */
    fun getVersion(): String {
        return prefs.getString("persona_version", "V1.0") ?: "V1.0"
    }

    /** 更新人格版本号 */
    fun setVersion(version: String) {
        prefs.edit().putString("persona_version", version).apply()
    }

    /** 获取自定义补充规则 */
    fun getCustomRules(): String {
        return prefs.getString("custom_rules", "") ?: ""
    }

    /** 保存自定义补充规则 */
    fun setCustomRules(rules: String) {
        prefs.edit().putString("custom_rules", rules).apply()
    }
}