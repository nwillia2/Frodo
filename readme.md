Frodo
=====
This project has been set up as a fun mechanism of learning the google android development language.

A pivotal tracker project exists for this, please email nwillia2 at glam dot ac dot uk if you like to contribute.

Development Environment / Tools
-------------------------------
*   Eclipse - ADT Bundle - http://developer.android.com/sdk/installing/bundle.html
*   Google Maps Android API v2 - https://developers.google.com/maps/documentation/android/
*   Parse - Android - https://www.parse.com/docs/android_guide
   
Set-up
------
1. Pull down this repo into your workspace
2. Import the project into Eclipse, Import -> Java -> Project from existing source
3. Setup the google maps api key. To do this, you need to generate your debug sha key and send it to me:
<code>
  keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
</code>
