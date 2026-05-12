package com.distillation.app.data.persona

import android.content.Context
import com.distillation.app.data.local.dao.FlashMemoryDao
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 人格管理器 — 三段式 System Prompt 拼装
 * M3: 静态核心(出厂设置+表达系统) + 规则摘要(策略+认知) + 动态成长(闪念记忆+持仓快照)
 */
@Singleton
class PersonaManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val flashMemoryDao: FlashMemoryDao,
    private val personaConfig: PersonaConfig
) {
    // 缓存静态核心文件，避免重复读取 Asset
    private var cachedCorePersona: String? = null
    private var cachedStrategySummary: String? = null

    /**
     * 核心方法：组装完整 System Prompt
     * 三段式：
     *   第一段 — 出厂设置 + 人格表达系统（完整加载）
     *   第二段 — 策略手册核心摘要 + 核心认知资产
     *   第三段 — 闪念记忆 + 持仓快照（动态读取 Room）
     */
    suspend fun assembleSystemPrompt(): String {
        val sb = StringBuilder()

        // 第一段：静态核心人格
        sb.appendLine(loadCorePersona())
        sb.appendLine()

        // 第二段：规则摘要
        sb.appendLine(loadStrategySummary())
        sb.appendLine()

        // 第三段：动态成长数据
        sb.appendLine(loadDynamicGrowth())
        sb.appendLine()

        // 版本信息
        sb.appendLine("【人格版本】${personaConfig.getVersion()}")

        // 自定义规则（如果有）
        val customRules = personaConfig.getCustomRules()
        if (customRules.isNotBlank()) {
            sb.appendLine("\n【自定义规则】")
            sb.appendLine(customRules)
        }

        return sb.toString()
    }

    /**
     * 第一段：加载出厂设置 + 人格表达系统
     */
    private fun loadCorePersona(): String {
        if (cachedCorePersona == null) {
            val core = StringBuilder()

            // 出厂设置
            try {
                core.appendLine(
                    context.assets.open("蒸馏仔_出厂设置.md")
                        .bufferedReader().use { it.readText() }
                )
            } catch (e: Exception) {
                core.appendLine("【蒸馏仔_出厂设置】加载失败：${e.message}")
            }
            core.appendLine()

            // 人格表达系统
            try {
                core.appendLine(
                    context.assets.open("蒸馏仔_技能体系.md")
                        .bufferedReader().use { it.readText() }
                )
            } catch (e: Exception) {
                core.appendLine("【人格表达系统】加载失败，使用默认语言风格")
            }

            cachedCorePersona = core.toString()
        }
        return cachedCorePersona!!
    }

    /**
     * 第二段：加载策略手册核心摘要 + 核心认知资产
     */
    private fun loadStrategySummary(): String {
        if (cachedStrategySummary == null) {
            val summary = StringBuilder()

            try {
                summary.appendLine()
                summary.appendLine(
                    context.assets.open("蒸馏仔_记忆档案.md")
                        .bufferedReader().use { it.readText() }
                )
            } catch (e: Exception) {
                // 非致命错误
            }

            cachedStrategySummary = summary.toString()
        }
        return cachedStrategySummary!!
    }

    /**
     * 第三段：动态成长数据（最新闪念记忆 + 当前持仓快照）
     */
    private suspend fun loadDynamicGrowth(): String {
        val sb = StringBuilder()

        // 最新闪念记忆（最多 20 条）
        val flashMemories = flashMemoryDao.getLatest(20)
        if (flashMemories.isNotEmpty()) {
            sb.appendLine("【成长记忆·闪念】")
            flashMemories.forEach { memory ->
                sb.appendLine("- [${memory.category}] ${memory.content}")
            }
            sb.appendLine()
        }

        return sb.toString()
    }

    /** 获取 skills/ 下所有文件名（供扩展使用） */
    fun getSkillFileList(): List<String> {
        return try {
            context.assets.list("skills/")?.map { "skills/$it" } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /** 清除缓存（Asset 资源更新后重新加载） */
    fun clearCache() {
        cachedCorePersona = null
        cachedStrategySummary = null
    }
}