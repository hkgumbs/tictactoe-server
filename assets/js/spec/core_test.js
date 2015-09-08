describe('App', function() {
    beforeEach(function() { jasmine.Ajax.install(); });
    afterEach(function() { jasmine.Ajax.uninstall(); });

    it('builds request correctly for /new', function() {
        var fixture =
            '<input type="number" name="size" value="4">' +
            '<select name="vs"><option value="minimax"></option></select>';
        setFixtures(fixture);
        expect(getNewGameUri()).toBe('new?size=4&vs=minimax');
    });

    it('creates buttons based on JSON response', function() {
        testButtonSetup(3);
        expect($('.move').length).toBe(9);
        testButtonSetup(5);
        expect($('.move').length).toBe(25);
    });

    function testButtonSetup(size) {
        setFixtures('<div class="game"></div>');
        var board = "";
        for (var i = 0; i < size * size; i++)
            board += '-';

        setupNewGame('/new?size=' + size + '&vs=minimax');
        jasmine.Ajax.requests.mostRecent().respondWith({
            'status': 200,
            'responseText': '{"board": "' + board + '"}'
        });
    };
});
