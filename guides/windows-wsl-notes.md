# Windows 10 Subsystem for Linux and bash setup

## Install Ubuntu from the Windows Store

I'm using Ubuntu and the Bash shell, so everything here relates to that particular setup. Follow the instructions here to enable WSL and download a Linux distribution:

* [How to Get Started Using WSL in Windows 10](https://www.linux.com/blog/learn/2018/2/how-get-started-using-wsl-windows-10)

## Opening the WSL shell

There are [multiple ways](https://blogs.msdn.microsoft.com/commandline/2017/11/28/a-guide-to-invoking-wsl/) to open the WSL bash shell :

* Open `Ubuntu` from the Windows Start menu
* Type `Windows key + R`, type `wsl` and hit Enter
* Open a Windows Command Prompt window, type `bash` and hit Enter

## Upgrading WSL

Be sure to update your Ubuntu installation using the following commands:

```
sudo apt-get update
sudo apt-get dist-upgrade
```

For larger updates:

Info: 

* https://askubuntu.com/questions/1428423/upgrade-ubuntu-in-wsl2-from-20-04-to-22-04

Try to set WSL2 if you're not already on it... This could be destructive, so be careful...

* `wsl --terminate Ubuntu-20.04`
* `wsl --update`
* `wsl --set-default-version 2`

Check your version in Powershell w/admin access:

* `wsl --version`
* `wsl -l --verbose`
* `wsl --status`
* `wsl cat /proc/version`

Set default install

* `wsl --setdefault INSTALL_NAME`

Check your version in wsl:

* `cat /etc/os-release`
* `lsb_release -a`

## Navigating the WSL shell

* Your Windows filesystem shows up as a mounted drive, like `/mnt/c/`
  * Since the Ubuntu path and your Windows path are different, there's a built-in tool called `wslpath` to help convert paths from one to the other. Unfortunately, dragging a file into the shell currently uses the Windows path, but hopefully there's an auto-translation in the future. Find some usage documentation [here](https://github.com/Microsoft/WSL/issues/2715).
    * For example, the command:
      * `wslpath "D:\workspace\data\2018-09-27-12-30-37.json"`
    * Outputs:
      * `/mnt/d/workspace/data/2018-09-27-12-30-37.json`
* The Linux filesystem is in a weird place on your Windows hard drive, and I would recommend not storing files here beyond those relating to your Linux installation. I just use Linux/bash as a way to control my Windows files
* You can right-click to paste your Windows clipboard contents into the WSL shell

## Installing packages

I mostly use the Bash shell for processing media files, so tools like `imagemagick` and `ffmpeg` are essential to my workflow. With Ubuntu, the common way of installing these tools is with the `apt` (Advanced Packaging Tool) package manager. For example:

* `sudo apt install ffmpeg`
* `sudo apt install imagemagick`

### Other tools

[Node.js](https://docs.microsoft.com/en-us/windows/dev-environment/javascript/nodejs-on-wsl):

* `curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/master/install.sh | bash`
* `. ~/.bashrc` - reload shell
* `command -v nvm` - confirms install
* `nvm install --lts` - install the current LTS version of Node
* `node --version`

## Install NVIDIA drivers

Info:

* https://docs.nvidia.com/cuda/wsl-user-guide/index.html

Try running `nvidia-smi`. It should either list your GPUs or show you some commands to install the NVIDIA toolkit.

## Running bash scripts

One gotcha that I ran into was that a lot of my bash scripts had non-unix carriage returns. I thought something was wrong with my bash setup, but I simply had to convert my scripts to use the unix version. There's a nice little helper for this called `dos2unix`. Install it using:

* `sudo apt install dos2unix`

And convert your script by calling:

* `dos2unix scriptFile.sh`

## Make sure Git works

Related to the bash script carriage return issue noted above, Git has a similar problem by default, which manifests as every file in `git status` being marked as "modified", when this really isn't the case. Info from this [WSL GitHub issue](https://github.com/Microsoft/WSL/issues/184#issuecomment-209913528) reveals the solution via your Git configuration:

* `git config --global core.autocrlf true`

Make sure to also store your git passphrase with the ssh tool

* https://stackoverflow.com/a/49942440

Store your git credentials if you keep getting asked for your username & password (and make sure the password on the CLI is actually your 'personal access token'):

* `git config credential.helper store`
	* https://stackoverflow.com/a/22652170
* https://stackoverflow.com/a/34919582
* https://docs.github.com/en/github/authenticating-to-github/keeping-your-account-and-data-secure/creating-a-personal-access-token

More Git config for WSL here: [https://peteoshea.co.uk/setup-git-in-wsl/](https://peteoshea.co.uk/setup-git-in-wsl/)

If you've cloned a repo with Github Desktop but want to use WSL git with ssh access, you might need to do this:
`git remote set-url origin git@github.com:Username/repo-here.git

Add these lines to ~/.bashrc to show the current git branch

```
parse_git_branch() {
   git branch 2> /dev/null | sed -e '/^[^*]/d' -e 's/* \(.*\)/ (\1)/'
}
PS1='${debian_chroot:+($debian_chroot)}\[\033[01;32m\]\u@\h\[\033[00m\]:\[\033[01;34m\]\w\[\033[00m\]\[\033[33m\]$(parse_git_branch)\[\033[00m\]\$ '
```

## Customizing the WSL shell

There are a bunch of options to customize your bash shell, as with any Linux distribution. Though with the latest WSL versions I've not had a good time customizing without breaking things.

If you want to reload your bash shell with any changes you might have made to your [dotfiles](https://www.quora.com/What-are-dotfiles), call:

`. ~/.bashrc` or `source ~/.bashrc`

You can customize Bash to a great extent by modifying dot files in your user directory, which is located at `/home/username` or with the shortcut `~/`.

Here's my `/home/cacheflowe/.bash_profile`, which provides me with lots of useful shortcuts. Some of these are relate to running Apache, which I describe below. To launch this in VSCode, you can run this command:

* `code /home/cacheflowe/.bash_profile`

```
##########################################
# Add to system PATH
##########################################

PATH="/mnt/d/workspace/media-utility-scripts/:$PATH"
PATH="$HOME/.linuxbrew/bin:$HOME/.linuxbrew/sbin:$PATH"

##########################################
# Add aliases/shortcuts
##########################################

alias workspace="cd /mnt/d/workspace"

alias openexplorer="explorer.exe ."
alias reload=". ~/.bashrc"
alias backupbash="cp ~/.* /mnt/d/workspace/cacheflowe-wsl/user-cacheflowe/"
cdwin() {   	# usage: `cdwin "D:\path\in\windows"`
  cd $(wslpath "$1")
}

alias loadbashfromwindows="cp /mnt/d/workspace/cacheflowe-wsl/user-cacheflowe/.*  ~/"
alias loadvhostsfromwindows="sudo cp /mnt/d/workspace/cacheflowe-wsl/sites-enabled/*.* /etc/apache2/sites-enabled"
alias loadhostsfromwindows="sudo cp /mnt/d/workspace/cacheflowe-wsl/etc/hosts /mnt/c/Windows/System32/drivers/etc/hosts"

alias apachestart="sudo service apache2 start"
alias apacherestart="sudo service apache2 restart"
alias apachestop="sudo service apache2 stop"
alias apacheopen="cd /etc/apache2"

alias mysqlstart="sudo service mysql start"
alias mysqlrun="/usr/bin/mysql -u root -p"
# alias edithosts="atom /mnt/d/workspace/cacheflowe-wsl/etc/hosts"
# alias edithosts="vi /etc/hosts"
alias phpinfo="php --ini"
alias phpsettings="sudo nano /etc/php/7.1/cli/php.ini"

##########################################
# Init WSL bash settings when terminal window opens, and navigate to projects directory
##########################################

reload
workspace
```

## Install Apache on WSL / Ubuntu

I use Apache as a basic web server, and also use php as a quick & easy web server scripting language. Below you'll find some helpful info to get these set up quickly.

Here are some helpful articles on getting the LAMP stack running:

* https://medium.com/@fiqriismail/how-to-setup-apache-mysql-and-php-in-linux-subsystem-for-windows-10-e03e67afe6ee
* https://www.digitalocean.com/community/tutorials/how-to-set-up-apache-virtual-hosts-on-ubuntu-16-04
* https://www.nextofwindows.com/allow-server-running-inside-wsl-to-be-accessible-outside-windows-10-host

Install commands:

Apache: 

* `sudo apt install apache2`
* `sudo a2enmod rewrite` - enable [mod_rewrite](https://www.digitalocean.com/community/tutorials/how-to-rewrite-urls-with-mod_rewrite-for-apache-on-ubuntu-16-04)
* `sudo a2enmod ssl` - enable SSL for vhosts
* `sudo a2enmod headers` - enable [headers](https://stackoverflow.com/a/5758551)

php :

Be sure to check for the latest version when you install. I've also included the php-xml lib for my own purposes

```
sudo apt install php8.1-common php8.1-cli -y
sudo apt install php8.1-{bz2,curl,intl,mysql,readline,xml} -y
sudo apt install libapache2-mod-php8.1 -y
sudo apt-get install php-xml
service apache2 reload 
```

Edit php.ini

* `sudo nano /etc/php/8.1/apache2/php.ini`
* Update: `upload_max_filesize = 200M`

Start Apache with:

`sudo service apache2 start`

The Apache installation resides at:

`/etc/apache2`

Apache error logs should be here:

`/var/log/apache2`

### Enable Apache vhosts to create named local web servers

* https://www.digitalocean.com/community/tutorials/how-to-rewrite-urls-with-mod_rewrite-for-apache-on-ubuntu-16-04

Vhost configuration files need to be created in `/etc/apache2/sites-enabled`, and seem to need more settings than my Apache setup on OS X did. Here's an example of my `localhost.conf` file, which sets up Apache to work locally and be reachable from another computer using my chosen port of 3333. I use the `D:\workspace` directory on Windows as the root of my server. Be sure To restart Apache if you change these files.

```
<VirtualHost *:80>
    ServerAlias localhost
    ServerName localhost
    DocumentRoot /mnt/d/workspace
    <Directory /mnt/d/workspace>
      Options Indexes FollowSymLinks
      AllowOverride All
      Require all granted
    </Directory>
</VirtualHost>

Listen 3333
<VirtualHost *:3333>
    ServerAdmin user@example.com
    ServerAlias localhost
    ServerName localhost
    DocumentRoot /mnt/d/workspace
    <Directory /mnt/d/workspace>
      Options Indexes FollowSymLinks
      AllowOverride All
      Order allow,deny
      Allow from all
      Require all granted
    </Directory>
</VirtualHost>

<VirtualHost _default_:*>
    DocumentRoot "/mnt/d/workspace"
</VirtualHost>
```

I also like to set up websites with a virtual host, so I can treat them like the production server. Here's a configuration that sets my own website up as `http://localhost.cacheflowe.com` for my development environment:

```
<VirtualHost *:80>
ServerAdmin user@example.com
    DocumentRoot "/mnt/d/workspace/cacheflowe.com"
    ServerName localhost.cacheflowe.com
    <Directory /mnt/d/workspace/cacheflowe.com>
      Options Indexes FollowSymLinks
      AllowOverride All
      Order allow,deny
      Allow from all
      Require all granted
    </Directory>
</VirtualHost>
```

#### Edit your hosts file

Working in tandem with vhost configurations, you need to edit your hosts file for your computer to recognize the virtual hosts. In this situation, the hosts file that matters is the __Windows__ hosts file, located here:

* `C:/Windows/System32/drivers/etc/hosts`

In new version of APache & WSL, you'll need [2 entries per vhost](https://github.com/microsoft/WSL/issues/4347) like so:

```
127.0.0.1       localhost.cacheflowe.com
::1             localhost.cacheflowe.com
```

#### Enable SSL for local development

Some web development that requires hardware access (real-time camera, accelerometer) require a web server that has SSL enabled. I've adapted the setup from [this guide](https://creativelogic.biz/blog/https-ssl-local-dev-with-windows), but their setup seems to be running Apache on Windows, not on Ubuntu. So, here are the steps to create an SSL certificate and allow your machine and others to make https requests to your Ubuntu Apache server. Be sure to enter a real passphrase when prompted, and write it down:

* Create a `certs` directory somewhere safe on your Windows drive. You won't want to move this.
* Run the following commands from the WSL command line in your `certs` directory. In this example, we'll use `/mnt/d/workspace/certs`
```
  mkdir certs
  cd certs
  openssl genrsa -des3 -out myCA.key 2048
  openssl req -x509 -new -nodes -key myCA.key -sha256 -days 1825 -out myCA.pem
```

* Tell your Windows machine to trust the certificate once it's being used by your Apache server:
  * Press the Windows key and type "Cert" on the start screen and launch the Windows Certificate Manager
  * Click "Trusted Root Certification Authorities" and right-click to Import your certificate
  * Click Browse, change file type you are looking for to All Files (.), select the myCA.pem file we just saved, and import!
  * You might also want to restart your web browser

* Create the following script in your `certs` directory. I named mine `cert-gen.sh` and ran it by calling:
* `./cert-gen.sh localhost`
* This will generate final certificate files in the same directory, with the name of your hostname: `localhost.crt` and `localhost.key`
```
  #!/bin/bash

  if [ "$#" -ne 1 ]
  then
  echo "You must supply a domain..."
  exit 1
  fi

  DOMAIN=$1

  openssl genrsa -out ./$DOMAIN.key 2048
  openssl req -new -key ./$DOMAIN.key -out ./$DOMAIN.csr

  cat > ./$DOMAIN.ext << EOF
  authorityKeyIdentifier=keyid,issuer
  basicConstraints=CA:FALSE
  keyUsage = digitalSignature, nonRepudiation, keyEncipherment, dataEncipherment
  subjectAltName = @alt_names

  [alt_names]
  DNS.1 = $DOMAIN
  EOF

  openssl x509 -req -in ./$DOMAIN.csr -CA myCA.pem -CAkey myCA.key -CAcreateserial \
  -out ./$DOMAIN.crt -days 1825 -sha256 -extfile ./$DOMAIN.ext

  rm ./$DOMAIN.csr
  rm ./$DOMAIN.ext
```

* Add the final certificates' location and SSL settings to your vhosts file, making a copy of your normal (non-SSL) entry. In my case it's called `localhost`. The ServerName should (I believe) match the name used when running `cert-gen.sh`. Port 443 is the default for an SSL host. Be sure to fill in anything else needed for your vhost entry:
```
  <VirtualHost *:443>
    DocumentRoot "/mnt/d/workspace"
    ServerName localhost
    SSLEngine on
    SSLCertificateFile "/mnt/d/workspace/certs/localhost.crt"
    SSLCertificateKeyFile "/mnt/d/workspace/certs/localhost.key"
  </VirtualHost>
```

* If all goes well, you should be able to make requests like `https://localhost` or `https://your.ip.address` from other devices. You might have to manually allow the self-signed certificate in any given browser.

* Here's [another set of instructions](https://gist.github.com/jitheshkt/7f578e3f450af9d0e8a248545d2662d7) for enabling SSL on WSL Apache

#### Fix a WSL warning message on Apache start

If you get an error like the following:

`Failed to enable APR_TCP_DEFER_ACCEPT`

Add this to the end of /etc/apache2/apache2.conf:

`AcceptFilter http none`


### Install & use mysql

* https://support.rackspace.com/how-to/installing-mysql-server-on-ubuntu/
* https://github.com/Microsoft/WSL/issues/2941
* https://github.com/Microsoft/WSL/issues/2087

* Start the mysql service: `sudo service mysql start`
* Run the mysql terminal: `/usr/bin/mysql -u root -p`

## Install Git

`sudo apt install git`

* https://help.github.com/articles/set-up-git/
* https://help.github.com/articles/generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent/
* https://help.github.com/articles/adding-a-new-ssh-key-to-your-github-account/

## Set up Java... but maybe don't do this.

https://www.digitalocean.com/community/tutorials/how-to-install-java-with-apt-get-on-ubuntu-16-04

* Install Java via `sudo apt-get install default-jdk`
* Find installation path: `sudo update-alternatives --config java`
* Set JAVA_HOME: `export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/jre/`


## DNS problems?

* Check `/etc/resolv.conf` and make sure the default is: `nameserver 8.8.8.8`

## Remove `.AAE` files left by importing iOS photos

`find . -name '*.AAE' -exec rm -r {} \;`

## Enable GUI applications

* https://scottspence.com/posts/gui-with-wsl