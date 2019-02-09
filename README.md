# SmartTechnician
An app to connect freelance technicians with their customers to develop a source of income for them

##### What does this app do?

This app is to enhance the experience of users dealing with home services by providing variety of services. This app can provide employment to freelance technicians and ease of convenience to pick any service from a single app for users.

##### Does it solve any problem?

Of course it does, by reducing the resistance that one feels while finding any technician located farer or closer to them within a specific time period and providing real time location of worker coming to them to make it easy for users to track them.

## Table of Contents

[Intended User](https://github.com/vishalrao8/SmartTechnician/blob/master/README.md#intended-user)

[Features](https://github.com/vishalrao8/SmartTechnician/blob/master/README.md#features)

[UI]()

[Key Considerations](https://github.com/vishalrao8/SmartTechnician/blob/master/README.md#key-considerations)

## Intended User

This is very useful for students, family living in an area lacking the medium to call any technician for getting things fixed at home or offices. 

## Features

- App is written solely in the Java Programming Language
- App keeps all strings in a strings.xml file and enables RTL layout switching on all layouts.
- App includes support for accessibility. That includes content descriptions, navigation using a D-pad, and, if applicable, non-audio -versions of audio cues.
- Stores mobile number and data given by user
- Stores real time location for specific session
- Allow one click login with OTP authentication
- Shows real time location of technician to user
- Have two every points, one for technician and other for user while login

### Key Considerations

##### How will this app handle data persistence? 

This app uses firebase real time database to store data and location coordinates taken from user/technician device. App also uses Shared Preferences to cache some data.

##### Edge or corner cases in the UX:

All user oriented functionality is within one Activity divided into 3 fragments hosted inside the ViewPager and TabLayout (similar to WhatsApp UX). All the services’ tiles to be picked are on 1st Tab, active requests on 2nd Tab and history of past requests on the last Tab.

##### Libraries used in this app:

- **Firebase library** to store user’s and technician’s data and location coordinates in firebase realtime database.
    
- **GeoFire library** to make realtime location queries with firebase.

- **Google maps library** to show technician's real time location to user on map.

- **Google places library** to provide ease of picking the location at which technician is expected to reach after raising the request.

- **Firebase authentication library** to provide authentication system and Firebase Authentication UI, authenticating via a mobile number by auto generating one time password (OTP).

Stable version of libraries, android studio and gradle are used.
