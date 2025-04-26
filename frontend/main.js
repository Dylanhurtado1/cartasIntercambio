const http = require('http');
const fs = require('fs');
const path = require('path');

const host = '0.0.0.0';
const port = 9090;

/*const routes = {
    'GET/': (req, res) => {
        serveFile('index.html', res);
    },
    'GET/style.css': (req, res) => {
        serveFile('style.css', res, 'text/css');
    },
    'POST/datos': (req, res) => {
        collectData(req, body => {
            console.log('POST recibido:', body);
            res.writeHead(200, { 'Content-Type': 'text/plain' });
            res.end('Ok');
        });
    }
};

const server = http.createServer((req, res) => {
    const key = req.method + req.url;
    if (routes[key]) {
        routes[key](req, res);
    } else {
        res.writeHead(404, { 'Content-Type': 'text/plain' });
        res.end('404 Not Found');
    }
});

function serveFile(fileName, res, contentType = 'text/html') {
    const fullPath = path.join(__dirname, 'public', fileName);
    fs.readFile(fullPath, (err, data) => {
        if (err) {
            res.writeHead(404, { 'Content-Type': 'text/plain' });
            res.end('404 Not Found');
        } else {
            res.writeHead(200, { 'Content-Type': contentType });
            res.end(data);
        }
    });
}*/

const server = http.createServer((req, res) => {
    const filePath = req.url === '/' ? '/index.html' : req.url;
    const fullPath = path.join(__dirname, 'public', filePath);
    

    fs.readFile(fullPath, (err, data) => {
        if (err) {
            res.writeHead(404, { 'Content-Type': 'text/plain' });
            res.end('404 Not Found');
        } else {
            const contentType = generarContentType(fullPath);
            res.writeHead(200, { 'Content-Type': contentType });
            res.end(data);
        }
    });
    
});

server.listen(port, host, () => {
    console.log(`HTTP server running at http://${host}:${port}/`);
});

function generarContentType(fullPath) {
    const ext = path.extname(fullPath).toLowerCase();
    const mimeTypes = {
        '.html': 'text/html',
        '.css': 'text/css',
        '.js': 'application/javascript',
        '.json': 'application/json',
        '.png': 'image/png',
        '.jpg': 'image/jpeg',
        '.gif': 'image/gif',
    };
    const contentType = mimeTypes[ext] || 'application/octet-stream';
    return contentType;
}
