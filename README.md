# UPNP for dedicated Minecraft servers

UPNP for dedicated Fabric/NeoForge Minecraft Servers (as opposed to LAN servers, which there are plenty of other equivalent mods for).
Opens ports when the server starts, and closes them when it stops.

The forwarded ports are configurable in the config file at `config/upnp.txt`.
Both UDP and TCP ports can be opened. By default, only TCP port 25565 will be opened (the default port for minecraft). If you are using mods such as Simple Voice Chat or Geyser, you must add the relevant ports to the config.

You can use an [online port checker](https://www.ecosia.org/search?q=open%20port%20checker) to check if a port is open.

This primarily just wraps the [WaifUPnP](https://github.com/adolfintel/WaifUPnP) library as a minecraft mod,
binding the functions of the library to the server lifecyle and a command. The library has been modified slightly to log errors and implement some of the pending PRs on the original project as well as bugfixes from its forks.

## Downloads

- [Modrinth](https://modrinth.com/mod/dedicatedmcupnp/)
- [Curseforge](https://curseforge.com/minecraft/mc-mods/dedicatedmcupnp)