# BAISM-CW-3

Laboratorium: bezpieczeństwo ruchu sieciowego aplikacji Android (Kotlin) — Rick and Morty API, mitmproxy, Network Security Config, certificate pinning (SPKI), bonus system CA.

## Aplikacja

- **Stack:** Kotlin, Jetpack Compose, Retrofit, OkHttp, Moshi
- **API:** `https://rickandmortyapi.com/api/`
  - lista postaci: `GET /character`
  - postać po ID: `GET /character/{id}`
- **UI:** dwa przyciski, pole ID, `LazyColumn`, karta szczegółów

### Flagi labowe (`app/build.gradle.kts`)

| Flaga | Znaczenie |
|--------|-----------|
| `ENABLE_CERT_PINNING` | OkHttp `CertificatePinner` (SPKI pin liścia API) |
| `TRUST_USER_CA` | flaga informacyjna w UI (rzeczywiste zaufanie = NSC) |

Warianty `network_security_config`:

- `network_security_config.xml` — system + user (etapy 4–5)
- `network_security_config_no_user_ca.xml` — tylko system (etapy 1–3)
- `network_security_config_system_only.xml` — bonus (system CA)

SPKI pin: `sha256/4HQtdsbE14lqcEw5YHG4JVx5rHWfpCmS09cZ0yfVd4U=`

## Build

```bat
gradlew.bat assembleDebug
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

Wymaga JDK 17 + Android SDK. Przy ścieżce z znakami nie-ASCII: `android.overridePathCheck=true` w `gradle.properties`.

## MITM (skrót)

1. `mitmweb --listen-host 0.0.0.0 --listen-port 8080`
2. Emulator: `adb shell settings put global http_proxy 10.0.2.2:8080`
3. Zainstaluj CA mitmproxy jako **User CA** (Settings → … → Install a certificate)
4. Addon podmiany ID: `mitm/rewrite_character_id.py` (`TARGET_ID=2`)

Bonus system CA: emulator `-writable-system`, hash `c8750f0d.0`, docs mitmproxy Android system CA.

## Sprawozdanie

Generator: `report/generate_report.py` → `report/BAISM_3_INDEKS_Imie_Nazwisko.pdf`  
Uzupełnij imię / indeks / link GitHub w skrypcie i przemianuj plik zgodnie z wytycznymi.
