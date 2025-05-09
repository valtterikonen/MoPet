# MobilePet - Android based virtual pet 

## Introduction
Mobilepet is an Android application built with Jetpack Compose. Create a custom pet, interact and experience!
___
## Installation
1. Clone the repository
2. Open the cloned project in Android Studios
3. Check the AndroidManifest.xml and make sure the following details are correct:
    - <uses-permission android:name="android.permission.INTERNET" />
    - <uses-permission android:name="android.permission.ACTIVITY_RECOGNITION" />
    - <uses-permission android:name="android.permission.CAMERA" />
    - <uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
    - allowBackup="false"
4. Run the application
___
## Features
- Create your custom pet (Name & type)
- Track pet statistics **mood**, **energy** and **hunger** visually through statusbar
- Pet feeding
- Pet rest (when user has been inactive for +2min)
- Picture Mode (CameraX)
- Exercise with 3D globe and random path generation (WorldWind SDK)
- Pet Reset (execution-button)
- Customizable theme toggle between Light and Dark.
- Data storage (SharedPreferences (DataStore) and Room DAO (SQLite Database))
- Jetpack Navigation (BottomNavigationBar)
___
## Technologies
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose (Material 3)
- **3D Visualization:** WorldWind SDK
- **Data Storage:** DataStore, Room DAO
- **MVVM**
    - Model: Pet (dataclass) + Data
    - View: Home, Exercise, Picture, Settings
    - ViewModel: PetModel
- **Animations:** Compose Animations API

___ 
## Authors
Valtteri Ikonen
Lenni Liukkonen
