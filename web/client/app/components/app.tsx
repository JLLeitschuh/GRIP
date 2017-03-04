/// <reference path="../../typings/index.d.ts" />

import * as React from 'react';
import {connect} from 'react-redux';
import * as Immutable from 'immutable';
import {incrementCounter, decrementCounter, addCounter, createOperationStep} from '../actions';
import {CounterList} from './counter_list';
import {OperationDescription} from 'grip-swagger';
import {GripToolbar} from './GripToolbar';
import {OperationList} from './operation_list';
import {Grid, Row, Col} from 'react-flexbox-grid';
import {Step} from 'grip-swagger';

export interface IOperationData {
  isFetching: boolean;
  didInvalidate: boolean;
  operations: OperationDescription[];
}

export interface IStepsData {
  isFetching: boolean;
  didInvalidate: boolean;
  steps: Immutable.List<Step>;
}

interface IAppState {
  counters: Immutable.List<number>;
  operationData: IOperationData;
  stepData: IStepsData;
}

interface IAppProps {
  dispatch?: (func: any) => void;
  counters?: Immutable.List<number>;
  operationData?: IOperationData;
  stepData?: IStepsData;
}

function select(state: IAppState): IAppState {
  return Object.assign({}, state, {});
}

@connect(select)
export class App extends React.Component<IAppProps, {}> {
  public render(): React.ReactElement<{}> {
    const {dispatch, counters, operationData}: any = this.props;

    return (
      <div>
        <GripToolbar/>
        <Grid>
          <Row>
            <Col xs={0} sm={8}/>
            <Col xs={12} sm={4}>
              <OperationList operationData={operationData}
                             createOperation={(name: string) => dispatch(createOperationStep(name))}
              />
            </Col>
          </Row>
          <Row>
            <Col xs={12}>
            </Col>
          </Row>
        </Grid>
        <CounterList counters={counters}
                     increment={(index: number) => dispatch(incrementCounter(index))}
                     decrement={(index: number) => dispatch(decrementCounter(index))}
        />
        <button onClick={() => dispatch(addCounter())}>Add Counter</button>
      </div>
    );
  }
}
