import requests
import csv

def open_file(filename, mode, encoding="utf-8", errors='ignore'):        
    myfile = open(filename, mode, encoding=encoding, errors=errors)
    return myfile

def extract_str(filename):
    """returns a (stripped) string from file"""
    try:
        myfile = open_file(filename, 'r')
    except FileNotFoundError:
        return None
    s = myfile.read()
    myfile.close()
    s = s.strip()
    return s

def get_google_results(address):
    api_key = "REDACTED"
    
    # Create the url, based on the address and key
    geocode_base_url = "https://maps.googleapis.com/maps/api/geocode/json?address={}&key={}"
    url = geocode_base_url.format(address, api_key)
        
    # Get results from Google
    results = requests.get(url).json()
    ##print(results)
    
    # Return first result
    if len(results['results']) != 0:
        location = results['results'][0].get('geometry').get('location')
        return (location.get('lat'), location.get('lng'))

def create_address(road, suburb, city, postcode):
    address = road
    if suburb not in ['', ' ', None]:
        address += ', ' + suburb
    address += ', ' + city + ' '+ postcode
    return address

def print_hospital_data(in_filename):
    print('Name, Lat, Long, Address')
    with open(in_filename, newline='') as csvfile:
        reader = csv.reader(csvfile, delimiter=',', quotechar='"')
        for row in reader:
            service_types = row[2]
            
            if 'Surgical' in service_types: # we only want hospitals that can perform surgery
                name = row[0]
                road = row[6]
                suburb = row[7]
                city = row[8]
                postcode = row[9]
                dhb_name = row[10]
                
                if ',' in name:
                    name = '"' + name + '"'
                
                address = create_address(road, suburb, city, postcode)
                lat, long = get_google_results(address)
                print(name)
                print(lat)
                print(long)
                print(address)
                print()
    

def get_addresses_to_geocode_from_stdin():
    while True:
        address = input("Address: ")
        print(get_google_results(address))
    
def main():
    print_hospital_data('LegalEntitySummaryPublicHospital.csv')
    
    
if __name__ == "__main__":
    main()
