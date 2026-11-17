"""Prueba de integración HTTP. Ejecutar solo contra la base de demostración de esta carpeta.
Genera datos ficticios identificables; no usar contra una base operativa.
"""
from pathlib import Path
import urllib.request,urllib.error,json,uuid,datetime,os
r=Path(__file__).resolve().parents[1];env={}
for line in (r/'.env').read_text(encoding='utf-8-sig').splitlines():
 if '=' in line and not line.startswith('#'):k,v=line.split('=',1);env[k]=v
base=os.environ.get('TEST_API_URL','http://localhost:8088')+'/api/v1/';token='';results=[];tag='DEMO-'+uuid.uuid4().hex[:8]
def call(path,data=None,status=200,method=None):
 body=None if data is None else json.dumps(data).encode();req=urllib.request.Request(base+path,data=body,method=method or ('GET' if data is None else 'POST'),headers={'Content-Type':'application/json',**({'Authorization':'Bearer '+token} if token else {})})
 try:
  with urllib.request.urlopen(req,timeout=30) as response:code=response.status;text=response.read()
 except urllib.error.HTTPError as e:code=e.code;text=e.read()
 assert code==status,f'{path}: esperado {status}, obtenido {code}: {text.decode(errors="replace")}'
 results.append({'ruta':path,'http':code,'resultado':'APROBADO'})
 return json.loads(text) if text else {}
call('mobile/inventario',status=401)
auth=call('auth/login',{'nombreUsuario':env['ADMIN_USERNAME'],'clave':env['ADMIN_PASSWORD']});token=auth['accessToken']
call('auth/me');l=call('llegadas',{'numeroGuia':tag,'cantidadDeclarada':1,'cantidadRecibida':1,'observacion':'Datos ficticios de integración'},201)['id']
m=call('motocicletas',{'llegadaId':l,'marca':'PRUEBA','modelo':tag,'color':'ROJO','chasisCompleto':tag,'numeroMotor':tag,'dua':'DEMO','itemDua':'1'},201)['id']
u=call('mobile/ubicaciones',{'codigo':tag,'nombre':'Ubicación ficticia '+tag,'tipo':'ALMACEN'})['id']
call('mobile/movimientos',{'motocicletaId':m,'destinoId':u,'nota':'Ubicación de prueba'})
s=call('mobile/solicitudes',{'destinoId':u,'marca':'PRUEBA','modelo':tag,'color':'ROJO','cantidad':1,'nota':'Solicitud ficticia'})['id']
call(f'mobile/solicitudes/{s}/estado',{'estado':'ATENDIDA','nota':'Salto inválido'},409)
call(f'mobile/solicitudes/{s}/estado',{'estado':'EN_PREPARACION','nota':'Preparación de prueba'})
call(f'mobile/solicitudes/{s}/unidades',{'motocicletaId':m})
call(f'mobile/solicitudes/{s}/unidades',{'motocicletaId':m},409)
call(f'mobile/solicitudes/{s}/estado',{'estado':'ATENDIDA','nota':'Atención de prueba completada'})
call(f'mobile/inventario/{m}/disponibilidad',{'estado':'NO_DISPONIBLE','nota':'Retiro ficticio de disponibilidad'})
i=call('mobile/inconsistencias',{'motocicletaId':m,'descripcion':'Corrección ficticia de color'})['id']
call(f'mobile/inconsistencias/{i}/estado',{'estado':'EN_REVISION','nota':'Comprobación de prueba'})
call(f'mobile/inconsistencias/{i}/correccion',{'marca':'PRUEBA','modelo':tag,'color':'AZUL','chasis':tag,'motor':tag,'dua':'DEMO','item':'1','nota':'Color contrastado y corregido en prueba'})
history=call(f'mobile/inconsistencias/{i}/historial');assert len(history)==3
for module in ['inventario','llegadas','ubicaciones','solicitudes','movimientos','inconsistencias','usuarios']:call('mobile/'+module)
call('auth/logout',{} ,204);call('mobile/inventario',status=401)
(r/'validacion/resultado-api.json').write_text(json.dumps({'fecha':datetime.datetime.now().astimezone().isoformat(),'prefijoDatosFicticios':tag,'resultados':results},ensure_ascii=False,indent=2),encoding='utf8')
print(f'API: {len(results)} comprobaciones aprobadas. Datos ficticios conservados con prefijo {tag}.')
