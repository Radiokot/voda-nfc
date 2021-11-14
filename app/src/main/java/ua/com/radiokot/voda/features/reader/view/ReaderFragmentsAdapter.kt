package ua.com.radiokot.voda.features.reader.view

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ReaderFragmentsAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            WELCOME_POSITION -> ReaderWelcomeFragment()
            CARD_DATA_POSITION -> ReaderCardDataFragment()
            else -> throw IndexOutOfBoundsException()
        }
    }

    companion object {
        const val WELCOME_POSITION = 0
        const val CARD_DATA_POSITION = 1
    }
}