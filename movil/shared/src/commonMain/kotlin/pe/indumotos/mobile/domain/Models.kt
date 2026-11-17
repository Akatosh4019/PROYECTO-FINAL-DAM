package pe.indumotos.mobile.domain
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable data class LoginRequest(val nombreUsuario:String,val clave:String)
@Serializable data class LoginResponse(val accessToken:String,val expiresIn:Long)
@Serializable data class Access(val usuarioId:Long,val nombreUsuario:String,val rolCodigo:String,val permisos:List<Permission>)
@Serializable data class Permission(val codigo:String,val permitido:Boolean)
enum class Module(val title:String,val route:String,val permission:String){
 INVENTORY("Inventario","inventario","INVENTARIO_CONSULTAR"),ARRIVALS("Recepciones","llegadas","LLEGADAS_REGISTRAR"),
 LOCATIONS("Ubicaciones","ubicaciones","LOGISTICA_GESTIONAR"),REQUESTS("Ensamblaje","solicitudes","SOLICITUDES_CREAR"),
 MOVEMENTS("Movimientos","movimientos","LOGISTICA_GESTIONAR"),ISSUES("Inconsistencias","inconsistencias","INCONSISTENCIAS_REPORTAR"),USERS("Equipo y accesos","usuarios","USUARIOS_CREAR")
}
interface LogisticsRepository{
 suspend fun login(baseUrl:String,user:String,password:String):Access
 suspend fun list(module:Module):List<JsonObject>
 suspend fun history(module:Module,id:String):List<JsonObject> = emptyList()
 suspend fun submit(path:String,body:JsonObject,method:String="POST")
 suspend fun logout()
 fun clearSession()
}
object Rules {
 fun positive(value:String)=value.toLongOrNull()?.let{it>0}==true
 fun required(value:String)=value.isNotBlank()
 fun server(value:String)=value.startsWith("http://")||value.startsWith("https://")
}
