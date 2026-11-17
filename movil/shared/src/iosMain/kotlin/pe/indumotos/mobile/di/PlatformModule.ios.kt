package pe.indumotos.mobile.di
import org.koin.dsl.module
import org.koin.core.module.Module
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
actual val platformModule:Module=module{single<HttpClientEngine>{Darwin.create()}}
