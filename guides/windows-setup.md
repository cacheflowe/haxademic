# Set up Windows 10 for installations

* Check out this [other guide](https://tomsepe.com/sepeblog/2020/11/08/show-machine-setup-for-permanent-and-temporary-art-installations-windows-10/) too!

* Set up Windows normally during blue setup screens, but: 
  * Don't attach the machine to a network or wifi! Click "I don't have Internet" instead of connecting to Wifi/Ethernet. If you connect, it will force you to create a Microsoft online account
    * Newer Windows installations don't give you this option, so do the following when confronted with internet-only setup
    * Hit `Shift + F10` to launch a cmd prompt
    * Run this command: 
      * `taskkill /F /IM oobenetworkconnectionflow.exe` (this should let you continue without restarting)
      * or `OOBE\BYPASSNRO` (this will erstart the machine)
    * If you're still stuck, try this:
      * Usually in the "Check Microsoft connection" section, there is an "I don't have Internet connection" option in the lower left corner of the page. If you don't see it, or if you have waited a long time for it to load, I recommend you use the key combination "Ctrl+Shift+F3" to try to skip this section(Some devices may need to hold down Ctrl+Shift and press Fn+F3 at the same time due to function keys).
    * When it restarts, you should now have an "I don't have Internet" button, followed by a "Continue with a limited setup" button, and can continue setup without a Microsoft account
  * Turn off non-essential services (but keep location services for clock-syncing)

## Uninstall non-essential apps & bloatware

* Uninstall from Settings and Control Panel
  * (WIN + "Add or Remove Programs") 
  * `Control Panel\Programs\Programs and Features`
* If you clearly have bloatware, delete any apps in Program Files (x86) that seem suspect and aren't in the Control Panel list (and aren't likely essential drivers). Some PCs come pretty stripped-down these days

## Run Windows system updates

* (WIN + "Check for updates") Settings -> Update & Security -> Windows Update -> Check for Updates
* Restart/Repeat until there are really no more updates
* Update video card drivers

## Clean up Windows UI

* Taskbar
  * Unpin apps
* (WIN + "Taskbar Settings") System Taskbar settings
  * Turn off Taskbar items
  * Taskbar Behaviors
    * Check Automatically hide the taskbar
    * Uncheck Show Badges
    * Uncheck Show Flashing
    * Uncheck Show Recent Searches
* Click notification icon in taskbar and "clear all notifications" at bottom
* Change background image
* (WIN + "Themes & Related Settings") Hide Recycle Bin -> Desktop Icon Settings

## Prevent interruptions

* Energy preferences:
  * (WIN + "Power Settings") Settings -> System -> Power & Sleep
    * When plugged in, turn off screen & PC sleep: Never
    * (WIN + "Edit Power Plan") Click Additional Power settings button:
      * Show additional plans -> High Performance -> Change plan settings -> Change advanced power settings
        * Select "High Performance" from dropdown
        * USB settings -> Selective suspend -> Plugged in -> Disabled
  * `Control Panel\Hardware and Sound\Power Options`
    * Power button should shut down
    * Laptop should do nothing on lid close
* (WIN + "Sign-in Options") "If you've been away, when should Windows require you to sign in again?" -> Never
  * Dynamic Lock -> Off
* (WIN + "Screen saver") Turn off screensaver : Settings -> Personalization -> Lock screen -> Screen saver settings
* Turn off system sounds:
  * (WIN + "Change system sounds") Settings -> Personalization -> Themes -> Sounds -> Sound Scheme -> No Sounds
* Turn off notifications:
  * (WIN + "Notifications") Settings -> System -> Uncheck "Notifications"
    * Turn on "Do Not Disturb" for all cases
    * Set Priority Notifications: Turn all off
  * (WIN + "Notifications privacy") Settings -> Privacy -> Notifications -> Turn off "Let apps access my notifications" and other checkbox
  * (WIN + "Firewall & Network protection" -> Settings) Firewall notification settings
    * Uncheck all "Notify me" boxes
  * (WIN + "Security & Maintenance") Control Panel -> System & Security -> Security & Maintenance -> Change Security & Maintenance settings
    * Uncheck all boxes
* Turn on Windows Defender
  * But turn off notifications
    * (WIN + "Startup") Task Manager -> Startup -> Disable Windows Defender Notifications
* Turn off power management for wifi connection:
  * (WIN + "Network Connections") Control Panel -> Network and Internet -> Network and Sharing Center -> Change Adapter Settings -> Right-click Wifi -> Properties -> Configure -> Power Management -> Uncheck "Allow ... turn off power

## Performance boost

* Defrag Hard drive (should be automatic by default):
  * Control Panel -> System & Security -> Administrative tools -> Defrag and optimize your drives
* (WIN + "Adjust performance") Control Panel -> System & Security -> System -> Advanced System settings -> Advanced tab -> Performance Settings -> Visual Effects -> Adjust for Best Performance
  * But re-check:
    * Show shadows under Mouse pointer
    * Show window contents while dragging
    * Smooth edges of screen fonts

## Windows settings

* (WIN + "Time and Date Settings") 
  * Check "Set Time Automatically"
  * Check "Set Time Zone Automatically"
  * Click "Sync Now"
* (WIN + "Display") Settings -> System -> Display -> Change the size of text, apps... -> 100%
* Show file name extensions: In a system Explorer window, click "View" in the toolbar, and check "File name extensions" and "Hidden files"
* (WIN + "Developer") Set to Developer mode:
  * Settings -> Privacy & Security -> Enable Developer Mode option
* (WIN + "About") Rename PC (requires restart):
  * Settings -> System -> About -> Rename PC
* Remove login screen:
  * Windows button + "R" -> "netplwiz" + Run
    * Uncheck "Users must enter..." -> Apply -> Type password twice
    * If you're using Windows 10 Pro or don't see this checkbox, in which case, you need to edit the registry:
      * `Computer\HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows NT\CurrentVersion\PasswordLess\Device`
      * Change DevicePasswordLessBuildVersion to 0

## Networking

* Enable network sharing
  * Click on the Network button in Explorer, and the first time, a notification should show in the window header, asking you to enable
  * This could be bad for network security vulnerability scans, so don't turn it on if this could be a concern with an IT department
* Set a static [IP address](https://portforward.com/networking/static-ip-windows-10.htm) - only needed for multi-machine networking situations
* Allow WebSockets messages to go through on a specific port (3001 is default in Haxademic)
  * Open `Windows Firewall with Advanced Security` and add an incoming and outgoing rule for port 3001, and allow on all networks

## Teamviewer settings

Be sure to remove Windows' "[Fast User Switching](https://www.howtogeek.com/howto/windows/disable-fast-user-switching-on-windows-xp/)" feature, to ensure that fullscreen OpenGL apps don't fail to view when switching windows.

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
* On local machine
  * Go to "Extras -> Options"
    * Remote Control
      * Optimize Speed
      * Uncheck "Remove Remote Wallpaper"
      * Check: "Show your partner's cursor"
      * Uncheck "Play computer sounds and music"
    * Advanced
      * "Lock remote computer": Never

## Make sure your app is using the graphics card

Windows doesn't necesaarily respect NVIDIA settings, when you want to specify that your app should use the dicrete GPU. Go to: 

* (WIN + "Graphics") System -> Display -> Graphics
* Click "Browse" and find your app. If you're using a system Java installation, find the JDK directory, then select `/bin/java` and `/bin/javaw`, and select your high performance graphics card from the menu, and save the setting

## Enable remote restarts

On top of Teamviewer, you might want to enable remote restart in the case that you have access to one machine, but not another, and they're on the same network. Use the following command to restart another machine:

`shutdown /r /m \\machine-name`

And you'll need to enable permissions to allow this in the registry:

`Computer\HKEY_LOCAL_MACHINE\Software\Microsoft\Windows\CurrentVersion\Policies\System`

Add new DWORD (32-bit) key `LocalAccountTokenFilterPolicy`, and set the value to `1`

And make sure `Remote Shutdown` is enabled in Windows Defender firewall

## Disable Windows update notifications
	
* Download [WUB (Windows Update Blocker)](https://www.sordum.org/9470/windows-update-blocker-v1-7/)

## Disable Java updates (If you installed Oracle Java)

* Find the Java icon in the system taskbar from the up arrow on the right and disable (might not be a thing anymore)
* Open Java app from Start Menu and disable from there
* (WIN + "Startup Apps") Open the system Task Manager (ctrl + alt + delete), go to the Startup tab, and disable java updater on startup

## Startup Blue Screen issues

* Only if you see bad blue screen startup after testing cutting power: Turn off blue screen after bad power cuts: "Automatic Repair couldn't repair your PC"
  * Open Command Prompt as Administrator
    * `bcdedit /set recoveryenabled NO`
    * `bcdedit /set {default} recoveryenabled No`
    * `bcdedit /set {current} recoveryenabled No`
    * `bcdedit /set {default} bootstatuspolicy ignoreallfailures`
    * `bcdedit /set {current} bootstatuspolicy ignoreallfailures`
    * Info from: https://www.thewindowsclub.com/automatic-repair-couldnt-repair-pc
    * And: https://www.itechpost.com/articles/103531/20200814/4-solutions-to-fix-boot-critical-file-is-corrupt-error.htm
  * If a blue screen Automatic Repair screen happens, run these commans in an elevated command prompt:
    * `chkdsk /r c:` - then restart
  * If you end up on the recovery screens, try the following:
    * https://support.microsoft.com/en-us/topic/use-bootrec-exe-in-the-windows-re-to-troubleshoot-startup-issues-902ebb04-daa3-4f90-579f-0fbf51f7dd5d
    * `bootrec.exe /rebuildbcd`
    * `bootrec.exe /fixmbr`
    * `bootrec.exe /fixboot`

## Networking vulnerability scans

* Turn off Remote Desktop Connection in Windows 10 Pro if you want to lock the computer down from vulnerabilities
  * (WIN + "Remote Desktop Settings") Uncheck "Enable Remote Desktop"
    * If the option to enable/disable RDP is greyed out, we might have to change the group policy for the computer. 
      * WIN + R -> `gpedit.msc`
        * Int the tree, find `Computer configuration\Administrative Templates\Windows Components\Remote Desktop Services\Remote Desktop Session\Connections\Allow users to connect remotely...` and make its status is "Not Configured" instead of "Enabled".
* Get Nessus and run a scan on your IP range:
  * https://www.tenable.com/downloads/nessus

## Create ssh key for machines' GitHub access

* Install Git Bash
* Generate a new key in Git Bash terminal:
  ```
  $ ssh-keygen -t ed25519 -C "your@email.com"
  ```

* Should output:  
  ```
  Generating public/private ed25519 key pair.
  Enter file in which to save the key (/c/Users/your_user/.ssh/id_ed25519):
  Created directory '/c/Users/your_user/.ssh'.
  Enter passphrase (empty for no passphrase): YOUR_PASSWORD
  Enter same passphrase again: YOUR_PASSWORD
  Your identification has been saved in /c/Users/your_user/.ssh/id_ed25519
  Your public key has been saved in /c/Users/your_user/.ssh/id_ed25519.pub
  The key fingerprint is:
  SHA256:j+2JvTnII7o7s8JkXIdH2amW/lF09mrUjR4Uad34nsk your@email.com
  The key's randomart image is:
  +--[ED25519 256]--+
  |       o .    oo |
  |      o o . o.. o|
  |     o o ..o + o+|
  |    o *   . . =oo|
  | . . +  S. . o.o+|
  |  +   . .=  o .E.|
  | +     oooo.     |
  |  o o .o=..o     |
  |   .=B o+.o.     |
  +----[SHA256]-----+
  ```

* Copy to Github project:
  ```
  $ cat /c/Users/your_user/.ssh/id_ed25519.pub
  ```
  Shows something like: 
  ```
  $ ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIA6mvfhC48oKJzihaAvZouPr7D1Uhxc4eWSaa5qE8RRU your@email.com
  ```

## Download essential apps & pin to taskbar

* Latest GeForce drivers
* TeamViewer
* VSCode
* Git Bash (download from Git website)
* Github Desktop
* Java latest version (Eclipse Adoptium, not Oracle)
* Eclipse
* Branded desktop background image
* Optional
  * Microsoft Kinect SDK or Realsense Viewer
  * Processing
  * Advatek Assistant
  * VLC

Look into using a package manager or automated installer if you need to set up multiple machines:

* [Chocolatey](https://chocolatey.org/)
* [Scoop](https://scoop.sh/)
* [Ninite](https://ninite.com/)

Here's a list of commands to install Chocolatey and install some essentials from an admin-elevated Powershell:

```
# Install Chocolatey
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# Install apps
choco install eclipse
choco install vscode
choco install github-desktop
choco install git
choco install teamviewer
choco install vlc
choco install googlechrome
choco install nodejs-lts
choco install rustdesk
choco install quicklook
choco install ffmpeg
```

### Optionally create a read-only Deploy Key for safe Git pulls

* Generate a project-specific (and potentially password-less) key via Git Bash
  * `ssh-keygen -t rsa -b 4096 -C "user@domain.com"`
* Go to the project page on GitHub, click **Settings**, then **Deploy Keys**, then **Add New**
  * Paste your public key (.pub file) into the **Key** textfield

### Add ssh key to each machine

* Add key to Windows w/Git Bash
* Copy ssh key files into `C:\Users\your_user\.ssh`
  ```
  $ eval `ssh-agent -s`
  $ ssh-add ~/.ssh/id_ed25519
  ```
* Pull repo
  ```
  $ `mkdir ~/Documents/workspace`
  $ `cd ~/Documents/workspace`
  $ `git clone git@github.com:GitHub-Account/github-repo.git`
  $ `cd ~/Documents/workspace/github-repo`
  $ `git pull`
  ```
* Set up your git identity:
```
  $ `git config --global user.email "you@example.com"`
  $ `git config --global user.name "Your Name"`
```
* Do git things in Git Bash, and run scripts in CMD
* CMD into project dir to pull, etc
  ```
  $ cd C:\Users\your_user\Documents\workspace\github-repo
  ```

## Set up a touchscreen kiosk

- Set touchscreen monitor as primary monitor in Display Settings. This will ensure that a (Chrome) launch script goes fullscreen on the touchscreen
- Disable EdgeUI in the Registry
  - https://www.thewindowsclub.com/disable-screen-edge-swipe-in-windows-10
- Turn off Windows multitouch gestures:
  - Settings -> Bluetooth & Devices -> Touchpad
  - Turn off three and four finger gestures and any other settings
- Use a [launch script](../scripts/chrome-launch-apps.cmd) to launch Chrome in locked-down kiosk mode

## Additional steps

  * https://github.com/farag2/Sophia-Script-for-Windows
  * https://github.com/Fahim732/Windows-10-optimize-script
  * BIOS settings to resume after power loss!!!
  * http://www.evsc.net/tech/prep-windows-machine-for-fulltime-exhibition-setup
  * https://github.com/morphogencc/ofxWindowsSetup
  * https://github.com/brangerbriz/up-4evr-windows-10
  * https://gist.github.com/jasonalderman/f7c27d18b978cc2566cdf848e8493b2c

## Scripting help
  * Set a timeout: `timeout 15 > NUL`
  * Set a timeout with a visible countdown: `timeout 15`
  * Reference User directory: `C:%HOMEPATH%\Documents\`
  * Remove Apple files from current dir: `del /s /q *.DS_Store`
  * List running tasks: `tasklist`
  * Find a task on a specific port: `netstat -a -n -o | find "8080"`
  * Get app name from task number: `tasklist | findstr 9260`
  * Kill a task by .exe: `Taskkill /F /T /IM javaw.exe`
  * Kill a task by id: `Taskkill /F /T /PID 26356`
  * Add script to system startup:
    * Windows Key + R
    * `shell:startup`
    * Copy alias into Startup folder
  * Add custom icon to script:
    * Make a shortcut -> right-click -> Properties -> Change Icon...
  * Pin a script to Start menu
    * Right-click the shortcut, and make the *Target* look like this: 
    * cmd /c "C:\Users\you\Documents/startup.cmd"
    * Now you can right-click and have more options

* Setup scripts
  * https://www.ghacks.net/2016/11/08/improve-windows-10-with-one-click-batch-files/
  * https://github.com/morphogencc/ofxWindowsSetup/tree/master/scripts
    
## USB device driver help

From elevated admin CMD:
https://docs.microsoft.com/en-us/windows-hardware/drivers/devtest/pnputil-command-syntax

List connected devices w/IDs:
$ `pnputil /enum-devices /connected`

Check for update devices:
$ `pnputil /scan-devices`

Enable/disable/restart/remove device by id:
$ `pnputil /enable-device "ROOT\WindowsHelloFaceSoftwareDriver\0000"`