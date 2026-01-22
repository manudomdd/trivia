const express = require('express');
const http = require('http');
const { Server } = require("socket.io");
const axios = require('axios');

const app = express();
const server = http.createServer(app);

const io = new Server(server, {
    cors: { origin: "*" }
});

const activeRooms = {}; 

let waitingPlayer = null;

io.on('connection', (socket) => {
    console.log('Jugador conectado: ' + socket.id);

    socket.on('join_game', async () => {
        if (waitingPlayer && waitingPlayer.id !== socket.id) {
            const roomId = waitingPlayer.id + "#" + socket.id;
            
            waitingPlayer.join(roomId);
            socket.join(roomId);

            activeRooms[roomId] = { finishedCount: 0 };

            console.log(`Sala ${roomId}: Iniciando...`);

            try {
                const response = await axios.get('https://opentdb.com/api.php?amount=10&type=multiple');
                const questionsFromApi = response.data.results;
                
                io.to(roomId).emit('game_start', {
                    roomId: roomId,
                    questions: questionsFromApi
                });
            } catch (error) {
                console.error("API Error:", error);
            }
            waitingPlayer = null;

        } else {
            waitingPlayer = socket;
            socket.emit('waiting_opponent');
        }
    });

    socket.on('submit_answer', (data) => {
        socket.to(data.roomId).emit('opponent_answered', {
            isCorrect: data.isCorrect
        });
    });

    socket.on('player_finished', (data) => {
        const roomId = data.roomId;
        
        if (activeRooms[roomId]) {
            activeRooms[roomId].finishedCount += 1;

            if (activeRooms[roomId].finishedCount === 2) {
                console.log(`Sala ${roomId}: Fin del juego para todos.`);
                io.to(roomId).emit('force_game_over'); // Ordenamos mostrar pantalla final
                delete activeRooms[roomId]; // Limpiamos memoria
            }
        }
    });

    socket.on('disconnect', () => {
        if (waitingPlayer === socket) waitingPlayer = null;
    });
});

server.listen(3000, () => {
    console.log('SERVER ONLINE (Puerto 3000)');
});
