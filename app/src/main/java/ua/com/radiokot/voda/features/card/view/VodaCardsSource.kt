package ua.com.radiokot.voda.features.card.view

import io.reactivex.Observable
import ua.com.radiokot.voda.features.card.model.VodaCard

interface VodaCardsSource {
    val cards: Observable<VodaCard>
}