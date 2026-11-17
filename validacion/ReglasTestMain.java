import pe.indumotos.domain.ReglasLogistica;
public class ReglasTestMain{
 static int total=0;
 static void test(boolean condition,String name){total++;if(!condition)throw new AssertionError(name);}
 public static void main(String[] args){
  test(ReglasLogistica.transicionSolicitud("PENDIENTE","EN_PREPARACION"),"iniciar preparación");
  test(ReglasLogistica.transicionSolicitud("PENDIENTE","RECHAZADA"),"rechazar");
  test(ReglasLogistica.transicionSolicitud("PENDIENTE","ANULADA"),"anular pendiente");
  test(!ReglasLogistica.transicionSolicitud("PENDIENTE","ATENDIDA"),"no saltar preparación");
  test(ReglasLogistica.transicionSolicitud("EN_PREPARACION","ATENDIDA"),"completar");
  test(ReglasLogistica.transicionSolicitud("EN_PREPARACION","ANULADA"),"anular preparación");
  test(!ReglasLogistica.transicionSolicitud("ATENDIDA","ANULADA"),"no anular atención terminada");
  test(!ReglasLogistica.transicionSolicitud("ANULADA","PENDIENTE"),"no reabrir anulada");
  test(ReglasLogistica.transicionInconsistencia("ABIERTA","EN_REVISION"),"revisar reporte");
  test(!ReglasLogistica.transicionInconsistencia("ABIERTA","RESUELTA"),"no saltar revisión");
  test(ReglasLogistica.transicionInconsistencia("EN_REVISION","RESUELTA"),"resolver");
  test(!ReglasLogistica.transicionInconsistencia("RESUELTA","EN_REVISION"),"no reabrir resuelta");
  test(ReglasLogistica.completa(2,2),"cantidad exacta");test(!ReglasLogistica.completa(2,1),"faltante");test(!ReglasLogistica.completa(2,3),"exceso");test(!ReglasLogistica.completa(0,0),"solicitud vacía");
  System.out.println("Reglas de negocio: "+total+" casos aprobados de "+total);
 }
}
