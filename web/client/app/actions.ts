/// <reference path="../typings/index.d.ts" />

import {OperationDescription, DefaultApi} from 'grip-swagger';
import {Step} from 'grip-swagger';
export enum ACTION {
  IncrementCounter = 1,
  DecrementCounter = 2,
  AddCounter = 3
}

export class FetchOperations {
  public static Request = 'Request Fetch Operations';
  public static Failure = 'Failure Fetch Operations';
  public static Success = 'Success Fetch Operations';
}

export class FetchSteps {
  public static Request = 'Request Fetch Steps';
  public static Failure = 'Failure Fetch Steps';
  public static Success = 'Success Fetch Steps';
}

export enum OPERATION_ACTION {
  Add = 7
}

const api: DefaultApi = new DefaultApi(undefined, 'http://localhost:3000/api');

export interface ICounterAction {
  type: ACTION;
  counterId?: number;
}

export interface IOperationAction {
  type: OPERATION_ACTION;
  name: string;
}


export interface IFetchOperationAction {
  type: string;
  error?: string;
  response?: OperationDescription[];
}

export interface IFetchStepsAction {
  type: string;
  error?: string;
  response?: Step[];
}

function requestSteps(): IFetchStepsAction {
  return {type: FetchSteps.Request};
}

function receiveSteps(steps: Step[]): IFetchStepsAction {
  return {response: steps, type: FetchSteps.Success};
}

export function createOperationStep(name: string): (dispatch: (event: any) => void) => Promise<any> {
  return function (dispatch: (event: any) => void): Promise<any> {
    dispatch(requestSteps());
    return api
      .stepsPut({operationName: name})
      .then((step: Step) => {
        return api.stepsGet({});
      })
      .then((steps: Step[]) => {
        console.log(steps);
        dispatch(receiveSteps(steps));
      })
      .catch(console.error);
  };
}

function requestOperations(): IFetchOperationAction {
  return {type: FetchOperations.Request};
}

function receiveOperations(operations: OperationDescription[]): IFetchOperationAction {
  return {response: operations, type: FetchOperations.Success};
}

export function fetchOperations(): (dispatch: (event: any) => void) => Promise<any> {
  return function (dispatch: (event: any) => void): Promise<any> {
    dispatch(requestOperations());
    return api
      .operationsGet()
      .then((operations: OperationDescription[]) => {
        dispatch(receiveOperations(operations));
      }).catch(console.error);
  };
}


export function incrementCounter(counterId: number): ICounterAction {
  return {type: ACTION.IncrementCounter, counterId};
}

export function decrementCounter(counterId: number): ICounterAction {
  return {type: ACTION.DecrementCounter, counterId};
}

export function addCounter(): ICounterAction {
  return {type: ACTION.AddCounter};
}
