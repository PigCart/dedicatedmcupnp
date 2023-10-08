# UPNP for dedicated Minecraft servers
UPNP for dedicated Fabric/Forge Minecraft Servers (as opposed to internal/LAN servers, which there are plenty of other equivalent mods for).

Opens ports when the server starts, and closes them when it stops.

The forwarded ports are configurable in the config file at `config/upnp.yaml`. Both UDP and TCP ports can be opened. By default only TCP port 25565 will be opened (the default port for minecraft). If you are using mods such as Simple Voice Chat or Geyser, you must add the relevant ports to the config.

You can use an [online port checker](https://www.ecosia.org/search?q=open%20port%20checker) to check if a port is open.

## Download Links

- [Modrinth](https://modrinth.com/mod/dedicatedmcupnp/)
- [Curseforge](https://curseforge.com/minecraft/mc-mods/dedicatedmcupnp)
