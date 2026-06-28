# MyThought

MyThought adalah aplikasi Android untuk menulis, menyimpan, dan melihat kembali catatan pikiran harian. Aplikasi ini memakai **Jetpack Compose** untuk UI, **Supabase** untuk autentikasi dan penyimpanan data, serta **Google Sign-In** untuk login.

## Fitur

- Login dengan akun Google
- Menulis catatan hingga 5000 karakter
- Dashboard ringkasan aktivitas dan statistik catatan
- Riwayat catatan dengan pengelompokan tanggal
- Detail catatan untuk edit dan hapus
- Dark mode
- Kunci fingerprint/biometrik

## Stack

- Kotlin
- Jetpack Compose + Material 3
- Navigation Compose
- ViewModel
- DataStore Preferences
- Supabase Auth + PostgREST
- Google Identity / Credential Manager
- Biometric API
- Coil
- Vico Chart

## Kebutuhan

- Android Studio terbaru
- JDK 11
- Android SDK minSdk 26

## Menjalankan Proyek

1. Buka folder proyek ini di Android Studio.
2. Isi konfigurasi lokal terlebih dulu.
3. Sync Gradle.
4. Jalankan app di emulator atau device.

## Build via Terminal

```bash
./gradlew :app:assembleDebug
```

## Konfigurasi

Aplikasi membaca konfigurasi `BuildConfig` dari salah satu sumber ini:

1. `~/.gradle/gradle.properties`
2. environment variable
3. `local.properties`

Value yang wajib diisi:

- `SUPABASE_URL`
- `SUPABASE_ANON_KEY`
- `WEB_CLIENT_ID`

Contoh isi `local.properties`:

```properties
sdk.dir=/path/ke/android/sdk
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_ANON_KEY=your-supabase-anon-key
WEB_CLIENT_ID=your-google-web-client-id
```

Atau simpan di `~/.gradle/gradle.properties`:

```properties
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_ANON_KEY=your-supabase-anon-key
WEB_CLIENT_ID=your-google-web-client-id
```

Konfigurasi ini **tidak perlu** di-commit ke Git. `app/build.gradle.kts` sekarang hanya membaca nilainya, bukan menyimpan secret secara hardcoded.

## Struktur Singkat

```text
app/src/main/java/com/asfine/mythought/
|- auth/           # AppState, GoogleAuthManager
|- core/           # Config, GoogleConfig, DateTimeUtils, AppSettingsStore
|- data/
|  |- model/       # Thought, UserProfile, DashboardSummary, WeeklyActivity
|  `- repository/  # AuthRepository, ThoughtRepository
|- navigation/     # Screen, AppNavigation, MainNavigation, NavigationManager
|- ui/
|  |- auth/        # SplashScreen, LoginScreen, ProfileScreen
|  |- components/  # AppTopBar, BottomBar, EmptyState, TimelineItem, dll
|  |- dashboard/   # DashboardScreen + components/
|  |- detail/      # ThoughtDetailScreen
|  |- history/     # HistoryScreen, HistoryItem
|  |- theme/       # Color, Theme, Type
|  `- write/       # WriteThoughtScreen
`- viewmodel/      # AuthViewModel, DashboardViewModel, DetailViewModel, ThoughtViewModel
```

## Layar Utama

- **Login**: autentikasi Google
- **Dashboard**: statistik, chart mingguan, dan catatan terbaru
- **Write**: membuat catatan baru
- **History**: daftar catatan berdasarkan tanggal
- **Detail**: lihat, edit, dan hapus catatan
- **Profile**: info akun, fingerprint, dark mode, logout

## Catatan

- Deep link login memakai scheme `com.asfine.mythought://login`
- Aplikasi menggunakan splash screen saat startup
- Parsing tanggal sudah dibuat lebih aman untuk menghindari crash saat data timestamp tidak valid
- Login Google otomatis retry jika `NoCredentialException` muncul (kemungkinan glitch Play Services/network) sebelum menampilkan error ke user
