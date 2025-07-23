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
        // Test for .ts and .tsx files.
        test: /\.(ts|tsx)$/,
        // Exclude files from the node_modules directory to speed up compilation.
        exclude: /node_modules/,
        // Use 'ts-loader' for TypeScript files.
        // 'ts-loader' is a Webpack loader for TypeScript. It uses your tsconfig.json file.
        use: "ts-loader",
      },
      {
        test: /\.css$/,
        use: ["style-loader", "css-loader"],
      },
    ],
  },
};
