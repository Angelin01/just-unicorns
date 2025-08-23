# Just Unicorns

A Minecraft mods that adds just unicorns (ok, maybe some minor other stuff too)

## Development notes

In Arch + Wayland:

Install glfw
```shell
pacman -Syu glfw flite
```

Add to run target:
```
-Dorg.lwjgl.glfw.libname=/usr/lib/libglfw.so
```
