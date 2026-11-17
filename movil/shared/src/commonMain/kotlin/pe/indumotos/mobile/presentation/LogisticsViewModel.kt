package pe.indumotos.mobile.presentation
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.*
import pe.indumotos.mobile.domain.*
data class UiState(val access:Access?=null,val module:Module=Module.INVENTORY,val rows:List<JsonObject> = emptyList(),val busy:Boolean=false,val error:String?=null,val message:String?=null,val form:Form?=null,val history:List<JsonObject>?=null)
class LogisticsViewModel(private val repo:LogisticsRepository,private val save:LogisticsUseCase):ViewModel(){
 private val mutable=MutableStateFlow(UiState());val state=mutable.asStateFlow()
 private fun work(block:suspend()->Unit){if(mutable.value.busy)return;mutable.update{it.copy(busy=true,error=null,message=null)};viewModelScope.launch{try{block()}catch(e:CancellationException){throw e}catch(e:Exception){mutable.update{it.copy(error=e.message?:"No hay conexión con el servidor")}}finally{mutable.update{it.copy(busy=false)}}}}
 fun login(url:String,user:String,password:String)=work{val a=repo.login(url,user,password);mutable.update{it.copy(access=a)};load()}
 fun open(module:Module){if(mutable.value.busy)return;mutable.update{it.copy(module=module,rows=emptyList())};work{load()}}
 fun reload()=work{load()}
 fun history(id:String)=work{val rows=repo.history(mutable.value.module,id);mutable.update{it.copy(history=rows)}}
 fun closeHistory(){mutable.update{it.copy(history=null)}}
 private suspend fun load(){val rows=repo.list(mutable.value.module);mutable.update{it.copy(rows=rows)}}
 fun form(form:Form?){
  if(form==null){mutable.update{it.copy(form=null)};return}
  val lookups=mapOf("destinoId" to Module.LOCATIONS,"llegadaId" to Module.ARRIVALS,"motocicletaId" to Module.INVENTORY)
  if(form.fields.none{it.key in lookups}){mutable.update{it.copy(form=form)};return}
  work{
   val fields=form.fields.map{field->val target=lookups[field.key];if(target==null)field else{
    val rows=repo.list(target)
    val key=when(target){Module.LOCATIONS->"id_ubicacion";Module.ARRIVALS->"id_llegada";else->"id_motocicleta"}
    val labels=rows.associate{row->val id=row.getValue(key).jsonPrimitive.content;id to when(target){Module.LOCATIONS->row["nombre"]!!.jsonPrimitive.content;Module.ARRIVALS->"Guía "+row["numero_guia"]!!.jsonPrimitive.content;else->row["marca"]!!.jsonPrimitive.content+" "+row["modelo"]!!.jsonPrimitive.content+" · "+row["chasis_completo"]!!.jsonPrimitive.content}}
    require(labels.isNotEmpty()){"Primero registra datos en ${target.title}"}
    field.copy(options=labels.keys.toList(),labels=labels)
   }}
   mutable.update{it.copy(form=form.copy(fields=fields))}
  }
 }
 fun submit(values:Map<String,String>){val f=mutable.value.form?:return;work{save.save(f.path,f.fields,values,f.method);mutable.update{it.copy(form=null,message="Cambios guardados")};load()}}
 fun logout()=work{try{repo.logout()}finally{mutable.value=UiState()}}
 fun allowed(permission:String)=state.value.access?.permisos?.any{it.codigo==permission&&it.permitido}==true
 fun canRead(module:Module)=when(module){Module.USERS->allowed("PERMISOS_ASIGNAR");Module.REQUESTS->allowed("INVENTARIO_CONSULTAR")||allowed("SOLICITUDES_CREAR")||allowed("SOLICITUDES_ATENDER");else->allowed("INVENTARIO_CONSULTAR")}
}
