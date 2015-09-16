function Game() {
    function getParameter(name) {
        return name + '=' + $('[data-' + name + ']').val();
    }

    function getNewGameUri() {
        return "/new?" + getParameter('size') + '&' + getParameter('vs');
    }

    function makeSlot(slot, n) {
        var position = 'data-position="' + n + '"';
        var annotation = (slot == '-') ? position : 'disabled'
        return '<button ' + annotation + '></button>';
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

    function makePlayerId(json) {
        var playerId = json['player-id'] ?
            json['player-id'] : $('[data-player-id]').val();
        return '<input type="hidden" value="' + playerId +
            '" data-player-id></input>';
    }

    function move() {
        var uri = 'move?' + getParameter('player-id') +
            '&position=' + $(this).data('position');
        $.getJSON(uri, create);
    }

    function create(json) {
        var board = json['board'];
        var size = Math.sqrt(board.length);
        var playerId = makePlayerId(json);
        var board = makeSlots(board, size);
        $('[data-game]').html(board + playerId);
        $('[data-position]').on('click', move);
    }

    this.start =  function() { $.getJSON(getNewGameUri(), create) };
    this.join = function() { $.getJSON('/join', create) };
}

var game = new Game();
$('[data-new]').on('click', game.start);
$('[data-join]').on('click', game.join);
