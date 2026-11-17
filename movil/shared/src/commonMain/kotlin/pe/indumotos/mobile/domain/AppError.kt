package pe.indumotos.mobile.domain

/** Fallos de aplicación independientes de Ktor y de la interfaz. */
sealed interface AppError {
 data class Validation(val fields:Map<String,String>,val detail:String):AppError
 data object Unauthorized:AppError
 data object Forbidden:AppError
 data class Conflict(val detail:String):AppError
 data object Network:AppError
 data class Server(val status:Int):AppError
}
fun AppError.message():String=when(this){
 is AppError.Validation->if(fields.isEmpty())detail else detail+"\n"+fields.entries.joinToString("\n"){it.key+": "+it.value}
 AppError.Unauthorized->"Tu sesión terminó. Vuelve a ingresar."
 AppError.Forbidden->"Tu cuenta no tiene permiso para esta operación."
 is AppError.Conflict->detail
 AppError.Network->"No hay conexión con el servidor. Comprueba la red y vuelve a intentar."
 is AppError.Server->"El servidor no pudo completar la operación ($status)."
}
class AppException(val error:AppError):Exception(error.message())
