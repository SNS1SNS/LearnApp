# LearnApp

Учебное Android-приложение на Kotlin для изучения иностранных слов.  
Содержит функционал регистрации, авторизации, добавления и просмотра слов, а также интерфейс для отслеживания прогресса изучения языков.

## Основные экраны (Activity)

- **LoginActivity** — Экран входа в приложение. Пользователь вводит свои данные (email/пароль).
- **RegisterActivity** — Экран регистрации. Позволяет новому пользователю создать аккаунт.
- **MainActivity** — Основной экран после авторизации. Может содержать список доступных функций или переходы к другим экранам приложения.
- **AddActivity** — Экран для добавления нового слова (или другой лексики) в базу.
- **addWordsToFirestore** — Класс/Activity/функционал для добавления слов в Firebase Firestore.
- **LanguageChangeActivity** — Экран/функция для смены языка или локализации приложения.
- **LanguageDetailActivity** — Экран, где можно посмотреть детальную информацию о языке или конкретном словаре.
- **LanguageProgressActivity** — Экран для отображения прогресса изучения языка (пройденные слова, статистика и т.д.).
- **LearnWordActivity** — Экран для непосредственного процесса обучения слов (карточки, тесты и т.п.).
- **WordListActivity** — Экран, отображающий список слов (например, уже добавленных в Firestore).

## Структура проекта
   ```bash
   LearnApp/
   ┣ app/
   ┃ ┗ src/
   ┃   ┗ main/
   ┃     ┣ java/com/example/learnapp/ui/theme/
   ┃     ┃  ┣ AddActivity.kt
   ┃     ┃  ┣ addWordsToFirestore.kt
   ┃     ┃  ┣ LanguageChangeActivity.kt
   ┃     ┃  ┣ LanguageDetailActivity.kt
   ┃     ┃  ┣ LanguageProgressActivity.kt
   ┃     ┃  ┣ LearnWordActivity.kt
   ┃     ┃  ┣ LoginActivity.kt
   ┃     ┃  ┣ MainActivity.kt
   ┃     ┃  ┣ RegisterActivity.kt
   ┃     ┃  ┗ WordListActivity.kt
   ┃     ┗ res/ ...
   ┣ gradle/ ...
   ┣ build.gradle.kts
   ┣ settings.gradle.kts
   ┗ ...
   ```

## Технологии

- **Kotlin**: основной язык для написания кода.
- **Android SDK**: платформа для разработки под Android.
- **Firebase Firestore**: облачное хранилище для данных (словарь, информация о пользователях и т.д.).
- **Gradle (KTS)**: система сборки с использованием Kotlin DSL.

## Как запустить

1. **Склонируйте репозиторий**:
   ```bash
   git clone https://github.com/SNS1SNS/LearnApp.git
    ```

2. Откройте проект в Android Studio или IntelliJ IDEA c плагином Android.
3. Убедитесь, что установлены необходимые SDK, плагины, и что Gradle успешно собрал проект.
4. Настройте файл google-services.json (если используете Firebase). Обычно его кладут в папку app/.
5. Запустите приложение на эмуляторе или физическом устройстве через Android Studio.