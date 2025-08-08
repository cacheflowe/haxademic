# Set up Windows 10 for installations

We'll use some .ps1 scripts and fewer manual steps

* Check out the [vanilla guide](./windows-setup.md) for the older fully-manual process. It includes lots of extra/unnecessary info for edge cases that I've seen over the years.

## Let's go

- Set up machine with [no Microsoft account](https://github.com/cacheflowe/haxademic/blob/master/guides/windows-setup.md?plain=1#L5-L12) 
  - Make sure ethernet is disconnected
  - Hit `Shift + F10` to launch a cmd prompt when you reach the wifi screen
    * Run this command: `OOBE\BYPASSNRO`
    - When it restarts, you'll have a new button "I don't have internet" - click that
- Run all Windows updates when you first log in and connect to wifi
- Now we can run our app installation scripts and manually (un)install other apps
  - Download **apps install/uninstall [script](../scripts/windows-setup/windows-apps-install.ps1).** This will install the Chocolatey package manager with a bunch of boilerplate apps & drivers, and uninstall bloatware.
    - Open Powershell ***as Administrator***
    - cd to the script that you downloaded (`cd $HOME\Downloads`) and run: `.\windows-apps-install.ps1`
      - If permissions don't allow, run it like this:
      - `powershell.exe -executionpolicy Bypass .\windows-apps-install.ps1`
  - Unzip and run Wub.exe from Downloads - turn off updates. We'll manually update Windows once in a while when we log in
- Set global windows settings with [**this script**](../scripts/windows-setup/set-windows-settings.ps1).
  - `powershell.exe -executionpolicy Bypass .\set-windows-settings.ps1`
  - Restart the machine!
- Now we manually go through Windows settings that aren't updated with the script

## Check for non-essential apps & bloatware

* Uninstall from Settings and Control Panel
  * (WIN + "Add or Remove Programs") 
  * `Control Panel\Programs\Programs and Features` (enter into Explorer address bar)
    * CMD: `control appwiz.cpl` (WIN + R) to run the command
    * CMD: `start ms-settings:appsfeatures`
  * Open the Start menu and uninstall anything that's trying to trick you into using it (LinkedIn, WhatsApp, etc)
* If you clearly have bloatware, delete any apps in Program Files (x86) that seem suspect and aren't in the Control Panel list (and aren't likely essential drivers). Some PCs come pretty stripped-down these days

## More settings

Use the CLI commands (or Start Menu shortcuts) to open specific [Settings pages](https://www.ninjaone.com/blog/shortcuts-to-directly-open-pages-windows/) and [Control Panel pages](https://www.tenforums.com/tutorials/86339-list-commands-open-control-panel-items-windows-10-a.html) 

* Set power plan
  * `Control Panel\Hardware and Sound\Power Options`
  * CMD: `control powercfg.cpl`
  * "Change plan settings" -> "Change advanced power settings"
    * Set "Turn off display after" to Never
    * Set "Put Computer to Sleep" to Never
* Don't sleep when closing the laptop lid
  * Power button should shut down
  * (WIN + "Lid") or (WIN + "Choose a power plan") or `Control Panel\All Control Panel Items\Power Options\System Settings` 
    * Choose what the power buttons do
      * When I Press the power button: Shut down
      * When I close the lid: Do nothing (if available)
  * CMD: `start ms-settings:powersleep` - Power & sleep button controls
    * Power Mode -> Best Performance
* Turn off system sounds:
  * CMD: `control mmsys.cpl sounds`
  * (WIN + "Change system sounds") Settings -> Personalization -> Themes -> Sounds -> Sound Scheme -> No Sounds
    * Uncheck "Play Windows Startup Sound"
* (WIN + "Firewall & Network protection" -> Settings) Firewall notification settings
  * CMD: `start ms-settings:windowsdefender` (Open Security Settings) -> Settings (bottom left corner) -> Firewall notification settings (or Manage Notifications)
  * Uncheck all "Notify me" boxes
* (WIN + "Security and Maintenance") Control Panel -> System & Security -> Security & Maintenance -> Change Security & Maintenance settings
  * CMD: `control wscui.cpl` -> Change Security & Maintenance settings
  * Uncheck all boxes
* Turn off Windows Defender notifications
  * (WIN + "Startup") Task Manager -> Startup Apps -> Disable Windows Defender Notifications (Also SecurityHealthSystray.exe)
  * CMD: `taskmgr`
  * Right-click -> Disable
* (WIN + "Time & Date Settings") 
  * ~~CMD: `control timedate.cpl`~~
  * CMD: `start ms-settings:dateandtime`
  * Check "Set Time Automatically"
    * If this is grayed out, click "Location Setings" and turn on Location Services
  * Check "Set Time Zone Automatically"
  * Click "Sync Now"
* (WIN + "About") Rename PC (requires restart):
  * CMD: `start ms-settings:about`
  * CMD: `control sysdm.cpl`
  * Settings -> System -> About -> Rename PC
* Remove login screen:
  * Windows button + "R" -> "netplwiz" + Run
  * CMD: `netplwiz`
    * Uncheck "Users must enter..." -> Apply -> Type password twice
* (WIN + "Sign-in Options") "If you've been away, when should Windows require you to sign in again?" -> Never
  * CMD: `start ms-settings:signinoptions`
  * Dynamic Lock -> Off
* Change background to black
  * (WIN + "Background") Settings -> Personalization -> Background
  * CMD: `start ms-settings:personalization-background`
  * Select "Solid color" and choose black
* Unpin apps from the Start menu
* Add Windows Defender with Advanced Security rule
  - Ports (in & out): 80, 443, 3000-3100, 5173-5176, 8000-8100
  - CMD: `wf.msc`
* (WIN + "Powershell Developer Setings") Allow Powershell scripts to run without signing -> On
  * ~~CMD: `start ms-settings:developer`~~
* Remove widgets from taskbar. This [can not be done](https://kolbi.cz/blog/2024/04/03/userchoice-protection-driver-ucpd-sys/) with the script
  * CMD: `start ms-settings:taskbar`

## Add firewall rules for apps



## Teamviewer settings

* On remote machine
  * Open Settings
    * General 
      * Check: "Start TeamViewer with Windows"
      * Incoming LAN connections: "Accept"
        * If you're going to access via IP address from another local machine, make sure both Private & Public networks are checked in the firewall settings
          * Control Panel\System and Security\Windows Defender Firewall\Allowed apps
    * Security
      * Windows Logon: Set to "All Users"
    * Remote Control
      * Quality: Optimize Speed
      * Uncheck: "Remove Remote Wallpaper"
      * Check: Show your partner's cursor
      * Uncheck: "Play Sounds and Music"
    * Advanced
      * Check: "Automatically Minimize local TeamViewer Panel"
      * Check: "Full access control when a partner is connecting to the Windows Logon screen"
      * Check: "Ignore Alpha Blending"
      * Set a personal password
      * Lock Remote Computer: "Never"


## Make sure your app is using the graphics card

Windows doesn't necesaarily respect NVIDIA settings, when you want to specify that your app should use the dicrete GPU. Go to: 

* (WIN + "Graphics") System -> Display -> Graphics
* Click "Browse" and find your app. If you're using a system Java installation, find the JDK directory, then select `/bin/java` and `/bin/javaw`, and select your high performance graphics card from the menu, and save the setting

