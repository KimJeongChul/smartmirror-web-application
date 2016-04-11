(function() {
    'use strict';

    function WeatherService($http) {
        var service = {};
        service.forcast = null;
        var geoloc = null;

        service.init = function(geoposition) {
            geoloc = geoposition;
            return $http.jsonp('https://api.forecast.io/forecast/'+config.forcast.key+'/'+
                    geoposition.coords.latitude+','+geoposition.coords.longitude+'?units=' +
                    config.forcast.units + "&callback=JSON_CALLBACK")
                .then(function(response) {
                    return service.forcast = response;
                });
        };

        //Returns the current forcast along with high and low tempratures for the current day
        service.currentForcast = function() {
            if(service.forcast === null){
                return null;
            }
            service.forcast.data.currently.day = moment.unix(service.forcast.data.currently.time).format('ddd')
            service.forcast.data.currently.temperature = parseFloat(service.forcast.data.currently.temperature).toFixed(1);
            service.forcast.data.currently.wi = "wi-forecast-io-" + service.forcast.data.currently.icon;
            return service.forcast.data.currently;
        }

        service.weeklyForcast = function(){
            if(service.forcast === null){
                return null;
            }
            // Add human readable info to info
            for (var i = 0; i < service.forcast.data.daily.data.length; i++) {

                /** 한글 UI 변경 - KimJeongChul*/
                var day = moment.unix(service.forcast.data.daily.data[i].time).format('ddd');
                if(day == "Mon") service.forcast.data.daily.data[i].day = "월";
                else if(day == "Tue") service.forcast.data.daily.data[i].day = "화";
                else if(day == "Wed") service.forcast.data.daily.data[i].day = "수";
                else if(day == "Thu") service.forcast.data.daily.data[i].day = "목";
                else if(day == "Fri") service.forcast.data.daily.data[i].day = "금";
                else if(day == "Sat") service.forcast.data.daily.data[i].day = "토";
                else if(day == "Sun") service.forcast.data.daily.data[i].day = "일";

                //service.forcast.data.daily.data[i].day = moment.unix(service.forcast.data.daily.data[i].time).format('ddd');
                service.forcast.data.daily.data[i].temperatureMin = parseFloat(service.forcast.data.daily.data[i].temperatureMin).toFixed(1);
                service.forcast.data.daily.data[i].temperatureMax = parseFloat(service.forcast.data.daily.data[i].temperatureMax).toFixed(1);
                service.forcast.data.daily.data[i].wi = "wi-forecast-io-" + service.forcast.data.daily.data[i].icon;
            };
            return service.forcast.data.daily;
        }

        service.hourlyForcast = function() {
            if(service.forcast === null){
                return null;
            }
            service.forcast.data.hourly.day = moment.unix(service.forcast.data.hourly.time).format('ddd')
            return service.forcast.data.hourly;
        }

        service.refreshWeather = function(){
            return service.init(geoloc);
        }

        return service;
    }

    angular.module('SmartMirror')
        .factory('WeatherService', WeatherService);

}());
