function Displayer() {
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

    function update(json, callback) {
        var board = json['board'];
        var size = Math.sqrt(board.length);
        var slots = makeSlots(board, size);
        $('[data-game]').html(slots);
        $('[data-position]').on('click', callback);
    }

    this.update = update;
}
