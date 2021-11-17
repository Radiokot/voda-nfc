package ua.com.radiokot.voda.view.base

import androidx.appcompat.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import org.koin.android.ext.android.inject
import ua.com.radiokot.voda.view.ToastManager

abstract class BaseActivity : AppCompatActivity() {
    /**
     * Disposable holder which will be disposed on activity destroy
     */
    protected val compositeDisposable: CompositeDisposable = CompositeDisposable()

    protected val toastManager: ToastManager by inject()

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}