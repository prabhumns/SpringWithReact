var path = require('path');

module.exports = {
    entry: './src/main/js/Main.tsx',
    devtool: 'sourcemaps',
    cache: true,
    mode: 'development',
    resolve: {
        extensions: [".js", ".ts", ".jsx", ".tsx"]
    },
    output: {
        path: __dirname,
        filename: './src/main/resources/static/built/bundle.js'
    },
    module: {
        rules: [
            {
                test: path.join(__dirname, '.'),
                exclude: /(node_modules)/,
                use: [{
                    loader: 'babel-loader',
                    options: {
                        presets: ["@babel/preset-env", "@babel/preset-react"]
                    }
                }]
            }
        ]
    }
};