var path = require('path');
var proxy = require('express-http-proxy');
var express = require('express');
var webpack = require('webpack');
var config = require('./config/webpack.dev');

var app = express();
var compiler = webpack(config);
var port = process.env.PORT || 3000;

app.use(require('webpack-dev-middleware')(compiler, {
  noInfo: true,
  publicPath: config.output.publicPath,
  stats: {
    colors: true
  }
}));

app.use(require('webpack-hot-middleware')(compiler));

app.use('/api', proxy('http://localhost:8080', {
  forwardPath: function(req) {
    const path = require('url').parse(req.url).path;
    // Removes empty query params from the request.
    // Fixes a bug with RestEasy not handling null params.
    const pathParsed = path.replace(/(?:&)?[a-zA-z]+=(?=&|$)/g, "");
    console.log(pathParsed);
    return '/server' + pathParsed;
  },
  preserveReqSession: true,
}));

app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'index.html'));
});

app.listen(port, 'localhost', err => {
  if (err) {
    console.log(err);
    return;
  }

  console.log(`Listening at http://localhost:${port}`);
});
