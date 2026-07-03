package com.animeai.app.feature.template

data class ChatTemplate(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val category: TemplateCategory,
    val prompt: String,
    val systemPrompt: String = "",
    val author: String = "官方",
    val downloads: Int = 0,
    val rating: Float = 0f,
    val tags: List<String> = emptyList(),
    val isBuiltIn: Boolean = true
)

enum class TemplateCategory(val displayName: String) {
    WRITING("写作创作"),
    CODING("编程开发"),
    LEARNING("学习辅导"),
    TRANSLATION("翻译"),
    BRAINSTORM("头脑风暴"),
    ANALYSIS("分析总结"),
    ROLEPLAY("角色扮演"),
    CUSTOM("自定义")
}

object BuiltInTemplates {
    val templates = listOf(
        ChatTemplate(
            name = "代码审查专家",
            description = "资深工程师帮你审查代码，找出潜在问题",
            category = TemplateCategory.CODING,
            prompt = "请帮我审查以下代码，找出潜在问题、性能瓶颈和安全漏洞，并给出改进建议：\n\n```\n[粘贴代码]\n```",
            systemPrompt = "你是一个资深的代码审查专家，精通多种编程语言。请从代码质量、性能、安全性、可维护性等方面进行全面审查。",
            tags = listOf("code-review", "best-practices", "security")
        ),
        ChatTemplate(
            name = "学术论文润色",
            description = "提升论文的学术写作水平",
            category = TemplateCategory.WRITING,
            prompt = "请帮我润色以下学术段落，使其更加正式、专业和流畅：\n\n[粘贴内容]",
            systemPrompt = "你是一个学术写作专家，擅长论文润色和学术表达优化。请保持原意的基础上提升表达质量。",
            tags = listOf("academic", "writing", "polish")
        ),
        ChatTemplate(
            name = "技术面试模拟",
            description = "模拟大厂技术面试场景",
            category = TemplateCategory.LEARNING,
            prompt = "请以面试官身份，模拟一场[岗位名称]的技术面试，从基础到深入提问。",
            systemPrompt = "你是一个资深的技术面试官，来自一线互联网公司。请模拟真实面试场景，考察候选人的技术深度和广度。",
            tags = listOf("interview", "tech", "career")
        ),
        ChatTemplate(
            name = "头脑风暴伙伴",
            description = "帮你拓展思路，激发创意",
            category = TemplateCategory.BRAINSTORM,
            prompt = "我们来围绕「[主题]」进行一次深度头脑风暴，请从多个角度提供创意方案。",
            systemPrompt = "你是一个创意总监，擅长头脑风暴和创意激发。请从不同维度提供创新思路，鼓励发散性思考。",
            tags = listOf("creative", "idea", "brainstorm")
        ),
        ChatTemplate(
            name = "中英互译专家",
            description = "专业级中英文翻译",
            category = TemplateCategory.TRANSLATION,
            prompt = "请将以下内容翻译成[目标语言]，注意语境和专业术语：\n\n[粘贴内容]",
            systemPrompt = "你是一个专业翻译专家，精通中英互译。请确保翻译准确、自然、符合目标语言习惯。",
            tags = listOf("translation", "english", "chinese")
        ),
        ChatTemplate(
            name = "SQL 优化大师",
            description = "优化SQL查询性能",
            category = TemplateCategory.CODING,
            prompt = "请优化以下SQL查询，添加索引建议和执行计划分析：\n\n```sql\n[粘贴SQL]\n```",
            systemPrompt = "你是一个数据库优化专家，精通SQL性能调优、索引设计和查询计划分析。",
            tags = listOf("sql", "database", "optimization")
        ),
        ChatTemplate(
            name = "英语学习伙伴",
            description = "陪你练习英语对话",
            category = TemplateCategory.LEARNING,
            prompt = "让我们用英语对话，话题是[主题]。请纠正我的语法错误并提供更好的表达方式。",
            systemPrompt = "你是一个英语教师，请用英语与用户对话，自然地纠正语法错误并给出更地道的表达方式。",
            tags = listOf("english", "learning", "practice")
        )
    )
}
