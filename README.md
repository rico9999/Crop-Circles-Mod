# Crop circles

Minecraft Mod for allowing Java programming of block fills.

Based on MBE-45 Commands Mod from MinecraftByExample from TheGreyGhost.


## Tips on Running it in Eclipse

* On command line, cd to a new directory:
  * git clone https://github.com/rico9999/crop-circles-mod/

* Still on command line, CD to dicemod folder, type and run 
  * ./gradlew genEclipseRuns --refresh-dependencies 
  * ./gradlew eclipse

* Open eclipse, import project as gradle project specifying the crop-circles-mod directory.
  * Right click on runClient.launch to run as Java application
  * In creative mode, use the new command:  /cropcircle
