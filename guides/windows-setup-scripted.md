# Set up Windows 10 for installations

We'll use some .ps1 scripts and fewer manual steps

* Check out the [vanilla guide](./windows-setup.md) for the older fully-manual process. It includes lots of extra/unnecessary info for edge cases that I've seen over the years.

## Let's go

- Set up machine with [no Microsoft account](https://github.com/cacheflowe/haxademic/blob/master/guides/windows-setup.md?plain=1#L5-L12) and run all Windows updates when you first log in
- Now we can run our app installation scripts and manually (un)install other apps
    - Download **apps install/uninstall [script](../scripts/windows-setup/windows-apps-install.ps1).** This will install the Chocolatey package manager with a bunch of boilerplate apps & drivers, and uninstall bloatware.
    - Open Powershell ***as Administrator***
    - cd to the script that you downloaded (`cd $HOME\Downloads`) and run: `.\windows-apps-install.ps1`
        - If permissions don't allow, run it like this:
        - `powershell.exe -executionpolicy unrestricted .\windows-apps-install.ps1`
    - Uninstall 2 McAfee apps manually
    - Uninstall Geforce Experience - ***can this be part of the windows uninstall .ps1***?
    - Unzip and run Wub.exe from Downloads - turn off updates. We'll manually update Windows once in a while when we log in
- Set global windows settings with [**this script**](../scripts/windows-setup/set-windows-settings.ps1).
    - `powershell.exe -noprofile -executionpolicy bypass -file .\set-windows-settings.ps1`
    - Restart the machine!
    - Now we manually go through Windows settings that aren't updated with the script

## Check for non-essential apps & bloatware

* Uninstall from Settings and Control Panel
  * (WIN + "Add or Remove Programs") 
  * `Control Panel\Programs\Programs and Features`
* If you clearly have bloatware, delete any apps in Program Files (x86) that seem suspect and aren't in the Control Panel list (and aren't likely essential drivers). Some PCs come pretty stripped-down these days

## More settings

* `Control Panel\Hardware and Sound\Power Options`
  * Power button should shut down
* Turn off system sounds:
  * (WIN + "Change system sounds") Settings -> Personalization -> Themes -> Sounds -> Sound Scheme -> No Sounds
* (WIN + "Firewall & Network protection" -> Settings) Firewall notification settings
  * Uncheck all "Notify me" boxes
* (WIN + "Security and Maintenance") Control Panel -> System & Security -> Security & Maintenance -> Change Security & Maintenance settings
  * Uncheck all boxes
* Turn on Windows Defender
  * But turn off notifications
    * (WIN + "Startup") Task Manager -> Startup -> Disable Windows Defender Notifications
* (WIN + "Time & Date Settings") 
  * Check "Set Time Automatically"
  * Check "Set Time Zone Automatically"
  * Click "Sync Now"
* (WIN + "About") Rename PC (requires restart):
  * Settings -> System -> About -> Rename PC
* Remove login screen:
  * Windows button + "R" -> "netplwiz" + Run
    * Uncheck "Users must enter..." -> Apply -> Type password twice


## Teamviewer settings

* On remote machine
  * Check "Start with Windows" under "Unattended Access"
  * Go to "Extras -> Options"
    * General 
      * Incoming LAN connections: "Accept"
      * For Windows, make sure both Private & Public networks are checked in the firewall settings!
        * Control Panel\System and Security\Windows Defender Firewall\Allowed apps
    * Security
      * Windows Logon: Set to "All Users"
    * Remote Control
      * Optimize Speed
      * Uncheck "Remove Remote Wallpaper"
      * Uncheck "Play Sounds and Music"
    * Advanced
      * Enter a personal password
      * Check: "Automatically Minimize local TeamViewer Panel"
      * Check: "Ignore Alpha Blending"
      * Check: "Full access control when a partner is connecting to the Windows Logon screen"


## Make sure your app is using the graphics card

Windows doesn't necesaarily respect NVIDIA settings, when you want to specify that your app should use the dicrete GPU. Go to: 

* (WIN + "Graphics") System -> Display -> Graphics
* Click "Browse" and find your app. If you're using a system Java installation, find the JDK directory, then select `/bin/java` and `/bin/javaw`, and select your high performance graphics card from the menu, and save the setting

