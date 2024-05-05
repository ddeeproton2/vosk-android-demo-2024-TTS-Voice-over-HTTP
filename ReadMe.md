Vosk Android demo - upgraded 2024

1. Speak to your phone, and send command over HTTPS.

2. Make you phone to speak, from others applications over HTTP.

Added features:

-Switch to audio bluetooth if aviable, or use your main microphone in last ressort.

-Continue to run in background

-Send the text of your voice over GET HTTPS (not HTTP, because Android limitations) or a TCP Server with SSL

-Recieve voice to speak on your phone with a local HTTP server started with this application

____________________________

This voice assistant is designed to be combined with a server :) 

The server will recieve a get request like this

https://everythingyouwant/mymessage

And the server can respond to your phone with

http://your_ip_phone/?message=mymessage

This project is compiled for French language. I will, translate it others languages "if I have time for it" and disk space here. 
But from now, you can compile yourself in your langage by downloading your model from Vosk website, and compile this with Android Studio (configuration modification is requested).

This project is a merging of others projects. I let everything as it was it this repository.

Thanks to all contributors. :)
