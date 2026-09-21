# Solução para Erro de JVM e Integração de Sincronização em Nuvem

Este plano visa resolver o erro de "Incompatible Gradle JVM version" e garantir que o aplicativo esteja devidamente sincronizado com a nuvem (Firebase).

## User Review Required

> [!IMPORTANT]
> **Versão do Java:** O Android Studio está configurado para usar o JDK 25 (embutido), mas o Gradle 8.11 não suporta esta versão. Vou atualizar o Gradle para uma versão mais recente (8.12), mas se o erro persistir, você precisará alterar manualmente o "Gradle JDK" nas configurações do Android Studio para o JDK 17.
>
> **Sincronização:** O código para Firebase já existe no projeto (`FirebaseSyncManager`), mas não estava sendo chamado pelos repositórios. Vou integrá-lo agora.

## Proposed Changes

### Build e Configuração
Atualização do Gradle e da versão do Java no projeto para garantir compatibilidade com as ferramentas modernas.

#### [MODIFY] [gradle-wrapper.properties](file:///C:/Users/Pedro Santos/Desktop/Aplicativos/ChamaKids/gradle/wrapper/gradle-wrapper.properties)
* Atualizar `distributionUrl` para o Gradle 8.12.

#### [MODIFY] [build.gradle.kts (composeApp)](file:///C:/Users/Pedro Santos/Desktop/Aplicativos/ChamaKids/composeApp/build.gradle.kts)
* Garantir que `jvmTarget` e `JavaVersion` estejam em 17 (já ajustado parcialmente, mas vamos consolidar).

---

### Sincronização com a Nuvem (Firebase)
Integração do `FirebaseSyncManager` nos repositórios para que os dados sejam salvos no Firestore automaticamente.

#### [MODIFY] [MemberRepository.kt](file:///C:/Users/Pedro Santos/Desktop/Aplicativos/ChamaKids/composeApp/src/commonMain/kotlin/com/pedro/ChamaKids/data/MemberRepository.kt)
* Chamar `FirebaseSyncManager.syncMember` após adicionar ou atualizar um membro.

#### [MODIFY] [AttendanceRepository.kt](file:///C:/Users/Pedro Santos/Desktop/Aplicativos/ChamaKids/composeApp/src/commonMain/kotlin/com/pedro/ChamaKids/data/AttendanceRepository.kt)
* Chamar `FirebaseSyncManager.syncAttendance` ao salvar uma chamada.

## Verification Plan

### Automated Tests
* Executar `./gradlew assembleDebug` para verificar se o erro de JVM foi resolvido ou se a nova versão do Gradle aceita o ambiente.
* Nota: Se falhar por causa do JDK 25, instruirei o usuário a mudar nas configurações da IDE.

### Manual Verification
* Verificar se a sincronização está sendo disparada (pode ser visto nos logs de rede ou no console do Firebase após o build).
