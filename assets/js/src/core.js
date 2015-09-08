function getNewGameUri() {
    return "new?" +
        'size=' + $('[name=size]').val() + '&' +
        'vs=' + $('[name=vs]').val();
}

function makeButtons(json) {
    var board = json['board'];
    var size = Math.sqrt(board.length);
    var html = '';
    for (var i = 0; i < size; i++) {
        for (var j = 0; j < size; j++) {
            html += '<button class="move">' + (i*size + j) + '</button>';
        }
        html += '<br>';
    }
    $('.game').html(html);
}

function setupNewGame(uri) {
    $.getJSON(uri, makeButtons);
}

$('[name=new]').on('click', function() { setupNewGame(getNewGameUri()); });
