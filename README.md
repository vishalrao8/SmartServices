# SmartServices
An app to connect freelancing technicians with their customers to develop a source of income for them.

#### What does this app do?

This app is to enhance the experience of users dealing with home services by providing variety of services. This app can provide employment to freelance technicians and ease of convenience to pick any service from a single app for users.

#### Does it solve any problem?

Of course it does, by reducing the resistance that one feels while finding any technician located farther or closer to them within a specific time period and providing real time location of worker coming to them to make it easy for users to track them.

#### Intended User

This is very useful for students, family living in an area lacking the medium to call any technician for getting things fixed at home or offices and for technicians want to earn some money in exchange of service.

## Index

- [Installation](https://github.com/vishalrao8/SmartTechnician/blob/master/README.md#installation)
- [Usage](https://github.com/vishalrao8/SmartTechnician/blob/master/README.md#usage)
  - [User Flow](https://github.com/vishalrao8/SmartTechnician/blob/master/README.md#user-flow)
- [Contribution](https://github.com/vishalrao8/SmartTechnician/blob/master/README.md#contribution)
- [Technical Description](https://github.com/vishalrao8/SmartTechnician/blob/master/README.md#technical-description)
  - [Features](https://github.com/vishalrao8/SmartTechnician/blob/master/README.md#features)
  - [UI](https://github.com/vishalrao8/SmartTechnician/blob/master/README.md#ui)
  - [Key Considerations](https://github.com/vishalrao8/SmartTechnician/blob/master/README.md#key-considerations)
- [Licence](https://github.com/vishalrao8/SmartTechnician/blob/master/README.md#licence)

# Installation

- Grab the APK from [here](https://github.com/vishalrao8/SmartTechnician/blob/master/app/release/app-release.apk).
- Enable installation from unknown sources in settings if asked during installation.

# Usage

- After installation, you can easily run the application by clicking on App icon in launcher menu.
- On first screen, navigate further depending upon your requirement. First option is for normal users and second option is for trained tehnicians seeking to find people in need.
- Authenticate yourself with an OTP sent to your mobile number. Speed up the process by allowing access to messages.
- For users, Choose any type of service as per your reqirement and pick the appropriate location from map to where the technician is expected to arrive.
- For technicians, You will be navigated straight to a map where you will get real-time updates about requests raised nearby you (within a radius of 5 Kms). If there is any request shown on map, you can easily navigate to that user using Google Maps navigation after selecting user icon on map.

### User Flow

<img src="https://drive.google.com/uc?export=view&id=1j1rAro_li7YSoGj5sWaiA12AEgafr8bU" width="800">

<img src="https://drive.google.com/uc?export=view&id=1kRIgIvAYnG3WVW3464MDtqhTpzw7zLG6" width="800">

# Contribution

- Fork this repo to make your own copy of repository, see it's there at the top right.
- Either download the zip from your forked repo or check out project from VCS using Android Studio startup wizard. Make sure you are importing project from you own copy of repository and not this.
- Contact me at vishal.rao103@gmail.com to get a copy of google-services.json file which is supposed to be placed at root/app directory before building the project or else build process will fail.
- Build the project using gradle and create a new branch with appropriate name with respect to the feature you are going to add.
- Start making modifications with complete code comments and method documentations to make it easier for us humans to read.
- Make sure your master branch is up to that with origin's master branch i.e. [this](https://github.com/vishalrao8/SmartServices) branch before merging any changes.
- Commit and merge the changes with your master branch and do resolve merge conflicts if any.
- Make a pull request to this master branch using IDE or browser (later one recommended).
- Happy contributing :)

# Technical Description

### Features

- App is written solely in the Java Programming Language.
- App keeps all strings in a strings.xml file and enables RTL layout switching on all layouts.
- App includes support for accessibility. That includes content descriptions, navigation using a D-pad, and, if applicable, non-audio -versions of audio cues.
- Stores mobile number and data given by user.
- Stores real time location for specific session.
- Allow one click login with OTP authentication.
- Shows real time location of technician to user.
- Have two every points, one for technician and one for user at login.

### UI

<img src="https://drive.google.com/uc?export=view&id=1ig-tCs23PRryKs1siSsCKEi584ffoRHf" width="150">&nbsp; &nbsp; &nbsp;<img src="https://drive.google.com/uc?export=view&id=1ooO8mRzPf2wpSWg9BZYfgdmsus_s8w4j" width="150">&nbsp; &nbsp; &nbsp;<img src="https://drive.google.com/uc?export=view&id=1kQ0CKsSV12-ypDKROK5ffy5mbcgkfLGw" width="150">&nbsp; &nbsp; &nbsp;<img src="https://drive.google.com/uc?export=view&id=1kAeidTXO0vgN-W_crMhfmBCaokp032_H" width="150">&nbsp; &nbsp; &nbsp;<img src="https://drive.google.com/uc?export=view&id=1zaYzmu8bYAbZ4_0LTGt93Y3EPRhNUzKj" width="150">&nbsp; &nbsp; &nbsp;<img src="https://drive.google.com/uc?export=view&id=13tDOos8tVGkSGadGSPz3j1Mdo7qEZK8x" width="150">

### Key Considerations

#### How will this app handle data persistence? 

This app uses firebase real time database to store data and location coordinates taken from user/technician device. App also uses Shared Preferences to cache some data.

#### Edge or corner cases in the UX:

All user oriented functionality is within one Activity divided into 3 fragments hosted inside the ViewPager and TabLayout (similar to WhatsApp UX). All the services’ tiles to be picked are on 1st Tab, active requests on 2nd Tab and history of past requests on the last Tab.

#### Libraries used in this app:

- **Firebase library** to store user’s and technician’s data and location coordinates in firebase realtime database.
    
- **GeoFire library** to make realtime location queries with firebase.

- **Google maps library** to show technician's real time location to user on map.

- **Google places library** to provide ease of picking the location at which technician is expected to reach after raising the request.

- **Firebase authentication library** to provide authentication system and Firebase Authentication UI, authenticating via a mobile number by auto generating one time password (OTP).

Stable version of libraries, android studio and gradle are used.

# Licence

ISC License

Copyright (c) [year], [fullname]

Permission to use, copy, modify, and/or distribute this software for any
purpose with or without fee is hereby granted, provided that the above
copyright notice and this permission notice appear in all copies.

THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.

