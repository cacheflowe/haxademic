@if (@CodeSection == @Batch) @then
@echo off
REM take parameter (%1) from command line and pass to SendKeys
REM https://ss64.com/vb/sendkeys.html
CScript //nologo //E:JScript "%~F0" %1
goto :EOF
@end

WScript.CreateObject("WScript.Shell").SendKeys(WScript.Arguments(0));