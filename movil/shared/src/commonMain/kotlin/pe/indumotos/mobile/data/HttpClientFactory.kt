package pe.indumotos.mobile.data
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
fun createHttpClient(engine:HttpClientEngine)=HttpClient(engine){
 install(HttpTimeout){requestTimeoutMillis=20000;connectTimeoutMillis=10000}
 // No registrar headers: contienen el token de autorización.
}
