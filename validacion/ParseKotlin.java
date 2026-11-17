import java.nio.file.*;
import java.util.*;
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment;
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles;
import org.jetbrains.kotlin.config.CompilerConfiguration;
import org.jetbrains.kotlin.psi.KtPsiFactory;
import com.intellij.openapi.util.Disposer;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.util.PsiTreeUtil;
public class ParseKotlin {
 public static void main(String[] args)throws Exception{
  var disposable=Disposer.newDisposable();int errors=0,files=0;
  try{var environment=KotlinCoreEnvironment.createForProduction(disposable,new CompilerConfiguration(),EnvironmentConfigFiles.JVM_CONFIG_FILES);var factory=new KtPsiFactory(environment.getProject(),false);
   try(var paths=Files.walk(Path.of(args[0]))){for(var p:paths.filter(p->p.toString().endsWith(".kt")).toList()){
    files++;var text=Files.readString(p).replace("\r\n","\n");var file=factory.createFile(p.getFileName().toString(),text);for(var e:PsiTreeUtil.collectElementsOfType(file,PsiErrorElement.class)){errors++;int n=e.getTextOffset();System.out.println(p+":"+(text.substring(0,n).chars().filter(c->c=='\n').count()+1)+": "+e.getErrorDescription()+" CONTEXTO: "+text.substring(Math.max(0,n-70),Math.min(text.length(),n+70)));}
   }}
  }finally{Disposer.dispose(disposable);}System.out.println("Kotlin: "+files+" archivos; "+errors+" errores de sintaxis. No sustituye compilación ni pruebas.");if(errors>0)System.exit(1);
 }
}
