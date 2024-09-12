package com.koi.projectlib.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EditLinkModel(
    @SerialName("link")
    var link: String = "",
    @SerialName("linkText")
    var linkText: String = "",
)