metadata {
    definition(name: "Ambient Weather Tile", namespace: "CordMaster", author: "Alden Howard") {
        capability "Temperature Measurement"
        capability "Relative Humidity Measurement"
        capability "Refresh"
        
        command "setTemperature", ["number"]
        command "setHumidity", ["number"]
    }
    
    //needed?
    tiles {
		
        standardTile("refresh", "device.switch") {
            state("default", label: "Refresh", action: "refresh");
        }
        
        valueTile("temperature", "device.temperature", width: 2, height: 2) {
            state("temperature", label: '${currentValue}', unit: "dF");
        }
    }
}

def refresh() {
	parent.fetchNewWeather();
}

def setTemperature(value) {
    sendEvent(name: "temperature", value: value, unit: '°F');
}

def setHumidity(value) {
    sendEvent(name: "humidity", value: value);
}
