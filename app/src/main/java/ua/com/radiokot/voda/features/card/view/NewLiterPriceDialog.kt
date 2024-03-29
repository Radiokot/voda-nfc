package ua.com.radiokot.voda.features.card.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import ua.com.radiokot.voda.R
import ua.com.radiokot.voda.databinding.LayoutDialogEditLiterPriceBinding
import ua.com.radiokot.voda.extensions.onEditorAction
import ua.com.radiokot.voda.util.BigDecimalUtil
import ua.com.radiokot.voda.util.input.DecimalDigitsInputFilter
import ua.com.radiokot.voda.util.input.SimpleTextWatcher
import java.math.BigDecimal

class NewLiterPriceDialog(
    private val context: Context
) {
    @SuppressLint("InflateParams")
    fun show(onPriceEntered: (BigDecimal) -> Unit) {
        val view = LayoutDialogEditLiterPriceBinding.inflate(LayoutInflater.from(context))

        lateinit var saveButton: Button
        var enteredPrice: BigDecimal = BigDecimal.ZERO

        view.newLiterPriceEditText.apply {
            filters += DecimalDigitsInputFilter(digitsBeforeComa = 2, digitsAfterComa = 2)
            onEditorAction {
                if (saveButton.isEnabled) {
                    saveButton.callOnClick()
                }
            }
            addTextChangedListener(SimpleTextWatcher { e ->
                enteredPrice = BigDecimalUtil.valueOf(e?.toString())
                saveButton.isEnabled = enteredPrice.signum() > 0
            })

            setCompoundDrawablesWithIntrinsicBounds(
                null, null,
                ContextCompat.getDrawable(context, R.drawable.uah_sign)!!
                    .also { currencySign ->
                        DrawableCompat.setTint(
                            currencySign,
                            textColors.defaultColor
                        )
                    },
                null
            )
        }

        AlertDialog.Builder(context, R.style.AlertDialog)
            .setTitle(R.string.new_liter_price)
            .setView(view.root)
            .setPositiveButton(R.string.save) { _, _ ->
                onPriceEntered(enteredPrice)
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
            .apply {
                window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
                show()
                saveButton = getButton(AlertDialog.BUTTON_POSITIVE)
                    .apply { isEnabled = false }
            }
    }
}
