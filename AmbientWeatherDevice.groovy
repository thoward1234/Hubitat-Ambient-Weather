metadata {
    definition(name: "Ambient Weather Device", namespace: "CordMaster", author: "Alden Howard") {
	capability "Temperature Measurement"
	capability "Relative Humidity Measurement"
	capability "Pressure Measurement"
	capability "Illuminance Measurement"
	capability "Refresh"
	capability "Sensor"
	capability "Actuator"
        
	//Current Conditions
	attribute "weather", "string"
	attribute "weatherIcon", "string"
	attribute "dewPoint", "number"
	attribute "comfort", "number"
	attribute "feelsLike", "number"
	attribute "pressure", "string"
		
	//Indoor Conditions
	attribute "humidityin", "string"
	attribute "temperaturein", "string"
	attribute "baromabsin", "string"
		
	//Precipitation
	attribute "precip_today", "number"
	attribute "precip_1hr", "number"
	attribute "hourlyrainin", "number"
	attribute "weeklyrainin", "number"
	attribute "monthlyrainin", "number"
	attribute "totalrainin", "number"
	attribute "lastRain", "string"
                
	//Wind
	attribute "wind", "number"
	attribute "wind_gust", "number"
	attribute "maxdailygust", "number"
	attribute "wind_degree", "number"
	attribute "wind_dir", "string"
	attribute "wind_direction", "string"
		
	//Light
	attribute "solarradiation", "number"
	attribute "uv", "number"
    }

	preferences {
	section("Preferences") {
	input "showLogs", "bool", required: false, title: "Show Debug Logs?", defaultValue: false
        }
    }
}

def refresh() {
	parent.fetchNewWeather(); 
}

def setWeather(weather){
	logger("debug", "Weather: "+weather);
	
	//Set outdoor temperature
	sendEvent(name: "temperature", value: weather.tempf, unit: '°F', isStateChange: true);
	
	//Set Indoor temperature
	sendEvent(name: "temperaturein", value: weather.tempinf, unit: '°F', isStateChange: true);

	//Set Humidity
	sendEvent(name: "humidity", value: weather.humidity, unit: '%', isStateChange: true);
    
	//Set Indoor Humidity
	sendEvent(name: "humidityin", value: weather.humidityin, unit: '%', isStateChange: true);
    
	//Set DewPoint
	sendEvent(name: "dewPoint", value: weather.dewPoint, unit:'°F', isStateChange: true);
	
	//Set Comfort Level 
	float temp = 0.0;
   
	temp = (weather.dewPoint - 35);
    if (temp <= 0) {
        temp = 0.0;
    } else if (temp >= 40.0) {
        temp = 100.0;
    } else {
        temp = (temp/40.0)*100.0;
    }
    temp = temp.round(1);
    sendEvent(name: "comfort", value: temp, isStateChange: true);
	
	//Set Barometric Pressure
	sendEvent(name: "pressure", value: weather.baromrelin, unit: 'in', isStateChange: true);
	
	//Set Indoor Barometric Pressure
	sendEvent(name: "indoor_pressure", value: weather.baromabsin, unit: 'in', isStateChange: true);
	
	//Set Feels Like Temperature
	sendEvent(name: "feelsLike", value: weather.feelsLike, unit: '°F', isStateChange: true);
    
    //Rain
	sendEvent(name: "precip_today", value: weather.dailyrainin, unit: 'in', isStateChange: true);
	sendEvent(name: "precip_1hr", value: weather.hourlyrainin, unit: 'in', isStateChange: true);
	sendEvent(name: "weeklyrainin", value: weather.weeklyrainin, unit: 'in', isStateChange: true);
	sendEvent(name: "monthlyrainin", value: weather.monthlyrainin, unit: 'in', isStateChange: true);
	sendEvent(name: "totalrainin", value: weather.totalrainin, unit: 'in', isStateChange: true);
	sendEvent(name: "hourlyrainin", value: weather.hourlyrainin, unit: 'in', isStateChange: true);
	sendEvent(name: "lastRain",  value: weather.lastRain, isStateChange: true);

	//Wind
	sendEvent(name: "wind", value: weather.windspeedmph, unit: 'mph', isStateChange: true);
	sendEvent(name: "wind_gust", value: weather.windgustmph, unit: 'mph', isStateChange: true);
	sendEvent(name: "maxdailygust", value: weather.maxdailygust, unit: 'mph', isStateChange: true);
	sendEvent(name: "wind_degree", value: weather.winddir, unit: '°', isStateChange: true);
	
	temp = weather.winddir
	if (temp < 22.5) { 		sendEvent(name:  "wind_direction", value: "North", isStateChange: true);
					            sendEvent(name:  "wind_dir", value: "N", isStateChange: true);
	} else if (temp < 67.5) {  sendEvent(name:  "wind_direction", value: "Northeast", isStateChange: true);
					    		sendEvent(name:  "wind_dir", value: "NE", isStateChange: true);
	} else if (temp < 112.5) {  sendEvent(name: "wind_direction", value: "East", isStateChange: true);
					    		sendEvent(name:  "wind_dir", value: "E", isStateChange: true);
	} else if (temp < 157.5) {  sendEvent(name: "wind_direction", value: "Southeast", isStateChange: true);
					    		sendEvent(name:  "wind_dir", value: "SE", isStateChange: true);
	} else if (temp < 202.5) {  sendEvent(name: "wind_direction", value: "South", isStateChange: true);
					    		sendEvent(name:  "wind_dir", value: "S", isStateChange: true);
	} else if (temp < 247.5) {  sendEvent(name: "wind_direction", value: "Southwest", isStateChange: true);
					    		sendEvent(name:  "wind_dir", value: "SW", isStateChange: true);
	} else if (temp < 292.5) {  sendEvent(name: "wind_direction", value: "West", isStateChange: true);
					    		sendEvent(name:  "wind_dir", value: "W", isStateChange: true);
	} else if (temp < 337.5) {  sendEvent(name: "wind_direction", value: "Northwest", isStateChange: true);
					    		sendEvent(name:  "wind_dir", value: "NW", isStateChange: true);
	} else 					 {  sendEvent(name:  "wind_direction", value: "North", isStateChange: true);
					    		sendEvent(name:  "wind_dir", value: "N", isStateChange: true);
	}
	
	//UV and Light
	sendEvent(name: "solarradiation", value: weather.solarradiation, isStateChange: true);
	sendEvent(name: "illuminance", value: weather.solarradiation, isStateChange: true);
	sendEvent(name: "uv", value: weather.uv, isStateChange: true);
}

private logger(type, msg){
	 if(type && msg && settings?.showLogs) {
        log."${type}" "${msg}"
    }
}
