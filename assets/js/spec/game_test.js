var requests = jasmine.Ajax.requests;
var PID = 12345;
var GID = 77777;

function makeResponse(object) {
    return {
        'status': 200,
        'responseText': JSON.stringify(object)
    }
}

function makeStartResponse(playerId, gameId, mark) {
    return makeResponse({
        'player-id':playerId, 'game-id': gameId, 'mark': mark});
}

function makeStatusResponse(board, status) {
    return makeResponse({'board': board, 'status': status});
}

describe('New game', function() {
    function testButtonSetup(size, playerId, gameId) {
        var board = "";
        for (var i = 0; i < size * size; i++)
            board += '-';

        appendSetFixtures('<input data-size="' + size + '"></input>');
        appendSetFixtures('<input data-vs="naive"></input>');
        game.start();
        var response = makeStartResponse(playerId, gameId, 'X');
        requests.mostRecent().respondWith(response);
        requests.mostRecent().respondWith(makeStatusResponse(board, "ready"));
    }

    beforeEach(function() {
        jasmine.Ajax.install();
        setFixtures('<div data-game></div>');
    });
    afterEach(function() {jasmine.Ajax.uninstall()});

    it('creates buttons and data based on JSON response', function() {
        testButtonSetup(3, PID, GID);
        expect($('[data-position]').length).toBe(9);
        testButtonSetup(5, PID, GID);
        expect($('[data-position]').length).toBe(25);

        expect(game.gameId).toBe(GID);
        expect(game.playerId).toBe(PID);
        expect(game.mark).toBe('X');
    });

    it('creates buttons with proper links', function() {
        testButtonSetup(3, PID, GID);
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
        testButtonSetup(3, PID, GID);
        testButtonSetup(3, 23456, 66666);

        $('[data-position=1]').trigger('click');
        var firstNaiveMove = makeStatusResponse('XO-------', 'ready');
        requests.mostRecent().respondWith(firstNaiveMove);
        expect(game.playerId).toBe(23456);
        expect(game.gameId).toBe(66666);
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
        var response = makeStartResponse(PID, GID, 'X');
        requests.mostRecent().respondWith(response);
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
