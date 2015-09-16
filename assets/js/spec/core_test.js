describe('New game', function() {
    var firstNaiveMove = {
        'status': 200,
        'responseText': '{"board": "OX-------"}'
    };

    function testButtonSetup(size, playerId) {
        var board = "";
        for (var i = 0; i < size * size; i++)
            board += '-';

        appendSetFixtures('<input data-size="' + size + '"></input>');
        appendSetFixtures('<input data-vs="naive"></input>');
        game.start();
        jasmine.Ajax.requests.mostRecent().respondWith({
            'status': 200,
            'responseText': '{"player-id":' + playerId +
                ', "board":"' + board + '"}'
        });
    };

    beforeEach(function() {
        jasmine.Ajax.install();
        setFixtures('<div data-game></div>');
    });
    afterEach(function() { jasmine.Ajax.uninstall(); });

    it('creates buttons based on JSON response', function() {
        testButtonSetup(3, "12345");
        expect($('[data-position]').length).toBe(9);
        testButtonSetup(5, "12345");
        expect($('[data-position]').length).toBe(25);
    });

    it('creates buttons with proper links', function() {
        testButtonSetup(3, "12345");
        $('[data-position=1]').trigger('click');
        var mostRecent = jasmine.Ajax.requests.mostRecent();
        expect(mostRecent.url).toMatch(/^move\?/);
        expect(mostRecent.url).toMatch(/position=1/);
        expect(mostRecent.url).toMatch(/player-id=12345/);
        mostRecent.respondWith(firstNaiveMove);
        expect($('[data-position=1]').length).toBe(0);
        expect($('[data-position=2]').length).toBe(1);
        expect($('[disabled]').length).toBe(2);
    });

    it('properly responds to successive games', function() {
        testButtonSetup(3, "12345");
        testButtonSetup(3, "23456");
        $('[data-position=1]').trigger('click');
        jasmine.Ajax.requests.mostRecent().respondWith(firstNaiveMove);
        expect($('[data-player-id]').val()).toBe("23456");
    });

    it('is joined by join', function() {
        var fixtures = '<div data-game></div>';
        setFixtures(fixtures);
        game.join();
        expect(jasmine.Ajax.requests.mostRecent().url).toBe('/join');
        jasmine.Ajax.requests.mostRecent().respondWith({
            'status': 200,
            'responseText': '{"player-id": 12345, "board":"----X----"}'
        });
        expect($('[data-position]').length).toBe(8);
        expect($('[disabled]').length).toBe(1);
    });
});
