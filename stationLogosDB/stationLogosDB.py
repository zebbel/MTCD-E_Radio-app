import sqlite3

# select rds country code
countryCode = 7
# get long names from www.fmlist.org list
longNameList = "RDSDX_ITUS/GER.lst.txt"
# input sqlite database from https://webspecial.volkswagen.de/vwinfotainment/de/de/index/downloads#/ "RADIOSTATIONSDATEN"
inputDB = "stl.1vw"
# output sqlite database for use with radio app
outputDB = "stations.db"


# read rds piSid and long names into dict
longNames = {}
with open(longNameList, "r") as file:
    for line in file:
        if(line[9:13] != "    " and line[9:13] != "____" and line[9:13] != "noPI"):
            longNames[int(line[9:13],16)] = line[33:len(line)].split(",")[0]

# open databases
inDB = sqlite3.connect(inputDB)
outDB = sqlite3.connect(outputDB)

# open cursors
inCursor = inDB.cursor()
outCursor = outDB.cursor()

# try to creat table, if tabel already exist delete data from table
try:
    outCursor.execute("CREATE TABLE 'stations' ('stationId'	INTEGER NOT NULL,'frequency' INTEGER,'shortName' TEXT,'longName' TEXT,'stationLogo' BLOB,PRIMARY KEY('stationId'))")
except sqlite3.OperationalError:
    outCursor.execute('DELETE FROM stations;',);

# write default logo to output database
inCursor.execute("SELECT stationLogo FROM StationLogos WHERE logoId is 100")
defaultLogo = inCursor.fetchone()
outCursor.execute("INSERT INTO stations (stationId, stationLogo) VALUES (?, ?)", (100, defaultLogo[0]))

# request data from input database
inCursor.execute("SELECT piSid, frequency, shortName, logoId FROM Stations WHERE country=? AND type='FM'", (str(countryCode)))

result = inCursor.fetchall()
for r in result:
    #load logo from input database
    inCursor.execute("SELECT stationLogo FROM StationLogos WHERE logoId is " + str(r[3]))
    logo = inCursor.fetchone()

    # try to get long name from long names dict
    longName = r[2]
    if(r[0] in longNames.keys()):
        longName = longNames[r[0]]

    # some regional rds piSid are used more thane once, we only pick the first one...
    try:
        # write data to output database
        outCursor.execute("INSERT INTO stations (stationId, frequency, shortName, longName, stationLogo) VALUES (?, ?, ?, ?, ?)", (r[0], r[1], r[2], longName, logo[0]))
    except sqlite3.IntegrityError:
        pass

outDB.commit()

# close cursors
inCursor.close()
outCursor.close()

# close databases
inDB.close()
outDB.close()