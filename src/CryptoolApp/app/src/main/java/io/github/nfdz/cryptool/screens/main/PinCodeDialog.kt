package io.github.nfdz.cryptool.screens.main

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AlertDialog
import io.github.nfdz.cryptool.R
import io.github.nfdz.cryptool.common.utils.*
import kotlinx.android.synthetic.main.dialog_request_pin.*
import java.util.*

/**
 * This custom dialog show PIN code pad and handle the input of user.
 */
class PinCodeDialog(
    private val onSuccessListener: () -> (Unit),
    private val createPinMode: Boolean,
    context: Context
) : AlertDialog(context) {

    companion object {
        /**
         * Create and show the dialog.
         * If createPinMode is false, dialog will not be cancelable and it will only exit and execute
         * onSuccessListener when user match the stored code.
         * If it is true, dialog will only execute onSuccessListener
         * when user save the PIN successfully.
         */
        fun show(
            context: Context,
            createPinMode: Boolean,
            onSuccessListener: () -> (Unit)
        ) {
            PinCodeDialog(onSuccessListener, createPinMode, context).apply {
                setCancelable(createPinMode)
                requestWindowFeature(Window.FEATURE_NO_TITLE)
            }.run {
                show()
                window?.setLayout(
                    context.resources.getDimensionPixelSize(R.dimen.pin_code_dialog_width),
                    context.resources.getDimensionPixelSize(R.dimen.pin_code_dialog_height)
                )
            }
        }
    }

    // Shuffle sets content
    private val set0 = CODE_SET_0.toCharArray().toMutableList().apply { shuffle() }
    private val set1 = CODE_SET_1.toCharArray().toMutableList().apply { shuffle() }
    private val set2 = CODE_SET_2.toCharArray().toMutableList().apply { shuffle() }

    private val prefs: PreferencesHelper by lazy { PreferencesHelper(getContext()) }

    private var selectedSet = getInitialSetRandomly()
    private var inputCode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_request_pin)
        setupInputCode()
        setupPinPad()
        setupActionsListeners()
        setupPinPadListeners()

    }

    private fun getInitialSetRandomly(): List<Char> {
        return when (Random().nextInt(3)) {
            2 -> set2
            1 -> set1
            else -> set0
        }
    }

    private fun setupActionsListeners() {
        pin_code_set_previous.setOnClickListener {
            val newSet = when (selectedSet) {
                set0 -> set2
                set1 -> set0
                set2 -> set1
                else -> set0
            }
            updateSet(newSet)
        }
        pin_code_set_next.setOnClickListener {
            val newSet = when (selectedSet) {
                set0 -> set1
                set1 -> set2
                set2 -> set0
                else -> set0
            }
            updateSet(newSet)
        }
        if (createPinMode) {
            pin_code_save.visibility = View.VISIBLE
            pin_code_save.setOnClickListener {
                if (inputCode.length < CODE_MIN_LENGTH) {
                    context.toast(R.string.pin_size_min)
                } else {
                    CODE = inputCode
                    prefs.setCode(inputCode)
                    onSuccessListener()
                    dismiss()
                }
            }
        }
    }

    private fun updateSet(newSet: List<Char>) {
        selectedSet = newSet
        setupPinPad()
    }

    private fun setupPinPadListeners() {
        val pinPadHandler: (Char) -> (Unit) = { input ->
            inputCode += input
            if (createPinMode) {
                updateInputCode()
            } else {
                checkCode()
            }
        }
        pin_code_tv_1.setOnClickListener { pinPadHandler(selectedSet[0]) }
        pin_code_tv_2.setOnClickListener { pinPadHandler(selectedSet[1]) }
        pin_code_tv_3.setOnClickListener { pinPadHandler(selectedSet[2]) }
        pin_code_tv_4.setOnClickListener { pinPadHandler(selectedSet[3]) }
        pin_code_tv_5.setOnClickListener { pinPadHandler(selectedSet[4]) }
        pin_code_tv_6.setOnClickListener { pinPadHandler(selectedSet[5]) }
        pin_code_tv_7.setOnClickListener { pinPadHandler(selectedSet[6]) }
        pin_code_tv_8.setOnClickListener { pinPadHandler(selectedSet[7]) }
        pin_code_tv_9.setOnClickListener { pinPadHandler(selectedSet[8]) }
    }

    private fun setupPinPad() {
        pin_code_tv_1.text = selectedSet[0].toString()
        pin_code_tv_2.text = selectedSet[1].toString()
        pin_code_tv_3.text = selectedSet[2].toString()
        pin_code_tv_4.text = selectedSet[3].toString()
        pin_code_tv_5.text = selectedSet[4].toString()
        pin_code_tv_6.text = selectedSet[5].toString()
        pin_code_tv_7.text = selectedSet[6].toString()
        pin_code_tv_8.text = selectedSet[7].toString()
        pin_code_tv_9.text = selectedSet[8].toString()
    }

    private fun setupInputCode() {
        val inputLength = inputCode.length
        pin_input_tv_1.text = if (inputLength >= 1) CODE_INPUT else CODE_SLOT
        pin_input_tv_2.text = if (inputLength >= 2) CODE_INPUT else CODE_SLOT
        pin_input_tv_3.text = if (inputLength >= 3) CODE_INPUT else CODE_SLOT
        pin_input_tv_4.text = if (inputLength >= 4) CODE_INPUT else CODE_SLOT
        pin_input_tv_5.text = if (inputLength >= 5) CODE_INPUT else CODE_SLOT
        pin_input_tv_6.text = if (inputLength >= 6) CODE_INPUT else CODE_SLOT
        pin_input_tv_7.text = if (inputLength >= 7) CODE_INPUT else CODE_SLOT
        pin_input_tv_8.text = if (inputLength >= 8) CODE_INPUT else CODE_SLOT
    }

    private fun checkCode() {
        CODE = inputCode
        val success: Boolean = !createPinMode && prefs.getCode() == inputCode
        if (success) {
            onSuccessListener()
            dismiss()
        } else {
            updateInputCode()
        }
    }

    private fun updateInputCode() {
        val lengthToClear = if (createPinMode) 9 else 8
        if (inputCode.length >= lengthToClear) {
            inputCode = ""
        }
        setupInputCode()
    }

}