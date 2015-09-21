function UI() {
    var self = this;
    var maxWaits = 3;
    self.waitingCount = 0;

    function informUserWaiting() {
        self.waitingCount++;
        if (self.waitingCount > maxWaits) {
            $('.message').text('Waiting on your opponent...');
        }
    }

    function resetUserWaiting() {
        self.waitingCount = 0;
        $('.message').text('Your turn');
    }

    function resetGame() {
        $('.game').fadeOut();
        $('.start-buttons').fadeIn();
    }

    function makeStatus(status) {
        self.waiting = status == 'waiting';
        if (status.match(/X|O|tie/)) {
            self.winner = status == 'tie' ? 'nobody' : status;
            $('.message').text(self.winner + ' won!');
            $('.slot').attr('disabled', true);
            setTimeout(resetGame, 1500);
        } else if (self.waiting)
            informUserWaiting();
        else
            resetUserWaiting();
    }

    function styleSlots(size) {
        var px = parseInt($('.content').css('height'));
        var dimen = px / size;
        $('.slot').css('width', dimen + 'px');
        $('.slot').css('height', dimen + 'px');
    }

    function setListeners(callback) {
        if (self.waiting)
            $('[data-position]').attr('disabled', true);
        $('[data-position]:not([disabled])').on('click', callback);
    }

    function makeSlot(slot, n) {
        var position = 'data-position="' + n + '"';
        var annotation = (slot == '-') ? position : 'disabled'
        var prepend = '<button class="slot"' + '';
        return prepend + annotation + '>' + slot + '</button>';
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
        setListeners(callback);
        styleSlots(size);
        self.lastBoard = board;
    }

    function update(status, board, callback) {
        makeStatus(status);
        if (board && board != self.lastBoard)
            makeBoard(board, callback);
    }

    function create(mark) {
        $('.start-buttons').hide();
        $('.message').text('You are ' + mark);
        $('.game').empty().show();
    }

    function load(element, mark) {
        $(element).text(mark);
    }

    this.update = update;
    this.create = create;
    this.load = load;
}
