Vosk Android demo - upgraded 2024

Speak to your phone, and send command over HTTPS.

Make you phone to speak, from others applications over HTTP.

Added features:

-Switch to audio bluetooth if aviable, or use your main microphone in last ressort.

-Continue to run in background

-Send the text of your voice over GET HTTPS (not HTTP, because Android limitations) or a TCP Server with SSL

-Recieve voice to speak on your phone into a Web server in HTTP (not HTTPS, because this is the first version of this application, and HTTP is the easiest way to make it)

Then, this voice assistant can be merged with any other server script. Like Apache-PHP, NodeJS, ...

Any other language allowing to create a web server in HTTPS and send back the GET request in HTTP to this Android apk.

This project is compiled for French language. I will, translate it others languages "if I have time for it" and disk space here. 
But from now, you can compile yourself in your langage by downloading your model from Vosk website, and compile this with Android Studio (configuration modification is requested before).

This project is a merging of others projects. I let everything as it was. So many things is aviable but I did not used them.

Thanks to all contributors. :)
