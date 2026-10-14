import { FeastGraphQL as KotlinFeastGraphQL } from "./kotlin/feast-multiplatform-backend-api.mjs";
import type {
  CuratedContainerByIdQuery,
  CuratedContainerByIdQueryVariables,
  FaciaChefSearchQuery,
  FaciaChefSearchQueryVariables,
  FaciaRecipeSearchQuery,
  FaciaRecipeSearchQueryVariables,
  GetDishOfTheDayRecipeQuery,
  GetDishOfTheDayRecipeQueryVariables,
  GetFrontsByRegionQuery,
  GetFrontsByRegionQueryVariables,
  GetRecipeBatchQuery,
} from "./models.js";

export type * from "./schema.js";
export type * from "./models.js";

export class FeastGraphQL {
  private readonly client: KotlinFeastGraphQL;

  constructor(baseUrl: string) {
    this.client = new KotlinFeastGraphQL(baseUrl);
  }

  async getFrontByRegion(
    region: GetFrontsByRegionQueryVariables["region"],
    edition: GetFrontsByRegionQueryVariables["edition"],
    recipesLimit: GetFrontsByRegionQueryVariables["recipesLimit2"],
  ): Promise<GetFrontsByRegionQuery["Front"]> {
    return JSON.parse(await this.client.getFrontByRegion(region, edition, recipesLimit));
  }

  async getDishOfTheDayContainer(
    region: GetDishOfTheDayRecipeQueryVariables["region"],
    edition: GetDishOfTheDayRecipeQueryVariables["edition"],
  ): Promise<GetDishOfTheDayRecipeQuery["Container"]> {
    return JSON.parse(await this.client.getDishOfTheDayContainer(region, edition));
  }

  async getCuratedCollection(
    collectionId: CuratedContainerByIdQueryVariables["collectionId"],
  ): Promise<CuratedContainerByIdQuery["curatedContainerById"]> {
    return JSON.parse(await this.client.getCuratedCollection(collectionId));
  }

  async faciaRecipeSearch(
    query: FaciaRecipeSearchQueryVariables["query"],
    celebrationId: string | null = null,
    dietId: string | null = null,
    uprateByDate: FaciaRecipeSearchQueryVariables["uprateByDate"] = null,
  ): Promise<FaciaRecipeSearchQuery["search"]> {
    return JSON.parse(await this.client.faciaRecipeSearch(query, celebrationId, dietId, uprateByDate));
  }

  async faciaChefSearch(
    limit: FaciaChefSearchQueryVariables["limit"],
    q: FaciaChefSearchQueryVariables["q"] = null,
  ): Promise<FaciaChefSearchQuery["chefs"]> {
    return JSON.parse(await this.client.faciaChefSearch(limit, q));
  }

  async getRecipeBatch(
    recipeIds: Array<string> | null = null,
  ): Promise<GetRecipeBatchQuery["recipes"]> {
    return JSON.parse(await this.client.getRecipeBatch(recipeIds));
  }

  close(): void {
    this.client.close();
  }
}