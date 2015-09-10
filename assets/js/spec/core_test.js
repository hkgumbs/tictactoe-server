describe('Url creation', function() {
    it('builds request correctly for /new', function() {
        var fixture =
            '<input type="number" name="size" value="4">' +
            '<select name="vs"><option value="minimax"></option></select>';
        setFixtures(fixture);
        expect(getNewGameUri()).toBe('new?size=4&vs=minimax');
    });
});
describe('App', function() {
    var firstNaiveMove = {
        'status': 200,
        'responseText': '{"board": "OX-------"}'
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
        var url = "move?position=1&player-id=12345";
        expect(jasmine.Ajax.requests.mostRecent().url).toBe(url);
        jasmine.Ajax.requests.mostRecent().respondWith(firstNaiveMove);
        expect($('[data-position=1]').length).toBe(0);
        expect($('[data-position=2]').length).toBe(1);
        expect($('[disabled]').length).toBe(2);
    });

    it('proberly responds to successive games', function() {
        testButtonSetup(3, "12345");
        testButtonSetup(3, "23456");
        $('[data-position=1]').trigger('click');
        jasmine.Ajax.requests.mostRecent().respondWith(firstNaiveMove);
        expect($('[data-player-id]').val()).toBe("23456");
    });

    function testButtonSetup(size, playerId) {
        var board = "";
        for (var i = 0; i < size * size; i++)
            board += '-';

        requestBoard('/new?size=' + size + '&vs=naive');
        jasmine.Ajax.requests.mostRecent().respondWith({
            'status': 200,
            'responseText': '{"player-id":' + playerId + ', "board":"' +
                board + '"}'
        });
    };
});
