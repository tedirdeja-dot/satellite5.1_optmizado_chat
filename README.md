# Satélite Zoom Earth

App de fondo animado Android con una captura de Zoom Earth. Esta revisión reduce el uso máximo de
memoria durante la captura y adapta su resolución a dispositivos con poca RAM. Requiere Android 6.0
(API 23) o posterior; el renderizado web también depende de la versión de Android System WebView
instalada en cada teléfono.

## Compilar en GitHub

1. Sube el contenido de este proyecto a una rama `main` o `master`.
2. Abre **Actions → Build Android APK** y espera a que termine el flujo.
3. Descarga el artefacto `SatelliteWallpaper-v5.0` desde la ejecución.

El flujo crea el Gradle wrapper si el repositorio no lo trae. Sin secretos de firma, genera un APK
de depuración con la clave temporal de GitHub; sirve para probar una instalación nueva, pero no para
actualizar encima de una instalación firmada con otra clave.

### Mantener la firma de una instalación existente

Android solo permite actualizar la app si se firma con el mismo keystore que firmó la instalación
actual. El keystore del ZIP de origen se omitió del proyecto entregable y nunca debe subirse al
repositorio. Si conservas ese keystore de forma privada, añádelo en **Settings → Secrets and
variables → Actions** con estos cuatro secretos:

- `ANDROID_KEYSTORE_BASE64`: keystore codificado en Base64 y en una sola línea.
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`

El workflow descodifica el keystore en un archivo temporal y lo borra al acabar. No publiques la
clave ni la pegues en chats.

## Cambios de rendimiento y compatibilidad

- Limita capturas a 3 MP y 2560 px por lado; en teléfonos marcados como low-RAM, a 1,5 MP y 1920 px.
- Mantiene el mismo viewport web al reducir la captura, ajustando también el DPI de la pantalla virtual.
- Decodifica el caché con muestreo para no cargar imágenes de mayor resolución que la pantalla y usa
  RGB_565 en dispositivos low-RAM para reducir a la mitad el espacio de esos bitmaps opacos.
- Reduce la cola de `ImageReader` de 3 fotogramas a 2 y guarda JPEG con calidad 92.
- Libera bitmaps al reemplazarlos o destruir la vista previa; agrupa desbloqueos simultáneos para evitar
  una cola de capturas redundantes.
- Acepta solo navegación HTTPS en `zoom.earth` o `www.zoom.earth`.
- Usa `versionName 5.0`, `versionCode 24`, superior al APK anterior build 23.

Los fabricantes pueden aplicar límites propios a procesos en segundo plano, ahorro de batería y
WebView. La optimización reduce memoria y trabajo redundante, pero no puede anular esas políticas ni
garantizar el mismo comportamiento en todos los modelos.
