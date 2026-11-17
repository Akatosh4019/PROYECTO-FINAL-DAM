from pathlib import Path
import subprocess,json,datetime,time,os
r=Path(__file__).resolve().parents[1];pg=Path('C:/Program Files/PostgreSQL/18/bin');dbname='indumotos_app_test_'+datetime.datetime.now().strftime('%H%M%S');base=['-h','127.0.0.1','-p','55440','-U','indumotos'];report=[]
log=r/'validacion/resultado-bd.log'
server_log=(r/'validacion/postgres-test.log').open('wb')
server=subprocess.Popen([str(pg/'postgres.exe'),'-D',str(r.parent/'.build-cache/pg-integration'),'-h','127.0.0.1','-p','55440'],stdout=server_log,stderr=server_log,creationflags=subprocess.CREATE_NO_WINDOW)
import atexit
atexit.register(lambda:server.terminate() if server.poll() is None else None)
for attempt in range(30):
 ready=subprocess.run([str(pg/'pg_isready.exe')]+base,stdout=subprocess.DEVNULL,stderr=subprocess.DEVNULL)
 if ready.returncode==0:break
 if server.poll() is not None:raise RuntimeError('PostgreSQL no inició; revisar postgres-test.log')
 time.sleep(.3)
with log.open('w',encoding='utf8') as f:
 def run(exe,args):
  p=subprocess.run([str(pg/exe)]+args,capture_output=True);s=(p.stdout+p.stderr).decode('utf8',errors='replace');f.write(s+'\n');f.flush();
  if p.returncode:raise RuntimeError(s)
 run('createdb.exe',base+[dbname])
 for p in sorted((r/'backend/src/main/resources/db/migration').glob('V*.sql'),key=lambda x:int(x.name.split('__')[0][1:])):
  run('psql.exe',base+['-d',dbname,'-v','ON_ERROR_STOP=1','-f',str(p)]);report.append({'migracion':p.name,'resultado':'APROBADA'})
 for name in ['verificar_base_sprint2.sql','verificar_incremento3.sql']:
  run('psql.exe',base+['-d',dbname,'-v','ON_ERROR_STOP=1','-f',str(r/'backend/src/test/resources/sql'/name)]);report.append({'prueba':name,'resultado':'APROBADA'})
 # Ruta de actualización independiente: datos S1 presentes antes de V8-V10.
 upgrade=dbname+'_upgrade';run('createdb.exe',base+[upgrade])
 migrations=sorted((r/'backend/src/main/resources/db/migration').glob('V*.sql'),key=lambda x:int(x.name.split('__')[0][1:]))
 for p in migrations[:7]:run('psql.exe',base+['-d',upgrade,'-v','ON_ERROR_STOP=1','-f',str(p)])
 seed="INSERT INTO usuario(id_rol,nombre_completo,nombre_usuario,clave_hash) SELECT id_rol,'Prueba actualizacion','upgrade_demo','NO_ES_CREDENCIAL' FROM rol WHERE codigo='ADMIN'; INSERT INTO llegada(numero_guia,id_receptor,cantidad_declarada,cantidad_recibida) SELECT 'UPGRADE',id_usuario,1,1 FROM usuario WHERE nombre_usuario='upgrade_demo'; INSERT INTO motocicleta(id_llegada,marca,modelo,color,chasis_completo,numero_motor,dua,item_dua,id_creador) SELECT l.id_llegada,'Prueba','Upgrade','Rojo','UPGRADE-UNICO','MOTOR','DUA','1',u.id_usuario FROM llegada l JOIN usuario u ON u.id_usuario=l.id_receptor;"
 run('psql.exe',base+['-d',upgrade,'-v','ON_ERROR_STOP=1','-c',seed])
 query="SELECT jsonb_build_object('usuarios',(SELECT jsonb_agg(to_jsonb(t)) FROM usuario t),'llegadas',(SELECT jsonb_agg(to_jsonb(t)) FROM llegada t),'motocicletas',(SELECT jsonb_agg(to_jsonb(t)) FROM motocicleta t))::text;"
 def snapshot():return subprocess.check_output([str(pg/'psql.exe')]+base+['-d',upgrade,'-At','-c',query])
 before=snapshot()
 for p in migrations[7:]:run('psql.exe',base+['-d',upgrade,'-v','ON_ERROR_STOP=1','-f',str(p)])
 assert snapshot()==before,'La actualización alteró filas existentes del Sprint 1'
 report.append({'prueba':'Actualización V7 a V10 con usuarios, llegada y motocicleta preexistentes','resultado':'APROBADA: mismas filas antes y después'})
(r/'validacion/resultado-bd.json').write_text(json.dumps({'fecha':datetime.datetime.now().astimezone().isoformat(),'entorno':'PostgreSQL 18, cluster aislado, puerto 55440','resultados':report},ensure_ascii=False,indent=2),encoding='utf8');print('BD: migraciones y pruebas SQL aprobadas')
