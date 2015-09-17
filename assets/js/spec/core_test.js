var requests = jasmine.Ajax.requests;

function makeResponse(object) {
    return {
        'status': 200,
        'responseText': JSON.stringify(object)
    }
}

function makeStartResponse(playerId) {
    return makeResponse({'player-id':playerId});
}

function makeStatusResponse(board, status) {
    return makeResponse({'board': board, 'status': status});
}

describe('New game', function() {
    function testButtonSetup(size, playerId) {
        var board = "";
        for (var i = 0; i < size * size; i++)
            board += '-';

        appendSetFixtures('<input data-size="' + size + '"></input>');
        appendSetFixtures('<input data-vs="naive"></input>');
        game.start();
        requests.mostRecent().respondWith(makeStartResponse(playerId));
        requests.mostRecent().respondWith(makeStatusResponse(board, "ready"));
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

        var firstNaiveMove = makeStatusResponse('XO-------', 'ready');
        mostRecent.respondWith(firstNaiveMove);
        expect($('[data-position=1]').length).toBe(0);
        expect($('[data-position=2]').length).toBe(1);
        expect($('[disabled]').length).toBe(2);
    });

    it('properly responds to successive games', function() {
        testButtonSetup(3, "12345");
        testButtonSetup(3, "23456");

        $('[data-position=1]').trigger('click');
        var firstNaiveMove = makeStatusResponse('XO-------', 'ready');
        requests.mostRecent().respondWith(firstNaiveMove);
        expect(game.playerId).toBe("23456");
    });
});

describe('Game against remote opponent', function() {
    var firstJoinMove = makeStatusResponse('X--------', "ready");
    beforeEach(function() {
        jasmine.Ajax.install();
        setFixtures('<div data-game></div>');
    });
    afterEach(function() {jasmine.Ajax.uninstall()});

    it('is joined by join', function() {
        game.join();
        expect(requests.mostRecent().url).toBe('/join');

        requests.mostRecent().respondWith(firstJoinMove);
        expect($('[data-position]').length).toBe(8);
        expect($('[disabled]').length).toBe(1);
    });

    it('keeps trying to connect when waiting', function() {
        game.join();
        requests.mostRecent().respondWith(makeStartResponse(12345));
        for (var i = 0; i < 5; i++) {
            var waitingResponse = makeStatusResponse('---------', 'waiting');
            requests.mostRecent().respondWith(waitingResponse);
            expect(game.status).toBe('waiting');
            expect(requests.mostRecent().url).toBe('/status?player-id=12345');
        }

        requests.mostRecent().respondWith(firstJoinMove);
        expect($('[data-position]').length).toBe(8);
        expect($('[disabled]').length).toBe(1);
    });
});
