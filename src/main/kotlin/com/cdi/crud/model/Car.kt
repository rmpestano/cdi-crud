package com.cdi.crud.model

import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.*

/**
 * Created by rmpestano on 6/9/16.
 */
@Entity
data class Car(
        @field:Id
        @field:GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
        var id: Long? = null,

        @field:NotNull
        @field:Size(min = 1, max = 200)
        var name: String? = null,

        var model: String? = null,
        var price: Double? = null,
        @field:Version
        var version: Int? = null

) : Serializable
