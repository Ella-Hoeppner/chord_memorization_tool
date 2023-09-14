const path = require('path');

module.exports = {
  entry: './out/external/index.js',
  resolve: {
    fallback: { 
      "url": require.resolve("url/"),
      "fs": false
    }
  },
  output: {
    filename: 'main.js',
    path: path.resolve(__dirname, 'out/public/libs'),
  }
};
