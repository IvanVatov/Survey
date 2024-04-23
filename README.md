## Survey

This project emerged from a student internship program, featuring two core components: an Android application and a Java backend application, both coded in Kotlin language.

Currently, testing is limited to emulator devices. The app communicates with the local machine via HTTPS at [https://10.0.2.2](https://10.0.2.2), relying on a self-signed certificate named `survey.jks` due to modern Android security standards.

To work with the project, open it in Android Studio or IntelliJ IDEA. First, run the app on the emulator, then launch the backend. Running the backend will create a `survey.db` file in the backend folder which is SQLite database, also log file in `log` folder.

Additionally, the backend serves as a host for a website accessible at [http://127.0.0.1](http://127.0.0.1) through a web browser.

**Access Levels:**

-   **Admin:** Has full access rights.
-   **User:** Has standard access rights.
-   **Suspended:** Access rights are temporarily revoked.

To set a user as an admin, execute the `Set Admin Level.bat` file located in the `scripts` folder.

Technologies used by application:
-   **Android SDK:** Toolkit for developing Android apps.
-   **Jetpack Compose:** Modern UI toolkit for Android app development.
-   **Ktor Client:** Kotlin library for making HTTP requests.
-   **Kotlin Serialization JSON:** Library for converting Kotlin objects to/from JSON format.

Technologies used by backend:
-   **Ktor Server:** Lightweight framework for building asynchronous servers in Kotlin.
-   **Kotlin Serialization JSON:** Library for serializing Kotlin objects to/from JSON format.
-   **Apache Velocity:** Template engine for generating dynamic content in Java applications.
-   **SQLite:** Embedded relational database management system often used in mobile and small-scale applications.
-   **HikariCP:** High-performance JDBC connection pooling library for Java applications.
-   **SLF4J:** Simple Logging Facade for Java, providing a common interface for various logging frameworks.

Credits to https://github.com/zuramai/mazer for the template