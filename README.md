# Makkit

An in-world editor for Minecraft.

Makkit (pronounced Make-it) is a lightweight creative mode world
editing tool, made for Fabric on Minecraft v1.16+. It's meant to make
a lot of simple operations quicker, while being easy to use!


### Features

* In world editing, with realtime selection boxes
    * You can view each other's selections in Multiplayer, too!
* The ability to **move** and **resize** selections
* Simple transformations, like *filling* entire selections with 
blocks (or just the walls!)
* The ability to **copy** and **paste** selections, even along a different
axis to **rotate** them
* The ability to **mirror** your selections over an axis, allowing you
to easily create roofing and other symmetric structures
* A pattern tool, which lets you **tile** blocks or **repeat** them 
in a certain direction
* Edit history, letting you **undo** and **redo** at will!

### Beta

Makkit is still in Beta, so there might be some niche bugs. I try to test
everything extensively before each release so releases should be quite
stable, but there may be some things I miss. Even if something does go 
wrong, you can always press undo!


### What about Feature "X"?

While Makkit intends to make creative mode editing easier, it can't do
everything. It tries to do most common world editing features in a very
intuitive way, but it isn't a replacement for other creative mods like 
[WorldEdit](https://www.curseforge.com/minecraft/mc-mods/worldedit).
Rather, you should use them both together!

In the future (once it is released), I plan on adding some compatibility 
with WorldEdit (e.g. letting your selection box act as a WorldEdit 
selection box). Stay tuned!


### Fabric?

Yes, this mod is for Fabric & Fabric API. I don't intend or have
time to maintain a version for both Forge and Fabric. This mod
also does some things that would be hard to do with Forge. 
However, if someone
would like to do a pull request with their own implementation
(written in Kotlin) in a separate branch, I'd be willing
to release a Forge version!


