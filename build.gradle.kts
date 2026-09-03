// Configuração de Plugins no nível raiz
plugins {
    // Usamos IDs diretos para evitar conflitos de nomenclatura do TOML no servidor
    id("com.android.application") version "9.3.2" apply false
    id("org.jetbrains.kotlin.multiplatform") version "2.1.0" apply false
    id("org.jetbrains.compose") version "1.7.3" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0" apply false
    id("androidx.room") version "2.7.0-alpha13" apply false
    id("com.google.devtools.ksp") version "2.1.0-1.0.29" apply false
}
