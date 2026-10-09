import { FeastGraphQL as KotlinFeastGraphQL } from "./kotlin/feast-multiplatform-backend-api.mjs";
import type {
  CuratedContainerByIdQuery,
  CuratedContainerByIdQueryVariables,
  GetDishOfTheDayRecipeQuery,
  GetDishOfTheDayRecipeQueryVariables,
  GetFrontsByRegionQuery,
  GetFrontsByRegionQueryVariables,
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

  close(): void {
    this.client.close();
  }
}