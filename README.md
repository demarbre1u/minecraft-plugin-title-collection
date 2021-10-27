# TitleCollection plugin

## What is this repository for? ###

This repository contains a plugin for Minecraft. It allows players to earn and manage a collection of titles.
They can set an active title, which will be displayed alongside their display name in the in-game chat.

This plugin's been tested on a Spigot server in its 1.11.2 version.

## How do I get set up? ###

You'll first need to stop your server.
As you would do for any plugin, you need to place the .jar file into the /plugins directory of your server.
You can then restart your server.
The required files will be generated at the server start up, and once a player will log to the server.

## Command list ###

- `/tc help` : displays the command list
- `/tc list` : displays the list of your available titles
- `/tc list [player]` : displays the list of titles owned by the targeted player
- `/tc listadmin` : displays the admin list
- `/tc add [player] [title]` : gives a title to a player
- `/tc addadmin [title]` : adds a new title to the admin list 
- `/tc remove [player] [title]` : removes a title from the list of the specified player
- `/tc removeadmin [title]` : removes a title from the admin list
- `/tc use [title]` : sets your current title to the specified title
