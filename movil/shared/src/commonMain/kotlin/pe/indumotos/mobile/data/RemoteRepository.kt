package pe.indumotos.mobile.data
import pe.indumotos.mobile.domain.*
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.*
import io.ktor.http.*
import kotlinx.serialization.json.*
import kotlinx.serialization.encodeToString
import kotlinx.coroutines.CancellationException

class RemoteRepository(engine:HttpClientEngine):LogisticsRepository{
 private val json=Json{ignoreUnknownKeys=true;encodeDefaults=true}
 private val http=HttpClient(engine){install(HttpTimeout){requestTimeoutMillis=20000;connectTimeoutMillis=10000}}
 private var base="";private var token=""
 private suspend fun request(path:String,body:String?=null,method:String="GET"):String{
  val response=http.request(base+path){this.method=HttpMethod.parse(method);contentType(ContentType.Application.Json);if(token.isNotEmpty())bearerAuth(token);if(body!=null)setBody(body)}
  val text=response.bodyAsText()
  if(response.status.value !in 200..299){
   if(response.status.value==401)token=""
   val message=runCatching{val o=json.parseToJsonElement(text).jsonObject;o["error"]?.jsonPrimitive?.content?:o["detail"]?.jsonPrimitive?.content?:o["message"]?.jsonPrimitive?.content}.getOrNull()
   throw IllegalStateException(message?:when(response.status.value){401->"La sesión terminó. Vuelve a ingresar.";403->"Tu cuenta no tiene permiso.";else->"No se pudo guardar (${response.status.value}). Revisa los datos."})
  };return text
 }
 override suspend fun login(baseUrl:String,user:String,password:String):Access{
  require(Rules.server(baseUrl)){"Ingresa una dirección HTTP o HTTPS"};base=baseUrl.trim().trimEnd('/')+"/api/v1/";token=""
  val auth=json.decodeFromString<LoginResponse>(request("auth/login",json.encodeToString(LoginRequest(user.trim(),password)),"POST"));token=auth.accessToken
  try{return json.decodeFromString<Access>(request("auth/me"))}catch(e:Exception){token="";throw e}
 }
 override suspend fun list(module:Module)=json.parseToJsonElement(request("mobile/"+module.route)).jsonArray.map{it.jsonObject}
 override suspend fun history(module:Module,id:String)=json.parseToJsonElement(request("mobile/${module.route}/$id/historial")).jsonArray.map{it.jsonObject}
 override suspend fun submit(path:String,body:JsonObject,method:String){request(path,body.toString(),method)}
 override suspend fun logout(){try{request("auth/logout","{}","POST")}finally{clearSession()}}
 override fun clearSession(){token=""}
}
