/// <reference path="../typings/index.d.ts" />
///<reference path='../node_modules/immutable/dist/immutable.d.ts'/>

import {Reducer, combineReducers} from 'redux';
import * as Immutable from 'immutable';
import {
  ICounterAction, ACTION, IFetchOperationAction, FetchOperations, IFetchStepsAction, FetchSteps,
} from './actions';
import {IOperationData, IStepsData} from './components/app';

function counters(state: Immutable.List<number> = Immutable.List.of(0, 0, 0),
                  action: ICounterAction): Immutable.List<number> {
  console.log('Counters', state);
  console.log('action', action);
  switch (action.type) {
    case ACTION.IncrementCounter:
      return state.set(action.counterId, state.get(action.counterId) + 1);

    case ACTION.DecrementCounter:
      return state.set(action.counterId, state.get(action.counterId) - 1);

    case ACTION.AddCounter:
      return state.push(0);

    default:
      return state;
  }
}

function operationData(
  state: IOperationData = Immutable
    .fromJS({didInvalidate: false, isFetching: false, operations: []}),
  action: IFetchOperationAction): IOperationData {
  console.log('Operation Data', state);
  console.log('action', action);
  switch (action.type) {
    case FetchOperations.Request:
      return Object.assign({}, state, {isFetching: true});
    case FetchOperations.Success:
      return Object.assign({}, state, {isFetching: false, operations: action.response});
    default:
      return state;
  }
}

function stepData(
  state: IStepsData = Immutable
    .fromJS({didInvalidate: false, isFetching: false, steps: Immutable.List()}),
  action: IFetchStepsAction): IStepsData {

  switch (action.type) {
    case FetchSteps.Request:
      return Object.assign({}, state, {isFetching: true});
    case FetchSteps.Success:
      return Object.assign({}, state, {isFetching: false, steps: action.response});
    default:
      return state;
  }
}

export const counterApp: Reducer = combineReducers({counters, operationData, stepData});