# Feast Multiplatform Backend - GraphQL

## What is it?

This is a Kotlin Multiplatform (KMP) library that provides GraphQL network integration and data fetching repositories for Guardian Feast client applications on **Android**, **iOS**, and **web**.

It encapsulates network communication, Apollo GraphQL query execution, response parsing, and exposes clean Kotlin coroutine / Swift async interfaces and Koin dependency injection modules.

---

## Scope of this library

### 1. Responsibilities
- **GraphQL Integration:** Manages GraphQL queries, operations, schema mapping, and serialization.
- **Repository Abstraction:** Exposes high-level data interfaces (`GraphQLRepository`) so client apps do not need to deal with GraphQL queries directly.
- **Cross-Platform Delivery:** Delivers Android AAR/JAR artifacts via Maven, iOS frameworks/Swift Packages via XCFramework, and JavaScript ES modules with TypeScript declarations via an npm-compatible archive.

### 2. Architecture & Modules
- **`backend:graphql`**: Internal module containing Apollo GraphQL configurations, generated query models, network execution logic, and endpoint providers.
- **`backend:api`**: Public-facing entry point exporting `GraphQLRepository`, platform-specific DI initialization functions (`androidFeastApiModule`, `iosFeastApiModule`), and Swift wrappers (`FeastIos`).

### 3. Versioning Strategy vs `library` Module
`backend` uses an independent versioning lifecycle from the root `library` module:
- **`backend` Modules (`backend:api` & `backend:graphql`):** Versioned independently via `backend/version.txt` (currently `1.0.0-alpha01`), following Semantic Versioning (SemVer) suited for network and API contract releases.
- **`library` Module:** Uses root `version.txt` corresponding to unit conversions and recipe scaling domain rules.

---

## How to publish to Maven Central?

> This section will be filled once we have a release YAML file ready in the CI pipeline to publish to Maven Central.
## How to publish on local maven?

Follow the steps below to publish the `backend` library to your local maven repository(run command in terminal at the root of the project):
1. Gradle sync to make sure all dependencies are downloaded

#### For Android:
1. ./gradlew graphql:publishToMavenLocal
2. ./gradlew api:publishToMavenLocal
3. Published artifacts can be found in `~/.m2/repository/com/gu/feast-multiplatform-api/<version>` and `~/.m2/repository/com/gu/feast-multiplatform-graphql/<version>` respectively.

> Both need to be published for Android. We have defined the :api dependency in the consuming app. :graphql is pulled automatically by Gradle when building - but it still needs to be available independently on mavencentral/mavenlocal.

#### For iOS:
1. ./gradlew api:assembleFeastMultiplatformBackendXCFramework
2. The XCFramework can be found in `backend/api/build/XCFrameworks/release/FeastMultiplatformBackend.xcframework`. You can copy this framework to your iOS project and link it manually.

> We don't need to publish the :graphql module for iOS, as it is only used internally by the :api module and also exported in api module's XCFramework.

## iOS Setup

> This section will be filled up after confirming or by an iOS develper

## Browser TypeScript / Node.js

Both `api` and `graphql` have Kotlin/JS targets with Node.js and browser support.
Build the combined library from the repository root:

```sh
./gradlew :api:packJavaScriptLibrary
```

The npm-compatible archive is written to `api/build/distributions/feast-multiplatform-backend-api-<version>.tgz`,
using the version from `version.txt`. It contains the API, GraphQL implementation, and Kotlin runtime modules;
there is no separate GraphQL package to install. The unpacked ES modules and `.d.mts` declarations
are available in `api/build/dist/js/productionLibrary`.

Install the archive in the consuming TypeScript project:

```sh
npm install /absolute/path/to/feast-multiplatform-backend-api-<version>.tgz
```

```typescript
import { FeastGraphQL } from "feast-multiplatform-backend-api";

const api = new FeastGraphQL("https://your-feast-api.example");
try {
	const fronts = JSON.parse(await api.getFrontByRegion("northern", "all", 10));
	console.log(fronts);
} finally {
	api.close();
}
```

The base URL has `/graphql` appended, matching the Android and iOS clients. Browser
requests use `fetch`, so the server must permit the consuming application's origin via CORS.
Use a modern browser bundler and TypeScript's `moduleResolution: "bundler"` (or `"nodenext"`
for Node.js) to resolve the ES modules and declarations.

The exported `FeastGraphQL` methods return native `Promise<string>` values containing JSON:

- `getFrontByRegion(region, edition, recipesLimit)` returns an array of fronts.
- `getDishOfTheDayContainer(region, edition)` returns a container or JSON `null`.
- `getCuratedCollection(collectionId)` returns a collection or JSON `null` and validates the UUID.
- `close()` cancels pending requests and releases the client. Create a new instance to make further requests.

Regions are `northern`, `southern`, and `us`; editions are `all` and `meatfree`.
Invalid arguments, transport failures, and GraphQL errors reject the Promise.
JSON uses GraphQL field names, including `__typename` for union members, rather than Kotlin's
synthetic `onRecipeReference`-style properties. Method signatures are typed; parsed response
objects are not given generated TypeScript model types. Consumers should type or validate those
objects against the GraphQL schema as needed.

Run both modules' shared tests and the JavaScript adapter tests on Node.js:

```sh
./gradlew :api:jsNodeTest :graphql:jsNodeTest
```

## Android Setup

Consumers only need to depend on `backend:api`. Transitive dependencies (including `backend:graphql`) are bundled automatically via Maven POM metadata:

// build.gradle.kts (module level)

dependencies {
implementation("com.gu:feast-multiplatform-api:<latest-version>") // e.g. 1.0.0-alpha01
}
