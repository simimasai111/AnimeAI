package com.animeai.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.animeai.app.feature.arena.ArenaScreen
import com.animeai.app.feature.arena.CrossValidationScreen
import com.animeai.app.feature.capsule.TimeCapsuleScreen
import com.animeai.app.feature.dashboard.DashboardState
import com.animeai.app.feature.knowledge.KnowledgeBaseScreen
import com.animeai.app.feature.persona_trainer.PersonaTrainerScreen
import com.animeai.app.feature.sandbox.CodeSandboxScreen
import com.animeai.app.feature.share.ShareCardScreen
import com.animeai.app.feature.sleep.SleepModeScreen
import com.animeai.app.feature.template.TemplateMarketScreen
import com.animeai.app.feature.tree.TreeScreen
import com.animeai.app.feature.workflow.PipelineScreen
import com.animeai.app.ui.screens.ChatScreen
import com.animeai.app.ui.screens.HomeScreen
import com.animeai.app.ui.screens.SettingsScreen
import com.animeai.app.viewmodel.ChatViewModel

object Routes {
    const val HOME = "home"
    const val CHAT = "chat/{conversationId}"
    const val CHAT_NEW = "chat_new"
    const val SETTINGS = "settings"
    const val ARENA = "arena"
    const val CROSS_VALIDATE = "cross_validate"
    const val TIME_CAPSULE = "time_capsule"
    const val TREE = "tree"
    const val SANDBOX = "sandbox"
    const val PIPELINE = "pipeline"
    const val KNOWLEDGE = "knowledge"
    const val TEMPLATE = "template"
    const val PERSONA_TRAINER = "persona_trainer"
    const val SHARE = "share"
    const val SLEEP = "sleep"

    fun chatRoute(id: String) = "chat/$id"
    fun chatNewRoute() = "chat_new"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    val conversations by viewModel.conversations.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentConversation by viewModel.currentConversation.collectAsState()
    val currentPersona by viewModel.currentPersona.collectAsState()
    val activeConfig by viewModel.activeConfig.collectAsState()
    val error by viewModel.error.collectAsState()
    val dashboardState by viewModel.dashboardState.collectAsState()
    val emotionAnalysis by viewModel.emotionAnalysis.collectAsState()

    val personaMap = conversations.mapNotNull { conv ->
        com.animeai.app.data.model.Personas.defaultPersonas.find { it.id == conv.personaId }?.let {
            conv.personaId to it
        }
    }.toMap()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                conversations = conversations,
                personaMap = personaMap,
                onNewChat = { persona ->
                    viewModel.createNewConversation(persona)
                    navController.navigate(Routes.CHAT_NEW)
                },
                onOpenChat = { id ->
                    viewModel.loadConversation(id)
                    navController.navigate(Routes.chatRoute(id))
                },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                onDeleteConversation = { id -> viewModel.deleteConversation(id) },
                onOpenArena = { navController.navigate(Routes.ARENA) },
                onOpenCrossValidate = { navController.navigate(Routes.CROSS_VALIDATE) },
                onOpenTimeCapsule = { navController.navigate(Routes.TIME_CAPSULE) },
                onOpenTree = { navController.navigate(Routes.TREE) },
                onOpenSandbox = { navController.navigate(Routes.SANDBOX) },
                onOpenPipeline = { navController.navigate(Routes.PIPELINE) },
                onOpenKnowledge = { navController.navigate(Routes.KNOWLEDGE) },
                onOpenTemplate = { navController.navigate(Routes.TEMPLATE) },
                onOpenPersonaTrainer = { navController.navigate(Routes.PERSONA_TRAINER) },
                onOpenShare = { navController.navigate(Routes.SHARE) },
                onOpenSleep = { navController.navigate(Routes.SLEEP) }
            )
        }

        composable(Routes.CHAT_NEW) {
            FullChatScreen(
                messages = messages,
                persona = currentPersona,
                isLoading = isLoading,
                dashboardState = dashboardState,
                emotionAnalysis = emotionAnalysis,
                onBack = {
                    viewModel.goBack()
                    navController.popBackStack()
                },
                onSendMessage = { text -> viewModel.sendMessage(text) },
                onGenerateImage = { prompt -> viewModel.generateImage(prompt) },
                onToggleDashboard = { viewModel.toggleDashboard() },
                onOpenArena = { navController.navigate(Routes.ARENA) },
                onOpenCrossValidate = { navController.navigate(Routes.CROSS_VALIDATE) },
                onOpenBranch = { navController.navigate(Routes.TREE) },
                onOpenShare = { navController.navigate(Routes.SHARE) },
                onOpenTemplate = { navController.navigate(Routes.TEMPLATE) },
                title = currentConversation?.title ?: "对话"
            )
        }

        composable(
            route = Routes.CHAT,
            arguments = listOf(navArgument("conversationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
            FullChatScreen(
                messages = messages,
                persona = currentPersona,
                isLoading = isLoading,
                dashboardState = dashboardState,
                emotionAnalysis = emotionAnalysis,
                onBack = {
                    viewModel.goBack()
                    navController.popBackStack()
                },
                onSendMessage = { text -> viewModel.sendMessage(text) },
                onGenerateImage = { prompt -> viewModel.generateImage(prompt) },
                onToggleDashboard = { viewModel.toggleDashboard() },
                onOpenArena = { navController.navigate(Routes.ARENA) },
                onOpenCrossValidate = { navController.navigate(Routes.CROSS_VALIDATE) },
                onOpenBranch = { navController.navigate(Routes.TREE) },
                onOpenShare = { navController.navigate(Routes.SHARE) },
                onOpenTemplate = { navController.navigate(Routes.TEMPLATE) },
                title = currentConversation?.title ?: "对话"
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                activeConfig = activeConfig,
                allConfigs = viewModel.getAllConfigs(),
                onBack = { navController.popBackStack() },
                onSelectConfig = { id -> viewModel.selectConfig(id) },
                onSaveConfig = { config -> viewModel.saveCustomConfig(config) },
                onDeleteConfig = { id -> viewModel.deleteCustomConfig(id) }
            )
        }

        composable(Routes.ARENA) { ArenaScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.CROSS_VALIDATE) { CrossValidationScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.TIME_CAPSULE) { TimeCapsuleScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.TREE) { TreeScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.SANDBOX) { CodeSandboxScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.PIPELINE) { PipelineScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.KNOWLEDGE) { KnowledgeBaseScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.TEMPLATE) {
            TemplateMarketScreen(
                onBack = { navController.popBackStack() },
                onUseTemplate = { template ->
                    navController.popBackStack()
                    viewModel.useTemplate(template)
                }
            )
        }
        composable(Routes.PERSONA_TRAINER) {
            PersonaTrainerScreen(
                onBack = { navController.popBackStack() },
                onSave = { blueprint -> viewModel.saveCustomPersona(blueprint) }
            )
        }
        composable(Routes.SHARE) { ShareCardScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.SLEEP) { SleepModeScreen(onBack = { navController.popBackStack() }) }
    }
}

@Composable
private fun FullChatScreen(
    messages: List<com.animeai.app.data.model.ChatMessage>,
    persona: com.animeai.app.data.model.Persona?,
    isLoading: Boolean,
    dashboardState: DashboardState,
    emotionAnalysis: com.animeai.app.feature.emotion.EmotionAnalysis?,
    onBack: () -> Unit,
    onSendMessage: (String) -> Unit,
    onGenerateImage: (String) -> Unit,
    onToggleDashboard: () -> Unit,
    onOpenArena: () -> Unit,
    onOpenCrossValidate: () -> Unit,
    onOpenBranch: () -> Unit,
    onOpenShare: () -> Unit,
    onOpenTemplate: () -> Unit,
    title: String
) {
    ChatScreen(
        messages = messages,
        persona = persona,
        isLoading = isLoading,
        dashboardState = dashboardState,
        emotionAnalysis = emotionAnalysis,
        onBack = onBack,
        onSendMessage = onSendMessage,
        onGenerateImage = onGenerateImage,
        onToggleDashboard = onToggleDashboard,
        onOpenArena = onOpenArena,
        onOpenCrossValidate = onOpenCrossValidate,
        onOpenBranch = onOpenBranch,
        onOpenShare = onOpenShare,
        onOpenTemplate = onOpenTemplate,
        title = title
    )
}
