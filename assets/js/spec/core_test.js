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
        expect($('[data-position]').length).toBe(9);
        testButtonSetup(5);
        expect($('[data-position]').length).toBe(25);
    });

    it('creates buttons with proper links', function() {
        testButtonSetup(3);
        $('[data-position=1]').trigger('click');
        expect(jasmine.Ajax.requests.mostRecent().url).toBe("move?position=1");
        jasmine.Ajax.requests.mostRecent().respondWith({
            'status': 200,
            'responseText': '{"board": "OX-------"}'
        });
        expect($('[data-position=1]').length).toBe(0);
        expect($('[data-position=2]').length).toBe(1);
        expect($('[disabled]').length).toBe(2);
    });

    function testButtonSetup(size) {
        setFixtures('<div data-game></div>');
        var board = "";
        for (var i = 0; i < size * size; i++)
            board += '-';

        requestBoard('/new?size=' + size + '&vs=naive');
        jasmine.Ajax.requests.mostRecent().respondWith({
            'status': 200,
            'responseText': '{"board": "' + board + '"}'
        });
    };
});
