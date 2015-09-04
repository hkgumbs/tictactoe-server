function getNewGameUri() {
    return "/new?" +
        "size=" + $("[name=size]").val() + "&" +
        "vs=" + $("[name=vs]").val();
}
