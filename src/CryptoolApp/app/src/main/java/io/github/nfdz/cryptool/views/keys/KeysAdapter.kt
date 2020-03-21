package io.github.nfdz.cryptool.views.keys

import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.nfdz.cryptool.R
import io.github.nfdz.cryptool.common.utils.*
import io.github.nfdz.cryptool.common.widgets.InputTextBoxView
import io.github.nfdz.cryptool.common.widgets.OutputTextBoxView
import kotlin.properties.Delegates


class KeysAdapter(private val listener: Listener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val KEY_TYPE = 0
        private const val CREATE_KEY_TYPE = 1
    }

    interface Listener {
        fun onCreateKey(label: String, key: String)
        fun onRemoveKey(index: Int)
    }

    val keysToShow = HashSet<Int>()

    var data by Delegates.observable(emptyList<KeysContract.KeyEntry>()) { _, oldList, newList ->
        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = oldList.size
            override fun getNewListSize(): Int = newList.size
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition] == newList[newItemPosition]
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition] == newList[newItemPosition]
            }
        })
        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == CREATE_KEY_TYPE) {
            val container = LinearLayout(parent.context)
            container.layoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            container.orientation = LinearLayout.VERTICAL
            val labelInput = InputTextBoxView(parent.context)
            val keyInput = InputTextBoxView(parent.context)
            container.addView(labelInput)
            container.addView(keyInput)
            CreateKeyViewHolder(container, labelInput, keyInput, listener)
        } else {
            val view = OutputTextBoxView(parent.context)
            val lp = RecyclerView.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.bottomMargin =
                parent.context.resources.getDimensionPixelSize(R.dimen.activity_vertical_margin)
            view.layoutParams = lp
            KeyViewHolder(view, keysToShow, listener) {
                notifyDataSetChanged()
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is KeyViewHolder) {
            holder.bind(data[position])
        }
    }

    override fun getItemCount(): Int = data.size + 1

    override fun getItemViewType(position: Int): Int {
        return if (position < data.size) {
            KEY_TYPE
        } else {
            CREATE_KEY_TYPE
        }
    }

    class CreateKeyViewHolder(
        view: View,
        labelInput: InputTextBoxView,
        keyInput: InputTextBoxView,
        private val listener: Listener
    ) : RecyclerView.ViewHolder(view) {
        init {
            labelInput.setupView(
                R.color.colorDark,
                R.drawable.selector_action_dark,
                R.color.colorLight,
                R.drawable.ic_short_text,
                R.string.keys_create_label
            )
            keyInput.setupView(
                R.color.colorDark,
                R.drawable.selector_action_dark,
                R.color.colorLight,
                R.drawable.ic_passphrase,
                R.string.keys_create_key
            )
            labelInput.setInputType(InputType.TYPE_CLASS_TEXT)
            keyInput.setInputType(InputType.TYPE_CLASS_TEXT)
            keyInput.setupAction1Icon(R.drawable.ic_add, R.color.colorLight)
            keyInput.setupAction1 {
                val label = labelInput.getText()
                val key = keyInput.getText()
                when {
                    label.isEmpty() && key.isEmpty() -> view.context.toast(R.string.error_label_key_empty)
                    label.isEmpty() -> view.context.toast(R.string.error_label_empty)
                    key.isEmpty() -> view.context.toast(R.string.error_key_empty)
                    else -> {
                        listener.onCreateKey(label, key)
                        labelInput.setText("")
                        keyInput.setText("")
                        keyInput.hideKeyboard()
                    }
                }
            }
            keyInput.setupAction2Icon(R.drawable.ic_paste, R.color.colorLight)
            keyInput.setupAction2 {
                view.context?.let {
                    ClipboardHelper.pasteText(it) { pasteText ->
                        keyInput.setText(pasteText)
                    }
                }
            }
            keyInput.setupAction3Icon(R.drawable.ic_random_line, R.color.colorLight)
            keyInput.setupAction3 {
                keyInput.setText(generateRandomKey())
            }
        }
    }

    class KeyViewHolder(
        private val keyOutput: OutputTextBoxView,
        private val keysToShow: MutableSet<Int>,
        private val listener: Listener,
        private val notifyDataChanged: () -> (Unit)
    ) :
        RecyclerView.ViewHolder(keyOutput) {
        init {
            val actionIconColor = getActionIconColor()
            keyOutput.setupAction1Icon(R.drawable.ic_copy, actionIconColor)
            keyOutput.setupAction2Icon(R.drawable.ic_eye, actionIconColor)
            keyOutput.setupAction3Icon(R.drawable.ic_clear, actionIconColor)
        }

        private fun getActionIconColor() =
            if (keyOutput.context.isNightUiMode() == true) {
                R.color.colorLight
            } else {
                R.color.colorDark
            }

        fun bind(entry: KeysContract.KeyEntry) = with(itemView) {
            val actionIconColor =
                if (context.isNightUiMode() == true) {
                    keyOutput.setupView(
                        R.color.colorDark,
                        R.drawable.selector_action_dark,
                        R.color.colorLight,
                        R.drawable.ic_passphrase,
                        entry.label
                    )
                    R.color.colorLight
                } else {
                    keyOutput.setupView(
                        R.color.colorLight,
                        R.drawable.selector_action_light,
                        R.color.colorDark,
                        R.drawable.ic_passphrase,
                        entry.label
                    )
                    R.color.colorDark
                }

            keyOutput.setText(entry.key)
            keyOutput.setupAction1 {
                context?.let {
                    ClipboardHelper.copyText(
                        it,
                        entry.key
                    )
                }
            }
            keyOutput.setupAction3 {
                context?.toast(R.string.keys_remove_key_long)
            }
            keyOutput.setupAction3LongPress {
                keysToShow.remove(entry.index)
                listener.onRemoveKey(entry.index)
            }
            val showKey = keysToShow.contains(entry.index)
            if (showKey) {
                keyOutput.setupAction2Icon(R.drawable.ic_eye_blind, actionIconColor)
                keyOutput.setInputTypePassword(visible = true)
            } else {
                keyOutput.setupAction2Icon(R.drawable.ic_eye, actionIconColor)
                keyOutput.setInputTypePassword(visible = false)
            }
            keyOutput.setupAction2 {
                if (showKey) {
                    keysToShow.remove(entry.index)
                } else {
                    keysToShow.add(entry.index)
                }
                notifyDataChanged()
            }
        }

    }

}