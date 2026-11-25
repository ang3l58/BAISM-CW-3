# BAISM-CW-3

Aplikacja Android (Kotlin, Jetpack Compose) na potrzeby ćwiczenia 3 z przedmiotu
*Bezpieczeństwo aplikacji i systemów mobilnych*. Klient korzysta z publicznego
[Rick and Morty API](https://rickandmortyapi.com/).

## Funkcje

- Lista wszystkich postaci (`GET /api/character`) w `LazyColumn`.
- Szczegóły postaci po ID (`GET /api/character/{id}`).
- Warstwa sieciowa: Retrofit 2.11 + OkHttp 4.12, obrazki przez Coil.

## Etapy bezpieczeństwa (gałąź historii commitów / tagi)

- `etap-1` – aplikacja bez konfiguracji sieciowej (ufa tylko systemowym CA).
- `etap-4` – `res/xml/network_security_config.xml` z `debug-overrides`
  dopuszczającym certyfikaty użytkownika (User CA) tylko w wariancie debug.
- `etap-6` – certificate pinning w `OkHttp CertificatePinner` oparty na hashu
  SPKI (SHA-256) klucza serwera i klucza pośredniego Google Trust Services.

## Build

```
gradlew.bat assembleDebug
```

minSdk 30, targetSdk 35, compileSdk 35.
