package ua.com.radiokot.voda.features.reader.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ua.com.radiokot.voda.view.base.BaseFragment
import ua.com.radiokot.voda.R

class ReaderWelcomeFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reader_welcome, container, false)
    }
}