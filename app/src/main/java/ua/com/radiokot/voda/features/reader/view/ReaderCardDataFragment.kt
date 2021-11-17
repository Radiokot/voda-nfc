package ua.com.radiokot.voda.features.reader.view

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_reader_card_data.*
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named
import ua.com.radiokot.voda.view.base.BaseFragment
import ua.com.radiokot.voda.R
import ua.com.radiokot.voda.di.InjectedAmountFormat
import ua.com.radiokot.voda.extensions.isInteger
import ua.com.radiokot.voda.features.reader.model.VodaCard
import ua.com.radiokot.voda.features.reader.model.VodaCardsSource
import ua.com.radiokot.voda.features.reader.storage.ReaderPreferences
import ua.com.radiokot.voda.util.format.AmountFormat
import java.math.BigDecimal

class ReaderCardDataFragment : BaseFragment() {
    private val uahAmountFormat: AmountFormat by inject(named(InjectedAmountFormat.UAH))
    private val litersFormat: AmountFormat by inject()
    private val preferences: ReaderPreferences by inject()

    private var literPrice: BigDecimal
        get() = preferences.literPrice
        set(value) {
            preferences.literPrice = value
            onLiterPriceChanged()
        }

    private var card: VodaCard? = null
        set(value) {
            field = value
            if (value != null) {
                onNewCard(value)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reader_card_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeToCardSource()

        initColors()
        initButtons()
    }

    private fun subscribeToCardSource() {
        ((parentFragment ?: activity) as VodaCardsSource)
            .cards
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { card = it }
            .addTo(compositeDisposable)
    }

    private fun initColors() {
        val bottomContentColor =
            if (background_image_view.visibility == View.VISIBLE)
                ContextCompat.getColor(requireContext(), R.color.primary_text_inverse)
            else
                ContextCompat.getColor(requireContext(), R.color.primary_text)

        listOf(
            balance_label_text_view, balance_text_view,
            liter_price_label_text_view, liter_price_text_view
        ).forEach {
            it.setTextColor(bottomContentColor)
        }

        ImageViewCompat.setImageTintList(
            edit_liter_price_button,
            ColorStateList.valueOf(bottomContentColor)
        )
    }

    private fun initButtons() {
        listOf(liter_price_text_view, edit_liter_price_button).forEach {
            it.setOnClickListener {
                showEditLiterPriceDialog()
            }
        }
    }

    private fun showEditLiterPriceDialog() {
        NewLiterPriceDialog(requireContext())
            .show { newPrice ->
                literPrice = newPrice
            }
    }

    private fun onLiterPriceChanged() {
        card?.also(this::displayLiters)
    }

    private fun onNewCard(card: VodaCard) {
        displayBalance(card)
        displayLiters(card)
    }

    private fun displayBalance(card: VodaCard) {
        balance_text_view.text = uahAmountFormat.format(card.balance)
    }

    private fun displayLiters(card: VodaCard) {
        val liters = card.getLiters(literPrice)

        liters_text_view.text = litersFormat.format(liters)
        liters_label_text_view.text =
                // 1 liter but 1.5 liters, 3 літри але 3.5 літра.
            if (liters.isInteger)
                resources.getQuantityString(R.plurals.liters, liters.intValueExact())
            else
                resources.getString(R.string.liters_when_fractional)


        liter_price_text_view.text = uahAmountFormat.format(literPrice)
    }
}