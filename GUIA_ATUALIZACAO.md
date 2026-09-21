# Guia de Atualização Remota - ChamaKids

Siga estes passos sempre que fizer uma alteração no código e desejar que todos os dispositivos recebam a nova versão automaticamente.

---

### Passo 1: Configuração de Versão (Android Studio)
1.  **build.gradle.kts (composeApp)**:
    *   Aumente o `versionCode` (ex: de 2 para 3).
    *   Atualize o `versionName` (ex: de "1.0.1" para "1.0.2").
2.  **HomeScreen.kt**:
    *   Procure por `val currentVersionCode = ...`.
    *   Mude para o **mesmo número** que você colocou no `versionCode` acima.
3.  Faça o **Commit e Push** do código para salvar as alterações.

---

### Passo 2: Gerar o Arquivo APK
1.  No menu superior, vá em `Build` > `Generate Signed Bundle / APK...`.
2.  Selecione `APK` e clique em `Next`.
3.  Preencha os dados da sua chave (`chamakids_key.jks`) e clique em `Next`.
4.  Selecione a variante **`release`** e clique em `Finish`.
5.  Ao terminar, clique em **Locate** para abrir a pasta com o arquivo `composeApp-release.apk`.

---

### Passo 3: Publicar no GitHub (Release)
1.  Acesse seu repositório: [github.com/pedrossantos16/ChamaKids](https://github.com/pedrossantos16/ChamaKids).
2.  Vá em **Releases** (lado direito) > **Create a new release**.
3.  Crie uma tag (ex: `v1.0.2`) e anexe o arquivo `.apk` gerado.
4.  Clique em **Publish release**.
5.  **IMPORTANTE**: Clique com o botão direito no arquivo APK (em Assets) e selecione **"Copiar endereço do link"**.

---

### Passo 4: Ativar a Atualização (update.json)
Este passo é o que faz o aviso aparecer nos celulares dos usuários.
1.  Abra o arquivo `update.json` na raiz do projeto.
2.  Mude o `versionCode` para o novo número (o mesmo do Passo 1).
3.  Cole o link do APK que você copiou no campo `apkUrl`.
4.  Escreva o que mudou no campo `releaseNotes`.
5.  Faça o **Commit e Push** do arquivo `update.json`.

---
**Dica**: O aplicativo checa por atualizações toda vez que é aberto. Se o `versionCode` no GitHub for maior que o instalado, o usuário verá o aviso de download.
