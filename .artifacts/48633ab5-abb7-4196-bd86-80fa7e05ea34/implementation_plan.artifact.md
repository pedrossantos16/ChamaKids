# Plano de Implementação - Correções Críticas e Melhorias no ChamaKids

Este plano visa corrigir falhas críticas identificadas na geração de relatórios PDF e iniciar a implementação do seletor de fotos para o cadastro de membros.

## User Review Required

> [!IMPORTANT]
> A correção do PDF requer a adição de um `FileProvider` no Manifesto. Isso é essencial para que o aplicativo possa compartilhar o arquivo PDF com visualizadores externos (como o Google Drive ou Adobe Reader) sem violar as políticas de segurança do Android.

> [!NOTE]
> Para o seletor de fotos, implementaremos uma solução nativa via `expect/actual` que abrirá a galeria do Android. Futuramente, isso pode ser expandido para o iOS.

## Proposed Changes

### [Android] Configuração e Segurança

#### [MODIFY] [AndroidManifest.xml](file:///F:/Android/Projects/ChamaKids/composeApp/src/androidMain/AndroidManifest.xml)
- Adicionar permissão `WRITE_EXTERNAL_STORAGE` (com `maxSdkVersion="28"`) para salvar PDFs em versões antigas do Android.
- Declarar o `FileProvider` dentro da tag `<application>`.

#### [NEW] [file_paths.xml](file:///F:/Android/Projects/ChamaKids/composeApp/src/androidMain/res/xml/file_paths.xml)
- Definir os caminhos de arquivos permitidos para compartilhamento (cache e diretórios externos).

---

### [UI] Cadastro de Membros e Seletor de Fotos

#### [MODIFY] [MemberImage.kt](file:///F:/Android/Projects/ChamaKids/composeApp/src/commonMain/kotlin/com/pedro/ChamaKids/ui/MemberImage.kt)
- Adicionar um parâmetro `onClick` opcional para permitir que a imagem seja clicável.

#### [MODIFY] [AddMemberScreen.kt](file:///F:/Android/Projects/ChamaKids/composeApp/src/commonMain/kotlin/com/pedro/ChamaKids/ui/AddMemberScreen.kt)
- Integrar a chamada ao seletor de fotos (que será definido via interface `expect/actual`).

#### [NEW] [PhotoPicker.kt](file:///F:/Android/Projects/ChamaKids/composeApp/src/commonMain/kotlin/com/pedro/ChamaKids/ui/PhotoPicker.kt)
- Definir a interface `expect` para o seletor de fotos multiplataforma.

#### [NEW] [PhotoPicker.android.kt](file:///F:/Android/Projects/ChamaKids/composeApp/src/androidMain/kotlin/com/pedro/ChamaKids/ui/PhotoPicker.android.kt)
- Implementar o `actual` para Android usando `rememberLauncherForActivityResult`.

## Verification Plan

### Manual Verification
1.  **Geração de PDF**: Testar a exportação de um relatório e verificar se o sistema abre o visualizador de PDF sem fechar o app.
2.  **Cadastro com Foto**: Tentar adicionar um novo membro, clicar no ícone de foto, selecionar uma imagem da galeria e verificar se a miniatura aparece corretamente.
3.  **Persistência**: Fechar e abrir o app para garantir que a foto e o PDF (se salvo) permanecem acessíveis.
