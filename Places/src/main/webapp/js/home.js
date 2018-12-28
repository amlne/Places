var mapsRoute = '/ws/app/maps/';
var accountRoute = 'ws/app/accounts/';

var mapsTemplate = _.template($('#map-template').html());
var emptyTemplate = _.template($('#empty-map').html());
var searchResultTemplate = _.template($('#search-result-template').html());

function postMapData(url, data, success) {
    if (localStorage.getItem("token") == null)
        window.location = 'login.html';
    else {
        $.ajax({
            dataType: 'json',
            data: JSON.stringify(data),
            headers: {
                'Authorization': 'Basic ' + localStorage.getItem("token")
            },
            url: url,
            type: 'POST',
            contentType: 'application/json',
            success: success
        });
    }
}

function getMapData(url, success, error) {
    if (localStorage.getItem("token") == null)
        window.location = 'login.html';
    else {
        $.ajax({
            dataType: 'json',
            headers: {
                'Authorization': 'Basic ' + localStorage.getItem("token")
            },
            url: url,
            success: success,
            error: error
        });
    }
}

function deleteMapData(url, success) {
    if (localStorage.getItem("token") == null)
        window.location = 'login.html';
    else {
        $.ajax({
            dataType: 'json',
            headers: {
                'Authorization': 'Basic ' + localStorage.getItem("token")
            },
            url: url,
            type: 'DELETE',
            contentType: 'application/json',
            success: success,
            error: function (value) {
            }
        });
    }
}

function getSearchData(url, success) {
    if (localStorage.getItem("token") == null)
        window.location = 'login.html';
    else {
        $.ajax({
            dataType: 'text',
            headers: {
                'Authorization': 'Basic ' + localStorage.getItem("token")
            },
            url: url,
            success: success
        });
    }
}

function activeNavItem(value) {
    if (value === '#my-maps') {
        $(value).addClass('active active-nav');
        $('#shared-maps').removeClass('active active-nav');
    } else if (value === '#shared-maps') {
        $(value).addClass('active active-nav');
        $('#my-maps').removeClass('active active-nav');
    }
}

function boxFactory(result) {
    var boxLine = $('#box-line');
    boxLine.html('');

    for (var item in result) {
        var html = mapsTemplate({
            'mapId': result[item].mapId,
            'name': result[item].mapName,
            'author': result[item].owner,
            'date': result[item].creationDate
        });
        boxLine.append(html);
    }
}

function boxEmpty() {
    var boxLine = $('#box-line');
    boxLine.html('');
    boxLine.append(emptyTemplate());
}

function switchToHome() {
    window.location = 'home.html';
}

function goMap(id) {
    localStorage.setItem("currentMap", id);
    window.location = 'mapView.html';
}

function getUser() {
    var token = atob(localStorage.getItem("token"));
    token = token.split(":");
    return token[0];
}

$(function () {
    $(document).ready(function () {
        activeNavItem('#my-maps');
        getMapData(mapsRoute + getUser(), boxFactory, boxEmpty());
    });

    $('#my-maps').click(function () {
        activeNavItem('#my-maps');
        getMapData(mapsRoute + getUser(), boxFactory);
    });

    $('#shared-maps').click(function () {
        activeNavItem('#shared-maps');
        getMapData(mapsRoute + getUser() + "/sharedMaps", boxFactory);
    });

    $('#add-map-modal').on('hidden.bs.modal', function () {
        $('#add-map-form').find('input:text, input[type=date]').val('');
        $('#default-check').prop("checked", true);
    });

    $('#create-map-btn').click(function () {
        var map = {
            mapName: $("#name").val(),
            creationDate: $("#date").val(),
            status: $("[name='status']:checked").val()
        };
        postMapData(mapsRoute + getUser() + "/add", map, location.reload(true))
    });

    $('.dis').click(function () {
        localStorage.removeItem("token");
        window.location = 'login.html';
    });

    $('#tags').click(function () {
        addTag(event);
    });

    $('.tags').click(function () {
        removeTag(this);
    });

    $('#search-btn').click(function () {
        var regex = $('#search-bar').val();
        getSearchData(accountRoute + regex + "/search", function () {
            localStorage.setItem('searchedUser', regex);
            window.location = 'user.html';
        });
    });

    $('#box-line').on('click', '.remove-map-btn', function () {
        var mapIdToDelete = $(this).next().attr('id');
        deleteMapData(mapsRoute + mapIdToDelete, switchToHome)
    });
});

function searchResultFactory(result) {
    $("#result").html = '';
    for (var item in result) {
        var html = searchResultTemplate({
            'result': result[item].result
        });
        $("#result").append(html);
    }
}
