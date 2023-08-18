# Приложение для NFC карт "Вода в кишені"

Приложение работает с картами "Вода в кишені" ("Вода в кармане") стандарта Mifare Classic 1K. 

## Функции
- Считывание и отображение баланса карты
- Отображение остатка литров на основе цены за литр
- Настройка цены за литр
- Открытие приложения из любого места в системе при поднесении карточки
- Темная тема

<p float="left">
  <img src="https://user-images.githubusercontent.com/5675681/142214180-d7d69e32-6094-481c-8ac0-e8965f2b39d7.png" width="250" />
  <img src="https://user-images.githubusercontent.com/5675681/142214240-b6cc9c81-c228-436c-b92b-7cbeee1aeb75.png" width="250" />
</p>

## Cборка
Для сборки приложения необходимо задать ключ авторизации карт в формате HEX в файл `app/keystore.properties`, например:
```properties
CardKeyHex=00226949A50A
```
Для запуска приложения с тестовым считывателем можно использовать конфигурацию `debugDummyReader`.

## Tech stack
- Kotlin
- RxJava
- Koin dependency injection
