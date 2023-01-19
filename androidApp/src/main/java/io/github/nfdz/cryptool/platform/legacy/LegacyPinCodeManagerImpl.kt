package io.github.nfdz.cryptool.platform.legacy

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import io.github.nfdz.cryptool.R
import io.github.nfdz.cryptool.shared.platform.storage.LegacyPreferencesStorage
import io.github.nfdz.cryptool.ui.platform.LegacyPinCodeManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.*

object LegacyPinCodeManagerImpl : LegacyPinCodeManager {
    override fun askCode(onSuccessListener: () -> Unit, onDeleteListener: () -> Unit, activity: FragmentActivity) {
        LegacyPinCodeDialog.show(
            onSuccessListener = onSuccessListener,
            onDeleteListener = onDeleteListener,
            context = activity,
        )
    }
}

class LegacyPinCodeDialog(
    private val onSuccessListener: () -> (Unit),
    private val onDeleteListener: () -> (Unit),
    context: Context
) : AlertDialog(context), CoroutineScope by MainScope() {

    companion object {
        private const val CODE_SET_0 = "146aZd?.-"
        private const val CODE_SET_1 = "832VgJ$+&"
        private const val CODE_SET_2 = "579iXW=€%"
        private const val CODE_SET_3 = "!uRj0@|#£"
        private const val CODE_INPUT = "*"
        private const val CODE_SLOT = "_"
        const val DEFAULT_CODE = "00"
        var CODE = DEFAULT_CODE
            private set

        fun show(
            context: Context,
            onSuccessListener: () -> (Unit),
            onDeleteListener: () -> (Unit),
        ) {
            LegacyPinCodeDialog(
                onSuccessListener = onSuccessListener,
                onDeleteListener = onDeleteListener,
                context = context,
            ).apply {
                setCancelable(false)
                requestWindowFeature(Window.FEATURE_NO_TITLE)
            }.run {
                show()
                window?.setLayout(
                    context.resources.getDimensionPixelSize(R.dimen.legacy_pin_code_dialog_size),
                    context.resources.getDimensionPixelSize(R.dimen.legacy_pin_code_dialog_size),
                )
            }
        }
    }

    // Shuffle sets content
    private val set0 = CODE_SET_0.toCharArray().toMutableList().apply { shuffle() }
    private val set1 = CODE_SET_1.toCharArray().toMutableList().apply { shuffle() }
    private val set2 = CODE_SET_2.toCharArray().toMutableList().apply { shuffle() }
    private val set3 = CODE_SET_3.toCharArray().toMutableList().apply { shuffle() }

    private val prefs: LegacyPreferencesStorage by lazy { LegacyPreferencesStorageAndroid(getContext()) }

    private var selectedSet = getInitialSetRandomly()
    private var inputCode: String = ""

    private val legacy_pin_code_tv_1: TextView by lazy { findViewById(R.id.legacy_pin_code_tv_1)!! }
    private val legacy_pin_code_tv_2: TextView by lazy { findViewById(R.id.legacy_pin_code_tv_2)!! }
    private val legacy_pin_code_tv_3: TextView by lazy { findViewById(R.id.legacy_pin_code_tv_3)!! }
    private val legacy_pin_code_tv_4: TextView by lazy { findViewById(R.id.legacy_pin_code_tv_4)!! }
    private val legacy_pin_code_tv_5: TextView by lazy { findViewById(R.id.legacy_pin_code_tv_5)!! }
    private val legacy_pin_code_tv_6: TextView by lazy { findViewById(R.id.legacy_pin_code_tv_6)!! }
    private val legacy_pin_code_tv_7: TextView by lazy { findViewById(R.id.legacy_pin_code_tv_7)!! }
    private val legacy_pin_code_tv_8: TextView by lazy { findViewById(R.id.legacy_pin_code_tv_8)!! }
    private val legacy_pin_code_tv_9: TextView by lazy { findViewById(R.id.legacy_pin_code_tv_9)!! }

    private val legacy_pin_input_tv_1: TextView by lazy { findViewById(R.id.legacy_pin_input_tv_1)!! }
    private val legacy_pin_input_tv_2: TextView by lazy { findViewById(R.id.legacy_pin_input_tv_2)!! }
    private val legacy_pin_input_tv_3: TextView by lazy { findViewById(R.id.legacy_pin_input_tv_3)!! }
    private val legacy_pin_input_tv_4: TextView by lazy { findViewById(R.id.legacy_pin_input_tv_4)!! }
    private val legacy_pin_input_tv_5: TextView by lazy { findViewById(R.id.legacy_pin_input_tv_5)!! }
    private val legacy_pin_input_tv_6: TextView by lazy { findViewById(R.id.legacy_pin_input_tv_6)!! }
    private val legacy_pin_input_tv_7: TextView by lazy { findViewById(R.id.legacy_pin_input_tv_7)!! }
    private val legacy_pin_input_tv_8: TextView by lazy { findViewById(R.id.legacy_pin_input_tv_8)!! }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.legacy_dialog_request_pin)
        setupInputCode()
        setupPinPad()
        setupActionsListeners()
        setupPinPadListeners()
    }

    private fun getInitialSetRandomly(): List<Char> {
        return when (Random().nextInt(4)) {
            3 -> set3
            2 -> set2
            1 -> set1
            else -> set0
        }
    }

    private fun setupActionsListeners() {
        findViewById<View>(R.id.legacy_pin_code_set_previous)!!.setOnClickListener {
            val newSet = when (selectedSet) {
                set0 -> set3
                set1 -> set0
                set2 -> set1
                set3 -> set2
                else -> set0
            }
            updateSet(newSet)
        }
        findViewById<View>(R.id.legacy_pin_code_set_next)!!.setOnClickListener {
            val newSet = when (selectedSet) {
                set0 -> set1
                set1 -> set2
                set2 -> set3
                set3 -> set0
                else -> set0
            }
            updateSet(newSet)
        }
        findViewById<View>(R.id.legacy_pin_code_clear)!!.setOnClickListener {
            inputCode = ""
            updateInputCode()
        }
        findViewById<View>(R.id.legacy_pin_code_reset)!!.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                askConfirmationToResetPin {
                    launch {
                        CODE = DEFAULT_CODE
                        prefs.deleteAll()
                        onDeleteListener()
                        dismiss()
                    }
                }
            }
        }
    }

    private fun askConfirmationToResetPin(onConfirmation: () -> (Unit)) {
        Builder(context)
            .setTitle(R.string.gatekeeper_delete_code_dialog_title)
            .setMessage(R.string.gatekeeper_delete_code_dialog_description)
            .setPositiveButton(R.string.gatekeeper_delete_code_dialog_action) { dialog, _ ->
                onConfirmation()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.dialog_cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun updateSet(newSet: List<Char>) {
        selectedSet = newSet
        setupPinPad()
    }

    private fun setupPinPadListeners() {
        val pinPadHandler: (Char) -> (Unit) = { input ->
            inputCode += input
            checkCode()
        }
        legacy_pin_code_tv_1.setOnClickListener { pinPadHandler(selectedSet[0]) }
        legacy_pin_code_tv_2.setOnClickListener { pinPadHandler(selectedSet[1]) }
        legacy_pin_code_tv_3.setOnClickListener { pinPadHandler(selectedSet[2]) }
        legacy_pin_code_tv_4.setOnClickListener { pinPadHandler(selectedSet[3]) }
        legacy_pin_code_tv_5.setOnClickListener { pinPadHandler(selectedSet[4]) }
        legacy_pin_code_tv_6.setOnClickListener { pinPadHandler(selectedSet[5]) }
        legacy_pin_code_tv_7.setOnClickListener { pinPadHandler(selectedSet[6]) }
        legacy_pin_code_tv_8.setOnClickListener { pinPadHandler(selectedSet[7]) }
        legacy_pin_code_tv_9.setOnClickListener { pinPadHandler(selectedSet[8]) }
    }

    private fun setupPinPad() {
        legacy_pin_code_tv_1.text = selectedSet[0].toString()
        legacy_pin_code_tv_2.text = selectedSet[1].toString()
        legacy_pin_code_tv_3.text = selectedSet[2].toString()
        legacy_pin_code_tv_4.text = selectedSet[3].toString()
        legacy_pin_code_tv_5.text = selectedSet[4].toString()
        legacy_pin_code_tv_6.text = selectedSet[5].toString()
        legacy_pin_code_tv_7.text = selectedSet[6].toString()
        legacy_pin_code_tv_8.text = selectedSet[7].toString()
        legacy_pin_code_tv_9.text = selectedSet[8].toString()
    }

    private fun setupInputCode() {
        val inputLength = inputCode.length
        legacy_pin_input_tv_1.text = if (inputLength >= 1) CODE_INPUT else CODE_SLOT
        legacy_pin_input_tv_2.text = if (inputLength >= 2) CODE_INPUT else CODE_SLOT
        legacy_pin_input_tv_3.text = if (inputLength >= 3) CODE_INPUT else CODE_SLOT
        legacy_pin_input_tv_4.text = if (inputLength >= 4) CODE_INPUT else CODE_SLOT
        legacy_pin_input_tv_5.text = if (inputLength >= 5) CODE_INPUT else CODE_SLOT
        legacy_pin_input_tv_6.text = if (inputLength >= 6) CODE_INPUT else CODE_SLOT
        legacy_pin_input_tv_7.text = if (inputLength >= 7) CODE_INPUT else CODE_SLOT
        legacy_pin_input_tv_8.text = if (inputLength >= 8) CODE_INPUT else CODE_SLOT
    }

    private fun checkCode() = launch {
        CODE = inputCode
        val success: Boolean = prefs.getCode() == inputCode
        if (success) {
            onSuccessListener()
            dismiss()
        } else {
            updateInputCode()
        }
    }

    private fun updateInputCode() {
        val lengthToClear = 8
        if (inputCode.length >= lengthToClear) {
            inputCode = ""
        }
        setupInputCode()
    }

}