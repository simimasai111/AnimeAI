package com.animeai.app.service

import com.animeai.app.data.model.Persona
import com.animeai.app.data.model.PersonaType
import com.animeai.app.data.model.Personas

object PersonaService {
    fun getDefaultPersonas(): List<Persona> = Personas.defaultPersonas

    fun getPersonaById(id: String): Persona? {
        return Personas.defaultPersonas.find { it.id == id }
    }

    fun getPersonasByType(type: PersonaType): List<Persona> {
        return Personas.defaultPersonas.filter { it.type == type }
    }

    fun getAnimePersonas(): List<Persona> = getPersonasByType(PersonaType.ANIME)

    fun getAssistantPersonas(): List<Persona> = getPersonasByType(PersonaType.ASSISTANT)

    fun getDeveloperPersonas(): List<Persona> = getPersonasByType(PersonaType.DEVELOPER)

    fun getGreeting(personaId: String): String {
        return getPersonaById(personaId)?.greeting ?: "你好！有什么可以帮助你的吗？"
    }
}
