var requests = jasmine.Ajax.requests;
function makeResponse(playerId, board, status) {
    var response = {'player-id':playerId, 'board':board, 'status': status};
    return {
        'status': 200,
        'responseText': JSON.stringify(response)
    }
}

describe('New game', function() {
    function testButtonSetup(size, playerId) {
        var board = "";
        for (var i = 0; i < size * size; i++)
            board += '-';

        appendSetFixtures('<input data-size="' + size + '"></input>');
        appendSetFixtures('<input data-vs="naive"></input>');
        game.start();
        var responseText = {'player-id': playerId, 'board': board};
        requests.mostRecent().respondWith({
            'status': 200,
            'responseText': JSON.stringify(responseText)
        });
    }

    beforeEach(function() {
        jasmine.Ajax.install();
        setFixtures('<div data-game></div>');
    });
    afterEach(function() {jasmine.Ajax.uninstall()});

    it('creates buttons based on JSON response', function() {
        testButtonSetup(3, "12345");
        expect($('[data-position]').length).toBe(9);
        testButtonSetup(5, "12345");
        expect($('[data-position]').length).toBe(25);
    });

    it('creates buttons with proper links', function() {
        testButtonSetup(3, "12345");
        $('[data-position=1]').trigger('click');
        var mostRecent = requests.mostRecent();
        expect(mostRecent.url).toMatch(/^move\?/);
        expect(mostRecent.url).toMatch(/position=1/);
        expect(mostRecent.url).toMatch(/player-id=12345/);

        var firstNaiveMove = makeResponse(12345, 'XO-------', 'ready');
        mostRecent.respondWith(firstNaiveMove);
        expect($('[data-position=1]').length).toBe(0);
        expect($('[data-position=2]').length).toBe(1);
        expect($('[disabled]').length).toBe(2);
    });

    it('properly responds to successive games', function() {
        testButtonSetup(3, "12345");
        testButtonSetup(3, "23456");

        $('[data-position=1]').trigger('click');
        var firstNaiveMove = makeResponse(23456, 'XO-------', 'ready');
        requests.mostRecent().respondWith(firstNaiveMove);
        expect($('[data-player-id]').val()).toBe("23456");
    });
});

describe('Game against remote opponent', function() {
    beforeEach(function() {
        jasmine.Ajax.install();
        setFixtures('<div data-game></div>');
    });
    afterEach(function() {jasmine.Ajax.uninstall()});

    it('is joined by join', function() {
        game.join();
        expect(requests.mostRecent().url).toBe('/join');

        var firstJoinMove = makeResponse(12345, 'X--------', 'ready');
        requests.mostRecent().respondWith(firstJoinMove);
        expect($('[data-position]').length).toBe(8);
        expect($('[disabled]').length).toBe(1);
    });

    it('keeps trying to connect when waiting', function() {
        game.join();
        var noJoinMove = makeResponse(12345, '---------', 'waiting');
        for (var i = 0; i < 5; i++) {
            requests.mostRecent().respondWith(noJoinMove);
            expect($('[data-status]').val()).toBe('waiting');
            expect(requests.mostRecent().url).toBe('/status?player-id=12345');
        }

        var firstJoinMove = makeResponse(12345, 'X--------', 'ready');
        requests.mostRecent().respondWith(firstJoinMove);
        expect($('[data-position]').length).toBe(8);
        expect($('[disabled]').length).toBe(1);
    });
});
