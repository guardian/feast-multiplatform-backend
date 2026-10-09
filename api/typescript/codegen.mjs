export default {
  schema: "../../graphql/src/commonMain/graphql/schema.graphqls",
  documents: "../../graphql/src/commonMain/graphql/*.graphql",
  config: {
    strictScalars: true,
    scalars: {
      LocalDateTime: "string",
      ZonedDateTime: "string",
      date: "string",
      uuid: "string",
      RawJson: "unknown",
    },
  },
  generates: {
    "../build/typescript/src/schema.ts": {
      plugins: ["typescript"],
      config: { enumsAsTypes: true },
    },
    "../build/typescript/src/models.ts": {
      plugins: ["typescript-operations"],
      config: {
        importSchemaTypesFrom: "../build/typescript/src/schema.js",
        emitLegacyCommonJSImports: false,
      },
    },
  },
};