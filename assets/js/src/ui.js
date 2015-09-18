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

    function makeBoard(board, callback) {
        var size = Math.sqrt(board.length);
        var slots = makeSlots(board, size);
        $('[data-game]').html(slots);
        $('[data-position]').on('click', callback);
    }

    function update(status, board, callback) {
        if (board)
            makeBoard(board, callback);
        if (status.match(/X|O|tie/)) {
            var team = status == 'tie' ? 'nobody' : status;
            $('.message').text(team + ' won!');
            $('.game').fadeOut();
            $('.start-buttons').fadeIn();
        }
    }

    function create(mark) {
        $('.start-buttons').hide();
        $('.message').text('You are X');
        $('.game').empty().show();
    }

    function load(element, mark) {
        $(element).text(mark);
        $('.slots').attr('disabled', true);
    }

    this.update = update;
    this.create = create;
    this.load = load;
}
