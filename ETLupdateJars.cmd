"C:\Program Files\Java\jdk1.8.0_60\bin\jar.exe" cmfe ETLmanifest.mf ETLPattFind.jar common.Main -C ETLPattFind\bin .
"C:\Program Files\Java\jdk1.8.0_60\bin\jar.exe" cmfe ETLmanifest.mf ETLPattFindDeamon.jar deamon.ETLPattFindDeamon -C ETLPattFindDeamon\bin .
copy /y ETLPattFind*.jar ETLPattFindServer
