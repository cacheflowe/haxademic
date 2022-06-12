Set WshShell = WScript.CreateObject("WScript.Shell")
WshShell.Run "notepad.exe", 9
WScript.Sleep 500
WshShell.AppActivate "notepad"
WshShell.SendKeys "Hello World!"
WshShell.SendKeys "{ENTER}"
WshShell.SendKeys "{F5}"
WshShell.SendKeys "{ENTER}"
