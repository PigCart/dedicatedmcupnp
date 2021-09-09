# U Pee N Piss
UPNP for dedicated Fabric Minecraft Servers. Opens ports when the server starts, and closes them when it stops.

The forwarded ports are configurable in the config file at `config/upnp.yaml`. Both UDP and TCP ports can be opened. By default only TCP port 25565 will be opened. If you are using mods such as Simple Voice Chat or Geyser, you must add the relevant ports to the config.

You can use an online port checker to check if a port is open, such as https://portchecker.co/

Ports might be left open if the server closes unexpectedly. If this happens, restarting and stopping the server will close them.
