package pe.indumotos.mobile
import kotlin.test.*
import pe.indumotos.mobile.domain.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.*
class RulesTest{
 @Test fun idsMustBePositive(){assertFalse(Rules.positive("0"));assertFalse(Rules.positive("-2"));assertFalse(Rules.positive("abc"));assertTrue(Rules.positive("12"))}
 @Test fun missingFieldCannotReachServer()=runTest{val repo=Fake();val use=LogisticsUseCase(repo);assertFailsWith<IllegalArgumentException>{use.save("mobile/movimientos",listOf(Field("motocicletaId","Moto",true)),mapOf("motocicletaId" to "-1"))};assertFalse(repo.called)}
 @Test fun numbersAreSerializedAsNumbers()=runTest{val repo=Fake();LogisticsUseCase(repo).save("x",listOf(Field("id","ID",true)),mapOf("id" to "23"));assertEquals(JsonPrimitive(23),repo.body["id"])}
 class Fake:LogisticsRepository{var called=false;var body=JsonObject(emptyMap());override suspend fun login(baseUrl:String,user:String,password:String)=error("unused");override suspend fun list(module:Module)=emptyList<JsonObject>();override suspend fun submit(path:String,body:JsonObject,method:String){called=true;this.body=body};override suspend fun logout(){};override fun clearSession(){}}
}
