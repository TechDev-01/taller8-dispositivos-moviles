**Diferencia entre los JSON: A diferencia de PokéAPI donde la lista de results solo contiene el nombre y una URL para buscar más datos, en el JSON de Rick & Morty la lista de results contiene los objetos con toda la información completa de cada personaje (id, name, status, species, etc.).   

**Predicción (Versión sin protección):** 
Si el Repository llama a Retrofit sin try/catch desde `viewModelScope.launch` y se desactiva el internet, la aplicación sufrirá un cierre inesperado (crash). Esto ocurre porque la excepción generada por la falla de red no es capturada, propagándose hasta cancelar la corrutina principal y el hilo de la UI.   

**Excepción observada:** Al cortar la conexión y pulsar el botón de recarga, Logcat registra una java.net.UnknownHostException.

**Causa técnica y corrección:** Retrofit lanza una excepción al no poder resolver el servidor o establecer el socket por la falta de internet. La corrección consiste en usar un bloque try/catch que retorne un objeto Result, atrapando Exception genéricas como Result.failure(e) y relanzando CancellationException para permitir que el framework de corrutinas gestione sus cancelaciones de forma natural.   

**Diferencia entre error HTTP y fallo de conexión:** Una IOException (fallo de red/conexión) indica un problema de transporte donde la petición nunca llegó o no pudo resolverse (ej. sin WiFi). Una HttpException indica que la petición sí llegó al servidor, pero este respondió con un código de error (ej. 404 Not Found o 500 Internal Server Error).   

**Decisión de diseño del detalle (Opción A):** Se optó por llamar a obtenerPersonaje(id) haciendo una nueva petición al abrir el detalle. La ventaja es que si se abriera directamente la ruta de detalle (por ejemplo, mediante un Deep Link) sin haber cargado la lista previa, la pantalla funcionaría perfectamente sin depender de la caché del ViewModel.