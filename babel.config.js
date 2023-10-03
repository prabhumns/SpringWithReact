const isTest = process.env.NODE_ENV === "test";

const presetEnv = isTest
    ? ["@babel/preset-env", { targets: { node: "current" } }]
    : [
        "@babel/preset-env",
        {
            useBuiltIns: "entry",
            corejs: 3,
            targets: {
                esmodules: true,
            },
        },
    ];

const presets = [presetEnv, "@babel/preset-react", "@babel/typescript"];
const env = {
    test: {
        plugins: ["@babel/plugin-transform-modules-commonjs"],
    },
};
const plugins = [
    "@babel/plugin-proposal-object-rest-spread",
    "@babel/plugin-proposal-class-properties",
    "@babel/plugin-proposal-optional-chaining",
    [
        "react-css-modules",
        {
            webpackHotModuleReloading: true,
            autoResolveMultipleImports: true,
            generateScopedName: "[name]__[local]__[hash:base64:5]",
            filetypes: {
                ".scss": {
                    syntax: "postcss-scss",
                },
            },
        },
    ],
];

module.exports = {
    presets,
    env,
    plugins,
};
