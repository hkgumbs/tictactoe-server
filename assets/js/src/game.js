function Game() {
    var self = this;
    self.ui = new UI();

    function getParameter(name) {
        return name + '=' + $('[data-' + name + ']').val();
    }

    function getNewGameUri() {
        return "new?" + getParameter('size') + '&' + getParameter('vs');
    }

    function getIdParameters() {
        return 'player-id=' + self.playerId + '&game-id=' + self.gameId;
    }

    function listenForStatusChange (json) {
        self.status = json['status'];
        if (self.status && self.status != 'waiting')
            self.ui.update(json, move);
        else
            setTimeout(500, function() {
                var uri = 'status?' + getIdParameters();
                $.getJSON(uri, listenForStatusChange)
            });
    }

    function move() {
        self.ui.load(this, self.mark);
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
