# 蒸馏仔 · Distillation-Agent-App

**面向AI辅助开发的模块化Android AI Agent框架**

## 📖 项目概述

蒸馏仔是一个面向手机端的轻量化AI对话框架，基于Kotlin + Jetpack Compose + MVVM + Room + Hilt + Retrofit构建。

本仓库**不是**完整的可编译项目——而是**面向AI辅助开发的标准化模块交付框架**。每个模块独立成文，包含完整的文件清单、开发步骤、核心代码和验收标准。开发者按M1→M2→M3→M4顺序阅读，逐模块创建代码并验收通过后，自然形成一个完整的Android AI Agent APP。

## 📦 仓库结构
```text
distillation-agent-app/
├── README.md # 当前文档
├── LICENSE # Apache 2.0
└── modules/
├── M1_项目骨架_API连接层.md # 模块一：项目骨架 + API连接层
├── M2_本地存储层_Room数据库.md # 模块二：本地存储层 + 历史对话加载
├── M3_人格加载系统.md # 模块三：三段式注入 + System Prompt拼装
└── M4_记忆复活模块.md # 模块四：记忆复活 + 上下文Token管理


## 📋 模块索引
```text
| 模块 | 核心目标 | 预计工时 | 前置依赖 |
|:---|:---|:---|:---|
| M1 | 创建可编译项目，成功调用DeepSeek API | 1-2天 | 无 |
| M2 | Room数据库 + 对话记录持久化 + 历史对话恢复 | 1天 | M1 |
| M3 | Asset资源加载 + System Prompt三段式拼装 | 1-2天 | M1, M2 |
| M4 | 新会话记忆注入 + 显式触发历史检索 + Token管理 | 1天 | M1, M2 |

## 🚀 快速开始

1. **按M1→M2→M3→M4顺序阅读模块文档**，每个文件包含完整的开发步骤、核心代码和验收标准。
2. **逐模块创建代码**，通过当前模块的全部验收标准后再进入下一模块。
3. **四个模块全部验收通过后**，你将拥有一个具备完整对话能力和本地记忆的Android AI Agent APP基础骨架。

## 🔧 技术栈

- Kotlin 2.0+ · Jetpack Compose + Material 3 · MVVM + Repository
- Retrofit + OkHttp + Kotlin Serialization · Room 2.6+
- Hilt 2.48+ · Kotlin Coroutines + Flow · EncryptedSharedPreferences

## 🧠 核心架构

- **三段式人格加载**：静态核心 → 规则摘要 → 动态成长（闪念记忆注入）
- **双层存储**：Asset只读（出厂基因） + Room可写（成长日记）
- **记忆复活**：新会话自动注入记忆摘要，显式触发回溯检索
- **Token管理**：滑动窗口自动裁剪，实时用量统计

## ⚠️ 重要说明

本交付包为**基础功能MVP版本**，不包含归元仔Agent体的完整认知架构。核心进化系统、认知调度协议、专利级能力保留闭源，基础框架完全开放给社区。

基于本项目开发的APP需自行评估相关专利风险。

## 📜 许可

Apache 2.0 License