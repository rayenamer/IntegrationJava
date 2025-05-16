# Careera JavaFX Client

🌟 **Careera** is a career opportunities platform with both desktop and web clients. This is the **JavaFX-based desktop application** offering a rich, intuitive experience for job seekers, freelancers, and recruiters alike.


## 📌 Overview

The Careera JavaFX Client allows users to:
- Search and apply for job opportunities  
- Post and manage job offers  
- Engage in discussions and use a built-in chatbot  
- View statistics, quizzes, and PDF documents  
- Manage user profiles and contracts  
- Access mission features and maps integration  

Designed with scalability and aesthetics in mind, this client is a part of the broader Careera ecosystem.


## 🧩 Features

- 🔐 **Authentication** – Login, Register, Reset Password  
- 🧑‍💼 **Profiles** – Manage profiles for job seekers, freelancers, and moderators  
- 📄 **Offers & Contracts** – Create, update, browse job offers and contract types  
- 📊 **Analytics** – Dashboard with offer/user stats, quiz scoring, and more  
- 💬 **Communication** – Real-time discussions, chatbot integration, reply system  
- 🗺 **Location** – Embedded Google Maps for mission geolocation  
- 🎉 **Extras** – Joke view, PDF viewer, skill quizzes  


## 📁 Project Structure

```
.idea/                # IntelliJ project settings
.mvn/                 # Maven wrapper
src/
└── main/
    ├── java/
    │   ├── controllers/  # JavaFX controllers
    │   ├── entities/     # Data models
    │   ├── services/     # Business logic
    │   └── utils/        # Helpers & utilities
    └── resources/
        ├── image/, images/  # Icons and visuals
        ├── style/, styles/   # CSS for UI
        ├── *.fxml            # JavaFX view files
        └── credentials.json   # Google API credentials (e.g. Maps)
test/                  # Unit tests
target/                # Build output
.gitignore
README.md
pom.xml               # Maven config
mvnw / mvnw.cmd       # Maven wrapper
module-info.java      # Java module definition
```


## 🚀 Getting Started

### ✅ Prerequisites
- Java 17 or higher  
- Maven 3.8+  
- JavaFX SDK (included via Maven dependencies)


### 🔧 Build & Run
```bash
git clone https://github.com/your-org/careera-desktop.git
cd careera-desktop
mvn clean install
mvn javafx:run
```


## 🖼 Key UI Screens (FXML)
- PageConnexion.fxml – Login Page
- PageCreationCompte.fxml – Register Page
- Acceuil.fxml, admin_dashboard.fxml – Main Dashboards
- AjouterOffre.fxml, AfficherOffre.fxml – Offer Management
- ProfileChercheur.fxml, ModifierProfile.fxml – Profile Pages
- Chatbot.fxml, Discussions.fxml, ReplyView.fxml – Messaging & Chat
- MapView.fxml – Job location viewer
- PDFViewer.fxml, Quiz.fxml, Statistics.fxml – Advanced features


## 🤝 Contributing

We welcome contributions from developers of all experience levels.

To contribute:
- Fork this repository
- Create your feature branch: `git checkout -b feature/YourFeature`
- Commit your changes: `git commit -m 'Add YourFeature'`
- Push to the branch: `git push origin feature/YourFeature`
- Open a pull request


## 📄 License

This project is licensed under the MIT License.  
Feel free to use, modify, and distribute.


## 🌐 About Careera

Careera connects talents with opportunities across industries.  
This JavaFX client is a core part of the platform — delivering power, clarity, and possibility through desktop design.

Built with 💙 by the Careera Team
