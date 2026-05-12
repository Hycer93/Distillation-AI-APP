package com.distillation.app.ui.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

/**
 * 聊天界面
 * M1: Compose实现的对话主界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        // 标题栏
        TopAppBar(
            title = { Text("蒸馏仔") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            actions = {
                TextButton(onClick = { viewModel.clearConversation() }) {
                    Text("清空")
                }
            }
        )

        // 对话列表
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            reverseLayout = false
        ) {
            items(uiState.messages) { message ->
                MessageBubble(message)
            }
            if (uiState.isLoading) {
                item {
                    Text(
                        "蒸馏仔思考中...",
                        modifier = Modifier.padding(16.dp),
                        color = Color.Gray
                    )
                }
            }
        }

        // 输入区域
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("给蒸馏仔发消息...") },
                maxLines = 4
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendMessage(inputText)
                        inputText = ""
                        coroutineScope.launch {
                            listState.animateScrollToItem(uiState.messages.size)
                        }
                    }
                },
                enabled = !uiState.isLoading
            ) {
                Text("发送")
            }
        }
    }
}

/**
 * 消息气泡组件
 */
@Composable
fun MessageBubble(message: ChatMessage) {
    val isUser = message.role == "user"
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val backgroundColor = if (isUser)
        MaterialTheme.colorScheme.primaryContainer
    else
        MaterialTheme.colorScheme.secondaryContainer

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = alignment
    ) {
        Text(
            text = if (isUser) "厂长" else "蒸馏仔",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = backgroundColor,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}