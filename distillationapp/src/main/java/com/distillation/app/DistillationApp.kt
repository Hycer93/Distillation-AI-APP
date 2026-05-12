package com.distillation.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * 蒸馏仔 Application 入口
 * M1: 初始化Hilt依赖注入框架
 */
@HiltAndroidApp
class DistillationApp : Application()