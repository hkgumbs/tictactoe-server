function Game() {
    var self = this;

    function getParameter(name) {
        return name + '=' + $('[data-' + name + ']').val();
    }

    function getNewGameUri() {
        return "new?" + getParameter('size') + '&' + getParameter('vs');
    }

    function makeSlot(slot, n) {
        var position = 'data-position="' + n + '"';
        var annotation = (slot == '-') ? position : 'disabled'
        return '<button ' + annotation + '>' + slot + '</button>';
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

    function update(json) {
        var board = json['board'];
        var size = Math.sqrt(board.length);
        var slots = makeSlots(board, size);
        $('[data-game]').html(slots);
        $('[data-position]').on('click', move);
    }

    function getIdParameters() {
        return 'player-id=' + self.playerId + '&game-id=' + self.gameId;
    }

    function listenForStatusChange (json) {
        self.status = json['status'];
        if (self.status && self.status != 'waiting')
            update(json);
        else {
            var uri = 'status?' + getIdParameters();
            $.getJSON(uri, listenForStatusChange)
        }
    }

    function move() {
        var uri = 'move?' + getIdParameters() +
            '&position=' + $(this).data('position');
        $.getJSON(uri, listenForStatusChange);
    }

    function create(json) {
        self.gameId = json['game-id'];
        self.playerId = json['player-id'];
        self.mark = json['mark'];
        listenForStatusChange(json);
    }

    this.start =  function() { $.getJSON(getNewGameUri(), create) };
    this.join = function() { $.getJSON('/join', create) };
}

var game = new Game();
$('[data-new]').on('click', game.start);
$('[data-join]').on('click', game.join);
