package org.ionproject.codegarten.controllers.models

import com.fasterxml.jackson.annotation.JsonIgnore
import org.ionproject.codegarten.responses.siren.Siren
import org.ionproject.codegarten.responses.siren.SirenAction
import org.ionproject.codegarten.responses.siren.SirenClass
import org.ionproject.codegarten.responses.siren.SirenLink

abstract class OutputModel {

    @JsonIgnore
    abstract fun getSirenClasses(): List<SirenClass>

    fun toSirenObject(
        rel: List<String>? = null,
        entities: List<Any>? = null,
        actions: List<SirenAction>? = null,
        links: List<SirenLink>
    ) = Siren(
        clazz = getSirenClasses(),
        rel = rel,
        this,
        entities = entities,
        actions = actions,
        links = links
    )
}