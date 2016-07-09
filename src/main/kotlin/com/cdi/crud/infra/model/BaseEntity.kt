package com.cdi.crud.infra.model

import java.io.Serializable

/**
 * Created by rmpestano on 6/10/16.
 */
interface BaseEntity : Serializable {

    fun getId(): Serializable

}
