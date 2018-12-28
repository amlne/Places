var accountRoute = 'ws/app/accounts/';

var subsTemplate = _.template($('#subs-template').html());
var foloTemplate = _.template($('#folo-template').html());

//barre de recherche
var friendResultSearchTemplate = _.template($('#friendResult-template').html());

function subsListFactory(result) {
    var subs = $("#subs");
    subs.append('<h4>Subscribers list</h4>');
    var item = result.substring(1, result.length - 1);
    item = item.split(',');

    for (var element in item) {
        var html = subsTemplate({
            'name': item[element].substring(1, item[element].length - 1),
            'userSubs': item[element].substring(1, item[element].length - 1)
        });
        subs.append(html);
    }
}

function foloListFactory(result) {
    var folo = $("#folo");
    folo.append('<h4>Followers list</h4>');
    var item = result.substring(1, result.length - 1);
    item = item.split(',');

    for (var element in item) {
        var html = foloTemplate({
            'name': item[element].substring(1, item[element].length - 1),
            'userFolo': item[element].substring(1, item[element].length - 1)
        });
        folo.append(html);
    }
}

function getServerFriendSearch(url, successSub, successFolo) {

    if (localStorage.getItem("token") == null) {
        window.location = 'login.html';
    } else {
        $.ajax({
            dataType: 'text',
            headers: {
                'Authorization': 'Basic ' + localStorage.getItem("token")
            },
            url: url + getUser() + '/subscriptions',
            success: successSub,
            error: function () {
                var subs = $("#subs");
                subs.append('<h4>No subscribers</h4>');
            }
        });

        $.ajax({
            dataType: 'text',
            headers: {
                'Authorization': 'Basic ' + localStorage.getItem("token")
            },
            url: url + getUser() + '/followers',
            success: successFolo,
            error: function () {
                var subs = $("#subs");
                subs.append('<h4>No followers</h4>');
            }
        });
    }

}

function deleteFriend(value) {
    var res = value.nextElementSibling.id;

    if (localStorage.getItem("token") == null) {
        window.location = 'login.html';
    } else {
        $.ajax({
            type: 'DELETE',
            dataType: 'html',
            headers: {
                'Authorization': 'Basic ' + localStorage.getItem("token")
            },
            url: accountRoute + res + '/removeAccess/' + getUser(),
            success: function () {
                location.reload(true);
            },
            error: function (value) {
            }
        });
    }
}

function switchToUser(value) {
    window.localStorage["searchedUser"] = value;
    window.location = 'user.html';
}

function getUser() {
    var token = atob(localStorage.getItem("token"));
    token = token.split(":");
    return token[0];
}

$(function () {
    $(document).ready(function () {
        getServerFriendSearch(accountRoute, subsListFactory, foloListFactory);
    });

    $(document).on('click', '.friend', function () {
        switchToUser(this.id);
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