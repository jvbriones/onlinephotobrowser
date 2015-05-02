# onlinephotobrowser
Let’s make the experience to browse online pictures from everywhere quick and easy!
This app allows you to see photos around the world taken by users of the most famous photo sharing communities: Yahoo’s Flickr and Google’s Panoramio.

## License

This project is released under the Creative Commons Attribution-NonCommercial 3.0 Unported (CC BY-NC 3.0) License.
More info at http://creativecommons.org/licenses/by-nc/3.0/

## Instructions

Just generate the .apk and install it in your Android device.

We are going to use the API for accessing photos from Yahoo’s Flickr service http://www.flickr.com/services/api/
as well as the Google’s Panoramio photo sharing service API http://www.panoramio.com/api/

How its works?

- Move around the map – Google maps (You need your own Google Maps Api for extend the code and make your own testing. Enter it in res\layout\main.xml)
- Select an area on the map (the geographical limit of the photos location is the map area shown in the device display)
- Browse photos taken in that geographic location
- When a photo is displayed it's possible get the photo details


The number of returned photos is up to 100.
The Location based queries to the photo providers and their responses are managed using The Representational State Transfer(REST), XML-RPC, DOM and JSON.

— App info —

Api level: 10 – All the tests were done on Android 2.3.3 Gingerbread.

Permissions:
- Network communication (Internet)

## Contributors
Started by [Javier Briones](https://github.com/jvbriones), Saqib Hanif and Julia Amaya

