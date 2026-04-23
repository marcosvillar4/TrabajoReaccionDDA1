# Juego de Reaccion y Atencion

Aplicacion Android (Java) sin internet que evalua reflejos y atencion con estimulos visuales.

## Funcionalidades principales

- Modos: `Entrenamiento`, `Facil`, `Medio` y `Dificil`.
- Configuracion inicial:
  - nombre del jugador
  - cantidad de iteraciones (default: 20)
  - tiempo maximo de reaccion (limite absoluto: 30 s)
- Estimulos visuales aleatorios de tres tipos: palabras, numeros y colores.
- Registro de:
  - aciertos
  - errores
  - timeouts
  - tiempo de reaccion promedio
  - mejor tiempo
  - puntaje
- Persistencia local con `SharedPreferences` para mejores resultados por jugador, modo y variante.
- Reinicio de partida al perder o completar las rondas.

## Agregados implementados

- Incremento dinamico de dificultad: cada 5 aciertos seguidos, la ventana de reaccion baja 1 segundo (hasta un minimo de 2 segundos).
- Modo reaccion inversa: no se debe responder ante color `ROJO` o numero primo.

## Estructura principal

- `app/src/main/java/com/example/juegoreaccion/Main.java`: pantalla de configuracion.
- `app/src/main/java/com/example/juegoreaccion/GameActivity.java`: motor de rondas y evaluacion.
- `app/src/main/java/com/example/juegoreaccion/ResultActivity.java`: estadisticas finales y reintento.
- `app/src/main/java/com/example/juegoreaccion/data/BestScoreRepository.java`: guardado local de records.
- `app/src/main/java/com/example/juegoreaccion/model/`: modelos de dominio.

## Probar rapido

```powershell
cd "C:\Users\marco\AndroidStudioProjects\JuegoReaccion"
.\gradlew.bat test
```

