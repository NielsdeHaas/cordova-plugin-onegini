const path = require('path');
const webpack = require('webpack');
const { CleanWebpackPlugin } = require('clean-webpack-plugin');
const TerserPlugin = require('terser-webpack-plugin');

module.exports = {
  mode: "production",
  entry: {
    onegini: './src/js/onegini.js'
  },

  output: {
    path: path.resolve('./dist/'),
    filename: '[name].js',
    library: 'onegini',
    libraryTarget: 'umd'
  },

  resolve: {
    extensions: ['.js'],
    modules: [
      'node_modules'
    ]
  },

  devtool:  '#eval-source-map',

  module: {
    rules: [
      {
        test: /\.js$/,
        exclude: /node_modules/,
        loader: 'babel-loader'
      }
    ]
  },

  plugins: [
    new CleanWebpackPlugin()
  ]
};

if (process.env.NODE_ENV === 'production') {
  module.exports.devtool = '#source-map';

  module.exports.optimization = {
    minimizer: [
      new TerserPlugin({
        sourceMap: true
      }),
    ]
  }

  module.exports.plugins = (module.exports.plugins || []).concat([
    new webpack.DefinePlugin({
      'process.env': {
        NODE_ENV: '"production"'
      }
    }),
    new webpack.LoaderOptionsPlugin({
      minimize: true
    })
  ]);
}
