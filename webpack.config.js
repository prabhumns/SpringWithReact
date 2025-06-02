var path = require("path");

module.exports = {
  entry: "./src/main/js/Main.tsx",
  mode: "production",
  resolve: {
    extensions: [".js", ".ts", ".jsx", ".tsx"],
  },
  output: {
    path: __dirname,
    filename: "./target/classes/static/built/bundle.js",
  },
  module: {
    rules: [
      {
        test: path.join(__dirname, "."),
        exclude: /(node_modules)/,
        use: [
          {
            loader: "ts-loader",
          },
        ],
      },
      {
        test: /\.css$/,
        use: ["style-loader", "css-loader"],
      },
    ],
  },
};
