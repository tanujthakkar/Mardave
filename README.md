<h1>Mardave</h1>

Mardave is a an RC car based on Arduino and Android. It is named after a British company that commercialized RC cars. This is an attempt to make a robust, simple and fun project.

For the car chassis, you can use any of your choice. Follow the steps below to setup your Mardave.

Componenets Required:

Arduino Uno/Mega

HC-05 Bluetooth Module

1K Resistor

2K Resistor

L293D Motor Driver

4 Motors

3 9V Cells

Circuilt Diagram:

![alt text](https://github.com/tanujthakkar/Mardave/blob/master/Circuit.png)

NOTE : To keep the L293D Motor Driver IC cool and get enough output we need to create a heat sink. This can be done by soldering all the ground pins/legs of the IC together and create a thick soldering layer.


Arduino Setup:

Just upload the code given to your Arduino board.

Android Setup:

Entire Android Project is provided, download it and in MainActivity.java, you need to provide the MAC address of your bluetooth module. That's it, create the apk and have fun with Mardave.
