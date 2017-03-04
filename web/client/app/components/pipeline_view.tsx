/// <reference path="../../typings/index.d.ts" />

import {IStepsData} from './app';
import * as React from 'react';
import {Step} from 'grip-swagger';
import {Row, Col} from 'react-flexbox-grid';


interface IPipelineProps {
  stepsData: IStepsData;
}


export class Pipeline extends React.Component<IPipelineProps, {}> {

  public render(): React.ReactElement<{}> {

    const steps: Immutable.List<Step> = this.props.stepsData.steps;
    return (
      <Row>
        {steps.map((step: Step) => {
          return (
            <div>
              <h3>{step.name}</h3>
            </div>
          );
        })}
      </Row>
    );
  }
}
