# Processing IDEs

This is a guide to explain a more advanced IDE setup, using traditional Java development tools, rather than the Processing IDE. This is especially useful for larger projects where organization and IDE features become more important.

## VS Code

- Install VS Code, and check out the [VS Code Java docs](https://code.visualstudio.com/docs/java/java-tutorial)
- Install the VS Code [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vs code-java-pack)
  - Upon completion, it will prompt you to install a Java JDK
    - Install the LTS 17 Hotspot version
    - Run the installer with default options
    - Open a Command Prompt or Powershell, and run `java -version`. This should show you the Java version you just installed
- Note the new drawer under the VS Code file Explorer, called Java Projects. This has extra controls for your Java project, but you can generally just continue using the file Explorer
  - You can create a `.vscode/launch.json` file, which can be used to add arguments to your Java launch command. Do this by clicking "Run -> Add Configuration" in VS Code
    - Inside of a launch.json entry, you might want to add app or vm args. This could look like the following:
    - ```
      "args": "testArg arg=1 agr2=2",
      "vmArgs": "-Xmx4G -Xms2G -Djava.library.path=lib/KinectPV2/library;lib/processing-4/libraries/serial/library/windows64"
      ```
- Open your main app file, in this case: `src/com/cacheflowe/RealsenseSocketBridge.java`
  - Right-click on the file and choose `Run Java`
  - This should launch the startup command in a terminal window in VS Code and launch an app window
  - If a network access prompt appears, allow access for both public & private networks
- If you want hot-reloading, choose `Debug Java` instead of `Run Java`
  - When your app launches, you'll see a new lightning bolt icon on the run toolbar that shows up in the upper-right corner of VS Code
  - When you make code changes, save the files, and click the lightning bolt to reload the code
  - If you add new class properties or functions, you'll need to recompile, which will be offered in another prompt that appears when you click the lightning bolt
- Optionally, add the following to your VS Code `settings.json` (View -> Command Palette -> "Preferences: Open User Settings JSON"):
  - `"java.debug.settings.vmArgs": "-Xmx4G -Xms2G"`
      - This will increase the memory available to the Java app
    - You can also add paths to native library locations here (and any other args that you want added to *every* Java launch), but this is not needed for this project:
      - `"java.debug.settings.vmArgs": "-Xmx4G -Xms2G  -Djava.library.path=/path1:/path2:/path3"`
- This setup relies on the Eclipse-generated `.classpath` and `.project` files, which points to all of the .jar file dependencies and general Java configuration
  - VS Code recognizes these files and uses them for Intellisense and compiling
  - If you don't want to use an existing Eclipse project, fo to the Java Projects panel in VS Code and click the `+` symbol to create a new Java project, which will set the project up with a couple of files inside a new `.vscode` directory, which will point to the JDK and .jar dependencies
