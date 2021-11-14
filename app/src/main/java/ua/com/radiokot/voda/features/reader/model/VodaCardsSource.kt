package ua.com.radiokot.voda.features.reader.model

import io.reactivex.Observable

interface VodaCardsSource {
    val cards: Observable<VodaCard>
}