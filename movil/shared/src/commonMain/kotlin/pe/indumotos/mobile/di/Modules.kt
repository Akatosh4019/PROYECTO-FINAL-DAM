package pe.indumotos.mobile.di
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import pe.indumotos.mobile.data.RemoteRepository
import pe.indumotos.mobile.data.createHttpClient
import pe.indumotos.mobile.domain.*
import pe.indumotos.mobile.presentation.LogisticsViewModel
import org.koin.core.module.dsl.viewModel
expect val platformModule:Module
val appModule=module{single{createHttpClient(get())};single<LogisticsRepository>{RemoteRepository(get())};factory{LogisticsUseCase(get())};viewModel{LogisticsViewModel(get(),get())}}
fun initKoin()=startKoin{modules(platformModule,appModule)}
