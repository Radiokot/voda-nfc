package ua.com.radiokot.voda.features.card.view

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named
import ua.com.radiokot.voda.R
import ua.com.radiokot.voda.databinding.FragmentReaderCardDataBinding
import ua.com.radiokot.voda.di.InjectedAmountFormat
import ua.com.radiokot.voda.extensions.isInteger
import ua.com.radiokot.voda.features.card.model.VodaCard
import ua.com.radiokot.voda.features.card.storage.CardPreferences
import ua.com.radiokot.voda.util.format.AmountFormat
import ua.com.radiokot.voda.view.base.BaseFragment
import java.math.BigDecimal

class CardDataFragment : BaseFragment() {
    private lateinit var view: FragmentReaderCardDataBinding
    private val uahAmountFormat: AmountFormat by inject(named(InjectedAmountFormat.UAH))
    private val litersFormat: AmountFormat by inject()
    private val preferences: CardPreferences by inject()

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
    ): View {
        view = FragmentReaderCardDataBinding.inflate(inflater, container, false)
        return view.root
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
            if (view.backgroundImageView.visibility == View.VISIBLE)
                ContextCompat.getColor(requireContext(), R.color.primary_text_inverse)
            else
                ContextCompat.getColor(requireContext(), R.color.primary_text)

        listOf(
            view.balanceLabelTextView, view.balanceTextView,
            view.literPriceLabelTextView, view.literPriceTextView
        ).forEach {
            it.setTextColor(bottomContentColor)
        }

        ImageViewCompat.setImageTintList(
            view.editLiterPriceButton,
            ColorStateList.valueOf(bottomContentColor)
        )
    }

    private fun initButtons() {
        listOf(view.literPriceTextView, view.editLiterPriceButton).forEach {
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
        view.balanceTextView.text = uahAmountFormat.format(card.balance)
    }

    private fun displayLiters(card: VodaCard) {
        val liters = card.getLiters(literPrice)

        view.litersTextView.text = litersFormat.format(liters)
        view.litersLabelTextView.text =
                // 1 liter but 1.5 liters, 3 літри але 3.5 літра.
            if (liters.isInteger)
                resources.getQuantityString(R.plurals.liters, liters.intValueExact())
            else
                resources.getString(R.string.liters_when_fractional)


        view.literPriceTextView.text = uahAmountFormat.format(literPrice)
    }
}
