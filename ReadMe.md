This project is discontinued from github from 20 jun 2024 because of 2FA.

You will find the next releases on my personnal TOR website (if online)

http://pb6eymfu7ow6jlvwgsjh75ojr5pwvcr47bx3axo4b6d3t6nyprmugzad.onion

____________________________

Vosk Android demo - upgraded 2024

1. Speak to your phone, and send command over HTTPS

2. Make your phone to speak, from others applications over HTTP.

3. Switch to audio bluetooth if aviable, or use your main microphone in last ressort.

4. Continue to run in background

____________________________

On the server part:

The server will recieve a get request like this

https://everythingyouwant/mymessage

And the server can respond to your phone with

http://your_ip_phone/?message=mymessage

You must script this part. But this is why this application is cool :)

You can see how to do this server here 

https://github.com/ddeeproton2/vosk-python/tree/main/others/NodeJS/socketionodejs

You can see how to link this application with a LLM (Large Language Model) to speak with bots here

https://github.com/ddeeproton2/vosk-python/tree/main/others/NodeJS/Jan-api

____________________________

Releases:

The apk Android French language is here:

https://github.com/ddeeproton2/vosk-android-demo-2024-TTS-Voice-over-HTTP/releases/tag/v1.0.0

Chinese, Dutch, English, German, Italian, Japanese, Portuguese, Russian, Spanish, Turkish, here :

https://github.com/ddeeproton2/vosk-android-demo-2024-TTS-Voice-over-HTTP/releases/tag/v1.0.0_

I will, translate it others languages "if I have time for it" and disk space here. 

But from now, you can compile yourself in your langage by downloading your model from Vosk website, and compile this with Android Studio (configuration modification is requested). I used Windows 10 to compile it.

https://alphacephei.com/vosk/models

https://developer.android.com/studio

You will need to download a text voice in your mobilephone also (from Samsung store for eg.) to make your phone speaking.

This project is a merging of others projects. I let everything as it was it this repository.

Thanks to all contributors. :)
