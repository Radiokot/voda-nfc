package ua.com.radiokot.voda.view.base

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import io.reactivex.disposables.CompositeDisposable
import org.koin.android.ext.android.inject
import ua.com.radiokot.voda.R
import ua.com.radiokot.voda.view.ToastManager

abstract class BaseActivity : AppCompatActivity() {
    /**
     * Disposable holder which will be disposed on activity destroy
     */
    protected val compositeDisposable: CompositeDisposable = CompositeDisposable()

    protected val toastManager: ToastManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setBackgroundDrawable(
            ColorDrawable(ContextCompat.getColor(this, R.color.background))
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}