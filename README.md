JuegoReconocimiento
===================

Juego de reconocimiento de objetos destinado a niños de entre 0 y 3 años con diversidad funcional visual.


En la vida real un niño puede tocar objetos usando manos, nariz y boca, para así reconocer de
forma táctil formas y texturas; al igual que deben aprender las diferentes partes de su cuerpo y del
cuerpo de distintas personas. Nuestra aplicación está destinada a ayudar a la identificación de
objetos, mediante el uso de técnicas de reconocimiento de objetos a través del procesamiento de
imágenes.

En nuestro sistema hay cuatro secciones claramente divididas, los alumnos, los ejercicios, las
series de ejercicios y los objetos.

    • Alumnos. Son los principales usuarios de la aplicación, ya que está pensada para ellos. Los
    alumnos son los usuarios finales, ya que ellos van a ser los que va a jugar con esta
    aplicación. Podrá haber tantos alumnos claramente identificados como el profesor quiera.
    Para cada alumno se guardar la información personal que el profesor vea conveniente,
    además de poder hacer un seguimiento continuo de su trayectoria a lo largo de todo el juego
    pudiendo acceder a sus resultados obtenidos.
  
    • Ejercicios. Un ejercicio es lo que un alumno va a efectuar cuando esté jugando. Los
    ejercicios están compuestos por dos conjuntos de objetos:
  
          ◦ Escenario. Este conjunto lo forman los objetos con los que el alumno va a 
          interaccionar a lo largo de todo el ejercicio
        
          ◦ Objetos a reconocer. Este segundo conjunto de objetos es un subconjunto 
          del anterior, que serán los objetos que el alumno debe reconocer 
          satisfactoriamente
    
    • Series de ejercicios. Como el propio nombre dice, es un grupo de ejercicios. Este elemento
    del juego es muy importante, ya que cuando un alumno va a jugar debe elegir una serie de
    ejercicios. Cada serie de ejercicios puede contener los ejercicios que el profesor quiera
    insertar dentro de cada una, pudiendo cambiar a su antojo el orden en el que irá cada
    ejercicio.
    
    • Objetos. Sin duda alguna, se trata del componente más importante de la aplicación, ya que
    el fin de la misma es el de interaccionar con ellos. Esta interacción dará como resultado un
    aprendizaje en los alumnos, ya que al experimentar con los objetos aprenderán a
    distinguirlos.

Una vez configuradas las series de ejercicios, los ejercicios, los objetos y lo alumnos, se sigue
el siguiente flujo de acciones cuando se decide iniciar una nueva partida:
  
    • El profesor elige al alumno y a la serie de ejercicios que se va jugar 
    
    • Se iniciará una descripción auditiva del primer ejercicio de la serie de ejercicios
    
    • Al niño se le colocará el conjunto de todos los objetos (escenario) frente a él.
    
    • La aplicación le indicará que objeto debe reconocer a partir de una descripción auditiva
    
    • El niño debe examinar todos los objetos que tiene frente a él, y elegir el objeto que la
    aplicación le ha pedido. El profesor puede ayudar a lo largo de este proceso pulsando la
    opción dentro de la aplicación de reproducción del sonido de ayuda o refuerzo, para así
    ayudar y animar al niño a seguir con la búsqueda
    
    • Una vez que el niño ha elegido el objeto que cree conveniente lo colocará delante de la
    cámara, y la aplicación debe responder indicando si el niño ha acertado, o en caso de error,
    cuál es el elemento que ha seleccionado erróneamente (siempre que pertenezca al conjunto
    inicial de objetos)
    
    • Cuando el niño haya acertado en el reconocimiento del objeto, se iniciará el mismo proceso
    con el siguiente objeto a reconocer
    
    • Una vez terminado de reconocer todos los objetos, se iniciará el siguiente ejercicio de la
    serie de ejercicios. Y así irá desarrollándose toda la serie de ejercicios hasta que se
    reconozcan todos los objetos de cada ejercicio de la serie de ejercicios.
  
Se usarán tres tipos de almacenamiento en nuestro sistema.
  
    • Base de datos local. En este tipo de almacenamiento se guardan los datos relacionados con
    los objetos, ejercicios, series de ejercicios, alumnos y resultados obtenidos por los alumnos
    en las series que han jugado. Los detalles de los datos que se almacenarán para cada
    elemento de la base de datos se explicará de manera detallada más adelante.
  
    • Base de datos remota. El uso de un servidor externo da mucha facilidad a la hora de
    configurar la aplicación. En el servidor se guardarán los datos referidos a los objetos y a los
    ejercicios. Se ha decidido actuar de esta manera debido a que son los dos miembros con los
    que se debe tener más cuidado a la hora de crearlos. En el caso de los objetos es conflictivo
    añadirlo a una base de datos, ya que si un objeto se guarda en la base de datos de una
    manera en la que no se ha obtenido suficiente información como para poder reconocerlo a
    posteriori, puede dar lugar a posibles fallos en la aplicación. En cuando a los ejercicios, 
    si se crea un ejercicio con dos o más objetos parecidos también dará lugar a un posible mal
    funcionamiento del juego. Por tanto el uso de la base de datos remota soluciona este tipo de
    problemas, ya que solamente se dará la posibilidad de obtener los objetos y los ejercicios
    desde el servidor a la base de datos del dispositivo. Únicamente los administradores de la
    aplicación podrán añadir de manera excepcional objetos o ejercicios a la base de datos local,
    con el fin de subirlos al servidor para que los demás usuarios de la aplicación puedan
    descargarlos. Las imágenes de los objetos y los sonidos de los ejercicios y objetos también
    se almacenan en el servidor.
  
    • Almacenamiento en memoria interna o externa del dispositivo. Para el almacenamiento
    de datos de datos de mayor tamaño, así como imágenes o sonidos se usará una carpeta
    creada en el almacenamiento interno o externo del dispositivo que tiene instalada la
    aplicación.
