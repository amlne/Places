var infoWindow;
var directionsDisplay;
var directionsService;
var map;
var pos;

var latLng;
var TheMap;


/**
 * Permet de recevoir les données du serveur et en conséquence faire une action en cas de succés ou générer une erreur dans la console.
 * @param url: chemin d'où l'on souhaite les données.
 * @param success: success va s'executer si l'ajax s'est fait correctement.
 */

function getServerData(url, success) {

    if (localStorage.getItem("token") == null) {
        console.log(" token home redirection null");
        window.location = 'login.html';
    } else {
        $.ajax({
            dataType: 'json',
            headers: {
                'Authorization': 'Basic ' + localStorage.getItem("token")
            },
            url: url,
            success: success,
            error: function (value) {
                console.log(value);
            }
        });
    }

}

/**
 * Permet d'ajouter des tags dans au moment de créer une place.
 * @param elem: permet de check les tags.
 * @param event: listener sur les touches.
 */
function addTag(elem, event) {
    if (event.keyCode == 13 || event.keyCode == 32) {
        var newTag = document.createElement('span');
        var txt = document.createTextNode(elem.value);
        elem.value = '';
        if (elem.id == "tags") {
            var x = document.getElementById('tagsBox');
            newTag.setAttribute('class', 'badge badge-success tags');
        } else {
            var x = document.getElementById('placeTagsBox');
            newTag.setAttribute('class', 'badge badge-success placeTags');
        }
        newTag.appendChild(txt);
        newTag.setAttribute('onclick', 'removeTag(this)');
        newTag.setAttribute('title', 'delete');
        x.insertBefore(newTag, x.lastElementChild);
    }
}

/**
 * Permet de retirer un tag
 * @param tag: tag à retirer.
 */

function removeTag(tag) {
    tag.parentNode.removeChild(tag);
}

/**
 * Permet d'obtenir les coordonnées gps en fonction du clic.
 * @param mymap: current google map utilisé.
 */
function getlocation(mymap) {
    var latitude, longitude;
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function (position) {
            latitude = position.coords.latitude;
            longitude = position.coords.longitude;
            mymap.setView([latitude, longitude], 15);
        })
    }

}

/**
 *  fonction qui check le mot de passe
 */
function checkPsw() {
    var x = document.getElementById('passwordNew');
    var y = document.getElementById('passwordNewConf');
    var form = document.getElementById('changePswForm');
    if (x.value != y.value) {
        var span = document.createElement('span');
        var txt = document.createTextNode("Password doesn't match");
        span.setAttribute('id', 'errorMsg');
        span.setAttribute('class', 'text-danger');
        span.appendChild(txt);
        x.value = '';
        y.value = '';
        if (document.getElementById('errorMsg') == null) {
            form.insertBefore(span, form.lastElementChild.nextSibling);
        }
    }
}

/**
 *  Permet l'initialisation de la map afin d'intéragir avec.
 */

function initMap() {
    // Constructor creates a new map - only center and zoom are required.
    infoWindow = new google.maps.InfoWindow();
    directionsDisplay = new google.maps.DirectionsRenderer();
    directionsService = new google.maps.DirectionsService();

    map = new google.maps.Map(document.getElementById('map'), {
        center: {lat: 48.8589614, lng: 2.3471836},
        zoom: 13
    });

    directionsDisplay.setMap(map);

    function handleLocationError(browserHasGeolocation, infoWindow, pos) {
        infoWindow.setPosition(pos);
        infoWindow.setContent(browserHasGeolocation ?
            'Error: The Geolocation service failed.' :
            'Error: Your browser doesn\'t support geolocation.');
        infoWindow.open(map);
    }

    map.addListener('click', function (position) {
        pos = position;
        infoWindow.setPosition(position.latLng);
        infoWindow.setContent("<a id='addPlaceBtn' data-toggle='modal'" +
            " data-target='#addPlaceModal' href='#'>Create a new Place</a>");
        infoWindow.open(map);
    });

    google.maps.event.addListener(map, "click", function (event) {
        posLat = event.latLng.lat();
        posLng = event.latLng.lng();
    });

    showMark(map);
}

/**
 *  Fonction permettant d'afficher les marqueurs en fonction de la réponse de jQuery
 */

function showMark(map) {
    getServerData('/ws/app/place/' + localStorage.getItem("currentMap") + '/getPlacesOf', testmarks);
}

/**
 *  Permet d'obtenir les tags
 *  @param string: class de la balise html
 */

function getTags(string) {        //récupère la liste des tags de la map|place
    var taglist = document.getElementsByClassName(string);
    var tagString = "";
    for (var i = 0; i < taglist.length; i++) {
        tagString += " " + taglist[i].innerText;
    }
    return tagString;
}

/**
 *  Permet d'ajouter une place dans la base de données via ajax
 */

function addPlaceAjax() {

    var files = document.getElementById('placePictures').files;

    // Create a new FormData object.
    var formData = new FormData();
    // Loop through each of the selected files.
    for (var i = 0; i < files.length; i++) {
        var file = files[i];

        // Check the file type.
        if (!file.type.match('image.*')) {
            continue;
        }

        // Add the file to the request.
        formData.append('placePictures', file, file.name);
    }

    var placedata = {
        "mapId" : localStorage.getItem("currentMap"),
        "lat": posLat,
        "lng": posLng,
        "title":  document.getElementById('placeName').value,
        "comments": document.getElementById('PlaceComment').value,
        //"placeCategory": document.getElementById('placeCategory').value,
        "tags": getTags('placeTags')

    };

    formData.append("place", JSON.stringify(placedata));


    var marker = new google.maps.Marker({
        position: {lat: posLat, lng: posLng},
        map: TheMap,
        title: document.getElementById('placeName').value
    });



    $.ajax({
        data: formData,
        headers: {
            'Authorization': 'Basic ' + localStorage.getItem("token")
        },
        url: "ws/app/place/add",
        type: 'POST',
        cache : false,
        contentType : false,
        processData : false,
        success: function () {

            initMap();

        },
        error: function (value) {
            console.log("error: upload "+value);
        }
    });

    infoWindow.close();

}


/**
 *  Fonction affiche les marqueurs suite à la réponse de la fonction showMark()
 *  @param response: argument qui n'a pas besoin d'être fournit il se fait automatiquement en fonction de la réponse
 *  de la réponse ajax.
 */

function testmarks(response) {

    var infowindow = new google.maps.InfoWindow();

    var marker, i, mode;
    var listMarker = [];
    for (i = 0; i < response.length; i++) {
        if (response[i].lat != null) {
            var posMarker = {lat: Number(response[i].lat), lng: Number(response[i].lng)};
            listMarker[i] = posMarker;
            marker = new google.maps.Marker({
                position: {lat: Number(response[i].lat), lng: Number(response[i].lng)}, // la position est du type: {lat,lng}
                map: map,
                title: "Marker test"
            });

            google.maps.event.addListener(marker, 'click', (function (marker, i) {
                return function () {
                    infowindow.open(map, marker);
                    var stringPicture;
                    if (response[i].pictures != null) {
                        var strings = response[i].pictures.split(":");
                        var stringPicture = "";
                        var j;
                        for (j = 0; j < strings.length; j++) {
                            if (strings[j] != "null") {
                                var index = "img" + j;
                                stringPicture += "<img id=\"" + index + "\" src=\"" + "uploadspictures" + "/" + strings[j] + "\" height=\"100px\" width=\"100px\"> ";
                            }
                        }
                    }
                    else {
                        stringPicture = "";
                    }

                    infowindow.setContent("<br id=\"content\">" +
                        "<h1 >" + response[i].title + "</h1>" +
                        "<p>" + response[i].comments + "</p>" +
                        "<div>" +
                        stringPicture +
                        "</div>" +
                        "</br>" +
                        "Vous souhaitez y aller ? Dites nous juste comment: " +
                        "<select id=\"mode\">" +
                        "<option value=\"TRANSIT\">En transport</option>" +
                        "<option value=\"DRIVING\">En voiture</option>" +
                        "<option value=\"WALKING\">A pied</option>" +
                        "<option value=\"BICYCLING\">A vélo</option>" +
                        "</select> </br>" +
                        "<button id=\"clickme\" type=\"button\" class=\"btn btn-success btn-sm\">OK</button>" +
                        "<div id=itineraire></div>"
                    );


                    document.getElementById("mode").addEventListener('click', function () {
                        $("#clickme").prop("disabled", false);
                    });

                    document.getElementById("clickme").addEventListener('click', function () {
                        $(this).prop("disabled", true);
                        var e = document.getElementById("mode");
                        mode = e.options[e.selectedIndex].value;
                        chemin(listMarker[i], mode);
                    });
                }
            })(marker, i));
        }
    }
}

/**
 *  Fonction permettant de calculer l'itineraire et d'afficher le resultat sur la carte
 *  @param posMarker: la position du marqueur
 *  @param mode: mode de transport souhaité
 */

function chemin(posMarker, mode) {

    navigator.geolocation.getCurrentPosition(function (position) {
        var pos = {
            lat: position.coords.latitude,
            lng: position.coords.longitude
        };

        var origin = new google.maps.LatLng(pos.lat, pos.lng);
        var destination = new google.maps.LatLng(posMarker.lat, posMarker.lng);
        var service = new google.maps.DistanceMatrixService;

        service.getDistanceMatrix({
            origins: [origin],
            destinations: [destination],
            travelMode: mode,
        }, function (response, status) {
            if (status == 'OK') {
                var itineraireDiv = document.getElementById('itineraire');
                itineraireDiv.innerHTML = '';
                itineraireDiv.innerHTML += "Temps: " + response.rows[0].elements[0].duration.text + '<br>';
            }
        });

        directionsService = new google.maps.DirectionsService;
        var itineraire = {
            origin: origin,
            destination: destination,
            travelMode: mode
        };
        directionsService.route(itineraire, function (result, status) {
            if (status == 'OK') {
                directionsDisplay.setDirections(result);
            }
            else {
                console.log(result);
            }
        });
    }, function () {console.log("impossible d'obtenir les coordonées GPS");}, {timeout: 10000});
}
