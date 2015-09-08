function getNewGameUri() {
    return "new?" +
        'size=' + $('[name=size]').val() + '&' +
        'vs=' + $('[name=vs]').val();
}

function makeSlot(slot, n) {
    if (slot == '-')
        return '<button class="move" id="' + n + '">' + n + '</button>';
    else
        return '<button disabled>' + slot + '</button>';
}

function makeSlots(board, size) {
    var html = '';
    for (var i = 0; i < size; i++) {
        for (var j = 0; j < size; j++) {
            var n = (i*size + j);
            html += makeSlot(board[n], n);
        }
        html += '<br>';
    }
    return html;
}

function move() {
    var uri = 'move?position=' + $(this).attr('id');
    requestBoard(uri);
}

function makeBoard(json) {
    var board = json['board'];
    var size = Math.sqrt(board.length);
    $('.game').html(makeSlots(board, size));
    $('.move').on('click', move);
}

function requestBoard(uri) { $.getJSON(uri, makeBoard); }
function makeNewGame() { requestBoard(getNewGameUri()); }
$('[name=new]').on('click', makeNewGame);
