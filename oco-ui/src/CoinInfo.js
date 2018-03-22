import React, { Component } from 'react';
import { TickerConsumer } from './context/TickerContext';
import { Button, Header, Icon, Grid, Statistic } from 'semantic-ui-react';

export default class CoinInfo extends Component {
  render() {
    const coin = this.props.coin;
    return (
      <TickerConsumer>
        { ticker => (
            <Grid>
              <Grid.Row divided columns={3}>
                <Grid.Column>
                  <Header as='h2'>
                    <Icon name='bitcoin' />
                    {coin.name}
                  </Header>
                </Grid.Column>
                <Grid.Column>
                  <Button onClick={this.props.onToggleChart}>
                    <Icon name="line chart"/>
                    Show chart
                  </Button>
                </Grid.Column>
                <Grid.Column>
                  <Button onClick={this.props.onRemove}>Remove coin</Button>
                </Grid.Column>
              </Grid.Row>
              <Grid.Row divided columns={4}>
                <Grid.Column>
                  <Statistic size="tiny">
                    <Statistic.Value>{ticker.last}</Statistic.Value>
                    <Statistic.Label>{coin.counter}</Statistic.Label>
                  </Statistic>
                </Grid.Column>
                <Grid.Column>
                  {'Bid: ' + ticker.bid}
                  <br/>
                  {'Last: ' + ticker.last}
                  <br/>
                  {'Ask: ' + ticker.ask}
                </Grid.Column>
                <Grid.Column>
                  {'High: ' + ticker.high}
                  <br/>
                  {'Open: ' + ticker.open}
                  <br/>
                  {'Low: ' + ticker.low}
                </Grid.Column>
                <Grid.Column>
                  {'Vol: ' + ticker.volume}
                </Grid.Column>
              </Grid.Row>
            </Grid>
        )}
      </TickerConsumer>
    );
  }
}