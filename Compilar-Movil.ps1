$ErrorActionPreference='Stop'
Push-Location (Join-Path $PSScriptRoot 'movil')
try{
 if(-not $env:JAVA_HOME){$studio=Join-Path $env:ProgramFiles 'Android/Android Studio/jbr';if(Test-Path $studio){$env:JAVA_HOME=$studio}}
 if(-not(Test-Path 'local.properties')){
  $sdk=if($env:ANDROID_HOME){$env:ANDROID_HOME}else{Join-Path $env:LOCALAPPDATA 'Android/Sdk'}
  [IO.File]::WriteAllText((Join-Path (Get-Location) 'local.properties'),'sdk.dir='+$sdk.Replace('\','/')+"`n")
 }
 & ./gradlew.bat :androidApp:assembleDebug :shared:testAndroidHostTest --no-daemon
 if($LASTEXITCODE -ne 0){throw 'La compilación no pasó. Revisar el error anterior; no se entrega un APK sin validación.'}
 $delivery=Join-Path $PSScriptRoot 'entrega';New-Item -ItemType Directory -Path $delivery -Force | Out-Null
 Copy-Item 'androidApp/build/outputs/apk/debug/androidApp-debug.apk' (Join-Path $delivery 'Indumotos-debug.apk') -Force
 Write-Host 'APK de desarrollo generado en entrega/Indumotos-debug.apk'
}finally{Pop-Location}
