definition(name: "Ambient Weather API", namespace: "CordMaster", author: "Alden Howard", description: "A simple api for providing ambient weather access", iconUrl: "", iconX2Url: "");

preferences {
    page(name: "page1", title: "Log In", nextPage: "page2", uninstall: true) {
        section {
            input(name: "applicationKey", title: "Application Key", type: "text", required: true);
            input(name: "apiKey", title: "API Key", type: "text", required: true);
        }
    }
    
    page(name: "page2");
    page(name: "page3");
}

def page2() {
    def stations = [];
    def stationMacs = [];
    try {
        stations = getStations();
        
        stations.each { stationMacs << it.macAddress };
    } catch(groovyx.net.http.HttpResponseException e) {
        //then unauthorized
        return dynamicPage(name: "page2", title: "Error", nextPage: "page1", uninstall: true) {
            section {
                paragraph("There was an error authorizing you. Please try again.");
            }
        }
    }
    
   	log.debug("Got stations: " + stations);
    
	return dynamicPage(name: "page2", title: "Select Station", nextPage: "page3", uninstall: true) {
		section {
			input(name: "station", title: "Station", type: "enum", options: stationMacs, required: true);
            input(name: "refreshInterval", title: "Refresh Interval (in minutes)", type: "number", range: "1..3600", defaultValue: 1, required: true);
		}
	}
}

def page3() {
    dynamicPage(name: "page3", title: "Confirm Settings", install: true, uninstall: true) {
        section {
            paragraph("Selected station: $station");
            paragraph("Refresh interval: $refreshInterval minute(s)");
        }
        
        section {
            paragraph("Press done to finish");
        }
    }
}

//lifecycle functions
def installed() {
    log.debug("Installed");
    
    addDevice();
    
    initialize();
    
    runEvery5Minutes(fetchNewWeather);
}

def updated() {
    log.debug("Updated");
    
    unsubscribe();
    unschedule();
    installed();
    initialize(); 
}

def initialize() {
    fetchNewWeather();
    
    //chron schedule, refreshInterval is int
    def m = refreshInterval;
    def h = Math.floor(m / 60);
    m -= h * 60;
    
    m = m == 0 ? "*" : "0/" + m.toInteger();
    h = h == 0 ? "*" : "0/" + h.toInteger();
    
    log.debug("CRON schedule with m: $m and h: $h");
    
    schedule("0 $m $h * * ? *", fetchNewWeather);
}

//children
def addDevice() {
	
    addChildDevice("CordMaster", "Ambient Weather Device", "AWTILE-$station", null, [completedSetup: true]);
}

//fetch functions
def getStations() throws groovyx.net.http.HttpResponseException {
    def data = [];
    
    def params = [
        uri: "https://api.ambientweather.net/",
        path: "/v1/devices",
        query: [applicationKey: applicationKey, apiKey: apiKey]
    ];
    
    requestData("/v1/devices", [applicationKey: applicationKey, apiKey: apiKey]) { response ->
        data = response.data;
    };
        
    return data;
}

def getWeather() throws groovyx.net.http.HttpResponseException {
    def data = [];
    
    requestData("/v1/devices/$station", [applicationKey: applicationKey, apiKey: apiKey, limit: 1]) { response ->
        data = response.data;
    };
        
	return data[0];
}

def requestData(path, query, code) {
    def params = [
        uri: "https://api.ambientweather.net/",
        path: path,
        query: query
    ];
    
    httpGet(params) { response ->
        code(response);
    };
}

//loop
def fetchNewWeather() {
        
    def weather = getWeather();
    
    //log.debug("Weather: " + weather);
	
	childDevices[0].setWeather(weather);
}

