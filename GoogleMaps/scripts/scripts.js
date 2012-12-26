/*
* 
* Author: arne.poths@gmail.com
* 
*/

var map;  
var geocoder; // gecoder for decoding a given address to lat/lon coorodinates. 
/**
* Method to init the map object and the geocoder.
* 
*/
function initialize() {
	var mapOptions = {
		zoom: 14,
		center: new google.maps.LatLng(52.457135, 13.527099),
		mapTypeId: google.maps.MapTypeId.ROADMAP
	};
	map = new google.maps.Map(document.getElementById('map_canvas'), 
		mapOptions);
	geocoder = new google.maps.Geocoder();
}
google.maps.event.addDomListener(window, 'load', initialize);
google.maps.event.addDomListener(window, 'load', findAddress);
function findAddress() {
	var address = document.getElementById('address').value;
	geocoder.geocode( { 'address': address}, function(results, status) {
		if (status == google.maps.GeocoderStatus.OK) {
			map.setCenter(results[0].geometry.location);
			var marker = new google.maps.Marker({
				map: map,
				position: results[0].geometry.location
			});
		} else {
	  		alert('Geocode was not successful for the following reason: ' + status);
		}
	});
}

function findByGPS() {
	if (navigator.geolocation)
	{
		navigator.geolocation.getCurrentPosition(showCurrentLocation);
	}
	else{
	}
}

function showCurrentLocation(position) {
	map.setCenter(position.coorods);
	var marker = new google.maps.marker({
		map: map,
		position: position.coords
	});
}

document.onkeydown = function(event) {
	var address = document.getElementById('address');	
}