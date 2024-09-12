package com.koi.projectlib.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UndoRedoModel(
    @SerialName("canUndo")
    var canUndo: String = "",
    @SerialName("canRedo")
    var canRedo: String = "",
)