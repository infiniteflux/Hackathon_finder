# 🚀 Hackathon Finder with AI Coach

Hackathon Finder is a **native Android application** designed to help developers **discover hackathons** and **prepare effectively** with the help of an integrated **AI Hackathon Coach**, powered by **Google Gemini**.

This app focuses on delivering real-time, hackathon-specific guidance — from idea brainstorming to pitching — making it a must-have tool for both beginners and seasoned hackers.

---

## ✨ Key Features

### 🤖 AI Chatbot Coach

A conversational assistant that answers questions **strictly related to hackathons** — such as:

* Tips & strategies
* Team building guidance
* Idea validation
* Pitching advice

### 🛡️ Smart Guardrails

The AI includes **custom system instructions** to ensure it stays on topic and *does not* answer irrelevant queries (e.g., weather, sports, unrelated topics).

### 📱 Modern UI

Built entirely with **Jetpack Compose** and **Material 3**, delivering a smooth and aesthetically pleasing UI.

### ⚡ Real-time Responses

Powered by **Kotlin Coroutines** for fast, non-blocking AI interactions.

---

## 🛠️ Tech Stack

* **Language:** Kotlin
* **UI Toolkit:** Jetpack Compose (Material 3)
* **Architecture:** MVVM (Model-View-ViewModel)
* **AI Model:** Google Gemini API (`gemini-1.5-flash-latest`)
* **Async:** Coroutines & StateFlow
* **Build System:** Gradle (Kotlin DSL)

---

## ⚙️ Setup & Installation

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/hackathon-finder.git
cd hackathon-finder
```

### 2. Get Your Gemini API Key

To enable the chatbot, generate a free API key:

1. Go to **Google AI Studio**.
2. Create a new API key.

### 3. Configure the API Key Securely

The project uses `local.properties` to keep your key safe.

Add the following line to **local.properties**:

```properties
GEMINI_API_KEY=your_actual_api_key_here
```

Sync Gradle afterwards.

### 4. Run the App

* Connect your Android device or start an emulator.
* Ensure Google Play Services is enabled (required for Gemini).
* Build & Run from Android Studio.

---

## 🧠 How the AI Logic Works

The chatbot uses the **gemini-1.5-flash-latest** model and relies on a strict System Instruction:

> "You are a helpful assistant for hackathon participants. Your ONLY job is to provide tips, strategies, and advice related to hackathons. If the user asks about ANYTHING else, you MUST respond with: 'I'm sorry, I am only able to respond to questions about hackathons.'"

This ensures the bot remains focused, productive, and hackathon-oriented.

---

## 📸 Screenshots
(screenshot/home.png)

### search Interface
(screenshot/search.png)


### fav Interface
(screenshot/fav.png)

### fav Interface
(screenshot/chatbot.png)

---

## 🤝 Contributing

Contributions are welcome! Feel free to propose improvements or new features (e.g., chat history, voice input).

### Steps to Contribute

1. Fork the Project
2. Create your Feature Branch

   ```bash
   git checkout -b feature/AmazingFeature
   ```
3. Commit your Changes

   ```bash
   git commit -m "Add some AmazingFeature"
   ```
4. Push to the Branch

   ```bash
   git push origin feature/AmazingFeature
   ```
5. Open a Pull Request

---

⭐ If you find this project useful, give it a star on GitHub!
