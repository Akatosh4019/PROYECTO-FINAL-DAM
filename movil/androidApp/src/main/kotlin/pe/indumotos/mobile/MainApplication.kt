package pe.indumotos.mobile
import android.app.Application
import pe.indumotos.mobile.di.initKoin
class MainApplication:Application(){override fun onCreate(){super.onCreate();initKoin()}}
