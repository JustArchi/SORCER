@echo off
pushd %~dp0
set PATH=%PATH%;%~dp0_archi\Gradle\bin;%~dp0_archi\Ant\bin

cd examples\archi
call gradle stopme

pause