package pe.indumotos.mobile
import kotlin.test.*
import pe.indumotos.mobile.data.apiError
import pe.indumotos.mobile.domain.*
class ApiErrorMapperTest{
 @Test fun validationPreservesFieldErrors(){val e=apiError(400,"Datos inválidos",mapOf("marca" to "Obligatoria"));assertIs<AppError.Validation>(e);assertEquals("Obligatoria",e.fields["marca"])}
 @Test fun forbiddenDoesNotMeanExpiredSession(){assertEquals(AppError.Forbidden,apiError(403,null));assertNotEquals(AppError.Unauthorized,apiError(403,null))}
 @Test fun unauthorizedMeansSessionExpired(){assertEquals(AppError.Unauthorized,apiError(401,null))}
 @Test fun conflictsPreserveBusinessMessage(){assertEquals(AppError.Conflict("Unidad asignada"),apiError(409,"Unidad asignada"))}
 @Test fun serverErrorDoesNotExposeInternalMessage(){val e=apiError(500,"detalle interno SQL");assertIs<AppError.Server>(e);assertFalse(e.message().contains("SQL"))}
}
