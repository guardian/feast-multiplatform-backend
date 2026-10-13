import {
  FeastGraphQL,
  type CuratedContainerByIdQuery,
  type Editions,
  type FaciaRecipeSearchQuery,
  type GetDishOfTheDayRecipeQuery,
  type GetFrontsByRegionQuery,
  type Regions,
} from "../../build/dist/js/typescriptLibrary/index.js";

function checkContracts(api: FeastGraphQL, region: Regions, edition: Editions) {
  const fronts: Promise<GetFrontsByRegionQuery["Front"]> = api.getFrontByRegion(region, edition, 10);
  const dish: Promise<GetDishOfTheDayRecipeQuery["Container"]> = api.getDishOfTheDayContainer(region, edition);
  const collection: Promise<CuratedContainerByIdQuery["curatedContainerById"]> =
    api.getCuratedCollection("123e4567-e89b-12d3-a456-426614174000");
  const search: Promise<FaciaRecipeSearchQuery["search"]> =
    api.faciaRecipeSearch("soup", "celebration-1", "diet-1", "firstPublishedDate");
  api.faciaRecipeSearch("");

  // @ts-expect-error Sort field must be a known sortable date.
  api.faciaRecipeSearch("soup", null, null, "unknown");
  // @ts-expect-error The JS bridge accepts a single celebration ID, not a list.
  api.faciaRecipeSearch("soup", ["celebration-1"]);

  // @ts-expect-error Invalid region must not compile.
  api.getFrontByRegion("unknown", edition, 10);
  // @ts-expect-error Invalid edition must not compile.
  api.getDishOfTheDayContainer(region, "unknown");
  // @ts-expect-error Recipe limit is a GraphQL Int, not a string.
  api.getFrontByRegion(region, edition, "10");
  // @ts-expect-error A nullable container must not be treated as always present.
  const nonNullableDish: Promise<NonNullable<GetDishOfTheDayRecipeQuery["Container"]>> = dish;

  return { fronts, dish, collection, search };
}

function checkUnion(item: GetFrontsByRegionQuery["Front"][number]["items"][number]) {
  switch (item.__typename) {
    case "RecipeReference": {
      const id: string | undefined = item.recipe?.id;
      // @ts-expect-error The operation does not select canonicalArticle here.
      item.recipe?.canonicalArticle;
      // @ts-expect-error RecipeReference is not a Chef.
      item.chef;
      return id;
    }
    case "Chef":
      return item.chef.id;
    case "SubCollection":
      return item.collection.title;
    default: {
      const exhaustive: never = item;
      return exhaustive;
    }
  }
}