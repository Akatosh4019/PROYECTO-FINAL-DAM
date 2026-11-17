package pe.indumotos.mobile
import androidx.compose.ui.window.ComposeUIViewController
import pe.indumotos.mobile.di.initKoin
fun MainViewController()=run{initKoin();ComposeUIViewController{App()}}
