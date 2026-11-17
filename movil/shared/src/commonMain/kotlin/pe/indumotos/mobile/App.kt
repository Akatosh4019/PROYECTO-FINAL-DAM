package pe.indumotos.mobile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.serialization.json.*
import pe.indumotos.mobile.domain.*
import pe.indumotos.mobile.presentation.*

private val Green=Color(0xFF166534)
private fun JsonObject.text(key:String)=get(key)?.jsonPrimitive?.contentOrNull.orEmpty()
private fun Field(key:String,label:String,numeric:Boolean)=pe.indumotos.mobile.domain.Field(key,label,numeric,emptyList(),false)
private val Roles=listOf("ADMIN","ALMACEN","INVENTARIOS","COMERCIAL","FACTURACION","ENSAMBLAJE")
private val ModelFields=listOf(Field("marca","Marca"),Field("modelo","Modelo"),Field("color","Color"))
private fun createForm(module:Module):Form=when(module){
 Module.INVENTORY->Form("Registrar motocicleta","motocicletas",listOf(Field("llegadaId","ID de recepción",true))+ModelFields+listOf(Field("chasisCompleto","Chasis completo"),Field("numeroMotor","Número de motor"),Field("dua","DUA"),Field("itemDua","Ítem DUA")))
 Module.ARRIVALS->Form("Nueva recepción","llegadas",listOf(Field("numeroGuia","Número de guía"),Field("cantidadDeclarada","Cantidad declarada",true),Field("cantidadRecibida","Cantidad recibida",true),Field("observacion","Observación")))
 Module.LOCATIONS->Form("Nueva ubicación","mobile/ubicaciones",listOf(Field("codigo","Código"),Field("nombre","Nombre"),Field("tipo","Tipo",options=listOf("ALMACEN","TIENDA","TALLER"))))
 Module.REQUESTS->Form("Solicitar ensamblaje","mobile/solicitudes",listOf(Field("destinoId","ID de ubicación destino",true))+ModelFields+listOf(Field("cantidad","Cantidad",true),Field("nota","Nota de la solicitud")))
 Module.MOVEMENTS->Form("Registrar movimiento","mobile/movimientos",listOf(Field("motocicletaId","ID de motocicleta",true),Field("destinoId","ID de ubicación destino",true),Field("nota","Motivo del movimiento")))
 Module.ISSUES->Form("Reportar inconsistencia","mobile/inconsistencias",listOf(Field("motocicletaId","ID de motocicleta",true),Field("descripcion","Descripción del problema")))
 Module.USERS->Form("Crear cuenta","usuarios",listOf(Field("nombreCompleto","Nombre completo"),Field("nombreUsuario","Usuario"),Field("clave","Contraseña (mínimo 12 caracteres)",secret=true),Field("rolCodigo","Rol",options=Roles)))
}
private fun icon(m:Module)=when(m){Module.INVENTORY->Icons.Default.Inventory2;Module.ARRIVALS->Icons.Default.LocalShipping;Module.LOCATIONS->Icons.Default.Place;Module.REQUESTS->Icons.Default.Build;Module.MOVEMENTS->Icons.Default.SwapHoriz;Module.ISSUES->Icons.Default.ReportProblem;Module.USERS->Icons.Default.Group}

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun App(){
 val vm:LogisticsViewModel=koinViewModel();val state by vm.state.collectAsState()
 MaterialTheme(colorScheme=lightColorScheme(primary=Green,secondary=Color(0xFF0F766E),background=Color(0xFFF4F7F5),surface=Color.White),shapes=Shapes(medium=RoundedCornerShape(18.dp),large=RoundedCornerShape(24.dp))){
  Surface(Modifier.fillMaxSize(),color=MaterialTheme.colorScheme.background){
   if(state.access==null)Login(state,vm)else{
    var menu by remember{mutableStateOf(false)};var search by remember(state.module){mutableStateOf("")};var detail by remember{mutableStateOf<JsonObject?>(null)}
    Scaffold(containerColor=MaterialTheme.colorScheme.background,topBar={TopAppBar(title={Column{Text("INDUMOTOS",fontWeight=FontWeight.ExtraBold,letterSpacing=2.sp);Text(state.access!!.nombreUsuario+" · "+state.access!!.rolCodigo,fontSize=11.sp)}},actions={IconButton(onClick={vm.reload()},enabled=!state.busy){Icon(Icons.Default.Refresh,"Actualizar")};Box{IconButton(onClick={menu=true}){Icon(Icons.Default.MoreVert,"Opciones")};DropdownMenu(menu,{menu=false}){DropdownMenuItem(text={Text("Cerrar sesión")},onClick={menu=false;vm.logout()})}}})},floatingActionButton={val permission=if(state.module==Module.INVENTORY)"FICHAS_REGISTRAR" else state.module.permission;if(vm.allowed(permission))FloatingActionButton(onClick={vm.form(createForm(state.module))},containerColor=Green,contentColor=Color.White){Icon(Icons.Default.Add,"Nuevo registro")}}){padding->
     Column(Modifier.padding(padding).fillMaxSize()){
      Row(Modifier.horizontalScroll(rememberScrollState()).padding(horizontal=16.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)){Module.entries.filter{vm.canRead(it)}.forEach{m->FilterChip(selected=state.module==m,onClick={vm.open(m)},label={Text(m.title)},leadingIcon={Icon(icon(m),null,Modifier.size(16.dp))})}}
      Column(Modifier.padding(horizontal=20.dp,vertical=12.dp)){
       Text(state.module.title,fontSize=28.sp,fontWeight=FontWeight.Bold)
       Text("${state.rows.size} registros · Información del servidor",color=Color(0xFF65736A),fontSize=12.sp)
       Spacer(Modifier.height(12.dp));OutlinedTextField(search,{search=it},placeholder={Text("Buscar por código, chasis o descripción")},leadingIcon={Icon(Icons.Default.Search,null)},modifier=Modifier.fillMaxWidth(),singleLine=true,shape=RoundedCornerShape(16.dp))
      }
      if(state.busy)LinearProgressIndicator(Modifier.fillMaxWidth())
      state.error?.let{Notice(it,true)};state.message?.let{Notice(it,false)}
      val rows=state.rows.filter{it.toString().contains(search,true)}
      if(rows.isEmpty()&&!state.busy)Column(Modifier.fillMaxWidth().padding(32.dp),horizontalAlignment=Alignment.CenterHorizontally){Icon(icon(state.module),null,Modifier.size(48.dp),tint=Green);Spacer(Modifier.height(12.dp));Text(if(search.isEmpty())"Todavía no hay registros" else "Sin coincidencias",fontWeight=FontWeight.Bold);Text("Actualiza o registra información con tu cuenta autorizada.",fontSize=12.sp)}
      LazyColumn(contentPadding=PaddingValues(start=20.dp,end=20.dp,bottom=100.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){items(rows){row->RecordCard(state.module,row,vm){detail=row}}}
     }
    }
    detail?.let{row->AlertDialog(onDismissRequest={detail=null},title={Text("Detalle del registro")},text={Column(Modifier.verticalScroll(rememberScrollState())){row.forEach{(k,v)->Text(k.replace('_',' '),fontSize=11.sp,color=Green);Text(v.jsonPrimitive.contentOrNull?:"Sin información",fontSize=14.sp);Spacer(Modifier.height(9.dp))}}},confirmButton={TextButton(onClick={detail=null}){Text("Cerrar")}})}
   }
   state.form?.let{form->FormDialog(form,state.busy,state.error,{vm.form(null)},vm::submit)}
   state.history?.let { events ->
    AlertDialog(
     onDismissRequest=vm::closeHistory,
     title={Text("Historial de cambios")},
     text={
      Column(Modifier.verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(12.dp)){
       if(events.isEmpty())Text("Sin eventos registrados")
       events.forEach { event ->
        Card {
         Column(Modifier.padding(12.dp)){
          event.forEach { (key,value) ->
           Text(key.replace('_',' ')+": "+(value.jsonPrimitive.contentOrNull?:"—"),fontSize=12.sp)
          }
         }
        }
       }
      }
     },
     confirmButton={TextButton(onClick=vm::closeHistory){Text("Cerrar")}}
    )
   }
  }
 }
}
@Composable private fun Notice(message:String,error:Boolean){Surface(Modifier.fillMaxWidth().padding(horizontal=20.dp,vertical=4.dp),shape=RoundedCornerShape(12.dp),color=if(error)Color(0xFFFEE2E2)else Color(0xFFDCFCE7)){Text(message,Modifier.padding(12.dp),fontSize=13.sp,color=if(error)Color(0xFF991B1B)else Green)}}
@Composable private fun Login(state:UiState,vm:LogisticsViewModel){
 var server by remember{mutableStateOf("http://10.0.2.2:8088")};var user by remember{mutableStateOf("")};var password by remember{mutableStateOf("")};var visible by remember{mutableStateOf(false)}
 Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(28.dp),verticalArrangement=Arrangement.Center){
  Surface(shape=RoundedCornerShape(22.dp),color=Green,modifier=Modifier.size(64.dp)){Box(contentAlignment=Alignment.Center){Icon(Icons.Default.TwoWheeler,null,tint=Color.White,modifier=Modifier.size(40.dp))}}
  Spacer(Modifier.height(24.dp));Text("Tu operación,\nen movimiento.",fontSize=35.sp,lineHeight=40.sp,fontWeight=FontWeight.ExtraBold,color=Color(0xFF173E2A));Spacer(Modifier.height(8.dp));Text("INDUMOTOS · Logística y trazabilidad",color=Color(0xFF65736A));Spacer(Modifier.height(30.dp))
  OutlinedTextField(user,{user=it},label={Text("Usuario")},singleLine=true,modifier=Modifier.fillMaxWidth());Spacer(Modifier.height(12.dp))
  OutlinedTextField(password,{password=it},label={Text("Contraseña")},singleLine=true,visualTransformation=if(visible)VisualTransformation.None else PasswordVisualTransformation(),trailingIcon={IconButton(onClick={visible=!visible}){Icon(if(visible)Icons.Default.VisibilityOff else Icons.Default.Visibility,"Mostrar contraseña")}},modifier=Modifier.fillMaxWidth())
  Spacer(Modifier.height(12.dp));OutlinedTextField(server,{server=it},label={Text("Dirección del servidor")},supportingText={Text("Emulador: 10.0.2.2 · Celular: IP de tu computadora")},singleLine=true,modifier=Modifier.fillMaxWidth());state.error?.let{Text(it,color=MaterialTheme.colorScheme.error)};Spacer(Modifier.height(18.dp))
  Button(onClick={vm.login(server,user,password)},enabled=!state.busy&&user.isNotBlank()&&password.isNotBlank(),modifier=Modifier.fillMaxWidth().height(54.dp)){if(state.busy)CircularProgressIndicator(Modifier.size(22.dp),color=Color.White)else Text("Ingresar",fontSize=16.sp)}
  Spacer(Modifier.height(20.dp));Text("Acceso protegido. Cada operación respeta los permisos de tu cuenta.",fontSize=12.sp,color=Color(0xFF65736A))
 }
}
@Composable private fun RecordCard(module:Module,row:JsonObject,vm:LogisticsViewModel,detail:()->Unit){
 val idKey=when(module){Module.INVENTORY->"id_motocicleta";Module.ARRIVALS->"id_llegada";Module.LOCATIONS->"id_ubicacion";Module.REQUESTS->"id_solicitud";Module.MOVEMENTS->"id_movimiento";Module.ISSUES->"id_inconsistencia";Module.USERS->"id_usuario"};val id=row.text(idKey)
 val title=when(module){Module.INVENTORY->row.text("marca")+" "+row.text("modelo");Module.ARRIVALS->"Guía "+row.text("numero_guia");Module.LOCATIONS->row.text("nombre");Module.REQUESTS->"Solicitud #$id";Module.MOVEMENTS->row.text("chasis_completo");Module.ISSUES->row.text("descripcion");Module.USERS->row.text("nombre_completo")}
 val subtitle=when(module){Module.INVENTORY->row.text("chasis_completo")+" · "+row.text("color");Module.ARRIVALS->row.text("cantidad_recibida")+" unidades recibidas";Module.LOCATIONS->row.text("codigo")+" · "+row.text("tipo");Module.REQUESTS->row.text("destino")+" · "+row.text("cantidad")+" unidades";Module.MOVEMENTS->row.text("origen").ifBlank{"Sin ubicación previa"}+" → "+row.text("destino");Module.ISSUES->row.text("chasis_completo");Module.USERS->row.text("nombre_usuario")+" · "+row.text("rol")}
 Card(onClick=detail,colors=CardDefaults.cardColors(containerColor=Color.White),modifier=Modifier.fillMaxWidth()){
  Column(Modifier.padding(16.dp)){Row(verticalAlignment=Alignment.CenterVertically){Icon(icon(module),null,tint=Green,modifier=Modifier.size(24.dp));Spacer(Modifier.width(10.dp));Text(title,fontWeight=FontWeight.Bold,modifier=Modifier.weight(1f));Text("#$id",fontSize=12.sp,color=Color.Gray)};Spacer(Modifier.height(8.dp));Text(subtitle,fontSize=13.sp,color=Color(0xFF526158))
   val status=row.text("estado").ifBlank{row.text("disponibilidad")};if(status.isNotBlank()){Spacer(Modifier.height(9.dp));Surface(color=Color(0xFFE8F3EB),shape=RoundedCornerShape(6.dp)){Text(status.replace('_',' '),Modifier.padding(6.dp),fontSize=10.sp,color=Green)}}
   if(module==Module.INVENTORY){Text("Ubicación: "+row.text("ubicacion").ifBlank{"No informada"},fontSize=12.sp);if(vm.allowed("DISPONIBILIDAD_GESTIONAR"))TextButton(onClick={vm.form(Form("Disponibilidad de #$id","mobile/inventario/$id/disponibilidad",listOf(Field("estado","Disponibilidad",options=listOf("NO_DISPONIBLE","DISPONIBLE")),Field("nota","Motivo"))))}){Text("Actualizar disponibilidad")}}
   if(module==Module.REQUESTS&&vm.allowed("SOLICITUDES_ATENDER")){
    val states=when(status){"PENDIENTE"->listOf("EN_PREPARACION","RECHAZADA","ANULADA");"EN_PREPARACION"->listOf("ATENDIDA","ANULADA");else->emptyList()}
    if(states.isNotEmpty())TextButton(onClick={vm.form(Form("Avance de solicitud #$id","mobile/solicitudes/$id/estado",listOf(Field("estado","Nuevo estado",options=states),Field("nota","Motivo o detalle"))))}){Text("Actualizar avance")}
    if(status=="EN_PREPARACION")TextButton(onClick={vm.form(Form("Asignar unidad a #$id","mobile/solicitudes/$id/unidades",listOf(Field("motocicletaId","ID de motocicleta",true))))}){Text("Asignar motocicleta")}
   }
   if(module==Module.ISSUES&&vm.allowed("INCONSISTENCIAS_RESOLVER")&&status!="RESUELTA"){
    TextButton(onClick={vm.form(Form("Seguimiento #$id","mobile/inconsistencias/$id/estado",listOf(Field("estado","Nuevo estado",options=if(status=="ABIERTA")listOf("EN_REVISION")else listOf("RESUELTA")),Field("nota","Acción o corrección realizada"))))}){Text("Registrar seguimiento")}
    if(status=="EN_REVISION")TextButton(onClick={vm.form(Form("Corregir ficha y resolver #$id","mobile/inconsistencias/$id/correccion",ModelFields+listOf(Field("chasis","Chasis correcto"),Field("motor","Motor correcto"),Field("dua","DUA"),Field("item","Ítem DUA"),Field("nota","Justificación de la corrección"))))}){Text("Corregir datos de la ficha")}
   }
   if(module in listOf(Module.INVENTORY,Module.REQUESTS,Module.ISSUES))TextButton(onClick={vm.history(id)}){Text("Ver historial")}
   if(module==Module.USERS&&vm.allowed("PERMISOS_ASIGNAR")){TextButton(onClick={vm.form(Form("Cambiar rol #$id","usuarios/$id/rol",listOf(Field("rolCodigo","Rol",options=Roles)),"PUT"))}){Text("Cambiar rol")};TextButton(onClick={vm.form(Form("Permiso individual #$id","usuarios/$id/permisos",listOf(Field("permisoCodigo","Código del permiso"),Field("decision","Decisión",options=listOf("PERMITIR","DENEGAR"))),"PUT"))}){Text("Asignar permiso")}}
  }
 }
}
@Composable private fun FormDialog(form:Form,busy:Boolean,error:String?,close:()->Unit,submit:(Map<String,String>)->Unit){
 val values=remember(form){mutableStateMapOf<String,String>().apply{form.fields.forEach{put(it.key,it.options.firstOrNull().orEmpty())}}};var local by remember(form){mutableStateOf<String?>(null)}
 AlertDialog(onDismissRequest={if(!busy)close()},title={Text(form.title)},text={Column(Modifier.verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(10.dp)){
  form.fields.forEach{f->if(f.options.isNotEmpty()){Text(f.label,fontSize=12.sp);Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(6.dp)){f.options.forEach{o->FilterChip(values[f.key]==o,{values[f.key]=o},label={Text(f.labels[o]?:o.replace('_',' '))})}}}else OutlinedTextField(values[f.key].orEmpty(),{values[f.key]=it},label={Text(f.label)},modifier=Modifier.fillMaxWidth(),visualTransformation=if(f.secret)PasswordVisualTransformation()else VisualTransformation.None,keyboardOptions=KeyboardOptions(keyboardType=if(f.numeric)KeyboardType.Number else KeyboardType.Text),singleLine=f.numeric||f.secret)}
  (local?:error)?.let{Text(it,color=MaterialTheme.colorScheme.error,fontSize=12.sp)}
 }},confirmButton={Button(onClick={local=if(form.fields.any{values[it.key].isNullOrBlank()})"Completa todos los campos"else null;if(local==null)submit(values.toMap())},enabled=!busy){Text(if(busy)"Guardando…"else"Guardar")}},dismissButton={TextButton(onClick=close,enabled=!busy){Text("Cancelar")}})
}
