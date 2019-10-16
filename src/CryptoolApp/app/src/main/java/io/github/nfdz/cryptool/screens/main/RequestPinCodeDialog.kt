package io.github.nfdz.cryptool.screens.main

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AlertDialog
import io.github.nfdz.cryptool.R
import io.github.nfdz.cryptool.common.utils.*
import kotlinx.android.synthetic.main.dialog_request_pin.*


class RequestPinCodeDialog(
    private val onSuccessListener: () -> (Unit),
    private val setPinMode: Boolean,
    context: Context
) : AlertDialog(context) {

    companion object {
        fun show(
            context: Context,
            setPinMode: Boolean,
            onSuccessListener: () -> (Unit)
        ) {
            RequestPinCodeDialog(onSuccessListener, setPinMode, context).apply {
                setCancelable(setPinMode)
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

    private val set0 = CODE_SET_0.toCharArray().toMutableList().apply { shuffle() }
    private val set1 = CODE_SET_1.toCharArray().toMutableList().apply { shuffle() }
    private val set2 = CODE_SET_2.toCharArray().toMutableList().apply { shuffle() }

    private val prefs: PreferencesHelper by lazy { PreferencesHelper(getContext()) }

    private var selectedSet = set0
    private var selectedSetIndex = 0
    private var inputCode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_request_pin)
        setupInputCode()
        setupPinPad()
        setupActionsListeners()
        setupPinPadListeners()

    }

    private fun setupActionsListeners() {
        pin_code_set_previous.setOnClickListener {
            selectedSetIndex = if (selectedSetIndex == 0) 2 else selectedSetIndex - 1
            updateSet()
        }
        pin_code_set_next.setOnClickListener {
            selectedSetIndex = if (selectedSetIndex == 2) 0 else selectedSetIndex + 1
            updateSet()
        }
        if (setPinMode) {
            pin_code_save.visibility = View.VISIBLE
            pin_code_save.setOnClickListener {
                if (inputCode.length < CODE_MIN_LENGTH) {
                    context.toast(R.string.pin_size_min)
                } else {
                    prefs.setCode(inputCode)
                    onSuccessListener()
                    dismiss()
                }
            }
        }
    }

    private fun updateSet() {
        selectedSet = when (selectedSetIndex) {
            2 -> set2
            1 -> set1
            else -> set0
        }
        setupPinPad()
    }

    private fun setupPinPadListeners() {
        val pinPadHandler: (Char) -> (Unit) = { input ->
            inputCode += input
            checkCode()
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
        val success: Boolean = !setPinMode && prefs.getCode() == inputCode
        if (success) {
            onSuccessListener()
            dismiss()
        } else {
            val lengthToClear = if (setPinMode) 9 else 8
            if (inputCode.length >= lengthToClear) {
                inputCode = ""
            }
            setupInputCode()
        }
    }

}