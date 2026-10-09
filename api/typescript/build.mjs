import { cp, mkdir, readFile, rm, writeFile } from "node:fs/promises";

const source = new URL("./src/", import.meta.url);
const staging = new URL("../build/typescript/src/", import.meta.url);
const kotlin = new URL("../build/dist/js/productionLibrary/", import.meta.url);
const output = new URL("../build/dist/js/typescriptLibrary/", import.meta.url);

switch (process.argv[2]) {
  case "prepare":
    await rm(staging, { recursive: true, force: true });
    await rm(output, { recursive: true, force: true });
    await mkdir(staging, { recursive: true });
    await cp(source, staging, { recursive: true });
    await cp(kotlin, new URL("kotlin/", staging), { recursive: true });
    break;
  case "package": {
    await cp(kotlin, new URL("kotlin/", output), { recursive: true });
    const { devDependencies, main, types, ...metadata } = JSON.parse(
      await readFile(new URL("package.json", kotlin), "utf8"),
    );
    await writeFile(new URL("package.json", output), JSON.stringify({
      ...metadata,
      type: "module",
      main: "./index.js",
      types: "./index.d.ts",
      exports: {
        ".": {
          types: "./index.d.ts",
          import: "./index.js",
          default: "./index.js",
        },
      },
    }, null, 2) + "\n");
    break;
  }
  default:
    throw new Error("Expected prepare or package");
}