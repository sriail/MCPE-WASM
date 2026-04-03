const express = require('express');
const http = require('http');
const WebSocket = require('ws');
const path = require('path');

const app = express();
const server = http.createServer(app);
const wss = new WebSocket.Server({ server });

// Serve built WASM output (index.html, index.js, index.wasm)
app.use(express.static(path.join(__dirname, 'web-build', 'project', 'emscripten')));
// Serve game data assets (textures, sounds, etc.)
app.use('/data', express.static(path.join(__dirname, 'web-build', 'data')));

// WebSocket connection for world data / chunk sync
wss.on('connection', (ws) => {
  console.log('Client connected');

  ws.on('message', (message) => {
    // Truncate logged output to avoid leaking large/sensitive payloads
    const preview = message.toString().slice(0, 120);
    console.log('Received message (%d bytes): %s%s', message.length, preview,
      message.length > 120 ? '…' : '');
    // Broadcast to all other connected clients (multiplayer world sync)
    wss.clients.forEach((client) => {
      if (client !== ws && client.readyState === WebSocket.OPEN) {
        client.send(message);
      }
    });
  });

  ws.on('close', () => {
    console.log('Client disconnected');
  });

  ws.on('error', (err) => {
    console.error('WebSocket error:', err.message);
  });
});

// Bind to localhost by default; set HOST=0.0.0.0 to expose on all interfaces
const HOST = process.env.HOST || '127.0.0.1';
const PORT = process.env.PORT || 8080;
server.listen(PORT, HOST, () => {
  console.log(`Server running on http://${HOST}:${PORT}`);
  console.log(`WebSocket server ready for world connections on ws://${HOST}:${PORT}`);
  if (HOST === '127.0.0.1') {
    console.log('(Set HOST=0.0.0.0 to expose on all network interfaces)');
  }
});
