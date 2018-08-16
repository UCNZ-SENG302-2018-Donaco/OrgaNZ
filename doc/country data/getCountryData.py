filename = "countryData.xml"

import xml.etree.ElementTree
e = xml.etree.ElementTree.parse(filename).getroot()

countries = []

class Country:
    def __init__(self):
        self.name = ""
        self.code = ""
        self.latitude = 0
        self.longitude = 0
        
    def __repr__(self):
        return self.name
        
    def __str__(self):
        return self.code + "(\"" + self.name + "\", " + str(self.latitude) + ", "+ str(self.longitude) + "),"
    
i=0

for country in e:
    i+=1
    countryItem = Country()
    countryItem.name = country.find('countryName').text
    countryItem.code = country.find('countryCode').text
    
    north = float(country.find('north').text)
    south = float(country.find('south').text)
    east = float(country.find('east').text)
    west = float(country.find('west').text)
    
    if (east < west):
        # gone past 180 to -180
        east += 360
    
    countryItem.latitude = (north + south) / 2
    countryItem.longitude = (east + west) / 2
    print(countryItem)