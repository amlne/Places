var registerRoute = '/ws/register';
var checkUsernameRoute = '/ws/register/checkUsername/';
var displayed = false;
var logTemplate = _.template($('#log-template').html());

function postRegisterServerData(url, data) {
    $.ajax({
        dataType: 'json',
        data: JSON.stringify(data),
        url: url,
        type: 'POST',
        contentType: 'application/json',
        success: function () {
            window.localStorage["token"] = btoa($("#username-register").val() + ':' + $("#password-register").val());
            switchToHome();
        },
        error: errorLoginRegister
    });
}

function getLoginServerData(url) {
    localStorage.clear();

    $.ajax({
        headers: {
            'Authorization': 'Basic ' + btoa($("#username-login").val() + ':' + $("#password-login").val())
        },
        url: url + $("#username-login").val(),
        type: 'GET',
        contentType: 'application/json',
        success: function () {
            window.localStorage["token"] = btoa($("#username-login").val() + ':' + $("#password-login").val());
            switchToHome();
        },
        error: errorLoginRegister
    });
}

function errorLoginRegister(data) {
    $('#result').html('');

    var html = logTemplate({
        'value': 'Identifiants introuvables, vérifiez les'
    });
    $('#result').append(html);
}

function switchToHome() {
    window.location = 'home.html';
}

function passwordVerification() {
    $('#result').html('');

    if ($("#password-register").val() === $("#confirm-password").val())
        return true;
    else {
        var html = logTemplate({
            'value': 'Password non identique'
        });
        $('#result').append(html);

        return false;
    }
}

function getCheckData(name) {
    $.ajax({
        url: checkUsernameRoute + name,
        type: 'GET',
        dataType: 'html',
        success: function () {
            $('#username-register').css('border', 'thick solid #b32442');
        },
        error: function () {
            $('#username-register').css('border', '');
        }
    });
}

$(function () {
    $('#login-form-link').click(function (e) {
        $("#login-form").delay(100).fadeIn(100);
        $("#register-form").fadeOut(100);
        $('#register-form-link').removeClass('active');
        $(this).addClass('active');
        e.preventDefault();
    });

    $('#register-form-link').click(function (e) {
        $("#register-form").delay(100).fadeIn(100);
        $("#login-form").fadeOut(100);
        $('#login-form-link').removeClass('active');
        $(this).addClass('active');
        e.preventDefault();
    });

    $('#register-submit').click(function () {
        var person = {
            email: $("#email-register").val(),
            password: $("#password-register").val(),
            username: $("#username-register").val()
        };
        if (passwordVerification())
            postRegisterServerData(registerRoute, person);
    });

    $('#login-submit').click(function () {
        getLoginServerData(checkUsernameRoute);
    });

    $('#username-register').blur(function () {
        getCheckData($('#username-register').val());
    });

    $('#login').click(function () {
        if (!displayed) {
            $('#loginForm').css('width', '100%');
            displayed = !displayed;
            $('#loginPanel').css('display', 'block');
        } else {
            $('#loginForm').css('width', '0%');
            displayed = !displayed;
            $('#loginPanel').css('display', 'none');
        }
    });
});
