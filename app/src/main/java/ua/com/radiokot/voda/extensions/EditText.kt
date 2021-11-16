package ua.com.radiokot.voda.extensions

import android.widget.EditText

fun EditText.onEditorAction(callback: () -> Unit) {
    this.setOnEditorActionListener { _, _, _ ->
        callback()
        true
    }
}