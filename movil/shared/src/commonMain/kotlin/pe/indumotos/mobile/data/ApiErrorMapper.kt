package pe.indumotos.mobile.data
import pe.indumotos.mobile.domain.AppError
/** La traducción del protocolo se mantiene en datos, fuera del dominio. */
fun apiError(status:Int,detail:String?,fields:Map<String,String> = emptyMap()):AppError=when(status){
 400,422->AppError.Validation(fields,detail?:"Revisa los datos del formulario.")
 401->AppError.Unauthorized
 403->AppError.Forbidden
 409->AppError.Conflict(detail?:"El registro cambió o ya existe. Actualiza la lista.")
 else->AppError.Server(status)
}
