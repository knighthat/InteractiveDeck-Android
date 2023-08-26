Turn your Android phone into a "**_Macro Keypad_**"

---

# Features

# General

🎯 Easy to use, straight forward.<br>
🔲 Customizable, button's foreground/background/text can be changed.<br>
🚀 Fast to connect, responsive to touch/update between devices.<br>
⚖️ Lightweight, built using minimal resources to maximize performance.

# Core Functions

📑 Multiple Pages (Profiles)

- Add
- Remove
- Modify
    - Title
    - Columns (up to 6)
    - Rows (up to 4)
    - Gap Between Buttons

🖲️ Buttons

- Label
- Background
- Foreground (Font Color)
- Task (Its Usage)
    - Executing BASH Script
    - Switch Profile on Mobile Device

# Installation

> These steps will only guide you to compile this project to .apk file

## Requirements

- JDK 8 or higher (20 is recommended). [DOWNLOAD HERE](https://jdk.java.net/20/).
- Android SDK 25 or higher.
- **_local.properties_** is pointing to a valid Android SDK path.

### Windows

Run command `gradlew.bat assembleRelease`, then go to `app/build/outputs/apk/release/` and copy `app-release-unsigned.apk` to desired Android phone to start the installation.

### Linux & MacOS

> You might want to enable execute as program or use command `chmod +x gradlew` before moving forward.

Run command `gradlew assembleRelease`, then go to `app/build/outputs/apk/release/` and copy `app-release-unsigned.apk` to desired Android phone to start the installation.

# Usage

> Follow [Installation](#installation) to compile code

Start app > put host's IP address, port > hit "Connect" > enjoy!

# Contribute

This project is open to public and anyone can contribute and be a part of it at any given time.
Please follow guidelines to prevent conflict between push

### Guidelines

* Please follow the existing code style and conventions in the projects.
* Be sure to write clear and concise code and documentation.
* If you're adding new features or make significant changes, consider adding relevant tests.
* If you're fixing a bug, please provide steps to reproduce the issue if applicable.

### Code of Conduct

1. **Do**

* **_Be respectful_**: Treat all individuals with respect, regardless of their background, identity, or opinions.
* **_Be inclusive_**: Welcome and include diverse perspectives, experiences, and ideas.
* **_Be understanding_**: Seek to understand differing viewpoints and engage in constructive discussions.
* **_Be mindful_**: Be mindful of the impact our words and actions have on others in the community.

---

2. **Don't**

* **_Harassment_**: Any form of harassment, discrimination, or offensive behavior is unacceptable and will not be tolerated.
* **_Trolling_**: Deliberate and disruptive actions or comments with the intent to provoke or offend others.
* **_Hateful speech_**: Use of derogatory or offensive language, slurs, or personal attacks.
* **_Spam_**: Posting irrelevant or unsolicited content, including excessive self-promotion.

# License (MIT)

> TL;DR You are free to obtain, make changes, or even distribute commercially without restrictions.

For details, please read [LICENSE](LICENSE.md)
