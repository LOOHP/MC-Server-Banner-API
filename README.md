# Minecraft Server Status Banner Generator API
https://www.spigotmc.org/resources/91843/

Usage Documentation for the Minecraft Server Status Banner Generator API.

## Usage
The generator functions by receiving HTTP GET requests through an API.

### Basic Usage

```html
https://api.loohpjames.com/serverbanner.png?ip=<server-ip>
```

This will create a server status banner with the default dirt background, cached for 5 minutes.

### Customization
You can add a few parameters to the query string to customize the appearance of the banner.

**Key (data type) [description]**
- **width** *(integer)* [Controls the size of the banner, defaults to 1836]
- **name** *(string)* [The top most line of text in the banner, defaults to `IP: <server-ip>`, if port is not 25565, `IP: <server-ip>:<port>`]
- **port** *(integer)* [The port number of the server, defaults to 25565]
- **offlinemessage** *(string)* [The message to be shown when the server is unreachable, defaults to `Can't connect to server` in red]
- **backgroundurl** *(string: URL or "transparent")* [The background of the banner, defaults to none]
- **ping** *(integer)* [The ping in milliseconds used in the ping icon, defaults to 0 (5 bars)]
- **timezone** *(string: [ZondId](https://docs.oracle.com/middleware/12211/wcs/tag-ref/MISC/TimeZones.html))* [The timezone displayed in the footer]
- **dateformat** *(string: [DateFormat](https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html))* [The date format used in the footer]

Note: It is the responsibility of the user to provide a background image with the correct dimensions

Why is the ping not measured? This is because whether the ping is good or not from my server is not useful to the end user.

## Examples

```html
https://api.loohpjames.com/serverbanner.png?ip=mc.loohpjames.com&width=1836
```
![example1](https://api.loohpjames.com/serverbanner.png?ip=mc.loohpjames.com&width=1836)
```html
https://api.loohpjames.com/serverbanner.png?ip=play.EarthMC.net&width=1836&name=Example%20Server&ping=500&backgroundurl=https://resources.loohpjames.com/spigot/serverbanner/earthmc.png
```
![example2](https://api.loohpjames.com/serverbanner.png?ip=play.EarthMC.net&width=1836&name=Example%20Server&ping=500&backgroundurl=https://resources.loohpjames.com/spigot/serverbanner/earthmc.png)
```html
https://api.loohpjames.com/serverbanner.png?ip=mc.twdtc.net&width=1836&name=Example%20Server&backgroundurl=https://resources.loohpjames.com/spigot/serverbanner/twdtc.png
```
![example3](https://api.loohpjames.com/serverbanner.png?ip=mc.twdtc.net&width=1836&name=Example%20Server&backgroundurl=https://resources.loohpjames.com/spigot/serverbanner/twdtc.png)
```html
https://api.loohpjames.com/serverbanner.png?ip=mc-sbs.com&ping=200&width=1836
```
![example4](https://api.loohpjames.com/serverbanner.png?ip=mc-sbs.com&ping=200&width=1836)
```html
(Example of cannot connect to server)
```
![example5](https://i.imgur.com/TOHtSps.png)

## Partnerships

### Server Hosting
**Use the link or click the banner** below to **get a 25% discount off** your first month when buying any of their gaming servers!<br>
It also **supports my development**, take it as an alternative way to donate while getting your very own Minecraft server as well!

*P.S. Using the link or clicking the banner rather than the code supports me more! (Costs you no extra!)*

**https://www.bisecthosting.com/loohp**

![https://www.bisecthosting.com/loohp](https://www.bisecthosting.com/partners/custom-banners/fc7f7b10-8d1a-4478-a23a-8a357538a180.png)
