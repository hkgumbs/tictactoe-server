describe("App", function() {
    it("builds request correctly for /new", function() {
        var fixture =
            '<input type="number" name="size" value="4">' +
            '<select name="vs"><option value="minimax"></option></select>';
        setFixtures(fixture);
        expect(getNewGameUri()).toBe("/new?size=4&vs=minimax");
    });
});
