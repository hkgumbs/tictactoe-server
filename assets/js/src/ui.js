function UI() {
    function makeSlot(slot, n) {
        var position = 'data-position="' + n + '"';
        var annotation = (slot == '-') ? position : 'disabled'
        return '<button ' + annotation + '>' + slot + '</button>';
    }

    function makeSlots(board, size) {
        var html = '';
        for (var row = 0; row < size; row++) {
            for (var column = 0; column < size; column++) {
                var n = (row*size + column);
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

    function load(element) {
        $(element).text('...');
    }

    this.update = update;
    this.load = load;
}
