import assert from "node:assert/strict";
import test from "node:test";
import { FeastGraphQL } from "../../build/dist/js/typescriptLibrary/index.js";

const collectionId = "123e4567-e89b-12d3-a456-426614174000";

async function withResponse(response, block) {
  const originalFetch = globalThis.fetch;
  const requests = [];
  globalThis.fetch = async (url, options) => {
    assert.equal(url, "https://example.invalid/graphql");
    requests.push(JSON.parse(await new Response(options.body).text()));
    return new Response(JSON.stringify(response), {
      headers: { "Content-Type": "application/json" },
    });
  };
  const api = new FeastGraphQL("https://example.invalid/");
  try {
    await block(api, requests);
  } finally {
    api.close();
    globalThis.fetch = originalFetch;
  }
}

test("fronts return objects with discriminated union members", async () => {
  const fronts = [{
    title: "Web",
    items: [{
      __typename: "RecipeReference",
      recipe: { id: "recipe-1", title: "Soup", timings: [], previewImage: null, diets: [], mealTypes: [] },
    }],
  }];
  await withResponse({ data: { Front: fronts } }, async (api, requests) => {
    assert.deepEqual(await api.getFrontByRegion("northern", "all", 10), fronts);
    assert.equal(requests[0].operationName, "GetFrontsByRegion");
    assert.deepEqual(requests[0].variables, { region: "northern", edition: "all", recipesLimit2: 10 });
  });
});

test("dish of the day returns its selected fields", async () => {
  const container = {
    title: "Dish of the day",
    items: [{
      __typename: "RecipeReference",
      recipe: { title: "Soup", featuredImage: null, id: "recipe-1", diets: [] },
    }],
  };
  await withResponse({ data: { Container: container } }, async (api, requests) => {
    assert.deepEqual(await api.getDishOfTheDayContainer("southern", "meatfree"), container);
    assert.deepEqual(requests[0].variables, { region: "southern", edition: "meatfree", alias: "dotd" });
  });
});

test("curated collections preserve nullable fields and union members", async () => {
  const collection = {
    title: "Collection",
    body: null,
    items: [{ __typename: "Chef", chef: { image: null, id: "chef-1", meta: null } }],
  };
  await withResponse({ data: { curatedContainerById: collection } }, async (api, requests) => {
    assert.deepEqual(await api.getCuratedCollection(collectionId), collection);
    assert.deepEqual(requests[0].variables, { collectionId });
  });
});

test("facia search returns objects and forwards filters", async () => {
  const search = { maxScore: 1, results: [] };
  await withResponse({ data: { search } }, async (api, requests) => {
    assert.deepEqual(
      await api.faciaRecipeSearch("soup", "celebration-1", "diet-1", "firstPublishedDate"),
      search,
    );
    assert.equal(requests[0].operationName, "FaciaRecipeSearch");
    assert.deepEqual(requests[0].variables, {
      query: "soup",
      celebrationId: ["celebration-1"],
      dietId: ["diet-1"],
      uprateByDate: "firstPublishedDate",
    });
  });
});

test("facia search omits unspecified filters", async () => {
  const search = { maxScore: null, results: [] };
  await withResponse({ data: { search } }, async (api, requests) => {
    assert.deepEqual(await api.faciaRecipeSearch(""), search);
    assert.deepEqual(requests[0].variables, { query: "" });
  });
});

test("facia chef search returns chef and byline results", async () => {
  const chefs = [
    { __typename: "ChefBio", webTitle: "Jane Chef", bylineImageUrl: null, contributorId: "chef-1" },
    { __typename: "byline", webTitle: "The Kitchen" },
  ];
  await withResponse({ data: { chefs } }, async (api, requests) => {
    assert.deepEqual(await api.faciaChefSearch(10, "chef", null), chefs);
    assert.equal(requests[0].operationName, "FaciaChefSearch");
    assert.deepEqual(requests[0].variables, { limit: 10, q: "chef" });
  });
});

test("facia chef search omits an unspecified search term", async () => {
  await withResponse({ data: { chefs: [] } }, async (api, requests) => {
    assert.deepEqual(await api.faciaChefSearch(10), []);
    assert.deepEqual(requests[0].variables, { limit: 10 });
  });
});

test("recipe batch returns selected fields and forwards recipe IDs", async () => {
  const recipes = [{
    id: "recipe-1",
    canonicalArticle: "lifeandstyle/2025/jan/01/soup",
    appExclusiveBranding: false,
    title: "Soup",
    lastModifiedDate: "2025-01-01T00:00:00Z",
    publishedDate: null,
    firstPublishedDate: null,
    featuredImage: { templateUrl: "https://example.invalid/{width}.jpg" },
    contributors: [{ webTitle: "Jane Chef" }],
  }];
  await withResponse({ data: { recipes } }, async (api, requests) => {
    assert.deepEqual(await api.getRecipeBatch(["recipe-1"]), recipes);
    assert.equal(requests[0].operationName, "GetRecipeBatch");
    assert.deepEqual(requests[0].variables, { recipeIds: ["recipe-1"] });
  });
});

test("recipe batch omits unspecified recipe IDs", async () => {
  await withResponse({ data: { recipes: [] } }, async (api, requests) => {
    assert.deepEqual(await api.getRecipeBatch(), []);
    assert.deepEqual(requests[0].variables ?? {}, {});
  });
});

test("missing containers and search return native null", async () => {
  await withResponse({ data: { Container: null, curatedContainerById: null, search: null } }, async (api) => {
    assert.equal(await api.getDishOfTheDayContainer("northern", "all"), null);
    assert.equal(await api.getCuratedCollection(collectionId), null);
    assert.equal(await api.faciaRecipeSearch("soup"), null);
  });
});

test("invalid inputs reject without making requests", async () => {
  await withResponse({}, async (api, requests) => {
    await assert.rejects(api.getCuratedCollection("invalid"), /Invalid collectionId UUID/);
    await assert.rejects(api.getFrontByRegion("unknown", "all", 10), /Unknown region/);
    await assert.rejects(api.getDishOfTheDayContainer("northern", "unknown"), /Unknown edition/);
    assert.equal(requests.length, 0);
  });
});

test("GraphQL errors reject instead of returning partial objects", async () => {
  await withResponse({ errors: [{ message: "Not authorised" }] }, async (api) => {
    await assert.rejects(api.getFrontByRegion("northern", "all", 10), /Not authorised/);
  });
});

test("close prevents further requests", async () => {
  await withResponse({}, async (api, requests) => {
    api.close();
    await assert.rejects(api.getFrontByRegion("northern", "all", 10));
    assert.equal(requests.length, 0);
  });
});