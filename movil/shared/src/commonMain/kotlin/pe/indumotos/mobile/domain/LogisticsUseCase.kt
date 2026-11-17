package pe.indumotos.mobile.domain
import kotlinx.serialization.json.*
class LogisticsUseCase(private val repository:LogisticsRepository){
 suspend fun save(path:String,fields:List<Field>,values:Map<String,String>,method:String="POST"){
  fields.forEach{f->val value=values[f.key].orEmpty();require(value.isNotBlank()){ "Completa ${f.label}" };if(f.numeric)require(Rules.positive(value)){"${f.label}: usa un número positivo"}}
  val endpoint=if(path.endsWith("/permisos"))path+"/"+values.getValue("permisoCodigo").trim() else path
  repository.submit(endpoint,buildJsonObject{fields.filterNot{path.endsWith("/permisos")&&it.key=="permisoCodigo"}.forEach{f->if(f.numeric)put(f.key,values.getValue(f.key).toLong())else put(f.key,values.getValue(f.key).trim())}},method)
 }
}
data class Field(val key:String,val label:String,val numeric:Boolean=false,val options:List<String> = emptyList(),val secret:Boolean=false,val labels:Map<String,String> = emptyMap())
data class Form(val title:String,val path:String,val fields:List<Field>,val method:String="POST")
