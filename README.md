# 🧠 Trivia Multiplayer Real-Time

<div align="center">

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Node.js](https://img.shields.io/badge/Node.js-43853D?style=for-the-badge&logo=node.js&logoColor=white)
![Socket.io](https://img.shields.io/badge/Socket.io-010101?style=for-the-badge&logo=socket.io&logoColor=white)

**Un juego de preguntas y respuestas multijugador en tiempo real con sincronización exacta y efectos visuales.**

</div>

---

## 📱 Descripción del Proyecto

Esta aplicación es una evolución de un juego de Trivia clásico, transformado en una experiencia **multijugador competitiva**. Utiliza una arquitectura Cliente-Servidor mediante **WebSockets** para conectar a dos jugadores, sincronizar las mismas preguntas desde una API externa y determinar un ganador en tiempo real.

### ✨ Características Principales

* **⚡ Conexión en Tiempo Real:** Comunicación bidireccional instantánea usando `Socket.io`.
* **⚔️ Modo Versus:** Marcador en vivo que muestra el progreso del oponente.
* **🌍 Preguntas Infinitas:** Integración con la **OpenTriviaDB API** para obtener preguntas aleatorias en cada partida.
* **⚖️ Fair Play:** El servidor gestiona las preguntas y valida las respuestas para evitar trampas.
* **🎉 Feedback Visual:** Sistema de partículas (**Konfetti**) para celebrar la victoria o lamentar la derrota.
* **🔄 Sincronización Final:** Sala de espera al terminar para asegurar que ambos jugadores ven el resultado al mismo tiempo.

---

## 📸 Capturas de Pantalla

| Buscando Rival | Gameplay (Versus) | Victoria (Confeti) |
|:---:|:---:|:---:|
| <img src="./screenshots/lobby.png" width="200"/> | <img src="./screenshots/gameplay.png" width="200"/> | <img src="./screenshots/win.png" width="200"/> |
| *Estado de espera y conexión* | *Marcador rival en tiempo real* | *Animación de partículas* |

*(Nota: Reemplaza las rutas de imagen con tus propias capturas)*

---

## 🛠️ Arquitectura Técnica

El sistema utiliza una arquitectura de estrella donde el servidor actúa como árbitro central.

```mermaid
sequenceDiagram
    participant A as 📱 Android A
    participant S as 🟢 Servidor (Node.js)
    participant B as 📱 Android B
    participant API as ☁️ OpenTriviaDB

    A->>S: join_game
    B->>S: join_game
    Note over S: Match Encontrado!
    S->>API: GET /api.php (10 Preguntas)
    API-->>S: JSON Preguntas
    S-->>A: game_start (Preguntas)
    S-->>B: game_start (Preguntas)
    
    rect rgb(240, 240, 240)
        Note over A, B: Bucle de Juego
        A->>S: submit_answer (+10 pts)
        S-->>B: opponent_answered (Update UI)
    end
    
    A->>S: player_finished (Espera)
    B->>S: player_finished (Espera)
    S->>A: force_game_over
    S->>B: force_game_over
