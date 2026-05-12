package com.distillation.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.distillation.app.ui.chat.ChatScreen
import com.distillation.app.ui.chat.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * 唯一Activity，Compose宿主
 * M1: 渲染ChatScreen作为主界面
 * M3: 启动时初始化会话（加载System Prompt）
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val chatViewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // M3: 启动时初始化会话（创建会话 + 加载System Prompt）
        chatViewModel.initConversation()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChatScreen(viewModel = chatViewModel)
                }
            }
        }
    }
}