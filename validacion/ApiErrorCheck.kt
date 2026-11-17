import pe.indumotos.mobile.data.apiError
import pe.indumotos.mobile.domain.*
fun main(){
 check(apiError(400,"Revisa",mapOf("marca" to "Obligatoria"))==AppError.Validation(mapOf("marca" to "Obligatoria"),"Revisa"))
 check(apiError(401,null)==AppError.Unauthorized)
 check(apiError(403,null)==AppError.Forbidden)
 check(apiError(409,"Ya existe")==AppError.Conflict("Ya existe"))
 check(!apiError(500,"SQL interno").message().contains("SQL"))
 println("Traducción de errores: 5/5 comprobaciones aprobadas")
}
