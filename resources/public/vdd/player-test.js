var playerUpdateFn = vdd.player.createPlayerFn($("div#player"));

function onVizData(topic, data) {
  console.log("onVizData with " + data)
  playerUpdateFn(data, displayData);
}

vdd.wamp.connect(onVizData);

function displayData(data) {
  console.log("Display data with " + data)
  $("p#target").text(data.toString());
}