var searchedUser = localStorage.getItem('searchedUser');
var mapsRoute = '/ws/app/maps/';
var accountRoute = 'ws/app/accounts/';
var mapsTemplate = _.template($('#map-template').html());
var friendResultSearchTemplate = _.template($('#friendResult-template').html());
$('#addAccess').click(function () {
    var user = atob(localStorage.getItem("token"));
    var cmp = user.split(":");

    var usernameLogin = cmp[0];
    postServerData(accountRoute + usernameLogin + "/addAccess/" + searchedUser, null, displaySuccess());

});

function displaySuccess(){
    alert('here');
    $('#addAccess').text = "success";
}

function lancement() {

    var user = atob(localStorage.getItem("token"));
    var cmp = user.split(":");

    var usernameLogin = cmp[0];
    getServerData(mapsRoute + searchedUser + "/" + usernameLogin + "/sharedMapsOfSomeone", boxFactory);
    //getServerData(accountRoute + usernameLogin + "/subscriptions", boxFactory);

}

function boxFactory(result) {
    var boxLine = $('#box-line');
    boxLine.html('');

    for (var item in result) {
        var html = mapsTemplate({
            'mapId': result[item].mapId,
            'name': result[item].mapName,
            'author': result[item].owner,
            'date': result[item].owner
        });
        boxLine.append(html);
    }
}

function postServerData(url, data, success) {

    if (localStorage.getItem("token") == null) {
        window.location = 'login.html';
    } else {

        $.ajax({
            dataType: 'json',
            data: JSON.stringify(data),
            headers: {
                'Authorization': 'Basic ' + localStorage.getItem("token")
            },
            url: url,
            type: 'POST',
            contentType: 'application/json',
            success: success,
            error: function (value) {
            }
        });

    }
}

function getServerData(url, success) {

    if (localStorage.getItem("token") == null) {
        window.location = 'login.html';
    } else {
        $.ajax({
            dataType: 'json',
            headers: {
                'Authorization': 'Basic ' + localStorage.getItem("token")
            },
            url: url,
            success: success,
            error: function () {
                /*                var boxLine = $('#box-line');
                                boxLine.html('');

                                var html = emptyTemplate();
                                boxLine.append(html);
                */
            }
        });
    }

}

$(function () {
    $(document).ready(function () {
        lancement()
    });
});
$('#search-btn').click(function () {
    var userToSearch = $('#input-search').val();
    getServerUserSearch(accountRoute, userToSearch, friendResultFactory)
});
function friendResultFactory(result) {
    var searchResult = $("#result");
    searchResult.html('');
    var html = friendResultSearchTemplate({
        'username':result
    })
    searchResult.append(html);
}
function getServerUserSearch(url,user,success) {
    if (localStorage.getItem("token") == null) {
        window.location = 'login.html';
    } else {
        $.ajax({
            dataType: 'text',
            headers: {
                'Authorization': 'Basic ' + localStorage.getItem("token")
            },
            url: url + user + '/search',
            success: success,
            error: function (value) {
            }
        });
    }
}