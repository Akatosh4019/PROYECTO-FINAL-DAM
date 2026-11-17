import java.nio.file.*;
import javax.tools.*;
import com.sun.source.util.JavacTask;
import java.util.*;
public class ParseJava {
 public static void main(String[] args)throws Exception{
  var compiler=ToolProvider.getSystemJavaCompiler();var diagnostics=new DiagnosticCollector<JavaFileObject>();
  try(var manager=compiler.getStandardFileManager(diagnostics,null,java.nio.charset.StandardCharsets.UTF_8);var paths=Files.walk(Path.of(args[0]))){
   var files=paths.filter(p->p.toString().endsWith(".java")).toList();var units=manager.getJavaFileObjectsFromPaths(files);var task=(JavacTask)compiler.getTask(null,manager,diagnostics,List.of("-proc:none"),null,units);task.parse();
   long errors=diagnostics.getDiagnostics().stream().filter(d->d.getKind()==Diagnostic.Kind.ERROR).count();diagnostics.getDiagnostics().forEach(System.out::println);System.out.println("Java: "+files.size()+" archivos; "+errors+" errores de sintaxis. No sustituye compilación ni pruebas.");if(errors>0)System.exit(1);
  }
 }
}
