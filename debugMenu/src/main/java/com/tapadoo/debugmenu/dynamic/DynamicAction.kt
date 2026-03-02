package com.tapadoo.debugmenu.dynamic

/**
 * Represents a dynamic action with a title, an optional description, and an action to be executed when clicked.
 *
 * @property title The title of the action.
 * @property description An optional description providing additional details about the action.
 * @property onClick A lambda function to be executed when the action is triggered.
 */
data class DynamicAction(
    val title: String,
    val description: String? = null,
    val onClick: () -> Unit
)
