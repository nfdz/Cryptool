package io.github.nfdz.cryptool.ui.extension

import androidx.compose.ui.text.input.PasswordVisualTransformation

fun String.enforceSingleLine(): String = this.replace("\n", "")

private val passwordMask by lazy { PasswordVisualTransformation().mask }
fun String.hidePassword(): String = passwordMask.toString().repeat(this.length)