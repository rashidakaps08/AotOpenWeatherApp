AOT Weather App
A themed Android weather app built around Attack on Titan.

Enter a ZIP code and the app hits the OpenWeatherMap Geocoding API to convert it into a latitude/longitude, then uses that to pull a 5-day / 3-hour forecast. Both calls run off a single button click, using AsyncTask for the background networking.

What it shows:

Latitude, longitude, and city name for the entered ZIP
Current conditions plus the next 5 days, each with temperature (°F), a weather description, and a matching icon
A themed quote that changes depending on the weather (rain, snow, sun, clouds, clear skies)
Built with: Java, Android Studio, OpenWeatherMap API

To run it yourself: you'll need your own free OpenWeatherMap API key — add it to local.properties (see comments in MainActivity.java for where it plugs in).

[Watch the video demo here]: https://drive.google.com/file/d/12wYU8a9LppsAQuVNCaeOrLZoDLCcJK-R/view?usp=sharing
