import requests

# This data was got from https://en.wikipedia.org/w/index.php?title=Template:Regions_of_New_Zealand&action=edit with Chatham Islands added on
data = """* [[Northland Region|Northland]]
* [[Auckland Region|Auckland]]*
* [[Waikato]]
* [[Bay of Plenty]]
* [[Gisborne District|Gisborne]]*
* [[Hawke's Bay Region|Hawke's Bay]]
* [[Taranaki]]
* [[Manawatu-Wanganui]]
* [[Wellington Region|Wellington]]
|group2 = [[South Island]]
|list2  =
* [[Tasman District|Tasman]]*
* [[Marlborough Region|Marlborough]]*
* [[Nelson, New Zealand|Nelson]]*
* [[West Coast, New Zealand|West Coast]]
* [[Canterbury, New Zealand|Canterbury]]
* [[Otago]]
* [[Southland, New Zealand|Southland]]
* [[Chatham Islands]]"""

items = data.split("\n")
i = 0
items = [region.strip() for region in items]
regions = []
for raw_region in items:
    ##print(raw_region)
    if raw_region[0] == "*":
        region = raw_region.strip("*").strip().strip("[").strip("]")
        if "|" in region:
            region = (region.split("|")[0], region.split("|")[1])
        else:
            region = (region, region)
        regions.append(region)
    
url_prefix = "https://en.wikipedia.org/w/api.php?action=query&prop=coordinates&titles="
url_suffix = "&format=json"

# Regions are stored (Wikipedia page name, Region name)

indent_space = '    '

for region in regions:
    ##print(region)
    url = url_prefix + region[0] + url_suffix
    response = requests.get(url)
    json = response.text
    x = eval(json)['query']['pages']
    lat = x[next(iter(x))]['coordinates'][0]['lat']
    long = x[next(iter(x))]['coordinates'][0]['lon']
    enum_name = region[1].upper().replace("-", "_").replace(" ", "_").replace("'", "")
    print(indent_space + enum_name + "(\"" + region[1] + "\", " + str(lat) + ", " + str(long) + "),")