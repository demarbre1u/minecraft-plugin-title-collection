# TitleCollection plugin

### What is this repository for? ###

This repository contains a plugin for Minecraft. It allows players to earn and manage a collection of titles.
They can set an active title, which will be displayed alongside their display name in the in-game chat.

This plugin's been tested on a Spigot server in its 1.11.2 version.

### How do I get set up? ###

You'll first need to stop your server.
As you would do for any plugin, you need to place the .jar file into the /plugins directory of your server.
You can then restart your server.
The required files will be generated at the server start up, and once a player will log to the server.

### Command list ###

__/tc help__ : displays the command list

__/tc list__ : displays the list of your available titles

__/tc list [player]__ : displays the list of titles owned by the targeted player

__/tc listadmin__ : displays the admin list

__/tc add [player] [title]__ : gives a title to a player

__/tc addadmin [title]__ : adds a new title to the admin list 

__/tc remove [player] [title]__ : removes a title from the list of the specified player

__/tc removeadmin [title]__ : removes a title from the admin list

__/tc use [title]__ : sets your current title to the specified title
